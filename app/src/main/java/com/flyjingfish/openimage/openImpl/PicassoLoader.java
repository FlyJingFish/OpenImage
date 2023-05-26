package com.flyjingfish.openimage.openImpl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.flyjingfish.openimageglidelib.BitmapUtils;
import com.flyjingfish.openimagelib.listener.OnLoadBigImageListener;
import com.squareup.picasso.Picasso;

public class PicassoLoader {
    Context context; String imageUrl; OnLoadBigImageListener onLoadBigImageListener;
    final Object tag = new Object();
    com.squareup.picasso.Target myTarget = new com.squareup.picasso.Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            myTarget = null;//这句不能删除否则图片加载异常
            if (onLoadBigImageListener != null){
                onLoadBigImageListener.onLoadImageSuccess(new BitmapDrawable(context.getResources(),bitmap),imageUrl);
            }
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
            if (onLoadBigImageListener != null){
                onLoadBigImageListener.onLoadImageFailed();
            }
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
        boolean isWeb = BitmapUtils.isWeb(imageUrl);
        if (!isWeb){
            boolean isContent = BitmapUtils.isContent(imageUrl);
            int[] size = BitmapUtils.getImageSize(context, imageUrl);
            int[] maxImageSize = BitmapUtils.getMaxImageSize(size[0], size[1]);
            Picasso.get().load((isContent?"":"file://")+imageUrl)
                    .resize(maxImageSize[0],maxImageSize[1]).onlyScaleDown().tag(tag).into(myTarget);
        }else {
            int[] maxImageSize = BitmapUtils.getMaxImageSize(8000,20000);
            Picasso.get().load(imageUrl).resize(maxImageSize[0],maxImageSize[1])
                    .onlyScaleDown().centerInside().tag(tag).into(myTarget);
        }
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
