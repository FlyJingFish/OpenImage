package com.flyjingfish.openimage.activity;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.flyjingfish.openimage.R;
import com.flyjingfish.openimage.bean.ImageEntity;
import com.flyjingfish.openimage.bean.TestBean;
import com.flyjingfish.openimage.databinding.ActivityCustomBinding;
import com.flyjingfish.openimage.imageloader.MyImageLoader;
import com.flyjingfish.openimage.openImpl.MyBigImageActivity;
import com.flyjingfish.openimage.openImpl.MyBigImageActivity2;
import com.flyjingfish.openimagelib.OpenImage;

public class CustomActivity extends BaseActivity{

    private ImageEntity itemData1;
    private ImageEntity itemData2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCustomBinding binding = ActivityCustomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        itemData1 = new ImageEntity();
        itemData1.testBean = new TestBean();
        itemData1.testBean.test = binding.tv1.getText().toString();
        itemData1.url = "https://pics4.baidu.com/feed/50da81cb39dbb6fd95aa0c599b8d0d1e962b3708.jpeg?token=bf17224f51a6f4bb389e787f9c487940";

        itemData2 = new ImageEntity();
        itemData2.testBean = new TestBean();
        itemData2.testBean.test = binding.tv2.getText().toString();
        itemData2.url = "https://img2.baidu.com/it/u=2415498875,118078114&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=889";

        MyImageLoader.getInstance().load(binding.iv1, itemData1.getCoverImageUrl(), R.mipmap.img_load_placeholder,R.mipmap.img_load_placeholder);
        MyImageLoader.getInstance().load(binding.iv2, itemData2.getCoverImageUrl(), R.mipmap.img_load_placeholder,R.mipmap.img_load_placeholder);

        binding.iv1.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable(MyBigImageActivity.MY_DATA_KEY, itemData1);
            OpenImage.with(this).setClickImageView(((ImageView) v))
                    .setImageUrl(itemData1)
                    .setClickPosition(0)
                    .setSrcImageViewScaleType(ImageView.ScaleType.CENTER_CROP,true)
                    .setOpenImageActivityCls(MyBigImageActivity.class,MyBigImageActivity.BUNDLE_DATA_KEY,bundle)
                    .show();
        });

        binding.iv2.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable(MyBigImageActivity2.MY_DATA_KEY, itemData2);
            OpenImage.with(this).setClickImageView(((ImageView) v))
                    .setImageUrl(itemData2)
                    .setClickPosition(0)
                    .setSrcImageViewScaleType(ImageView.ScaleType.CENTER_CROP,true)
                    .setOpenImageActivityCls(MyBigImageActivity2.class, MyBigImageActivity2.BUNDLE_DATA_KEY,bundle)
                    .show();
        });
    }
}
