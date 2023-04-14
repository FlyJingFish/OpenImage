package com.flyjingfish.openimage.openImpl;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.flyjingfish.openimage.bean.ImageEntity;
import com.flyjingfish.openimage.databinding.MyActivityViewpager2Binding;
import com.flyjingfish.openimagelib.OpenImageActivity;
import com.flyjingfish.openimagelib.widget.TouchCloseLayout;

public class MyBigImageActivity2 extends OpenImageActivity {

    private MyActivityViewpager2Binding rootBinding;
    public static final String BUNDLE_DATA_KEY = "bundle_data";
    public static final String MY_DATA_KEY = "my_data";

    @Override
    public View getContentView() {
        rootBinding = MyActivityViewpager2Binding.inflate(getLayoutInflater());
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
        return rootBinding.touchCloseLayout;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getBundleExtra(BUNDLE_DATA_KEY);
        ImageEntity imageEntity = (ImageEntity) bundle.getSerializable(MY_DATA_KEY);
        rootBinding.tv1.setText(imageEntity.testBean.test);
    }
}
