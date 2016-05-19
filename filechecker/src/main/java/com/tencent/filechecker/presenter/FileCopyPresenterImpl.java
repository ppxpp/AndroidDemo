package com.tencent.filechecker.presenter;

import com.tencent.filechecker.entity.CheckDiff;
import com.tencent.filechecker.entity.CopyConfig;
import com.tencent.filechecker.logic.FileCopyManager;
import com.tencent.filechecker.view.FileCopyView;

/**
 * Created by haoozhou on 2016/5/18.
 */
public class FileCopyPresenterImpl implements FileCopyPresenter, FileCopyManager.FileCopyListener {

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
    public void startCopy(CopyConfig config) {
        mFileCopyManager.startCopy(config);
    }

    @Override
    public void cancelCopy() {
        mFileCopyManager.cancelCopy();
    }


    @Override
    public void onCopyStarted(int size) {
        if (mFileCopyView == null){
            return;
        }
        mFileCopyView.onFileCopyStarted(size);
    }

    @Override
    public void onCopyProgressChanged(int completed, int total) {
        if (mFileCopyView == null){
            return;
        }
        mFileCopyView.onFileCopyProgressChanged(completed, total);
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
        if (mFileCopyView == null){
            return;
        }
        mFileCopyView.onFileCopyError(err);
    }

    @Override
    public void onCopyCompleted() {
        if (mFileCopyView == null){
            return;
        }
        mFileCopyView.onFileCopyCompleted();
    }

    @Override
    public void onCopyCanceled() {
        if (mFileCopyView == null){
            return;
        }
        mFileCopyView.onFileCopyCanceled();
    }
}
