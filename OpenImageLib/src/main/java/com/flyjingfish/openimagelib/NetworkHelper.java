package com.flyjingfish.openimagelib;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.listener.DownloadMediaHelper;
import com.flyjingfish.openimagelib.listener.OnDownloadMediaListener;
import com.flyjingfish.openimagelib.listener.OnLoadBigImageListener;
import com.flyjingfish.openimagelib.utils.ActivityCompatHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

enum NetworkHelper {
    INSTANCE;

    public void loadImage(Context context, String url, OnLoadBigImageListener onLoadBigImageListener, LifecycleOwner owner) {
        final String key = UUID.randomUUID().toString();
        final Map<String,OnLoadBigImageListener> onLoadBigImageListenerMap = new HashMap<>();
        onLoadBigImageListenerMap.put(key,onLoadBigImageListener);
        final LifecycleEventObserver lifecycleEventObserver = new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                if (event == Lifecycle.Event.ON_DESTROY){
                    onLoadBigImageListenerMap.clear();
                    source.getLifecycle().removeObserver(this);
                }
            }
        };
        OpenImageConfig.getInstance().getBigImageHelper().loadImage(context, url, new OnLoadBigImageListener() {
            @Override
            public void onLoadImageSuccess(Drawable drawable, String filePath) {
                OnLoadBigImageListener onLoadBigImageListener;
                if ((onLoadBigImageListener = onLoadBigImageListenerMap.get(key)) != null){
                    onLoadBigImageListener.onLoadImageSuccess(drawable,filePath);
                }
            }

            @Override
            public void onLoadImageFailed() {
                OnLoadBigImageListener onLoadBigImageListener;
                if ((onLoadBigImageListener = onLoadBigImageListenerMap.get(key)) != null){
                    onLoadBigImageListener.onLoadImageFailed();
                }
            }
        });
        owner.getLifecycle().addObserver(lifecycleEventObserver);
    }

    public void download(FragmentActivity activity, LifecycleOwner lifecycleOwner, OpenImageUrl openImageUrl, OnDownloadMediaListener onDownloadMediaListener) {
        DownloadMediaHelper downloadMediaHelper = OpenImageConfig.getInstance().getDownloadMediaHelper();
        if (downloadMediaHelper == null) {
            if (ImageLoadUtils.getInstance().isApkInDebug()) {
                throw new IllegalArgumentException("DownloadMediaHelper 不可为null 请调用 OpenImageConfig 的 setDownloadMediaHelper 来设置");
            }
            return;
        }
        final String key = UUID.randomUUID().toString();
        final Map<String,OnDownloadMediaListener> onDownloadMediaListenerHashMap = new HashMap<>();
        onDownloadMediaListenerHashMap.put(key,onDownloadMediaListener);
        final LifecycleEventObserver lifecycleEventObserver = new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                if (event == Lifecycle.Event.ON_DESTROY){
                    onDownloadMediaListenerHashMap.clear();
                    source.getLifecycle().removeObserver(this);
                }
            }
        };
        downloadMediaHelper.download(activity, lifecycleOwner, openImageUrl, new OnDownloadMediaListener() {
            @Override
            public void onDownloadStart(boolean isWithProgress) {
                if (!ActivityCompatHelper.isMainThread() && ImageLoadUtils.getInstance().isApkInDebug()){
                    throw new RuntimeException("必须在主线程回调 onDownloadStart");
                }
                OnDownloadMediaListener onDownloadMediaListener1;
                if ((onDownloadMediaListener1 = onDownloadMediaListenerHashMap.get(key)) != null){
                    onDownloadMediaListener1.onDownloadStart(isWithProgress);
                }
            }

            @Override
            public void onDownloadSuccess(String path) {
                if (!ActivityCompatHelper.isMainThread() && ImageLoadUtils.getInstance().isApkInDebug()){
                    throw new RuntimeException("必须在主线程回调 onDownloadSuccess");
                }
                OnDownloadMediaListener onDownloadMediaListener1;
                if ((onDownloadMediaListener1 = onDownloadMediaListenerHashMap.get(key)) != null){
                    onDownloadMediaListener1.onDownloadSuccess(path);
                }
            }

            @Override
            public void onDownloadProgress(int percent) {
                if (!ActivityCompatHelper.isMainThread() && ImageLoadUtils.getInstance().isApkInDebug()){
                    throw new RuntimeException("必须在主线程回调 onDownloadProgress");
                }
                OnDownloadMediaListener onDownloadMediaListener1;
                if ((onDownloadMediaListener1 = onDownloadMediaListenerHashMap.get(key)) != null){
                    onDownloadMediaListener1.onDownloadProgress(percent);
                }
            }

            @Override
            public void onDownloadFailed() {
                if (!ActivityCompatHelper.isMainThread() && ImageLoadUtils.getInstance().isApkInDebug()){
                    throw new RuntimeException("必须在主线程回调 onDownloadFailed");
                }
                OnDownloadMediaListener onDownloadMediaListener1;
                if ((onDownloadMediaListener1 = onDownloadMediaListenerHashMap.get(key)) != null){
                    onDownloadMediaListener1.onDownloadFailed();
                }
            }
        });
        lifecycleOwner.getLifecycle().addObserver(lifecycleEventObserver);
    }
}
