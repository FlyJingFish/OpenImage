package com.flyjingfish.openimagelib;

import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import com.flyjingfish.openimagelib.enums.MoreViewShowType;
import com.flyjingfish.openimagelib.listener.OnLoadViewFinishListener;

class MoreViewOption {
    @LayoutRes
    private int layoutRes;
    private FrameLayout.LayoutParams layoutParams;
    private MoreViewShowType moreViewShowType;
    private OnLoadViewFinishListener onLoadViewFinishListener;
    private View view;

    public MoreViewOption(int layoutRes, FrameLayout.LayoutParams layoutParams, MoreViewShowType moreViewShowType, OnLoadViewFinishListener onLoadViewFinishListener) {
        this.layoutRes = layoutRes;
        this.layoutParams = layoutParams;
        this.moreViewShowType = moreViewShowType;
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

    public MoreViewShowType getMoreViewShowType() {
        return moreViewShowType;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }
}
