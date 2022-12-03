package com.flyjingfish.openimage.openImpl;

import com.flyjingfish.openimage.databinding.MyActivityViewpagerBinding;
import com.flyjingfish.openimagelib.ViewPagerActivity;

public class MyBigImageActivity extends ViewPagerActivity {

    @Override
    protected void initRootView() {
        MyActivityViewpagerBinding binding = MyActivityViewpagerBinding.inflate(getLayoutInflater());
        vBg = binding.vBg;
        flTouchView = binding.flTouchView;
        viewPager = binding.viewPager;
        rootView = binding.getRoot();
        contentView = binding.getRoot();
    }
}
