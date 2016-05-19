package com.tencent.filechecker.presenter;

import com.tencent.filechecker.entity.CopyConfig;

/**
 * Created by haoozhou on 2016/5/8.
 */
public interface FileCopyPresenter {

    void init();

    void uninit();

    void startCopy(CopyConfig config);

    void cancelCopy();

    /*void onFileCopyStarted(int fileCount);

    void onFileCopyProgressChanged(int progress, int total);

    void onCopyOrCheckFailed(CheckDiff diff);

    void onFileCopyCompleted();

    void onFileCopyCanceled();

    void onFileCopyError(int err);*/

}
