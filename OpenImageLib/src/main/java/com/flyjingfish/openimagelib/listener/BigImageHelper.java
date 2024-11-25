package com.flyjingfish.openimagelib.listener;

import android.content.Context;
import android.widget.ImageView;

public interface BigImageHelper {
    void loadImage(Context context, String imageUrl,OnLoadBigImageListener onLoadBigImageListener);
}
