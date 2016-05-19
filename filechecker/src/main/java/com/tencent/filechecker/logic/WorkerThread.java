package com.tencent.filechecker.logic;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.filechecker.FileUtils;
import com.tencent.filechecker.entity.CheckDiff;
import com.tencent.filechecker.entity.DataFile;

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
    private String mSrcPathPrefix;
    private String mDstPathPrefix;

    /**
     * 是否允许快速复制。若源文件和目标文件都存在且文件大小一致，则不进行复制操作
     */
    boolean mEnableQuickCopy;

    /*public WorkerThread(FileCopyThread fileCopyThread, String srcPathPrefix, String dstPathPrefix, boolean doCopy, boolean doCheck, Handler callbackHandler, String name) {
        this(fileCopyThread, srcPathPrefix, dstPathPrefix, doCopy, doCheck, false, callbackHandler, name);
    }*/

    public WorkerThread(FileCopyThread fileCopyThread, String srcPathPrefix, String dstPathPrefix, boolean doCopy, boolean doCheck, boolean enableQuickCopy, Handler callbackHandler, String name) {
        super(name);
        this.fileCopyThread = fileCopyThread;
        mDoCopy = doCopy;
        mDoCheck = doCheck;
        mCallbackHandler = callbackHandler;
        mCompleted = false;
        mCanceled = false;
        mWaitingCancel = false;
        mEnableQuickCopy = enableQuickCopy;
        mSrcPathPrefix = srcPathPrefix;
        mDstPathPrefix = dstPathPrefix;
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
            //String srcFilePath = FileUtils.PREFIX_SDCARD_EXTERNAL + File.separator + file.path;
            String srcFilePath = mSrcPathPrefix + File.separator + file.path;
            //String dstFilePath = FileUtils.PREFIX_NATIVE_EXTERNAL + File.separator + file.path;
            String dstFilePath = mDstPathPrefix + File.separator + file.path;
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
                if (!mDoCheck && copyResult == FileUtils.ERR_SUCCESS){
                    //如果不校验，则需要通知复制成功
                    notifyCopyOrCheckSuccess(file);
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
                    if (!(new File(dstFilePath).exists())){
                        diff.diffType = CheckDiff.DiffType.Missing;
                    }
                    diff.filePath = file.path;
                    diff.dataType = CheckDiff.DataType.map(file.path);
                    diff.md5Stander = file.md5;
                    notifyCopyOrCheckFailed(diff);
                } else {
                    //计算MD5值
                    String md5Calculated = FileUtils.calculateMD5(dstFilePath, file.offset, file.checkSize);
                    if (!TextUtils.isEmpty(md5Calculated) && md5Calculated.equals(file.md5)) {
                        //文件完全相同，拷贝成功
                        notifyCopyOrCheckSuccess(file);
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


    private void notifyCopyOrCheckSuccess(DataFile dataFile) {
        if (mCallbackHandler != null) {
            Message msg = mCallbackHandler.obtainMessage(FileCopyManager.MSG_COPY_OR_CHECK_SUCCESS);
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
