package com.flyjingfish.openimage.openImpl;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.flyjingfish.openimage.bean.ImageEntity;
import com.flyjingfish.openimage.databinding.MyActivityViewpagerBinding;
import com.flyjingfish.openimagelib.BaseInnerFragment;
import com.flyjingfish.openimagelib.OpenImageActivity;
import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.listener.OnItemClickListener;
import com.flyjingfish.openimagelib.widget.TouchCloseLayout;

public class MyBigImageActivity extends OpenImageActivity {

    private MyActivityViewpagerBinding rootBinding;
    public static final String BUNDLE_DATA_KEY = "bundle_data";
    public static final String MY_DATA_KEY = "my_data";

    @Override
    public View getContentView() {
        rootBinding = MyActivityViewpagerBinding.inflate(getLayoutInflater());
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getBundleExtra(BUNDLE_DATA_KEY);
        ImageEntity imageEntity = (ImageEntity) bundle.getSerializable(MY_DATA_KEY);
        rootBinding.tv1.setText(imageEntity.testBean.test);
        addOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseInnerFragment fragment, OpenImageUrl openImageUrl, int position) {
                Log.e("MyBigImageActivity","onItemClick");
            }
        });
    }
}
