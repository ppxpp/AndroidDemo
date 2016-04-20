package com.android.demo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.demo.R;

/**
 * Created by zhouhao on 2016/4/20.
 */
public class FragmentA extends Fragment {

    public static FragmentA newInstance(){
        return new FragmentA();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment_a, container,false);
        return view;
    }
}
