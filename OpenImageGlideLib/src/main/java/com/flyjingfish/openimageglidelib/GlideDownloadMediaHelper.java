package com.flyjingfish.openimageglidelib;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.enums.MediaType;
import com.flyjingfish.openimagelib.listener.DownloadMediaHelper;
import com.flyjingfish.openimagelib.listener.OnDownloadMediaListener;
import com.flyjingfish.openimagelib.utils.SaveImageUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GlideDownloadMediaHelper implements DownloadMediaHelper {
    @Override
    public void download(FragmentActivity activity, LifecycleOwner lifecycleOwner, OpenImageUrl openImageUrl, OnDownloadMediaListener onDownloadMediaListener) {
        final String downloadUrl = openImageUrl.getType() == MediaType.VIDEO?openImageUrl.getVideoUrl():openImageUrl.getImageUrl();
        boolean isInitOkHttpClient = LoadImageUtils.INSTANCE.isInitOkHttpClient();
        if (onDownloadMediaListener != null) {
            onDownloadMediaListener.onDownloadStart(isInitOkHttpClient);
        }
        final Context context = activity.getApplicationContext();
        final String key = UUID.randomUUID().toString();
        final Map<String, OnDownloadMediaListener> onDownloadMediaListenerHashMap = new HashMap<>();
        onDownloadMediaListenerHashMap.put(key,onDownloadMediaListener);
        lifecycleOwner.getLifecycle().addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                if (event == Lifecycle.Event.ON_DESTROY){
                    onDownloadMediaListenerHashMap.clear();
                    source.getLifecycle().removeObserver(this);
                    ProgressManager.getInstance().removeResponseListeners(downloadUrl);
                }
            }
        });
        if (isInitOkHttpClient){
            ProgressManager.getInstance().addResponseListener(downloadUrl, new ProgressListener() {
                @Override
                public void onProgress(ProgressInfo progressInfo) {
                    OnDownloadMediaListener onDownloadMediaListener;
                    if ((onDownloadMediaListener = onDownloadMediaListenerHashMap.get(key)) != null) {
                        onDownloadMediaListener.onDownloadProgress(progressInfo.getPercent());
                    }
                }

                @Override
                public void onError(long id, Exception e) {

                }
            });
        }

        Glide.with(activity).downloadOnly().skipMemoryCache(true)
                .load(downloadUrl).into(new CustomTarget<File>() {

                    @Override
                    public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                        if (onDownloadMediaListenerHashMap.get(key) != null) {
                            SaveImageUtils.INSTANCE.saveFile(context, resource, openImageUrl.getType() == MediaType.VIDEO, sucPath -> {
                                OnDownloadMediaListener onDownloadMediaListener;
                                if ((onDownloadMediaListener = onDownloadMediaListenerHashMap.get(key)) != null) {
                                    if (!TextUtils.isEmpty(sucPath)){
                                        onDownloadMediaListener.onDownloadSuccess(sucPath);
                                    }else {
                                        onDownloadMediaListener.onDownloadFailed();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        OnDownloadMediaListener onDownloadMediaListener;
                        if ((onDownloadMediaListener = onDownloadMediaListenerHashMap.get(key)) != null) {
                            onDownloadMediaListener.onDownloadFailed();
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

}
