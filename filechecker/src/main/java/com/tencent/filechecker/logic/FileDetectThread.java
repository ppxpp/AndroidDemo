package com.tencent.filechecker.logic;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import com.tencent.filechecker.FileUtils;
import com.tencent.filechecker.entity.DetectConfig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by haoozhou on 2016/5/17.
 */
public class FileDetectThread extends Thread {

    private String TAG = getClass().getSimpleName();

    //默认检查10KB的数据量
    private static final long CHECK_SIZE = 1024 * 10;


    private Handler mCallbackHandler;
    private List<String> mFileList;
    private DetectConfig mConfig;
    //预置的结果文件
    private String outFilePath = FileUtils.RESULT_FILE_PATH;

    private String[] mDataSourceList = new String[]{
            "tencent/wecarmusic/data/preloaded",
            "tencent/wecarspeech/data",
            "sogou/scel",
            "tencent/wecarnavi/data",
    };

    public FileDetectThread(DetectConfig config, Handler callbackHandler){
        mCallbackHandler = callbackHandler;
        mFileList = new ArrayList<>(1350);
        if (config == null){
            config = DetectConfig.defaultConfig();
        }
        mConfig = config;
    }


    public void run(){
        long time = SystemClock.elapsedRealtime();
        notifyCheckStarted();
        collectFiles();
        //删除之前的结果文件
        //String outFullPath = FileUtils.PREFIX_SDCARD_EXTERNAL + "/" + outFilePath;
        String outFullPath = mConfig.PREFIX_USB_EXTERNAL + "/" + outFilePath;
        FileUtils.deleteFile(outFullPath);

        File outFile = new File(outFullPath);
        //确保目录存在
        FileUtils.mkdirsIfNeeded(outFullPath);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
            final int size = mFileList.size();
            for (int i = 0; i < mFileList.size(); i++){
                String path = mFileList.get(i);
                //String fullPath = FileUtils.PREFIX_SDCARD_EXTERNAL + "/" + path;
                String fullPath = mConfig.PREFIX_USB_EXTERNAL + "/" + path;
                File file = new File(fullPath);
                if (!file.exists()){
                    Log.d(TAG, "file not exists, " + file);
                    continue;
                }
                long length = file.length();
                long offset = 0L;
                long checkSize = CHECK_SIZE;
                if (length < checkSize){
                    checkSize = length;
                }else if (length > checkSize){
                    //可以在range范围内随机选择一个offset
                    long range = length - CHECK_SIZE;
                    Random random = new Random();
                    offset = Math.abs(random.nextLong()) % range;
                }
                String md5 = FileUtils.calculateMD5(fullPath, offset, checkSize);
                try {
                    StringBuilder line = new StringBuilder(path + FileUtils.SEG_STR
                            + length + FileUtils.SEG_STR
                            + offset + FileUtils.SEG_STR
                            + checkSize + FileUtils.SEG_STR
                            + md5);
                    writer.write(line.toString() + "\r\n");
                    notifyProgressChanged(i + 1, size);
                }catch (IOException e){
                    e.printStackTrace();
                    notifyCheckError(1);
                    return;
                }
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            notifyCheckError(1);
            return;
        }
        time = SystemClock.elapsedRealtime() - time;
        Log.d(TAG, "time = " + time / 1000);
        notifyCheckCompleted();

    }


    /**
     * 遍历指定目录下的所有子文件
     */
    private void collectFiles(){
        mFileList.clear();
        for (String source : mDataSourceList){
            //mFileList.addAll(FileUtils.traverseFiles(FileUtils.PREFIX_SDCARD_EXTERNAL, source, mFilenameFilter));
            mFileList.addAll(FileUtils.traverseFiles(mConfig.PREFIX_USB_EXTERNAL, source, mFilenameFilter));
        }
    }

    private FilenameFilter mFilenameFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String filename) {
            File file = new File(dir.getAbsolutePath() + File.separator + filename);
            if (file.isDirectory()) {
                return true;
            }else if (file.isFile()) {
                return true;//filename.endsWith(".java");
            }
            return false;
        }
    };

    private void notifyCheckStarted(){
        if (mCallbackHandler != null){
            Message msg = mCallbackHandler.obtainMessage(FileDetectManager.MSG_DETECT_STARTED);
            mCallbackHandler.sendMessage(msg);
        }
    }


    /**
     * 通知进度
     * @param progress
     * @param total
     */
    private void notifyProgressChanged(int progress, int total){
        Log.d(TAG, "onProgress, " + progress + "/" + total);
        if (mCallbackHandler != null){
            Message msg = mCallbackHandler.obtainMessage(FileDetectManager.MSG_DETECT_PROGRESS);
            msg.arg1 = progress;
            msg.arg2 = total;
            mCallbackHandler.sendMessage(msg);
        }
    }

    private void notifyCheckCompleted(){
        if (mCallbackHandler != null){
            Message msg = mCallbackHandler.obtainMessage(FileDetectManager.MSG_DETECT_COMPLETED);
            mCallbackHandler.sendMessage(msg);
        }
    }

    private void notifyCheckError(int err){
        if (mCallbackHandler != null){
            Message msg = mCallbackHandler.obtainMessage(FileDetectManager.MSG_DETECT_ERROR);
            msg.arg1 = err;
            mCallbackHandler.sendMessage(msg);
        }
    }

}
