package com.tencent.filechecker.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.filechecker.R;
import com.tencent.filechecker.entity.CheckDiff;
import com.tencent.filechecker.entity.CopyConfig;
import com.tencent.filechecker.logic.FileCopyManager;
import com.tencent.filechecker.FileUtils;
import com.tencent.filechecker.presenter.FileCopyPresenterImpl;
import com.tencent.filechecker.presenter.FileCopyPresenter;

public class FileCopyViewImplFragment extends Fragment implements FileCopyView, View.OnClickListener,
        CompoundButton.OnCheckedChangeListener,
        AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener{

    private String TAG = getClass().getSimpleName();

    public FileCopyViewImplFragment() {
    }

    public static FileCopyViewImplFragment newInstance() {
        FileCopyViewImplFragment fragment = new FileCopyViewImplFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private FileCopyPresenter mPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new FileCopyPresenterImpl(this);
        mPresenter.init();
    }

    private final int STATUS_WAITING_START = 1;
    private final int STATUS_WAITING_COMPLETED = 2;
    private final int STATUS_WAITING_CANCELED = 3;

    private CheckBox mCopyCB, mCheckCB, mQuickCB;
    private TextView mTVStatus;
    private Button mControlBtn;
    private EditText mUSBPrefixET, mNativePrefixET;
    private ListView mDiffListView;
    private CheckDiffAdapter mAdapter;
    private CopyConfig mCopyConfig;
    private int mCurtStatus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_file_copy_view_impl, container, false);

        mTVStatus = (TextView) view.findViewById(R.id.tv_status);

        mCopyConfig = CopyConfig.defaultConfig();
        mCopyConfig.loadConfig(getActivity());

        mCopyCB = (CheckBox) view.findViewById(R.id.do_copy);
        mCopyCB.setChecked(mCopyConfig.DoCopy);
        mCopyCB.setOnCheckedChangeListener(this);

        mCheckCB = (CheckBox) view.findViewById(R.id.do_check);
        mCheckCB.setChecked(mCopyConfig.DoCheck);
        mCheckCB.setOnCheckedChangeListener(this);

        mQuickCB = (CheckBox) view.findViewById(R.id.enable_quick_copy);
        mQuickCB.setChecked(mCopyConfig.EnableQuickCopy);
        mQuickCB.setOnCheckedChangeListener(this);

        updateCheckBoxVisible(mCopyConfig.DoCopy);

        mUSBPrefixET = (EditText) view.findViewById(R.id.usb_prefix);
        mUSBPrefixET.setText(mCopyConfig.PREFIX_USB_EXTERNAL);
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
                    prefix = prefix.substring(0, prefix.length() - 1);
                }
                mCopyConfig.PREFIX_USB_EXTERNAL = prefix;
            }
        });

        mNativePrefixET = (EditText) view.findViewById(R.id.native_prefix);
        mNativePrefixET.setText(mCopyConfig.PREFIX_NATIVE_EXTERNAL);
        mNativePrefixET.addTextChangedListener(new TextWatcher() {
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
                    prefix = prefix.substring(0, prefix.length() - 1);
                }
                mCopyConfig.PREFIX_NATIVE_EXTERNAL = prefix;
            }
        });

        mControlBtn = (Button) view.findViewById(R.id.btn_control);
        mControlBtn.setOnClickListener(this);

        mDiffListView = (ListView) view.findViewById(R.id.diff_list);
        mAdapter = new CheckDiffAdapter(mOnDeleteBtnClickListener);
        mDiffListView.setAdapter(mAdapter);

        mControlBtn.setText("开始任务");
        mControlBtn.setEnabled(true);
        mCurtStatus = STATUS_WAITING_START;

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCurtStatus == STATUS_WAITING_COMPLETED){
            cancel();
            mControlBtn.setText("正在取消操作...");
            mControlBtn.setEnabled(false);
            mCurtStatus = STATUS_WAITING_CANCELED;
        }
        if (mCopyConfig != null){
            mCopyConfig.saveConfig(getActivity());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.uninit();
        }
    }

    /*public void generateMD5(){
        FileDetectThread thread = new FileDetectThread(null);
        thread.start();
    }*/

    private void copy(){
        mPresenter.startCopy(mCopyConfig);
    }

    private void cancel(){
        mPresenter.cancelCopy();
    }

    private View.OnClickListener mOnDeleteBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getTag() != null && v.getTag() instanceof CheckDiffAdapter.ItemWrapper){
                CheckDiffAdapter.ItemWrapper item = (CheckDiffAdapter.ItemWrapper) v.getTag();
                String fullDstFilePath = mCopyConfig.PREFIX_NATIVE_EXTERNAL + "/" + item.checkDiff.filePath;
                FileUtils.deleteFile(fullDstFilePath);
                Toast.makeText(v.getContext(), "删除:" + fullDstFilePath, Toast.LENGTH_SHORT).show();
                mAdapter.removeItem(item);
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_control:
                if (mCurtStatus == STATUS_WAITING_START){
                    mAdapter.clear();
                    copy();
                    mControlBtn.setText("正在开始...");
                    mControlBtn.setEnabled(false);
                    mDiffListView.setOnItemClickListener(null);
                    mDiffListView.setOnItemLongClickListener(null);
                    mUSBPrefixET.setEnabled(false);
                    mNativePrefixET.setEnabled(false);
                }else if (mCurtStatus == STATUS_WAITING_COMPLETED){
                    //取消
                    cancel();
                    mControlBtn.setText("正在取消操作...");
                    mControlBtn.setEnabled(false);
                    mCurtStatus = STATUS_WAITING_CANCELED;
                }
                break;
        }
    }

    @Override
    public void onFileCopyStarted(int fileCount) {
        mControlBtn.setText("点击取消");
        mControlBtn.setEnabled(true);
        mCurtStatus = STATUS_WAITING_COMPLETED;
        mTVStatus.setText("");
    }

    @Override
    public void onFileCopyCanceled() {
        Log.d(TAG, "onFileCopyCanceled");
        mControlBtn.setText("开始任务");
        mControlBtn.setEnabled(true);
        mCurtStatus = STATUS_WAITING_START;
        mTVStatus.setText("任务已取消");
        mDiffListView.setOnItemClickListener(this);
        mDiffListView.setOnItemLongClickListener(this);
        mUSBPrefixET.setEnabled(true);
        mNativePrefixET.setEnabled(true);
    }

    @Override
    public void onFileCopyError(int err) {
        mControlBtn.setText("开始任务");
        mControlBtn.setEnabled(true);
        mCurtStatus = STATUS_WAITING_START;
        String info = "任务失败";
        if (err == FileCopyManager.ERR_MD5RESULT_COPY_FAILED){
            info = info + ": 拷贝MD5文件失败";
        }
        mTVStatus.setText(info);
        mDiffListView.setOnItemClickListener(this);
        mDiffListView.setOnItemLongClickListener(this);
        mUSBPrefixET.setEnabled(true);
        mNativePrefixET.setEnabled(true);
    }


    @Override
    public void onFileCopyCompleted() {
        Log.d(TAG, "onFileCopyCompleted");
        mControlBtn.setText("开始任务");
        mControlBtn.setEnabled(true);
        mCurtStatus = STATUS_WAITING_START;
        mDiffListView.setOnItemClickListener(this);
        mDiffListView.setOnItemLongClickListener(this);
        mUSBPrefixET.setEnabled(true);
        mNativePrefixET.setEnabled(true);
        int modify = mAdapter.getDiffTypeCount(CheckDiff.DiffType.Modify);
        int missing = mAdapter.getDiffTypeCount(CheckDiff.DiffType.Missing);
        int difSize = mAdapter.getDiffTypeCount(CheckDiff.DiffType.DifSize);
        String info = "发现修改" + modify +"处,丢失" + missing + "处,文件大小不一致" + difSize + "处";
        mTVStatus.setText(mTVStatus.getText().toString() + ", " + info);
    }

    @Override
    public void onFileCopyProgressChanged(int progress, int total) {
        double percent = (double)progress / (double)total * 100;
        String info = "已完成: "+ progress + "/" + total + String.format("(%.2f)", percent) + "%";
        mTVStatus.setText(info);
    }

    @Override
    public void onCopyOrCheckFailed(CheckDiff diff) {
        Log.d(TAG, "copy or check  failed, diff = " + diff);
        mAdapter.addDiffItem(diff);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.do_copy:
                mCopyConfig.DoCopy = isChecked;
                break;
            case R.id.do_check:
                mCopyConfig.DoCheck = isChecked;
                break;
            case R.id.enable_quick_copy:
                mCopyConfig.EnableQuickCopy = isChecked;
                break;
        }
        updateCheckBoxVisible(mCopyConfig.DoCopy);
    }

    private void updateCheckBoxVisible(boolean enableCopy){
        if (!enableCopy){
            mQuickCB.setVisibility(View.INVISIBLE);
        }else{
            mQuickCB.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        CheckDiffAdapter.ItemWrapper item = (CheckDiffAdapter.ItemWrapper) mAdapter.getItem(position);
        if (item != null){
            item.showDeleteBtn = true;
            mAdapter.notifyDataSetChanged();
        }
        //Toast.makeText(getActivity(), "onItemLongClick", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CheckDiffAdapter.ItemWrapper item = (CheckDiffAdapter.ItemWrapper) mAdapter.getItem(position);
        if (item != null){
            item.showDeleteBtn = false;
            mAdapter.notifyDataSetChanged();
        }
    }
}
