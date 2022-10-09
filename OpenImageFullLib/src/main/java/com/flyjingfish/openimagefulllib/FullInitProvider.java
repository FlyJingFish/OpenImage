package com.flyjingfish.openimagefulllib;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.flyjingfish.openimagelib.OpenImageConfig;

public class FullInitProvider extends ContentProvider {
    @Override
    public boolean onCreate() {
        //初始化视频加载，如果有多个请每次在调用openImage.show之前设置一遍
        if (OpenImageConfig.getInstance().getVideoFragmentCreate() == null){
            OpenImageConfig.getInstance().setVideoFragmentCreate(new VideoFragmentCreateImpl());
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