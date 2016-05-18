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

    private static FileCopyManager instance = new FileCopyManager();

    private FileCopyManager(){}

    public static FileCopyManager getInstance(){
        return instance;
    }

    public static final int ERR_MD5RESULT_COPY_FAILED = 1;


    public static final int MSG_COPY_STARTED = 1;
    public static final int MSG_COPY_OR_CHECK_FAILED = 2;
    public static final int MSG_CHECK_SUCCESS = 3;
    public static final int MSG_COPY_COMPLETED = 5;
    public static final int MSG_COPY_CANCELED = 6;
    public static final int MSG_COPY_ERROR = 7;

    private Handler mUIHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_COPY_STARTED:
                    mFileCount = msg.arg1;
                    notifyCopyStarted(mFileCount);
                    break;
                case MSG_COPY_OR_CHECK_FAILED:
                    mCurtProgress++;
                    notifyCopyProgressChanged(mCurtProgress, mFileCount);
                    notifyCopyOrCheckFailed((CheckDiff) msg.obj);
                    break;
                case MSG_CHECK_SUCCESS:
                    mCurtProgress++;
                    notifyCopyProgressChanged(mCurtProgress, mFileCount);
                    break;
                case MSG_COPY_COMPLETED:
                    mFileCopyThread = null;
                    notifyCopyCompleted();
                    break;
                case MSG_COPY_ERROR:
                    int err = msg.arg1;
                    notifyCopyError(err);
                    break;
                case MSG_COPY_CANCELED:
                    notifyCopyCanceled();
                    break;
            }
        }
    };

    private int mFileCount = 0;
    private int mCurtProgress = 0;

    FileCopyThread mFileCopyThread;

    public void startCopy(){
        if (mFileCopyThread != null){
            mFileCopyThread.cancel();
            mFileCopyThread = null;
        }
        mFileCount = -1;
        mCurtProgress = 0;
        mFileCopyThread = new FileCopyThread(mUIHandler);
        mFileCopyThread.start();
    }

    public void cancelCopy(){
        if (mFileCopyThread != null){
            mFileCopyThread.cancel();
            mFileCopyThread = null;
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

    private synchronized void notifyCopyStarted(int fileCount){
        if (mFileCopyListeners != null){
            for (FileCopyListener listener : mFileCopyListeners){
                listener.onCopyStarted(fileCount);
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

    private synchronized void notifyCopyOrCheckFailed(CheckDiff diff){
        if (mFileCopyListeners != null){
            for (FileCopyListener listener : mFileCopyListeners){
                listener.onCopyOrCheckFailed(diff);
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

    private synchronized void notifyCopyError(int err){
        if (mFileCopyListeners != null){
            for (FileCopyListener listener : mFileCopyListeners){
                listener.onCopyError(err);
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

        void onCopyStarted(int size);

        void onCopyProgressChanged(int completed, int total);

        /**
         * 拷贝单个文件失败或校验单个文件失败
         * @param diff
         */
        void onCopyOrCheckFailed(CheckDiff diff);

        /**
         * 拷贝任务失败
         * @param err
         */
        void onCopyError(int err);

        void onCopyCompleted();

        void onCopyCanceled();
    }


}
