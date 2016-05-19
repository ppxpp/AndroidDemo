package com.tencent.filechecker.presenter;

import com.tencent.filechecker.entity.DetectConfig;

/**
 * Created by haoozhou on 2016/5/8.
 */
public interface FileDetectPresenter {

    void init();

    void uninit();

    void startDetect(DetectConfig config);

    //void onFileDetectStarted(int fileCount);

    //void onFileDetectProgressChanged(int progress, int total);

    //void onCopyOrCheckFailed(CheckDiff diff);

    //void onFileDetectCompleted();

    //void onFileCopyCanceled();

    //void onFileDetectError(int err);

}
