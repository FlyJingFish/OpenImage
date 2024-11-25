package com.flyjingfish.openimagecoillib;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.flyjingfish.openimagelib.OpenImageConfig;
import com.flyjingfish.openimagelib.utils.OpenImageLogUtils;

public class OpenImageCoilInitProvider extends ContentProvider {
    @Override
    public boolean onCreate() {
        OpenImageLogUtils.init(getContext().getApplicationContext());
        //初始化大图加载器
        if (OpenImageConfig.getInstance().getBigImageHelper() == null){
            try {
                OpenImageConfig.getInstance().setBigImageHelper(new CoilBigImageHelper());
            } catch (NoClassDefFoundError e) {
                OpenImageConfig.getInstance().setBigImageHelper(new Coil3BigImageHelper());
            }
        }
        //初始化下载原图或视频类
        if (OpenImageConfig.getInstance().getDownloadMediaHelper() == null){
            try {
                OpenImageConfig.getInstance().setDownloadMediaHelper(new CoilDownloadMediaHelper());
            } catch (NoClassDefFoundError e) {
                OpenImageConfig.getInstance().setDownloadMediaHelper(new Coil3DownloadMediaHelper());
            }
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