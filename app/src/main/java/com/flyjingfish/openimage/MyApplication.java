package com.flyjingfish.openimage;

import static android.os.Build.VERSION.SDK_INT;

import android.app.Application;
import android.os.Build;

import androidx.annotation.NonNull;

import com.flyjingfish.openimage.openImpl.AppDownloadFileHelper;
import com.flyjingfish.openimage.openImpl.AppGlideBigImageHelper;
import com.flyjingfish.openimage.openImpl.PicassoLoader;
import com.flyjingfish.openimage.openImpl.download.ProgressManager;
import com.flyjingfish.openimagecoillib.CoilLoadImageUtils;
import com.flyjingfish.openimagelib.OpenImageConfig;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import coil.ComponentRegistry;
import coil.ImageLoader;
import coil.ImageLoaderFactory;
import coil.decode.GifDecoder;
import coil.decode.ImageDecoderDecoder;
import coil.util.DebugLogger;
import okhttp3.Cache;
import okhttp3.OkHttpClient;

public class MyApplication extends Application implements ImageLoaderFactory {
    public static MyApplication mInstance;
    private OkHttpClient okHttpClient;
    public static ExecutorService cThreadPool = Executors.newFixedThreadPool(5);
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
//        OpenImageConfig.getInstance().setBigImageHelper(new AppGlideBigImageHelper());
//        AppDownloadFileHelper appDownloadFileHelper = new AppDownloadFileHelper();
//        OpenImageConfig.getInstance().setDownloadMediaHelper(appDownloadFileHelper);
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

    @NonNull
    @Override
    public ImageLoader newImageLoader() {
        ComponentRegistry.Builder builder = new ComponentRegistry.Builder();
        if (SDK_INT >= 28) {
            builder.add(new ImageDecoderDecoder.Factory());
        } else {
            builder.add(new GifDecoder.Factory());
        }
        return new ImageLoader.Builder(this).logger(new DebugLogger())
                .okHttpClient(CoilLoadImageUtils.INSTANCE.getOkHttpClient())
                .components(builder.build())
            .build();
    }
}
