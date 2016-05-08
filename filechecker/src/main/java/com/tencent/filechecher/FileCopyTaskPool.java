package com.tencent.filechecher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by haoozhou on 2016/5/8.
 */
public class FileCopyTaskPool extends TaskPool {


    private List<String> mFileList;

    private String[] mDataSourceList = new String[]{
            "tencent/wecarmusic/data/preloaded",
            "tencent/wecarspeech/data",
    };


    @Override
    public void initPool() {
        mFileList = new ArrayList<>();
        for (String source : mDataSourceList){
            mFileList.addAll(FileUtils.traverseFiles(FileUtils.PREFIX_SDCARD_EXTERNAL, source, null));
        }
    }

    @Override
    public synchronized Task getTask(){
        if (mFileList == null || mFileList.size() == 0){
            return null;
        }
        String file = mFileList.get(0);
        CopyTask copyTask = new CopyTask(FileUtils.PREFIX_SDCARD_EXTERNAL, FileUtils.PREFIX_NATIVE_EXTERNAL, file);
        return copyTask;
    }

}
