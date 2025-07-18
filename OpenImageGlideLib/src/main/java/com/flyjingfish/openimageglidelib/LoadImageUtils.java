package com.flyjingfish.openimageglidelib;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.flyjingfish.openimagelib.listener.OnLoadBigImageListener;
import com.flyjingfish.openimagelib.utils.BitmapUtils;
import com.flyjingfish.openimagelib.utils.ExifHelper;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;

public enum LoadImageUtils {
    INSTANCE;
    private final ExecutorService cThreadPool = Executors.newFixedThreadPool(5);
    private final Handler handler = new Handler(Looper.getMainLooper());
    private OkHttpClient okHttpClient;

    synchronized void initOkHttpClient(){
        this.okHttpClient = ProgressManager.getInstance().with(new OkHttpClient.Builder())
                .build();
    }

    public OkHttpClient getOkHttpClient() {
        if (okHttpClient == null){
            initOkHttpClient();
        }
        return okHttpClient;
    }

    boolean isInitOkHttpClient() {
        return okHttpClient != null;
    }

    void loadImageForSize(Context context, String imageUrl, OnLocalRealFinishListener finishListener) {
        boolean isWeb = BitmapUtils.isWeb(imageUrl);

        if (!isWeb) {
            cThreadPool.submit(() -> {
                int[] size = BitmapUtils.getImageSize(context, imageUrl);
                int[] maxImageSize = BitmapUtils.getMaxImageSize(size[0], size[1]);
                ExifInterface exif = ExifHelper.getExifInterface(context,imageUrl);

                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                int rotate = 0;
                switch (orientation){
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotate = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotate = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotate = 270;
                        break;
                }
                final int rotateFinal = rotate;

                handler.post(() -> {
                    if (context instanceof LifecycleOwner){
                        LifecycleOwner lifecycleOwner = (LifecycleOwner) context;
                        if (lifecycleOwner.getLifecycle().getCurrentState() != Lifecycle.State.DESTROYED){
                            finishListener.onGoLoad(imageUrl,maxImageSize, false,rotateFinal);
                        }
                    }else if (context instanceof Activity){
                        if (!((Activity) context).isFinishing() && !((Activity) context).isDestroyed()){
                            finishListener.onGoLoad(imageUrl,maxImageSize, false,rotateFinal);
                        }
                    }
                });
            });
        } else {
            int[] maxImageSize = new int[]{Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL};
            finishListener.onGoLoad(imageUrl,maxImageSize, true,0);
        }

    }


    void loadWebImage(Context context, String imageUrl, final OnLoadBigImageListener onLoadBigImageListener, OnLocalRealFinishListener finishListener){
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
        Glide.with(context).asFile()
                .load(imageUrl).apply(requestOptions).into(new CustomTarget<File>() {
                    @Override
                    public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                        loadImageForSize(context,resource.getPath(),finishListener);
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
