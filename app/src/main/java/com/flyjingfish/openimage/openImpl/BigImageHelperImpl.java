package com.flyjingfish.openimage.openImpl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.flyjingfish.openimage.imageloader.MyImageLoader;
import com.flyjingfish.openimagelib.listener.BigImageHelper;
import com.flyjingfish.openimagelib.listener.OnLoadBigImageListener;
import com.squareup.picasso.Picasso;

public class BigImageHelperImpl implements BigImageHelper {
    @Override
    public void loadImage(Context context, String imageUrl, OnLoadBigImageListener onLoadBigImageListener) {
        if (MyImageLoader.loader_os_type == MyImageLoader.GLIDE){
            RequestOptions requestOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .format(DecodeFormat.PREFER_RGB_565);
            Glide.with(context)
                    .load(imageUrl).apply(requestOptions).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    onLoadBigImageListener.onLoadImageFailed();
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    onLoadBigImageListener.onLoadImageSuccess(resource);
                    return false;
                }
            }).into(new CustomTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {

                }
            });
        }else {
            new PicassoLoader(context, imageUrl, onLoadBigImageListener).load();
        }


    }

    @Override
    public void loadImage(Context context, String imageUrl, ImageView imageView) {
        if (MyImageLoader.loader_os_type == MyImageLoader.GLIDE){
            RequestOptions requestOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .format(DecodeFormat.PREFER_RGB_565);
            Glide.with(context)
                    .load(imageUrl).apply(requestOptions).into(imageView);
        }else {
            Picasso.get().load(imageUrl).into(imageView);
        }

    }

//    @Override
//    public void loadImage(Context context, String imageUrl, ImageView imageView, OnLoadBigImageListener onLoadBigImageListener) {
//        if (MyImageLoader.type == MyImageLoader.GLIDE){
//            RequestOptions requestOptions = new RequestOptions()
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
//                    .format(DecodeFormat.PREFER_RGB_565);
//            Glide.with(context)
//                    .load(imageUrl).apply(requestOptions).addListener(new RequestListener<Drawable>() {
//                @Override
//                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                    onLoadBigImageListener.onLoadImageFailed();
//                    return false;
//                }
//
//                @Override
//                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                    return false;
//                }
//            }).into(new CustomTarget<Drawable>() {
//                @Override
//                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
//                    new Handler(Looper.getMainLooper()).post(new Runnable() {
//                        @Override
//                        public void run() {
//                            imageView.setImageDrawable(resource);
//                            onLoadBigImageListener.onLoadImageSuccess();
//                        }
//                    });
//
//                }
//
//                @Override
//                public void onLoadCleared(@Nullable Drawable placeholder) {
//
//                }
//            });
//        }else {
//            final Object tag = new Object();
//            Picasso.get().load(imageUrl).tag(tag).into(imageView, new Callback() {
//                @Override
//                public void onSuccess() {
//                    onLoadBigImageListener.onLoadImageSuccess();
//                }
//
//                @Override
//                public void onError(Exception e) {
//                    onLoadBigImageListener.onLoadImageFailed();
//                }
//            });
//            if (context instanceof LifecycleOwner){
//                LifecycleOwner owner = (LifecycleOwner) context;
//                owner.getLifecycle().addObserver(new LifecycleEventObserver() {
//                    @Override
//                    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
//                        if (event == Lifecycle.Event.ON_DESTROY){
//                            Picasso.get().cancelTag(tag);
//                            source.getLifecycle().removeObserver(this);
//                        }
//                    }
//                });
//            }
//        }
//    }


}
