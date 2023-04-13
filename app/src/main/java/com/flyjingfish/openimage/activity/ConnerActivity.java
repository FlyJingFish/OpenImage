package com.flyjingfish.openimage.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.flyjingfish.openimage.R;
import com.flyjingfish.openimage.bean.ImageEntity;
import com.flyjingfish.openimage.databinding.ActivityConnerBinding;
import com.flyjingfish.openimage.imageloader.MyImageLoader;
import com.flyjingfish.openimagelib.OpenImage;
import com.flyjingfish.openimagelib.beans.RectangleConnerRadius;
import com.flyjingfish.openimagelib.enums.ImageShapeType;
import com.flyjingfish.openimagelib.transformers.ScaleInTransformer;
import com.flyjingfish.openimagelib.utils.ScreenUtils;
import com.flyjingfish.shapeimageviewlib.ShapeImageView;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


public class ConnerActivity extends BaseActivity{

    private ActivityConnerBinding binding;
    private ImageEntity itemData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConnerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        itemData = new ImageEntity();
        itemData.url = "https://pics4.baidu.com/feed/50da81cb39dbb6fd95aa0c599b8d0d1e962b3708.jpeg?token=bf17224f51a6f4bb389e787f9c487940";
        setData();
    }

    private void setData() {
        MyImageLoader.getInstance().load(binding.iv1, itemData.getCoverImageUrl(), R.mipmap.img_load_placeholder, R.mipmap.img_load_placeholder);
        MyImageLoader.getInstance().load(binding.iv2, itemData.getCoverImageUrl(), R.mipmap.img_load_placeholder, R.mipmap.img_load_placeholder);
        MyImageLoader.getInstance().load(binding.iv3, itemData.getCoverImageUrl(), R.mipmap.img_load_placeholder, R.mipmap.img_load_placeholder);
        MyImageLoader.getInstance().loadCircle(binding.iv4, itemData.getCoverImageUrl(), R.mipmap.img_load_placeholder, R.mipmap.img_load_placeholder);
        MyImageLoader.getInstance().loadRoundCorner(binding.iv5, itemData.getCoverImageUrl(),20, R.mipmap.img_load_placeholder, R.mipmap.img_load_placeholder);
        MyImageLoader.getInstance().loadRoundImageByType(binding.iv6, itemData.getCoverImageUrl(),20, RoundedCornersTransformation.CornerType.DIAGONAL_FROM_TOP_LEFT, R.mipmap.img_load_placeholder, R.mipmap.img_load_placeholder);
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
            if (view == binding.iv4){
                openImage.setImageShapeParams(ImageShapeType.OVAL,null);
            }else if (view == binding.iv5){
                openImage.setImageShapeParams(ImageShapeType.RECTANGLE,new RectangleConnerRadius(ScreenUtils.dp2px(this,20)));
            }else if (view == binding.iv6){
                openImage.setImageShapeParams(ImageShapeType.RECTANGLE,new RectangleConnerRadius(ScreenUtils.dp2px(this,20),0,ScreenUtils.dp2px(this,20),0));
            }
        }

        openImage.show();
    }
}
