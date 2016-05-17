package com.tencent.filechecher;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by haoozhou on 2016/5/8.
 */
public class FileCopyTaskPool extends TaskPool {


    private List<String> mFileList;

    /*private String[] mDataSourceList = new String[]{
            "tencent/wecarmusic/data/preloaded",
            "tencent/wecarspeech/data",
    };*/

    private Handler mUIHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case CopyTask.MSG_TASK_COMPLETED:

                    break;
            }
        }
    };

    public FileCopyTaskPool(String seedFilePath){
        mFileList = new ArrayList<>();
        mSeedFilePath = seedFilePath;
    }

    //从文件读出所有待拷贝的文件路径集合
    private String mSeedFilePath = "";

    /**
     * 初始化任务数据
     */
    private void init(){
        mFileList.clear();
        File seed = new File(mSeedFilePath);
        if (!seed.exists()){
            return;
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(seed));
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                String filePath = parserPath(line);
                if (!TextUtils.isEmpty(filePath)){
                    mFileList.add(filePath);
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从文件的每一行抽取出文件路径
     * @param line
     * @return
     */
    private String parserPath(String line){
        if (!TextUtils.isEmpty(line)){
            String[] segs = line.split("  ");
            if (segs != null && segs.length == 2){
                return segs[1];
            }
        }
        return null;
    }


    @Override
    public void initPool() {
        init();
        /*mFileList.clear();// = new ArrayList<>();
        for (String source : mDataSourceList){
            mFileList.addAll(FileUtils.traverseFiles(FileUtils.PREFIX_SDCARD_EXTERNAL, source, null));
        }*/
    }

    @Override
    public synchronized Task getTask(){
        if (mFileList == null || mFileList.size() == 0){
            return null;
        }
        String file = mFileList.remove(0);//.get(0);
        CopyTask copyTask = new CopyTask(FileUtils.PREFIX_SDCARD_EXTERNAL, FileUtils.PREFIX_NATIVE_EXTERNAL, file, mUIHandler);
        return copyTask;
    }

    @Override
    public int getTaskCount() {
        return 0;
    }


}
