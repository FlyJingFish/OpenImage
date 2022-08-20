package com.flyjingfish.openimage;

import android.app.Application;

import com.flyjingfish.openimage.openImpl.BigImageHelperImpl;
import com.flyjingfish.openimage.openImpl.VideoFragmentCreateImpl;
import com.flyjingfish.openimagelib.OpenImageConfig;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyApplication extends Application {
    public static MyApplication mInstance;
    public static ExecutorService cThreadPool = Executors.newFixedThreadPool(5);
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        //初始化大图加载器
        OpenImageConfig.getInstance().setBigImageHelper(new BigImageHelperImpl());
        //初始化视频加载，如果有多个请每次在调用openImage.show之前设置一遍
        OpenImageConfig.getInstance().setVideoFragmentCreate(new VideoFragmentCreateImpl());
    }
}
