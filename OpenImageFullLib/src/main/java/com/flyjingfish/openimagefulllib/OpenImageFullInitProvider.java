package com.flyjingfish.openimagefulllib;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.flyjingfish.openimageglidelib.GlideDownloadMediaHelper;
import com.flyjingfish.openimagelib.OpenImageConfig;
import com.flyjingfish.openimagelib.listener.DownloadMediaHelper;

public class OpenImageFullInitProvider extends ContentProvider {
    @Override
    public boolean onCreate() {
        //初始化视频加载，如果有多个请每次在调用openImage.show之前设置一遍
        if (OpenImageConfig.getInstance().getVideoFragmentCreate() == null){
            OpenImageConfig.getInstance().setVideoFragmentCreate(new VideoFragmentCreateImpl());
        }
        FullGlideDownloadMediaHelper fullGlideDownloadMediaHelper = FullGlideDownloadMediaHelper.getInstance();
        //初始化下载原图或视频类
        if (OpenImageConfig.getInstance().getDownloadMediaHelper() == null || OpenImageConfig.getInstance().getDownloadMediaHelper() != fullGlideDownloadMediaHelper){
            try {
                fullGlideDownloadMediaHelper.setDefaultDownloadMediaHelper(new GlideDownloadMediaHelper());
            } catch (NoClassDefFoundError e) {
                DownloadMediaHelper oldDownloadMediaHelper = OpenImageConfig.getInstance().getDownloadMediaHelper();
                if (oldDownloadMediaHelper != null){
                    fullGlideDownloadMediaHelper.setDefaultDownloadMediaHelper(oldDownloadMediaHelper);
                }
            }
            OpenImageConfig.getInstance().setDownloadMediaHelper(fullGlideDownloadMediaHelper);
        }
        OpenImageConfig.getInstance().setPreloadCount(false,4);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

}