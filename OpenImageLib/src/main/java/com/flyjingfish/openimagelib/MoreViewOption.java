package com.flyjingfish.openimagelib;

import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;

import com.flyjingfish.openimagelib.enums.MoreViewShowType;
import com.flyjingfish.openimagelib.listener.OnLoadViewFinishListener;

class MoreViewOption {
    public static final int LAYOUT_RES = 1;
    public static final int LAYOUT_VIEW = 2;
    @LayoutRes
    private int layoutRes;
    private FrameLayout.LayoutParams layoutParams;
    private MoreViewShowType moreViewShowType;
    private OnLoadViewFinishListener onLoadViewFinishListener;
    private View view;
    private int viewType;
    private boolean followTouch;
    public MoreViewOption(int layoutRes, FrameLayout.LayoutParams layoutParams, MoreViewShowType moreViewShowType,boolean followTouch, OnLoadViewFinishListener onLoadViewFinishListener) {
        this.layoutRes = layoutRes;
        this.layoutParams = layoutParams;
        this.moreViewShowType = moreViewShowType;
        this.followTouch = followTouch;
        this.onLoadViewFinishListener = onLoadViewFinishListener;
        viewType = LAYOUT_RES;
    }

    public MoreViewOption(View view,FrameLayout.LayoutParams layoutParams, MoreViewShowType moreViewShowType,boolean followTouch) {
        this.view = view;
        this.layoutParams = layoutParams;
        this.moreViewShowType = moreViewShowType;
        this.followTouch = followTouch;
        viewType = LAYOUT_VIEW;
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

    public int getViewType() {
        return viewType;
    }

    public boolean isFollowTouch() {
        return followTouch;
    }
}
