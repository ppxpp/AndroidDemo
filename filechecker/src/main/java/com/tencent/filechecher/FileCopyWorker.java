package com.tencent.filechecher;

import android.content.Context;
import android.os.Handler;

/**
 * Created by haoozhou on 2016/5/8.
 */
public class FileCopyWorker extends Thread {

    private Handler mCallbackHandler;
    private Context mContext;
    private boolean mIsWatingCancel;
    private TaskPool mTaskPool;

    public FileCopyWorker(Context context, Handler callbackHandler, TaskPool taskPool){
        mContext = context.getApplicationContext();
        mCallbackHandler = callbackHandler;
        mIsWatingCancel = false;
        mTaskPool = taskPool;
    }

    public void cancel(){
        mIsWatingCancel = true;
    }


    public void run(){

        while (!mIsWatingCancel){
            Task task = mTaskPool.getTask();
            if (task == null){
                break;
            }
            task.execute();
        }
    }

}
