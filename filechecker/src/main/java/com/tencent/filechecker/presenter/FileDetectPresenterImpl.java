package com.tencent.filechecker.presenter;

import com.tencent.filechecker.entity.DetectConfig;
import com.tencent.filechecker.logic.FileDetectManager;
import com.tencent.filechecker.view.FileDetectView;

/**
 * Created by haoozhou on 2016/5/19.
 */
public class FileDetectPresenterImpl implements FileDetectPresenter, FileDetectManager.FileDetectListener {


    private FileDetectView mFileDetectView;
    private FileDetectManager mFileDetectManager;

    public FileDetectPresenterImpl(FileDetectView view){
        mFileDetectView = view;
        mFileDetectManager = FileDetectManager.getInstance();
    }

    @Override
    public void init() {
        mFileDetectManager.registerFileDetectListener(this);
    }

    @Override
    public void uninit() {
        mFileDetectManager.unregisterFileDetectListener(this);
    }

    @Override
    public void startDetect(DetectConfig config) {
        mFileDetectManager.startDetect(config);
    }


    @Override
    public void onDetectStarted(int size) {
        mFileDetectView.onFileDetectStarted(size);
    }

    @Override
    public void onDetectProgressChanged(int completed, int total) {
        mFileDetectView.onFileDetectProgressChanged(completed, total);
    }

    @Override
    public void onDetectError(int err) {
        mFileDetectView.onFileDetectError(err);
    }

    @Override
    public void onDetectCompleted() {
        mFileDetectView.onFileDetectCompleted();
    }

    @Override
    public void onDetectCanceled() {

    }
}
