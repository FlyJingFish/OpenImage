package com.flyjingfish.openimagelib.listener;


import android.graphics.drawable.Drawable;

public interface OnLoadBigImageListener {
    void onLoadImageSuccess(Drawable drawable);
    void onLoadImageFailed();
}
