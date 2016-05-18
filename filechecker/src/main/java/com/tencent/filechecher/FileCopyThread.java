package com.tencent.filechecher;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by haoozhou on 2016/5/16.
 */
public class FileCopyThread extends Thread {

    private String TAG = getClass().getSimpleName();

    private boolean mWaitingCancel;
    private Handler mCallbackHandler;
    private List<DataFile> mFileList;

    public FileCopyThread(/*String seedFilePath, */Handler callbackHandler){
        mCallbackHandler = callbackHandler;
        mFileList = new ArrayList<>(1350);
        mWaitingCancel = false;
    }

    /**
     * 获取下一个需要被处理的文件
     * @return
     */
    public synchronized DataFile getNextDataFile(){
        if (mFileList == null || mFileList.size() == 0){
            return null;
        }
        return mFileList.remove(0);
    }

    public void cancel(){
        mWaitingCancel = true;
    }

    public void run(){
        long time = SystemClock.elapsedRealtime();
        //拷贝预置结果文件
        String srcResultPath = FileUtils.PREFIX_SDCARD_EXTERNAL + "/" + FileUtils.RESULT_FILE_PATH;
        String dstResultPath = FileUtils.PREFIX_NATIVE_EXTERNAL + "/" + FileUtils.RESULT_FILE_PATH;
        int copyResult = FileUtils.copy(srcResultPath, dstResultPath);
        if (copyResult != FileUtils.ERR_SUCCESS || FileUtils.length(srcResultPath) != FileUtils.length(dstResultPath)){
            //拷贝失败
            /*//通知改类型文件拷贝失败
            CheckDiff diff = new CheckDiff();
            diff.diffType = CheckDiff.DiffType.Missing;
            diff.filePath = FileUtils.RESULT_FILE_PATH;
            diff.dataType = CheckDiff.DataType.map(FileUtils.RESULT_FILE_PATH);
            notifyCopyOrCheckFailed(diff);*/
            //通知拷贝失败
            notifyCopyError(FileCopyManager.ERR_MD5RESULT_COPY_FAILED);
            return;
        }
        //读取需要拷贝的文件列表
        mFileList.clear();
        mFileList.addAll(getFileList(dstResultPath));
        Log.d(TAG, "size = " + mFileList.size());
        //通知任务已开始
        notifyCopyStarted(mFileList.size());

        if(mWaitingCancel){
            notifyCopyCanceled();
            return;
        }

        //开始拷贝
        WorkerThread[] workers = new WorkerThread[8];
        for (int i = 0; i < workers.length; i++){
            workers[i] = new WorkerThread(this, true, true, true, mCallbackHandler, "worker-" + i);
            //workers[i] = new WorkerThread(this, false, true, true, mCallbackHandler, "worker-" + i);
            workers[i].start();
        }
        boolean completed = false;
        while (!completed){
            //检查是否完成
            completed = true;
            for (int i = 0; i < workers.length; i++){
                completed = completed && workers[i].isCompleted();
            }
            if (completed){
                //通知任务已完成
                notifyCopyCompleted();
                break;
            }
            //检查任务是否需要取消
            if (mWaitingCancel){
                boolean canceled = true;
                for (int i = 0; i < workers.length; i++){
                    workers[i].cancel();
                }
                for (int i = 0; i < workers.length; i++){
                    canceled = canceled && workers[i].isCanceled();
                }
                if (canceled){
                    //通知任务已取消
                    notifyCopyCanceled();
                    break;
                }
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        time = SystemClock.elapsedRealtime() - time;
        Log.d(TAG, "thread finish, cost time = " + time / 1000);
    }

    /**
     * 从预置的结果文件中解析出所有需要处理的文件列表
     * @param seedFilePath
     * @return
     */
    private List<DataFile> getFileList(String seedFilePath){
        List<DataFile> files = new ArrayList<>();
        File seed = new File(seedFilePath);
        if (!seed.exists()){
            return files;
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(seed));
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                DataFile file = parserPath(line);
                if (file != null){
                    files.add(file);
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return files;
    }

    /**
     * 从文件的每一行抽取出文件路径
     * @param line
     * @return
     */
    private DataFile parserPath(String line){
        if (!TextUtils.isEmpty(line)){
            //line format: <filePath>\t<length>\t<offset>\t<checkSize>\t<md5>
            String[] segs = line.split(FileUtils.SEG_STR);
            if (segs != null && segs.length == 5){
                DataFile file = new DataFile();
                file.path = segs[0];
                file.length = Long.valueOf(segs[1]);
                file.offset = Long.valueOf(segs[2]);
                file.checkSize = Long.valueOf(segs[3]);
                file.md5 = segs[4];
                return file;
            }
        }
        return null;
    }

    private void notifyCopyStarted(int size){
        if (mCallbackHandler != null){
            Message msg = mCallbackHandler.obtainMessage(FileCopyManager.MSG_COPY_STARTED);
            msg.arg1 = size;
            mCallbackHandler.sendMessage(msg);
        }

    }

    private void notifyCopyCompleted(){
        if (mCallbackHandler != null){
            mCallbackHandler.sendEmptyMessage(FileCopyManager.MSG_COPY_COMPLETED);
        }
    }

    private void notifyCopyError(int err){
        if (mCallbackHandler != null){
            Message msg = mCallbackHandler.obtainMessage(FileCopyManager.MSG_COPY_ERROR, err, 0);
            mCallbackHandler.sendMessage(msg);
        }
    }

    private void notifyCopyCanceled(){
        if (mCallbackHandler != null){
            mCallbackHandler.sendEmptyMessage(FileCopyManager.MSG_COPY_CANCELED);
        }
    }

    /*private void notifyCopyOrCheckFailed(CheckDiff diff){
        if (mCallbackHandler != null){
            Message msg = mCallbackHandler.obtainMessage(FileCopyManager.MSG_COPY_OR_CHECK_FAILED);
            msg.obj = diff;
            mCallbackHandler.sendMessage(msg);
        }
    }*/

}
