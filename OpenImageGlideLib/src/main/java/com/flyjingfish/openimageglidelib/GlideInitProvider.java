package com.flyjingfish.openimageglidelib;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.flyjingfish.openimagelib.OpenImageConfig;

public class GlideInitProvider extends ContentProvider {
    @Override
    public boolean onCreate() {
        //初始化大图加载器
        if (OpenImageConfig.getInstance().getBigImageHelper() == null){
            OpenImageConfig.getInstance().setBigImageHelper(new GlideBigImageHelper());
        }
        //初始化下载大图类
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