package com.tencent.filechecher;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by haoozhou on 2016/5/16.
 */
public class FileCopyManager {

    public static final int MSG_COPY_PROGRESS_CHANGED = 1;
    public static final int MSG_COPY_STARTED = 2;
    public static final int MSG_WORKER_COMPLETED = 3;
    public static final int MSG_WORKER_CANCELED = 4;

    private Handler mUIHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_COPY_STARTED:
                    notifyCopyStarted();
                    break;
                case MSG_COPY_PROGRESS_CHANGED:
                    mCompletedTask++;
                    //notify progress changed
                    notifyCopyProgressChanged(mCompletedTask, mTotalTask);
                    break;
                case MSG_WORKER_COMPLETED:
                    if (mWorkerTeam != null && mWorkerTeam.isCompleted()){
                        //copy completed
                        notifyCopyCompleted();
                    }
                    break;
                case MSG_WORKER_CANCELED:

                    break;
            }
        }
    };

    private TaskPool mCopyTaskPool;
    private FileCopyWorker.WorkerTeam mWorkerTeam;
    private int mTotalTask, mCompletedTask;

    private void init(){
        mCopyTaskPool = new FileCopyTaskPool(FileUtils.PREFIX_SDCARD_EXTERNAL + "/tencent/seed");
    }

    /**
     * not run in UI thread
     */
    public void startCopy(){
        //cancelCopy();
        mCopyTaskPool.initPool();
        mTotalTask = mCopyTaskPool.getTaskCount();
        mCompletedTask = 0;
        mWorkerTeam = new FileCopyWorker.WorkerTeam();
        mWorkerTeam.startWorking(mCopyTaskPool, mUIHandler);
    }

    public void cancelCopy(){
        if (mWorkerTeam != null){
            mWorkerTeam.stopWorking();
            mWorkerTeam = null;
        }
    }

    private List<FileCopyListener> mFileCopyListeners;

    public synchronized void registerFileCopyListener(FileCopyListener listener){
        if (mFileCopyListeners == null){
            mFileCopyListeners = new ArrayList<>();
        }
        if (listener != null && !mFileCopyListeners.contains(listener)){
            mFileCopyListeners.add(listener);
        }
    }

    public synchronized void unregisterFileCopyListener(FileCopyListener listener){
        if (mFileCopyListeners == null){
            mFileCopyListeners = new ArrayList<>();
        }
        if (listener != null && mFileCopyListeners.contains(listener)){
            mFileCopyListeners.remove(listener);
        }
    }

    private synchronized void notifyCopyStarted(){
        if (mFileCopyListeners != null){
            for (FileCopyListener listener : mFileCopyListeners){
                listener.onCopyStarted();
            }
        }
    }

    private synchronized void notifyCopyProgressChanged(int completed, int total){
        if (mFileCopyListeners != null){
            for (FileCopyListener listener : mFileCopyListeners){
                listener.onCopyProgressChanged(completed, total);
            }
        }
    }

    private synchronized void notifyCopyCompleted(){
        if (mFileCopyListeners != null){
            for (FileCopyListener listener : mFileCopyListeners){
                listener.onCopyCompleted();
            }
        }
    }

    private synchronized void notifyCopyCanceled(){
        if (mFileCopyListeners != null){
            for (FileCopyListener listener : mFileCopyListeners){
                listener.onCopyCanceled();
            }
        }
    }

    public interface FileCopyListener{

        void onCopyStarted();

        void onCopyProgressChanged(int completed, int total);

        void onCopyCompleted();

        void onCopyCanceled();
    }


}
