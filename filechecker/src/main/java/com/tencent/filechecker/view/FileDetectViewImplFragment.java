package com.tencent.filechecker.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tencent.filechecker.R;
import com.tencent.filechecker.entity.DetectConfig;
import com.tencent.filechecker.logic.FileDetectManager;
import com.tencent.filechecker.FileUtils;
import com.tencent.filechecker.presenter.FileDetectPresenter;
import com.tencent.filechecker.presenter.FileDetectPresenterImpl;


public class FileDetectViewImplFragment extends Fragment implements View.OnClickListener,
                                                            FileDetectView{

    public FileDetectViewImplFragment() {
    }

    public static FileDetectViewImplFragment newInstance() {
        FileDetectViewImplFragment fragment = new FileDetectViewImplFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFileDetectManager = FileDetectManager.getInstance();
        mPresenter = new FileDetectPresenterImpl(this);
        mPresenter.init();
        mDetectConfig = DetectConfig.defaultConfig();
        mDetectConfig.loadConfig(getActivity());
    }

    private final int STATUS_WAITING_START = 1;
    private final int STATUS_WAITING_COMPLETED = 2;


    private FileDetectPresenter mPresenter;
    private FileDetectManager mFileDetectManager;
    private DetectConfig mDetectConfig;
    private Button mStartBtn;
    private TextView mStatusTV;
    private EditText mUSBPrefixET;
    private int mCurtStatus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_detect_view_impl, container, false);

        mStartBtn = (Button) view.findViewById(R.id.btn_start);
        mStartBtn.setOnClickListener(this);
        mStatusTV = (TextView) view.findViewById(R.id.status);

        mUSBPrefixET = (EditText) view.findViewById(R.id.prefix_usb);
        mUSBPrefixET.setText(mDetectConfig.PREFIX_USB_EXTERNAL);
        mUSBPrefixET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String prefix = s.toString();
                if (prefix.endsWith("/")){
                    prefix = prefix.substring(0, prefix.length() -1);
                }
                mDetectConfig.PREFIX_USB_EXTERNAL = prefix;
            }
        });

        mCurtStatus = STATUS_WAITING_START;

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        mDetectConfig.saveConfig(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.uninit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_start:
                if (mCurtStatus == STATUS_WAITING_START){
                    mFileDetectManager.startDetect(mDetectConfig);
                    mCurtStatus = STATUS_WAITING_COMPLETED;
                    mStartBtn.setEnabled(false);
                }
                break;
        }
    }

    private int DetectedFileCount = 0;
    @Override
    public void onFileDetectStarted(int size) {
        mStatusTV.setText("正在准备...");
        DetectedFileCount = 0;
    }

    @Override
    public void onFileDetectCompleted() {
        mCurtStatus = STATUS_WAITING_START;
        mStartBtn.setEnabled(true);
        mStatusTV.setText("完成, 共处理了 " + DetectedFileCount + " 个文件, 处理结果文件："
                + mDetectConfig.PREFIX_USB_EXTERNAL + "/" + FileUtils.RESULT_FILE_PATH);
    }

    @Override
    public void onFileDetectError(int err) {
        mCurtStatus = STATUS_WAITING_START;
        mStartBtn.setEnabled(true);
    }

    @Override
    public void onFileDetectCanceled() {
        mCurtStatus = STATUS_WAITING_START;
        mStartBtn.setEnabled(true);
    }

    @Override
    public void onFileDetectProgressChanged(int progress, int total) {
        DetectedFileCount++;
        double percent = (double)progress / (double)total * 100;
        mStatusTV.setText("正在生成MD5文件, " + progress + "/" + total + "(" + String.format("%.2f", percent) + "%)");
    }
}
