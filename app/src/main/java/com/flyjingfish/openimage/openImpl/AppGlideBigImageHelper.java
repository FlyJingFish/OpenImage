package com.flyjingfish.openimage.openImpl;

import android.content.Context;

import com.flyjingfish.openimage.imageloader.MyImageLoader;
import com.flyjingfish.openimagelib.OpenImageConfig;
import com.flyjingfish.openimagelib.listener.BigImageHelper;
import com.flyjingfish.openimagelib.listener.OnLoadBigImageListener;

public class AppGlideBigImageHelper implements BigImageHelper {
    @Override
    public void loadImage(Context context, String imageUrl, OnLoadBigImageListener onLoadBigImageListener) {
        if (MyImageLoader.loader_os_type == MyImageLoader.GLIDE||MyImageLoader.loader_os_type == MyImageLoader.COIL){
            OpenImageConfig.getInstance().getBigImageHelper().loadImage(context, imageUrl, onLoadBigImageListener);
        }else {
            new PicassoLoader(context, imageUrl, onLoadBigImageListener).load();
        }

    }
}
