package com.flyjingfish.openimage.openImpl;

import android.content.Context;
import android.widget.ImageView;

import com.flyjingfish.openimage.imageloader.MyImageLoader;
import com.flyjingfish.openimagecoillib.CoilBigImageHelper;
import com.flyjingfish.openimagelib.listener.BigImageHelper;
import com.flyjingfish.openimagelib.listener.OnLoadBigImageListener;

public class AppGlideBigImageHelper extends CoilBigImageHelper {
    @Override
    public void loadImage(Context context, String imageUrl, OnLoadBigImageListener onLoadBigImageListener) {
        if (MyImageLoader.loader_os_type == MyImageLoader.GLIDE){
            super.loadImage(context, imageUrl, onLoadBigImageListener);
        }else if (MyImageLoader.loader_os_type == MyImageLoader.COIL){
            new CoilLoader(context, imageUrl, onLoadBigImageListener).load();
        }else {
            new PicassoLoader(context, imageUrl, onLoadBigImageListener).load();
        }

    }
}
