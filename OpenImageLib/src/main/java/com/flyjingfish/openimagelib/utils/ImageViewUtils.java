package com.flyjingfish.openimagelib.utils;

import android.util.LayoutDirection;
import android.widget.ImageView;

import androidx.core.text.TextUtilsCompat;

import java.util.Locale;

public class ImageViewUtils {
    public static int[] getShowWidthHeight(ImageView imageView){
        boolean isRtl = TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == LayoutDirection.RTL;
        int[] widthHeight = new int[2];
        int paddingStart = imageView.getPaddingStart();
        int paddingEnd = imageView.getPaddingEnd();
        int paddingLeft = imageView.getPaddingLeft();
        int paddingRight = imageView.getPaddingRight();
        int paddingLeftMax;
        int paddingRightMax;
        if (isRtl) {
            paddingLeftMax = Math.max(paddingEnd, paddingLeft);
            paddingRightMax = Math.max(paddingStart, paddingRight);
        } else {
            paddingLeftMax = Math.max(paddingStart, paddingLeft);
            paddingRightMax = Math.max(paddingEnd, paddingRight);
        }

        int paddingTop = imageView.getPaddingTop();
        int paddingBottom = imageView.getPaddingBottom();
        int width = imageView.getWidth();
        int height = imageView.getHeight();
        widthHeight[0] =  width - paddingLeftMax - paddingRightMax;
        widthHeight[1] =  height - paddingTop - paddingBottom;

        return widthHeight;
    }

    public static int getViewPaddingLeft(ImageView imageView){
        boolean isRtl = TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == LayoutDirection.RTL;
        int paddingStart = imageView.getPaddingStart();
        int paddingEnd = imageView.getPaddingEnd();
        int paddingLeft = imageView.getPaddingLeft();
        int paddingRight = imageView.getPaddingRight();
        int paddingLeftMax;
        if (isRtl) {
            paddingLeftMax = Math.max(paddingEnd, paddingLeft);
        } else {
            paddingLeftMax = Math.max(paddingStart, paddingLeft);
        }


        return paddingLeftMax;
    }

    public static int getViewPaddingRight(ImageView imageView){
        boolean isRtl = TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == LayoutDirection.RTL;
        int paddingStart = imageView.getPaddingStart();
        int paddingEnd = imageView.getPaddingEnd();
        int paddingLeft = imageView.getPaddingLeft();
        int paddingRight = imageView.getPaddingRight();
        int paddingRightMax;
        if (isRtl) {
            paddingRightMax = Math.max(paddingStart, paddingRight);
        } else {
            paddingRightMax = Math.max(paddingEnd, paddingRight);
        }


        return paddingRightMax;
    }
}