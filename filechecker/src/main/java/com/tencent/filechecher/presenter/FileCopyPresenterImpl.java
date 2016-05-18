package com.tencent.filechecher.presenter;

import com.tencent.filechecher.CheckDiff;
import com.tencent.filechecher.FileCopyManager;
import com.tencent.filechecher.view.FileCopyView;

/**
 * Created by haoozhou on 2016/5/18.
 */
public class FileCopyPresenterImpl implements IFileCopyPresenter, FileCopyManager.FileCopyListener {

    private FileCopyView mFileCopyView;
    private FileCopyManager mFileCopyManager;

    public FileCopyPresenterImpl(FileCopyView view){
        mFileCopyManager = FileCopyManager.getInstance();
        mFileCopyView = view;
    }

    @Override
    public void init() {
        mFileCopyManager.registerFileCopyListener(this);
    }

    @Override
    public void uninit() {
        mFileCopyManager.unregisterFileCopyListener(this);
    }

    @Override
    public void startCopy() {
        mFileCopyManager.startCopy();
    }

    @Override
    public void cancelCopy() {
        mFileCopyManager.cancelCopy();
    }

    @Override
    public void onFileCopyStarted(int fileCount) {
        if (mFileCopyView == null){
            return;
        }
        mFileCopyView.onFileCopyStarted(fileCount);
    }

    @Override
    public void onFileCopyProgressChanged(int progress, int total) {
        if (mFileCopyView == null){
            return;
        }
        mFileCopyView.onFileCopyProgressChanged(progress, total);
    }

    @Override
    public void onCopyStarted(int size) {
        onFileCopyStarted(size);
    }

    @Override
    public void onCopyProgressChanged(int completed, int total) {
        onFileCopyProgressChanged(completed, total);
    }

    @Override
    public void onCopyOrCheckFailed(CheckDiff diff) {
        if (mFileCopyView == null){
            return;
        }
        mFileCopyView.onCopyOrCheckFailed(diff);
    }

    @Override
    public void onCopyError(int err) {
        onFileCopyError(err);
    }

    @Override
    public void onCopyCompleted() {
        onFileCopyCompleted();
    }

    @Override
    public void onCopyCanceled() {
        onFileCopyCanceled();
    }

    @Override
    public void onFileCopyCompleted() {
        if (mFileCopyView == null){
            return;
        }
        mFileCopyView.onFileCopyCompleted();
    }

    @Override
    public void onFileCopyCanceled() {
        if (mFileCopyView == null){
            return;
        }
        mFileCopyView.onFileCopyCanceled();
    }

    @Override
    public void onFileCopyError(int err) {
        if (mFileCopyView == null){
            return;
        }
        mFileCopyView.onFileCopyError(err);
    }
}
