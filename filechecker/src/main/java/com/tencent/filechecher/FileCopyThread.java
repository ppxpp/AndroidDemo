package com.tencent.filechecher;

import android.os.Handler;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by haoozhou on 2016/5/16.
 */
public class FileCopyThread extends Thread {

    private Handler mCallbackHandler;
    private String mSeedFilePath;
    private List<String> mFileList;
    //private BlockingQueue<String> mFileList;

    public FileCopyThread(String seedFilePath, Handler callbackHandler){
        mSeedFilePath = seedFilePath;
        mCallbackHandler = callbackHandler;
        mFileList = new ArrayList<>();
        //mFileList = new LinkedBlockingQueue<>();
    }

    private synchronized String getNextFilePath(){
        if (mFileList == null || mFileList.size() == 0){
            return null;
        }
        return mFileList.remove(0);
    }

    public void run(){
        //读取需要拷贝的文件列表
        mFileList.clear();
        mFileList.addAll(getFileList(mSeedFilePath));

        //开始拷贝

    }

    private List<String> getFileList(String seedFilePath){
        List<String> files = new ArrayList<>();
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
                String filePath = parserPath(line);
                if (!TextUtils.isEmpty(filePath)){
                    files.add(filePath);
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
    private String parserPath(String line){
        if (!TextUtils.isEmpty(line)){
            String[] segs = line.split("  ");
            if (segs != null && segs.length == 2){
                return segs[1];
            }
        }
        return null;
    }

    private class WorkerThread extends Thread{

        boolean mWaitingCancel;

        public void run(){

            while (!mWaitingCancel){
                String filePath = getNextFilePath();
                /*mSrcFilePath= sdcardPrefix + File.separator + fileName;
                mDstFilePath = nativePrefix + File.separator + fileName;
                FileUtils.copy(mSrcFilePath, mDstFilePath);*/
            }
        }

    }
}
