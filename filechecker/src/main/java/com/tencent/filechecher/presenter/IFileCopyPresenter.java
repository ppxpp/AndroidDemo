package com.tencent.filechecher.presenter;

import com.tencent.filechecher.CheckDiff;
import com.tencent.filechecher.view.FileCopyView;

import java.io.File;

/**
 * Created by haoozhou on 2016/5/8.
 */
public interface IFileCopyPresenter {

    void init();

    void uninit();

    void startCopy();

    void cancelCopy();

    void onFileCopyStarted(int fileCount);

    void onFileCopyProgressChanged(int progress, int total);

    void onCopyOrCheckFailed(CheckDiff diff);

    void onFileCopyCompleted();

    void onFileCopyCanceled();

    void onFileCopyError(int err);

}
