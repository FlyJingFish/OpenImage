package com.flyjingfish.openimage.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.annotation.Nullable;

import com.bumptech.glide.request.target.Target;
import com.flyjingfish.openimage.R;
import com.flyjingfish.openimage.bean.ImageEntity;
import com.flyjingfish.openimage.databinding.ActivityScaleTypeBinding;
import com.flyjingfish.openimage.imageloader.MyImageLoader;
import com.flyjingfish.openimagelib.OpenImage;
import com.flyjingfish.openimagelib.transformers.ScaleInTransformer;
import com.flyjingfish.shapeimageviewlib.ShapeImageView;


public class ScaleTypeActivity extends BaseActivity {

    private ActivityScaleTypeBinding binding;
    private ImageEntity itemData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScaleTypeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        itemData = new ImageEntity();
        itemData.url = "https://pics4.baidu.com/feed/50da81cb39dbb6fd95aa0c599b8d0d1e962b3708.jpeg?token=bf17224f51a6f4bb389e787f9c487940";
        itemData.coverUrl = null;
        setData();
        binding.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float radio = progress *1f/2 +1;
                binding.tvRadioValue.setText("autoCrop_height_width_ratio = " + radio);
                binding.ivAutoStartCrop.setAutoCropHeightWidthRatio(radio);
                binding.ivAutoEndCrop.setAutoCropHeightWidthRatio(radio);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    private void setData() {
        MyImageLoader.getInstance().load(binding.ivCenter, itemData.getCoverImageUrl(), Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL, R.mipmap.img_load_placeholder, R.mipmap.img_load_placeholder);
        MyImageLoader.getInstance().load(binding.ivCenterCrop, itemData.getCoverImageUrl(), R.mipmap.img_load_placeholder, R.mipmap.img_load_placeholder);
        MyImageLoader.getInstance().load(binding.ivCenterInside, itemData.getCoverImageUrl(), R.mipmap.img_load_placeholder, R.mipmap.img_load_placeholder);
        MyImageLoader.getInstance().load(binding.ivFitStart, itemData.getCoverImageUrl(), R.mipmap.img_load_placeholder, R.mipmap.img_load_placeholder);
        MyImageLoader.getInstance().load(binding.ivFitCenter, itemData.getCoverImageUrl(), R.mipmap.img_load_placeholder, R.mipmap.img_load_placeholder);
        MyImageLoader.getInstance().load(binding.ivFitEnd, itemData.getCoverImageUrl(), R.mipmap.img_load_placeholder, R.mipmap.img_load_placeholder);
        MyImageLoader.getInstance().load(binding.ivFitXY, itemData.getCoverImageUrl(), R.mipmap.img_load_placeholder, R.mipmap.img_load_placeholder);
        MyImageLoader.getInstance().load(binding.ivStartCrop, itemData.getCoverImageUrl(), R.mipmap.img_load_placeholder, R.mipmap.img_load_placeholder);
        MyImageLoader.getInstance().load(binding.ivEndCrop, itemData.getCoverImageUrl(), R.mipmap.img_load_placeholder, R.mipmap.img_load_placeholder);
        MyImageLoader.getInstance().load(binding.ivAutoStartCrop, itemData.getCoverImageUrl(), R.mipmap.img_load_placeholder, R.mipmap.img_load_placeholder);
        MyImageLoader.getInstance().load(binding.ivAutoEndCrop, itemData.getCoverImageUrl(), R.mipmap.img_load_placeholder, R.mipmap.img_load_placeholder);
    }

    public void onPicClick(View view) {
        switch (view.getId()) {
            case R.id.tv_pic1:
                binding.tvPic1.setBackgroundColor(getResources().getColor(R.color.teal_200));
                binding.tvPic2.setBackgroundColor(Color.TRANSPARENT);
                binding.tvPic3.setBackgroundColor(Color.TRANSPARENT);
                itemData.url = "https://pics4.baidu.com/feed/50da81cb39dbb6fd95aa0c599b8d0d1e962b3708.jpeg?token=bf17224f51a6f4bb389e787f9c487940";
                itemData.coverUrl = null;
                break;
            case R.id.tv_pic2:
                binding.tvPic2.setBackgroundColor(getResources().getColor(R.color.teal_200));
                binding.tvPic1.setBackgroundColor(Color.TRANSPARENT);
                binding.tvPic3.setBackgroundColor(Color.TRANSPARENT);
                itemData.url = "https://desk-fd.zol-img.com.cn/t_s960x600c5/g5/M00/02/03/ChMkJ1bKxnyIIvklAAHSVV_HEKsAALHlwPNfqMAAdJt436.jpg";
                itemData.coverUrl = "https://desk-fd.zol-img.com.cn/t_s432x270c5/g5/M00/02/03/ChMkJ1bKxnyIIvklAAHSVV_HEKsAALHlwPNfqMAAdJt436.jpg";
                break;
            case R.id.tv_pic3:
                binding.tvPic3.setBackgroundColor(getResources().getColor(R.color.teal_200));
                binding.tvPic1.setBackgroundColor(Color.TRANSPARENT);
                binding.tvPic2.setBackgroundColor(Color.TRANSPARENT);
                itemData.url = "https://img1.baidu.com/it/u=3124924291,3476865151&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=4841";
                itemData.coverUrl = null;
                break;
        }
        setData();
    }

    public void onIvClick(View view) {
        OpenImage openImage = OpenImage.with(this).setClickImageView(((ImageView) view))
                .setImageUrl(itemData).addPageTransformer(new ScaleInTransformer())
                .setOpenImageStyle(R.style.DefaultPhotosTheme)
                .setClickPosition(0);

        if (view instanceof ShapeImageView) {
            ShapeImageView.ShapeScaleType scaleType = ((ShapeImageView) view).getShapeScaleType();
            openImage.setSrcImageViewScaleType(scaleType, true);
        } else {
            ImageView.ScaleType scaleType = ((ImageView) view).getScaleType();
            openImage.setSrcImageViewScaleType(scaleType, true);
        }

        openImage.show();
    }
}
