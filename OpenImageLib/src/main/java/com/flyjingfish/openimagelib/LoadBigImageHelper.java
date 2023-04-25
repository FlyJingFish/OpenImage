package com.flyjingfish.openimagelib;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.flyjingfish.openimagelib.listener.OnLoadBigImageListener;

import java.util.HashMap;
import java.util.Map;

enum LoadBigImageHelper {
    INSTANCE;
    private final Map<Object,OnLoadBigImageListener> onLoadBigImageListenerMap = new HashMap<>();

    public void loadImage(Context context, String url, OnLoadBigImageListener onLoadBigImageListener, LifecycleOwner owner) {
        final Object tag = new Object();
        onLoadBigImageListenerMap.put(tag,onLoadBigImageListener);
        final LifecycleEventObserver lifecycleEventObserver = (source, event) -> {
            if (event == Lifecycle.Event.ON_DESTROY){
                onLoadBigImageListenerMap.remove(tag);
            }
        };
        OpenImageConfig.getInstance().getBigImageHelper().loadImage(context, url, new OnLoadBigImageListener() {
            @Override
            public void onLoadImageSuccess(Drawable drawable) {
                OnLoadBigImageListener listener;
                if ((listener = onLoadBigImageListenerMap.get(tag)) != null){
                    listener.onLoadImageSuccess(drawable);
                    owner.getLifecycle().removeObserver(lifecycleEventObserver);
                }
                onLoadBigImageListenerMap.remove(tag);
            }

            @Override
            public void onLoadImageFailed() {
                OnLoadBigImageListener listener;
                if ((listener = onLoadBigImageListenerMap.get(tag)) != null){
                    listener.onLoadImageFailed();
                    owner.getLifecycle().removeObserver(lifecycleEventObserver);
                }
                onLoadBigImageListenerMap.remove(tag);
            }
        });
        owner.getLifecycle().addObserver(lifecycleEventObserver);
    }
}
