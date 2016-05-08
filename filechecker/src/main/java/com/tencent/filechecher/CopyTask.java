package com.tencent.filechecher;

import java.io.File;

/**
 * Created by haoozhou on 2016/5/8.
 */
public class CopyTask extends Task {

    private String mSrcFilePath;
    private String mDstFilePath;

    public CopyTask(String sdcardPrefix, String nativePrefix, String fileName){
        mSrcFilePath= sdcardPrefix + File.separator + fileName;
        mDstFilePath = nativePrefix + File.separator + fileName;
        callback =  mFileCopy;
    }

    private Runnable mFileCopy = new Runnable() {
        @Override
        public void run() {
            FileUtils.copy(mSrcFilePath, mDstFilePath);
        }
    };

}
