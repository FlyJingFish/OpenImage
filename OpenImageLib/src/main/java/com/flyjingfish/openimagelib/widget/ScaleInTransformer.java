package com.flyjingfish.openimagelib.widget;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

public class ScaleInTransformer implements ViewPager2.PageTransformer {
    public static final float DEFAULT_MIN_SCALE = 0.85f;
    public static final float DEFAULT_CENTER = 0.5f;

    private float mMinScale = DEFAULT_MIN_SCALE;
    @Override
    public void transformPage(@NonNull View page, float position) {
        int pageWidth = page.getWidth();
        int pageHeight = page.getHeight();
        //动画锚点设置为View中心
        page.setPivotX(pageWidth/2);
        page.setPivotY(pageHeight/2);
        if(position < -1){
            //屏幕左侧不可见时
            page.setScaleX(mMinScale);
            page.setScaleY(mMinScale);
            page.setPivotY(pageWidth / 2);
        } else if(position <= 1){
            if (position < 0) {
                //屏幕左侧
                //(0,-1)
                float scaleFactor = (1 + position) * (1 - mMinScale) + mMinScale;
                page.setScaleX(scaleFactor);
                page.setScaleY(scaleFactor);
                page.setPivotX(pageWidth);
            } else {
                //屏幕右侧
                //(1,0)
                float scaleFactor = (1 - position) * (1 - mMinScale) + mMinScale;
                page.setScaleX(scaleFactor);
                page.setScaleY(scaleFactor);
                page.setPivotX(pageWidth * ((1 - position) * DEFAULT_CENTER));
            }
        } else{
            //屏幕右侧不可见
            page.setPivotX(0f);
            page.setScaleY(mMinScale);
            page.setScaleY(mMinScale);
        }
    }
}
