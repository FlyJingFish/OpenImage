package com.flyjingfish.openimagelib.listener;

import android.content.Context;
import android.widget.ImageView;

import com.flyjingfish.openimagelib.beans.OpenImageUrl;

public interface ItemLoadHelper {
    void loadImage(Context context, OpenImageUrl openImageUrl, String imageUrl, ImageView imageView, int overrideWidth, int overrideHeight, OnLoadCoverImageListener onLoadCoverImageListener);
}
