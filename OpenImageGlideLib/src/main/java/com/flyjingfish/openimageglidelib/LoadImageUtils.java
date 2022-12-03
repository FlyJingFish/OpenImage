package com.flyjingfish.openimageglidelib;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.bumptech.glide.request.target.Target;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public enum LoadImageUtils {
    INSTANCE;
    private final ExecutorService cThreadPool = Executors.newFixedThreadPool(5);
    private final Handler handler = new Handler(Looper.getMainLooper());

    public void loadImage(Context context, String imageUrl, OnLocalRealFinishListener finishListener) {
        boolean isWeb = BitmapUtils.isWeb(imageUrl);

        if (!isWeb) {
            cThreadPool.submit(() -> {
                int[] size = BitmapUtils.getImageSize(context, imageUrl);
                int[] maxImageSize = BitmapUtils.getMaxImageSize(size[0], size[1]);

                handler.post(() -> {
                    if (context instanceof LifecycleOwner){
                        LifecycleOwner lifecycleOwner = (LifecycleOwner) context;
                        if (lifecycleOwner.getLifecycle().getCurrentState() != Lifecycle.State.DESTROYED){
                            finishListener.onGoLoad(maxImageSize);
                        }
                    }
                });
            });
        } else {
            int[] maxImageSize = new int[]{Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL};
            finishListener.onGoLoad(maxImageSize);
        }

    }
}
