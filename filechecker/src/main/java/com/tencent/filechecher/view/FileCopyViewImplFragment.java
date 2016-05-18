package com.tencent.filechecher.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tencent.filechecher.CheckDiff;
import com.tencent.filechecher.FileCopyThread;
import com.tencent.filechecher.FileDetectThread;
import com.tencent.filechecher.R;
import com.tencent.filechecher.presenter.FileCopyPresenterImpl;
import com.tencent.filechecher.presenter.IFileCopyPresenter;

public class FileCopyViewImplFragment extends Fragment implements FileCopyView, View.OnClickListener {

    private String TAG = getClass().getSimpleName();

    public FileCopyViewImplFragment() {
    }

    public static FileCopyViewImplFragment newInstance() {
        FileCopyViewImplFragment fragment = new FileCopyViewImplFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private IFileCopyPresenter mPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new FileCopyPresenterImpl(this);
        mPresenter.init();
    }

    private TextView mTVStatus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_portal, container, false);
        mTVStatus = (TextView) view.findViewById(R.id.tv_status);
        view.findViewById(R.id.btn_generate_md5).setOnClickListener(this);
        view.findViewById(R.id.btn_copy).setOnClickListener(this);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.uninit();
        }
    }

    public void generateMD5(){
        FileDetectThread thread = new FileDetectThread(null);
        thread.start();
    }

    private void copy(){
        //FileCopyThread thread = new FileCopyThread(null);
        //thread.start();
        mPresenter.startCopy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_generate_md5:
                generateMD5();
                break;
            case R.id.btn_copy:
                copy();
                break;
        }
    }

    @Override
    public void onFileCopyStarted(int fileCount) {
        Log.d(TAG, "onFileCopyStarted, count = " + fileCount);
        mTVStatus.setText("onFileCopyStarted, count = " + fileCount);
    }

    @Override
    public void onFileCopyProgressChanged(int progress, int total) {
        //Log.d(TAG, "onFileCopyProgressChanged: " + progress + "/" + total);
        mTVStatus.setText("onFileCopyProgressChanged: " + progress + "/" + total);
    }

    @Override
    public void onCopyOrCheckFailed(CheckDiff diff) {
        Log.d(TAG, "copy or check  failed, diff = " + diff);
    }

    @Override
    public void onFileCopyCompleted() {
        Log.d(TAG, "onFileCopyCompleted");
        mTVStatus.setText("onFileCopyCompleted");
    }

    @Override
    public void onFileCopyCanceled() {
        Log.d(TAG, "onFileCopyCanceled");
        mTVStatus.setText("onFileCopyCanceled");
    }

    @Override
    public void onFileCopyError(int err) {
        Log.d(TAG, "onFileCopyError, err = " + err);
        mTVStatus.setText("onFileCopyError, err = " + err);
    }
}
