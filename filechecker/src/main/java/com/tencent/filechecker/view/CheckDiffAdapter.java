package com.tencent.filechecker.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.tencent.filechecker.R;
import com.tencent.filechecker.entity.CheckDiff;

public class CheckDiffAdapter extends BaseAdapter {

	private List<ItemWrapper> mItems;
	private View.OnClickListener mOnDeleteListener;
	
	public CheckDiffAdapter(View.OnClickListener onDeleteListener){
		mItems = new ArrayList<ItemWrapper>();
		mOnDeleteListener = onDeleteListener;
	}


	public synchronized void clear(){
		mItems.clear();
		notifyDataSetChanged();
	}

	public synchronized void removeItem(ItemWrapper item) {
		mItems.remove(item);
		notifyDataSetChanged();
	}

	public synchronized void addDiffItem(CheckDiff diff){
		ItemWrapper item = new ItemWrapper();
		item.checkDiff = diff;
		item.showDeleteBtn = false;
		mItems.add(item);
		Collections.sort(mItems);
		notifyDataSetChanged();
	}

	public int getDiffTypeCount(CheckDiff.DiffType diffType){
		int count = 0;
		for (ItemWrapper item : mItems) {
			if (item.checkDiff.diffType == diffType) {
				count++;
			}
		}
		return count;
	}
	
	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public synchronized  Object getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_file_check_diff_item, parent, false);
		}
		ItemWrapper item = (ItemWrapper) getItem(position);
		setDiffNo(position + 1, view);
		setDiffType(item.checkDiff, view);
		setDiffFilePath(item.checkDiff, view);
		setMd5Calculated(item.checkDiff, view);
		setMd5Stander(item.checkDiff, view);
		setDataType(item.checkDiff, view);

		Button mDelBtn = (Button) view.findViewById(R.id.btn_del);
		mDelBtn.setVisibility(item.showDeleteBtn ? View.VISIBLE : View.INVISIBLE);
		mDelBtn.setTag(item);
		mDelBtn.setOnClickListener(mOnDeleteListener);
		return view;
	}


	
	private void setDiffNo(int no , View rootView){
		TextView noTV = (TextView) rootView.findViewById(R.id.diff_no);
		noTV.setText(rootView.getResources().getString(R.string.result_diff_no, no));
	}
	
	private void setDiffType(CheckDiff diff, View rootView){
		TextView type = (TextView) rootView.findViewById(R.id.diff_type);
		
		String typeStr = "未知";
		int bgColor = Color.RED;//red
		switch (diff.diffType) {
			case Missing:
			bgColor = Color.RED;//red
			typeStr = "丢失";
			break;
		case DifSize:
				bgColor = Color.RED;//red
				typeStr = "长度不同";
				break;
		case Modify:
			bgColor = Color.YELLOW;//yellow
			typeStr = "修改";
			break;
		/*case Add:
			bgColor = Color.GREEN;//green
			typeStr = "新增";
			break;*/
		default:
			break;
		}
		type.setText(typeStr);
		type.setBackgroundColor(bgColor);
	}
	
	private void setDiffFilePath(CheckDiff diff, View rootView){
		TextView filePathTV = (TextView) rootView.findViewById(R.id.file_path);
		filePathTV.setText(rootView.getResources().getString(R.string.result_file_path, diff.filePath));
	}
	
	private void setMd5Calculated(CheckDiff diff, View rootView){
		TextView md5CalculatedTV = (TextView) rootView.findViewById(R.id.md5_calculated);
		if (diff.diffType == CheckDiff.DiffType.Missing) {
			md5CalculatedTV.setText(rootView.getResources().getString(R.string.result_md5_calculated, "---"));
		}else{
			md5CalculatedTV.setText(diff.md5Stander);
			md5CalculatedTV.setText(rootView.getResources().getString(R.string.result_md5_calculated, diff.md5Calculated));
		}
	}
	
	private void setMd5Stander(CheckDiff diff, View rootView){
		TextView md5StanderTV = (TextView) rootView.findViewById(R.id.md5_stander);
		/*if (diff.diffType == CheckDiff.DiffType.Add) {
			md5StanderTV.setText(rootView.getResources().getString(R.string.result_md5_statder, "---"));
		}else*/{
			md5StanderTV.setText(diff.md5Stander);
			md5StanderTV.setText(rootView.getResources().getString(R.string.result_md5_statder, diff.md5Stander));
		}
	}
	
	private void setDataType(CheckDiff diff, View rootView){
		TextView dataTypeTV = (TextView) rootView.findViewById(R.id.data_type);
		String dataType = "unknow";
		switch (diff.dataType) {
		case WeCarMusic:
			dataType = "音乐数据";
			break;
		case WeCarNavi:
			dataType = "导航数据";
			break;
		case WeCarSpeech:
			dataType = "语音数据";
			break;
		case SogouInputMethod:
			dataType = "输入法数据";
			break;
		}
		dataTypeTV.setText(rootView.getResources().getString(R.string.result_data_type, dataType));
	}
	
	/*private void setLastModified(CheckDiff diff, View rootView){
		TextView lastModifiedTV = (TextView) rootView.findViewById(R.id.last_modified);
		String lastModified = TimeHelper.unixTimeToStr(diff.lastModified / 1000);
		if (diff.diffType == CheckDiff.DiffType.Missing) {
			lastModifiedTV.setText(rootView.getResources().getString(R.string.result_last_modified, "--"));
		}else{
			lastModifiedTV.setText(rootView.getResources().getString(R.string.result_last_modified, lastModified));
		}
	}*/

	public class ItemWrapper  implements Comparable<ItemWrapper>{
		public CheckDiff checkDiff;
		public boolean showDeleteBtn = false;

		@Override
		public int compareTo(ItemWrapper another) {
			return checkDiff.compareTo(another.checkDiff);
		}
	}
}
