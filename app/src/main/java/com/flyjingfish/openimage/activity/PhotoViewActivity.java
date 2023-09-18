package com.flyjingfish.openimage.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.flyjingfish.openimage.R;
import com.flyjingfish.openimage.databinding.ActivityPhotoViewBinding;
import com.flyjingfish.openimage.imageloader.MyImageLoader;

public class PhotoViewActivity extends BaseActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityPhotoViewBinding binding = ActivityPhotoViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        MyImageLoader.getInstance().load(binding.photoView,"https://img2.baidu.com/it/u=2415498875,118078114&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=889", R.mipmap.img_load_placeholder,R.mipmap.img_load_placeholder);

    }
}
