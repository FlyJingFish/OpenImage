package com.flyjingfish.openimageglidelib;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.flyjingfish.openimagelib.OpenImageConfig;
import com.flyjingfish.openimagelib.utils.OpenImageLogUtils;

public class OpenImageGlideInitProvider extends ContentProvider {
    @Override
    public boolean onCreate() {
        OpenImageLogUtils.init(getContext().getApplicationContext());
        //初始化大图加载器
        if (OpenImageConfig.getInstance().getBigImageHelper() == null){
            OpenImageConfig.getInstance().setBigImageHelper(new GlideBigImageHelper());
        }
        //初始化下载原图或视频类
        if (OpenImageConfig.getInstance().getDownloadMediaHelper() == null){
            OpenImageConfig.getInstance().setDownloadMediaHelper(new GlideDownloadMediaHelper());
        }
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}