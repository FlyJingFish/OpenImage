package com.flyjingfish.openimageglidelib;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
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
                            .load(imageUrl).apply(requestOptions).addListener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    onLoadBigImageListener.onLoadImageFailed();
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    return false;
                                }
                            }).into(new CustomTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    onLoadBigImageListener.onLoadImageSuccess(resource);
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {

                                }
                            });
                }

            }
        });

    }

    @Override
    public void loadImage(Context context, String imageUrl, ImageView imageView) {
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(context)
                .load(imageUrl).apply(requestOptions).into(imageView);

    }


}
