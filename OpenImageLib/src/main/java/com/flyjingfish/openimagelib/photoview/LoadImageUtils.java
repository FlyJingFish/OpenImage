package com.flyjingfish.openimagelib.photoview;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

enum LoadImageUtils {
    INSTANCE;
    private final ExecutorService cThreadPool = Executors.newFixedThreadPool(5);
    private final Handler handler = new Handler(Looper.getMainLooper());

    public void loadImageForSize(Context context, String filePath, OnLocalRealFinishListener finishListener) {
        boolean isWeb = BitmapUtils.isWeb(filePath);

        if (!isWeb) {
            cThreadPool.submit(() -> {
                int[] size = BitmapUtils.getImageSize(context, filePath);

                handler.post(() -> {
                    if (context instanceof LifecycleOwner){
                        LifecycleOwner lifecycleOwner = (LifecycleOwner) context;
                        if (lifecycleOwner.getLifecycle().getCurrentState() != Lifecycle.State.DESTROYED){
                            finishListener.onGoLoad(filePath,size, false);
                        }
                    }else if (context instanceof Activity){
                        if (!((Activity) context).isFinishing() && !((Activity) context).isDestroyed()){
                            finishListener.onGoLoad(filePath,size, false);
                        }
                    }
                });
            });
        }else {
            finishListener.onGoLoad(filePath,null,true);
        }

    }

}
