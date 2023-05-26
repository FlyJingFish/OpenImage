package com.flyjingfish.openimagelib.listener;


import android.graphics.drawable.Drawable;

public interface OnLoadBigImageListener {
    void onLoadImageSuccess(Drawable drawable, String filePath);
    void onLoadImageFailed();
}
