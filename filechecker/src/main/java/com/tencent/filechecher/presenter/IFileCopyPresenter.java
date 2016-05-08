package com.tencent.filechecher.presenter;

/**
 * Created by haoozhou on 2016/5/8.
 */
public interface IFileCopyPresenter {


    void startCopy();

    void stopCopy();



    void onFileCopyStarted(int fileCount);

    void onFileCopyProgressChanged(int progress, int total);

    void onFileCopyCompleted();

    void onFileCopyCanceled();

    void onFileCopyError();

}
