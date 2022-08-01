package com.flyjingfish.openimage.openImpl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.flyjingfish.openimagelib.listener.OnLoadBigImageListener;
import com.squareup.picasso.Picasso;

public class PicassoLoader {
    Context context; String imageUrl; OnLoadBigImageListener onLoadBigImageListener;
    final Object tag = new Object();
    com.squareup.picasso.Target myTarget = new com.squareup.picasso.Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            myTarget = null;//这句不能删除否则图片加载异常
            onLoadBigImageListener.onLoadImageSuccess(new BitmapDrawable(context.getResources(),bitmap));
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
            onLoadBigImageListener.onLoadImageFailed();
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };
    public PicassoLoader(Context context, String imageUrl, OnLoadBigImageListener onLoadBigImageListener) {
        this.context = context;
        this.imageUrl = imageUrl;
        this.onLoadBigImageListener = onLoadBigImageListener;
    }

    public void load(){
        Picasso.get().load(imageUrl).into(myTarget);
        if (context instanceof LifecycleOwner){
            LifecycleOwner owner = (LifecycleOwner) context;
            owner.getLifecycle().addObserver(new LifecycleEventObserver() {
                @Override
                public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                    if (event == Lifecycle.Event.ON_DESTROY){
                        Picasso.get().cancelTag(tag);
                        source.getLifecycle().removeObserver(this);
                    }
                }
            });
        }
    }
}
