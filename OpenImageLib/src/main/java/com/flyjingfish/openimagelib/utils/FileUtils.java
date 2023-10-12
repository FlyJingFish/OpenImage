package com.flyjingfish.openimagelib.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

class FileUtils {

    public static String save(Context context, File resource, boolean video) {
        String sucPath = null;
        String name = System.currentTimeMillis() + "";
        String var10001 = resource.getAbsolutePath();
        String mimeType;
        if (video){
            try {
                mimeType = getMimeType(context,resource);
            } catch (Throwable ignored) {
                mimeType = null;
            }
            if (TextUtils.isEmpty(mimeType)){
                mimeType = "mp4";
            }
        }else {
            mimeType = BitmapUtils.getImageTypeWithMime(context, var10001);
        }
        name = name + '.' + mimeType;
        if (Build.VERSION.SDK_INT >= 29) {
            ContentResolver resolver = context.getContentResolver();
            ContentValues values = new ContentValues();
            String relativePath =  (video ?Environment.DIRECTORY_MOVIES:Environment.DIRECTORY_PICTURES) + "/";
            values.put(MediaStore.Images.Media.DESCRIPTION, name);
            values.put(MediaStore.Images.Media.DISPLAY_NAME, name);
            values.put(MediaStore.Images.Media.MIME_TYPE, (video ?"video/":"image/") + mimeType);
            values.put(MediaStore.Images.Media.TITLE, name);
            values.put(MediaStore.Images.Media.RELATIVE_PATH, relativePath);

            Uri insertUri = resolver.insert(video ?MediaStore.Video.Media.EXTERNAL_CONTENT_URI:MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (insertUri == null) {
                return null;
            }
            BufferedInputStream inputStream = null;
            OutputStream os = null;
            try {
                inputStream = new BufferedInputStream(new FileInputStream(resource.getAbsolutePath()));

                os = resolver.openOutputStream(insertUri);
                if (os != null) {
                    final byte[] data = new byte[1024];
                    int len;
                    while ((len = inputStream.read(data)) != -1) {
                        os.write(data, 0, len);
                    }

                }
                refresh(resolver, insertUri);

                sucPath = relativePath;
            } catch (Exception ignored) {
            } finally {
                try {
                    if (os != null){
                        os.close();
                    }
                } catch (Exception ignored) {
                }
                try {
                    if (inputStream != null){
                        inputStream.close();
                    }
                } catch (Exception ignored) {
                }
            }
        } else {
            String path = Environment.getExternalStorageDirectory().toString() + "/" + Environment.DIRECTORY_DCIM + "/Camera/";
            File folderFile = new File(path);
            if (!folderFile.exists()) {
                boolean r = folderFile.mkdirs();
                if (!r) {
                    return null;
                }
            }
            File newFile = new File(path + name);
            boolean suc;
            if (!newFile.exists()) {
                suc = copySdcardFile(resource,newFile);
            } else {
                suc = true;
            }
            if (suc){
                new SingleMediaScanner(context, newFile.getAbsolutePath(), () -> {
                });
                sucPath = path;
            }

        }
        return sucPath;
    }

    static boolean copySdcardFile(File fromFile, File toFile) {
        FileInputStream fosfrom = null;
        FileOutputStream fosto = null;
        boolean suc = false;
        try {
            fosfrom = new FileInputStream(fromFile);
            fosto = new FileOutputStream(toFile);
            final byte[] bt = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c);
            }

            suc = true;
        } catch (Exception ignored) {
        } finally {
            try {
                if (fosfrom != null){
                    fosfrom.close();
                }
                if (fosto != null){
                    fosto.close();
                }
            } catch (IOException ignored) {
            }
        }
        return suc;
    }

    static void refresh(ContentResolver resolver, Uri insertUri) {
        ContentValues imageValues = new ContentValues();
        // Android Q添加了IS_PENDING状态，为0时其他应用才可见
        imageValues.put(MediaStore.Images.Media.IS_PENDING, 0);
        resolver.update(insertUri, imageValues, null, null);
    }

    static String getMimeType(Context context,File file) {
        MediaMetadataRetriever mMediaMetadataRetriever = new MediaMetadataRetriever();
        mMediaMetadataRetriever.setDataSource(context.getApplicationContext(), Uri.fromFile(file));


        String mimeType = mMediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
    }
}
