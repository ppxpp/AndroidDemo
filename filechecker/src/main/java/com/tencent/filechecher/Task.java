package com.tencent.filechecher;

/**
 * Created by haoozhou on 2016/5/8.
 */
public class Task {

    public int type;
    public Runnable callback;

    public void execute(){
        if (callback != null){
            callback.run();
            callback = null;
        }
    }

    /**
     * 通知任务执行完成
     */
    public void notifyCompleted(){

    }

    public void notifyCanceled(){

    }

    /**
     * 通知任务执行失败
     */
    public void notifyFailed(){

    }

}
