package com.flyjingfish.openimage;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
//import com.flyjingfish.openimagelib.photoview.LoadImageUtils;
//import com.flyjingfish.openimageglidelib.LoadImageUtils;

import java.io.InputStream;

@GlideModule
public class MyAppGlideModule extends AppGlideModule {

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        super.applyOptions(context, builder);
        Log.e("MyAppGlideModule","applyOptions");
        builder.setLogLevel(Log.DEBUG);
    }

    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        //Glide 底层默认使用 HttpConnection 进行网络请求,这里替换为 Okhttp 后才能使用本框架,进行 Glide 的加载进度监听
        Log.e("MyAppGlideModule","registerComponents"+(Looper.getMainLooper() == Looper.myLooper()));
//        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(LoadImageUtils.INSTANCE.getOkHttpClient()));
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
