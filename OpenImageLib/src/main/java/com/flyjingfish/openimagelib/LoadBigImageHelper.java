package com.flyjingfish.openimagelib;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.flyjingfish.openimagelib.listener.OnLoadBigImageListener;

enum LoadBigImageHelper {
    INSTANCE;

    public void loadImage(Context context, String url, OnLoadBigImageListener onLoadBigImageListener, LifecycleOwner owner) {
        final boolean[] isDestroy = new boolean[]{false};
        final LifecycleEventObserver lifecycleEventObserver = new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                if (event == Lifecycle.Event.ON_DESTROY){
                    isDestroy[0] = true;
                    source.getLifecycle().removeObserver(this);
                }
            }
        };
        OpenImageConfig.getInstance().getBigImageHelper().loadImage(context, url, new OnLoadBigImageListener() {
            @Override
            public void onLoadImageSuccess(Drawable drawable, String filePath) {
                if (onLoadBigImageListener != null && !isDestroy[0]){
                    onLoadBigImageListener.onLoadImageSuccess(drawable,filePath);
                }
            }

            @Override
            public void onLoadImageFailed() {
                if (onLoadBigImageListener != null && !isDestroy[0]){
                    onLoadBigImageListener.onLoadImageFailed();
                }
            }
        });
        owner.getLifecycle().addObserver(lifecycleEventObserver);
    }
}
