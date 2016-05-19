package com.tencent.filechecker.logic;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.tencent.filechecker.entity.DetectConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by haoozhou on 2016/5/16.
 */
public class FileDetectManager {

    private static FileDetectManager instance = new FileDetectManager();

    private FileDetectManager(){}

    public static FileDetectManager getInstance(){
        return instance;
    }

    public static final int ERR_MD5RESULT_COPY_FAILED = 1;


    public static final int MSG_DETECT_STARTED = 1;
    //public static final int MSG_COPY_OR_CHECK_FAILED = 2;
    public static final int MSG_DETECT_PROGRESS = 3;
    public static final int MSG_DETECT_COMPLETED = 5;
    public static final int MSG_DETECT_CANCELED = 6;
    public static final int MSG_DETECT_ERROR = 7;

    private Handler mUIHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_DETECT_STARTED:
                    //mFileCount = msg.arg1;
                    //notifyDetectStarted(mFileCount);
                    notifyDetectStarted(msg.arg1);
                    break;
                /*case MSG_COPY_OR_CHECK_FAILED:
                    mCurtProgress++;
                    notifyDetectProgressChanged(mCurtProgress, mFileCount);
                    notifyCopyOrCheckFailed((CheckDiff) msg.obj);
                    break;*/
                case MSG_DETECT_PROGRESS:
                    notifyDetectProgressChanged(msg.arg1, msg.arg2);
                    break;
                case MSG_DETECT_COMPLETED:
                    mFileDetectThread = null;
                    notifyDetectCompleted();
                    break;
                case MSG_DETECT_ERROR:
                    int err = msg.arg1;
                    notifyDetectError(err);
                    break;
                case MSG_DETECT_CANCELED:
                    notifyDetectCanceled();
                    break;
            }
        }
    };

    //private int mFileCount = 0;
    //private int mCurtProgress = 0;

    FileDetectThread mFileDetectThread;

    public void startDetect(DetectConfig config){
        if (mFileDetectThread != null){
            //mFileDetectThread.cancel();
            mFileDetectThread = null;
        }
        //mFileCount = -1;
        //mCurtProgress = 0;
        mFileDetectThread = new FileDetectThread(config, mUIHandler);
        mFileDetectThread.start();
    }

    public void cancelCopy(){
        if (mFileDetectThread != null){
            //mFileDetectThread.cancel();
            mFileDetectThread = null;
        }
    }

    private List<FileDetectListener> mFileDetectListeners;

    public synchronized void registerFileDetectListener(FileDetectListener listener){
        if (mFileDetectListeners == null){
            mFileDetectListeners = new ArrayList<>();
        }
        if (listener != null && !mFileDetectListeners.contains(listener)){
            mFileDetectListeners.add(listener);
        }
    }

    public synchronized void unregisterFileDetectListener(FileDetectListener listener){
        if (mFileDetectListeners == null){
            mFileDetectListeners = new ArrayList<>();
        }
        if (listener != null && mFileDetectListeners.contains(listener)){
            mFileDetectListeners.remove(listener);
        }
    }

    private synchronized void notifyDetectStarted(int fileCount){
        if (mFileDetectListeners != null){
            for (FileDetectListener listener : mFileDetectListeners){
                listener.onDetectStarted(fileCount);
            }
        }
    }

    private synchronized void notifyDetectProgressChanged(int completed, int total){
        if (mFileDetectListeners != null){
            for (FileDetectListener listener : mFileDetectListeners){
                listener.onDetectProgressChanged(completed, total);
            }
        }
    }

    /*private synchronized void notifyCopyOrCheckFailed(CheckDiff diff){
        if (mFileDetectListeners != null){
            for (FileDetectListener listener : mFileDetectListeners){
                listener.onCopyOrCheckFailed(diff);
            }
        }
    }*/

    private synchronized void notifyDetectCompleted(){
        if (mFileDetectListeners != null){
            for (FileDetectListener listener : mFileDetectListeners){
                listener.onDetectCompleted();
            }
        }
    }

    private synchronized void notifyDetectError(int err){
        if (mFileDetectListeners != null){
            for (FileDetectListener listener : mFileDetectListeners){
                listener.onDetectError(err);
            }
        }
    }

    private synchronized void notifyDetectCanceled(){
        if (mFileDetectListeners != null){
            for (FileDetectListener listener : mFileDetectListeners){
                listener.onDetectCanceled();
            }
        }
    }

    public interface FileDetectListener {

        void onDetectStarted(int size);

        void onDetectProgressChanged(int completed, int total);

        /**
         * 拷贝单个文件失败或校验单个文件失败
         * @param diff
         */
        //void onCopyOrCheckFailed(CheckDiff diff);

        /**
         * 拷贝任务失败
         * @param err
         */
        void onDetectError(int err);

        void onDetectCompleted();

        void onDetectCanceled();
    }


}
