package com.flyjingfish.openimagefulllib;

import com.flyjingfish.openimageglidelib.OpenImageGlideInitProvider;
import com.flyjingfish.openimagelib.OpenImageConfig;

public class OpenImageFullInitProvider extends OpenImageGlideInitProvider {
    @Override
    public boolean onCreate() {
        super.onCreate();
        //初始化视频加载，如果有多个请每次在调用openImage.show之前设置一遍
        if (OpenImageConfig.getInstance().getVideoFragmentCreate() == null){
            OpenImageConfig.getInstance().setVideoFragmentCreate(new VideoFragmentCreateImpl());
        }
        FullGlideDownloadMediaHelper fullGlideDownloadMediaHelper = FullGlideDownloadMediaHelper.getInstance();
        //初始化下载原图或视频类
        if (OpenImageConfig.getInstance().getDownloadMediaHelper() == null || OpenImageConfig.getInstance().getDownloadMediaHelper() != fullGlideDownloadMediaHelper){
            OpenImageConfig.getInstance().setDownloadMediaHelper(fullGlideDownloadMediaHelper);
        }
        OpenImageConfig.getInstance().setPreloadCount(false,4);
        return true;
    }

}