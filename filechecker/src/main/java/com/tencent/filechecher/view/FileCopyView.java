package com.tencent.filechecher.view;

import com.tencent.filechecher.CheckDiff;

/**
 * Created by haoozhou on 2016/5/18.
 */
public interface FileCopyView {

    void onFileCopyStarted(int fileCount);

    void onFileCopyProgressChanged(int progress, int total);

    void onCopyOrCheckFailed(CheckDiff diff);

    void onFileCopyCompleted();

    void onFileCopyCanceled();

    void onFileCopyError(int err);
}
