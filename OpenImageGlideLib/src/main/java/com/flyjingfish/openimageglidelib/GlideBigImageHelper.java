package com.flyjingfish.openimageglidelib;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.flyjingfish.openimagelib.listener.BigImageHelper;
import com.flyjingfish.openimagelib.listener.OnLoadBigImageListener;


public class GlideBigImageHelper implements BigImageHelper {
    @Override
    public void loadImage(Context context, String imageUrl, OnLoadBigImageListener onLoadBigImageListener) {
        LoadImageUtils.INSTANCE.loadImageForSize(context, imageUrl, new OnLocalRealFinishListener() {
            @Override
            public void onGoLoad(int[] maxImageSize, boolean isWeb) {
                if (isWeb){
                    LoadImageUtils.INSTANCE.loadWebImage(context, imageUrl, onLoadBigImageListener, this);
                }else {
                    RequestOptions requestOptions = new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .override(maxImageSize[0], maxImageSize[1]);
                    Glide.with(context)
                            .load(imageUrl).apply(requestOptions).into(new CustomTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    onLoadBigImageListener.onLoadImageSuccess(resource);
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {

                                }

                                @Override
                                public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                    super.onLoadFailed(errorDrawable);
                                    onLoadBigImageListener.onLoadImageFailed();
                                }
                            });
                }

            }
        });

    }

}
