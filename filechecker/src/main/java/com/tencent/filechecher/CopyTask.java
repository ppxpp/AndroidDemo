package com.tencent.filechecher;

import android.os.Handler;

import java.io.File;

/**
 * Created by haoozhou on 2016/5/8.
 */
public class CopyTask extends Task {

    public static final int MSG_TASK_COMPLETED = 1;

    /**
     * 源文件路径
     */
    private String mSrcFilePath;
    /**
     * 目标文件路径
     */
    private String mDstFilePath;


    private Handler mCallbackHandler;

    public CopyTask(String sdcardPrefix, String nativePrefix, String fileName){
        this(sdcardPrefix, nativePrefix, fileName, null);
    }

    public CopyTask(String sdcardPrefix, String nativePrefix, String fileName, Handler callbackHandler){
        mSrcFilePath= sdcardPrefix + File.separator + fileName;
        mDstFilePath = nativePrefix + File.separator + fileName;
        callback =  mFileCopy;
        mCallbackHandler = callbackHandler;
    }

    private Runnable mFileCopy = new Runnable() {
        @Override
        public void run() {
            FileUtils.copy(mSrcFilePath, mDstFilePath);
        }
    };

    @Override
    public void notifyCompleted() {
        if (mCallbackHandler != null){
            mCallbackHandler.sendEmptyMessage(1);
        }
    }
}
