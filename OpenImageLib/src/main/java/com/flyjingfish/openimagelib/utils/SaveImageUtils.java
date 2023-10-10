package com.flyjingfish.openimagelib.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public enum SaveImageUtils {
    INSTANCE;
    private final ExecutorService cThreadPool = Executors.newFixedThreadPool(5);
    private final Handler handler = new Handler(Looper.getMainLooper());

    public void saveFile(Context context, File resource, boolean video,OnSaveFinish onSaveFinish) {
        cThreadPool.submit(() -> saveFileIgnoreThread(context, resource, video, onSaveFinish));

    }

    public void saveFileIgnoreThread(Context context, File resource, boolean video, OnSaveFinish onSaveFinish) {
        String sucPath = FileUtils.save(context, resource, video);
        if (onSaveFinish != null){
            handler.post(() -> onSaveFinish.onFinish(sucPath));
        }

    }

    public interface OnSaveFinish{
        void onFinish(String sucPath);
    }
}
