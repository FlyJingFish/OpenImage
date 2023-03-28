package com.flyjingfish.openimagelib;

import android.view.View;
import android.widget.FrameLayout;

import androidx.viewpager2.widget.ViewPager2;

import com.flyjingfish.openimagelib.databinding.OpenImageActivityViewpagerBinding;
import com.flyjingfish.openimagelib.widget.TouchCloseLayout;

public class StandardOpenImageActivity extends OpenImageActivity implements TouchCloseLayout.OnTouchCloseListener {

    private OpenImageActivityViewpagerBinding rootBinding;

    @Override
    public View getContentView() {
        rootBinding = OpenImageActivityViewpagerBinding.inflate(getLayoutInflater());
        return rootBinding.getRoot();
    }

    @Override
    public View getBgView() {
        return rootBinding.vBg;
    }

    @Override
    public FrameLayout getViewPager2Container() {
        return rootBinding.flTouchView;
    }

    @Override
    public ViewPager2 getViewPager2() {
        return rootBinding.viewPager;
    }

    @Override
    public TouchCloseLayout getTouchCloseLayout() {
        return rootBinding.getRoot();
    }
}
