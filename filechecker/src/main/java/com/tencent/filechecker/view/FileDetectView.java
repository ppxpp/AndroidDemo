package com.tencent.filechecker.view;

/**
 * Created by haoozhou on 2016/5/19.
 */
public interface FileDetectView {

    void onFileDetectStarted(int size);

    void onFileDetectCompleted();

    void onFileDetectError(int err);

    void onFileDetectCanceled();

    void onFileDetectProgressChanged(int progress, int total);
}
