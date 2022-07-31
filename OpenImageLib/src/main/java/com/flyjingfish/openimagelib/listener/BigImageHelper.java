package com.flyjingfish.openimagelib.listener;

import android.content.Context;
import android.widget.ImageView;

public interface BigImageHelper {
    void loadImage(Context context, String imageUrl,OnLoadBigImageListener onLoadBigImageListener);
    void loadImage(Context context, String imageUrl,ImageView imageView);
//    void loadImage(Context context, String imageUrl,ImageView imageView,OnLoadBigImageListener onLoadBigImageListener);
}
