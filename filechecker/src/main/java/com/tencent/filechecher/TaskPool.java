package com.tencent.filechecher;

/**
 * Created by haoozhou on 2016/5/8.
 */
public abstract class TaskPool {





    abstract public void initPool();

    abstract public Task getTask();

    /**
     * 获取任务总数，包括已执行完的，正在执行的和未执行的
     * @return
     */
    abstract public int getTaskCount();


}
