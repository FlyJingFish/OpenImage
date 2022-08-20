package com.flyjingfish.openimagelib.beans;

import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import com.flyjingfish.openimagelib.listener.OnLoadViewFinishListener;

public class MoreViewOption {
    @LayoutRes
    private int layoutRes;
    private FrameLayout.LayoutParams layoutParams;
    private OnLoadViewFinishListener onLoadViewFinishListener;

    public MoreViewOption(@LayoutRes int layoutRes, @NonNull FrameLayout.LayoutParams layoutParams, OnLoadViewFinishListener onLoadViewFinishListener) {
        this.layoutRes = layoutRes;
        this.layoutParams = layoutParams;
        this.onLoadViewFinishListener = onLoadViewFinishListener;
    }

    public int getLayoutRes() {
        return layoutRes;
    }

    public FrameLayout.LayoutParams getLayoutParams() {
        return layoutParams;
    }

    public OnLoadViewFinishListener getOnLoadViewFinishListener() {
        return onLoadViewFinishListener;
    }

}
