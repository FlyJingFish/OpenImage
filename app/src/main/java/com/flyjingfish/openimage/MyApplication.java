package com.flyjingfish.openimage;

import android.app.Application;

import com.flyjingfish.openimage.openImpl.AppDownloadFileHelper;
import com.flyjingfish.openimage.openImpl.AppGlideBigImageHelper;
import com.flyjingfish.openimage.openImpl.PicassoLoader;
import com.flyjingfish.openimage.openImpl.download.ProgressManager;
import com.flyjingfish.openimagelib.OpenImageConfig;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

public class MyApplication extends Application {
    public static MyApplication mInstance;
    private OkHttpClient okHttpClient;
    public static ExecutorService cThreadPool = Executors.newFixedThreadPool(5);
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        OpenImageConfig.getInstance().setBigImageHelper(new AppGlideBigImageHelper());
        OpenImageConfig.getInstance().setDownloadMediaHelper(new AppDownloadFileHelper());
        initPicasso();
        okHttpClient = ProgressManager.getInstance().with(new OkHttpClient.Builder()).build();
    }

    private void initPicasso(){
        File cacheDir = PicassoLoader.createDefaultCacheDir(this);
        long maxSize = PicassoLoader.calculateDiskCacheSize(cacheDir);
        OkHttpClient okHttpClient = ProgressManager.getInstance().with(new OkHttpClient.Builder().cache(new Cache(cacheDir, maxSize)))
                .build();
        Picasso picasso = new Picasso.Builder(this).downloader(new OkHttp3Downloader(okHttpClient)).build();
        Picasso.setSingletonInstance(picasso);
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }
}
