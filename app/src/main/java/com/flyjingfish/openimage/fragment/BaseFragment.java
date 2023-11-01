package com.flyjingfish.openimage.fragment;

import android.view.KeyEvent;

import androidx.fragment.app.Fragment;

public class BaseFragment extends Fragment {
    public boolean onKeyBackDown(int keyCode, KeyEvent event){
        return true;
    }
}
