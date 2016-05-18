package com.tencent.filechecher;

import android.os.Handler;

/**
 * Created by haoozhou on 2016/5/8.
 */
public class FileCopyWorker extends Thread {

    private Handler mCallbackHandler;
    //private Context mContext;
    private boolean mCompleted;
    private boolean mCanceled;
    private boolean mIsWaitingCancel;
    private TaskPool mTaskPool;

    public FileCopyWorker(/*Context context, */ Handler callbackHandler, TaskPool taskPool){
        //mContext = context.getApplicationContext();
        mCallbackHandler = callbackHandler;
        mCompleted = false;
        mCanceled = false;
        mIsWaitingCancel = false;
        mTaskPool = taskPool;
    }

    public void cancel(){
        mIsWaitingCancel = true;
    }

    public boolean isCompleted(){
        return mCompleted;
    }

    public boolean isCanceled(){
        return mCanceled;
    }

    public void run(){

        while (!mIsWaitingCancel){
            Task task = mTaskPool.getTask();
            if (task == null){
                mCompleted = true;
                break;
            }
            task.execute();
            task.notifyCompleted();
        }
        /*if (mCompleted && mCallbackHandler != null){
            mCallbackHandler.sendEmptyMessage(FileCopyManager.MSG_WORKER_COMPLETED);
        }else if (mIsWaitingCancel){
            mCanceled = true;
            if (mCallbackHandler != null){
                mCallbackHandler.sendEmptyMessage(FileCopyManager.MSG_WORKER_CANCELED);
            }
        }*/
    }

    public static class WorkerTeam {

        int mSize;
        FileCopyWorker[] workers;

        public WorkerTeam(){
            this(2);
        }

        public WorkerTeam(int size){
            mSize = size;
            workers = new FileCopyWorker[mSize];
        }

        public void startWorking(TaskPool taskPool, Handler callbackHandler){
            for (int i = 0; i < mSize; i++){
                FileCopyWorker worker = new FileCopyWorker(callbackHandler, taskPool);
                workers[i] = worker;
                workers[i].start();
            }
        }

        public void stopWorking(){
            if (workers != null && workers.length > 0){
                for (FileCopyWorker worker : workers){
                    worker.cancel();
                }
            }
        }

        /**
         * 是否所有worker都完成了工作
         * @return
         */
        public boolean isCompleted(){
            boolean completed = true;
            if (workers != null && workers.length > 0){
                for (FileCopyWorker worker : workers){
                    completed =  completed && worker.isCompleted();
                }
            }
            return completed;
        }

        public boolean isCanceled(){
            boolean canceled = true;
            if (workers != null && workers.length > 0){
                for (FileCopyWorker worker : workers){
                    canceled =  canceled && worker.isCanceled();
                }
            }
            return canceled;
        }
    }

}
