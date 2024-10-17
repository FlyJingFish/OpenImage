package com.flyjingfish.openimagelib.photoview;

import android.app.Activity;
import android.content.Context;
import android.media.ExifInterface;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.flyjingfish.openimagelib.utils.BitmapUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

enum LoadImageUtils {
    INSTANCE;
    private final ExecutorService cThreadPool = Executors.newFixedThreadPool(5);
    private final Handler handler = new Handler(Looper.getMainLooper());
    private ExifInterface getExifInterface(Context context, String filePath) {
        ExifInterface exifInterface = null;
        try {
            if (filePath.startsWith("file:///android_asset/")){
                String fileName = filePath.replace("file:///android_asset/", "");
                InputStream inputStream = context.getAssets().open(fileName);
                exifInterface = new ExifInterface(inputStream);
                inputStream.close();
            }else {
                exifInterface = new ExifInterface(filePath);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return exifInterface;
    }
    public void loadImageForSize(Context context, String filePath, OnLocalRealFinishListener finishListener) {
        boolean isWeb = BitmapUtils.isWeb(filePath);

        if (!isWeb) {
            cThreadPool.submit(() -> {
                int[] size = BitmapUtils.getImageSize(context, filePath);
                ExifInterface exif = getExifInterface(context,filePath);

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
                            finishListener.onGoLoad(filePath,size, false,rotateFinal);
                        }
                    }else if (context instanceof Activity){
                        if (!((Activity) context).isFinishing() && !((Activity) context).isDestroyed()){
                            finishListener.onGoLoad(filePath,size, false,rotateFinal);
                        }
                    }
                });
            });
        }else {
            finishListener.onGoLoad(filePath,null,true,0);
        }

    }

}
