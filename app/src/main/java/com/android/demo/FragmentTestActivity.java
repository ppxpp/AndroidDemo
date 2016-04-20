package com.android.demo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.android.demo.fragment.FragmentA;
import com.android.demo.fragment.FragmentB;

public class FragmentTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_test);
        showFragment(1);
    }


    public void showFragment(int idx){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container, getFragment(idx));
        transaction.commit();
    }

    private Fragment getFragment(int idx){
        if (idx == 1){
            return FragmentA.newInstance();
        }else if (idx == 2){
            return FragmentB.newInstance();
        }
        return null;
    }

    public void showFragment1(View view){
        showFragment(1);
    }

    public void showFragment2(View view){
        showFragment(2);
    }
}
