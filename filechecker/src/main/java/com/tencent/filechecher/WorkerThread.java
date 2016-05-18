package com.tencent.filechecher;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

/**
 * Created by haoozhou on 2016/5/18.
 */
class WorkerThread extends Thread {

    private String TAG = getClass().getSimpleName();

    private FileCopyThread fileCopyThread;
    //private String TAG = Thread.currentThread().getName();//getClass().getSimpleName();
    boolean mWaitingCancel;
    Handler mCallbackHandler;
    boolean mCompleted;
    boolean mCanceled;

    boolean mDoCopy, mDoCheck;

    /**
     * 是否允许快速复制。若源文件和目标文件都存在且文件大小一致，则不进行复制操作
     */
    boolean mEnableQuickCopy;

    public WorkerThread(FileCopyThread fileCopyThread, boolean doCopy, boolean doCheck, Handler callbackHandler, String name) {
        /*super(name);
        this.fileCopyThread = fileCopyThread;
        mDoCopy = doCopy;
        mDoCheck = doCheck;
        mCallbackHandler = callbackHandler;
        mCompleted = false;
        mCanceled = false;
        mWaitingCancel = false;
        mEnableQuickCopy = false;*/
        this(fileCopyThread, doCopy, doCheck, false, callbackHandler, name);
    }

    public WorkerThread(FileCopyThread fileCopyThread, boolean doCopy, boolean doCheck, boolean enableQuickCopy, Handler callbackHandler, String name) {
        super(name);
        this.fileCopyThread = fileCopyThread;
        mDoCopy = doCopy;
        mDoCheck = doCheck;
        mCallbackHandler = callbackHandler;
        mCompleted = false;
        mCanceled = false;
        mWaitingCancel = false;
        mEnableQuickCopy = enableQuickCopy;
    }

    /**
     * 取消任务
     */
    public void cancel() {
        mWaitingCancel = true;
    }

    /**
     * 判断任务是否执行完成
     *
     * @return
     */
    public boolean isCompleted() {
        return mCompleted;
    }

    /**
     * 判断任务是否已取消
     *
     * @return
     */
    public boolean isCanceled() {
        return mCanceled;
    }

    public void run() {
        mCompleted = false;
        mCanceled = false;
        while (!mWaitingCancel) {
            DataFile file = fileCopyThread.getNextDataFile();
            if (file == null) {
                //finish copying
                mCompleted = true;
                break;
            }
            String srcFilePath = FileUtils.PREFIX_SDCARD_EXTERNAL + File.separator + file.path;
            String dstFilePath = FileUtils.PREFIX_NATIVE_EXTERNAL + File.separator + file.path;
            int copyResult = FileUtils.ERR_SUCCESS;
            if (mDoCopy) {
                if (mEnableQuickCopy){
                    if (FileUtils.length(srcFilePath) != FileUtils.length(dstFilePath)){
                        Log.d(TAG, "do real file copy, " + file);
                        copyResult = FileUtils.copy(srcFilePath, dstFilePath);
                    }
                }else {
                    copyResult = FileUtils.copy(srcFilePath, dstFilePath);
                }
            }
            if (copyResult != FileUtils.ERR_SUCCESS) {
                //拷贝失败
                CheckDiff diff = new CheckDiff();
                diff.diffType = CheckDiff.DiffType.Missing;
                diff.filePath = file.path;
                diff.dataType = CheckDiff.DataType.map(file.path);
                notifyCopyOrCheckFailed(diff);
                continue;
            }
            if (mDoCheck) {
                if (file.length != FileUtils.length(dstFilePath)) {
                    //拷贝失败，文件长度不一致
                    CheckDiff diff = new CheckDiff();
                    diff.diffType = CheckDiff.DiffType.DifSize;
                    diff.filePath = file.path;
                    diff.dataType = CheckDiff.DataType.map(file.path);
                    notifyCopyOrCheckFailed(diff);
                } else {
                    //计算MD5值
                    String md5Calculated = FileUtils.calculateMD5(dstFilePath, file.offset, file.checkSize);
                    if (!TextUtils.isEmpty(md5Calculated) && md5Calculated.equals(file.md5)) {
                        //文件完全相同，拷贝成功
                        notifyCheckSuccess(file);
                    } else {
                        //文件MD5值不同，拷贝失败
                        CheckDiff diff = new CheckDiff();
                        diff.diffType = CheckDiff.DiffType.Modify;
                        diff.filePath = file.path;
                        diff.dataType = CheckDiff.DataType.map(file.path);
                        diff.md5Stander = file.md5;
                        diff.md5Calculated = md5Calculated;
                        notifyCopyOrCheckFailed(diff);
                    }
                }
            }
        }
        if (!mCompleted) {
            //被终止操作
            mCanceled = true;
            Log.d(getName(), "worker cancel copying");
        } else {
            Log.d(getName(), "worker complete copying");
        }
    }

    /*private void notifyCopySuccess(DataFile dataFile) {
        Log.d(getName(), "copy success: " + dataFile);
        if (mCallbackHandler != null) {
            Message msg = mCallbackHandler.obtainMessage(1);
            msg.obj = dataFile;
            mCallbackHandler.sendMessage(msg);
        }
    }*/

    private void notifyCopyOrCheckFailed(CheckDiff diff) {
        if (mCallbackHandler != null) {
            Message msg = mCallbackHandler.obtainMessage(FileCopyManager.MSG_COPY_OR_CHECK_FAILED);
            msg.obj = diff;
            mCallbackHandler.sendMessage(msg);
        }
    }


    private void notifyCheckSuccess(DataFile dataFile) {
        if (mCallbackHandler != null) {
            Message msg = mCallbackHandler.obtainMessage(FileCopyManager.MSG_CHECK_SUCCESS);
            msg.obj = dataFile;
            mCallbackHandler.sendMessage(msg);
        }
    }

    /*private void notifyCheckFailed(CheckDiff diff) {
        if (mCallbackHandler != null) {
            Message msg = mCallbackHandler.obtainMessage(FileCopyManager.MSG_CHECK_FAILED);
            msg.obj = diff;
            mCallbackHandler.sendMessage(msg);
        }
    }*/

}
