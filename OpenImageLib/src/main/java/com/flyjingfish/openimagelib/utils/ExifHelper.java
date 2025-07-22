package com.flyjingfish.openimagelib.utils;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

public class ExifHelper {
    public static ExifInterface getExifInterface(Context context, String filePath) {
        try {
            if (BitmapUtils.isContent(filePath)) {
                return getExifFromContentUri(context, Uri.parse(filePath));
            } else if (BitmapUtils.isAsset(filePath)){
                String fileName = filePath.replace("file:///android_asset/", "");
                return ExifHelper.getExifFromAssets(context, fileName);
            } else if (BitmapUtils.isLocalFile(filePath)) {
                String path = Uri.parse(filePath).getPath();
                return new ExifInterface(path);
            } else {
                return new ExifInterface(filePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 从 Content URI 获取 ExifInterface
     */
    private static ExifInterface getExifFromContentUri(Context context, Uri uri) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try (InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
                if (inputStream != null) {
                    return new ExifInterface(inputStream);
                }
            }
        } else {
            // Android 10 以下版本，尝试从 MediaStore 查询文件路径
            String path = getFilePathFromUri(context, uri);
            if (path != null) {
                return new ExifInterface(path);
            }
        }
        return null;
    }

    /**
     * 从 Content URI 查询文件路径（仅适用于 Android 10 以下）
     */
    private static String getFilePathFromUri(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri,
                new String[]{MediaStore.Images.Media.DATA}, null, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    return cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                }
            } finally {
                cursor.close();
            }
        }
        return null;
    }

    private static ExifInterface getExifFromAssets(Context context, String assetImageName) {
        try (InputStream inputStream = context.getAssets().open(assetImageName);){

            File tempFile = createTempImageFile(context, inputStream);
            
            if (tempFile != null) {
                // Step 3: 使用 ExifInterface 读取 EXIF 信息
                ExifInterface exifInterface = new ExifInterface(tempFile.getAbsolutePath());

                tempFile.delete();
                return exifInterface;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建临时文件，将图片流写入文件
     */
    private static File createTempImageFile(Context context, InputStream inputStream) throws IOException {
        // 获取临时文件目录
        File cacheDir = context.getCacheDir();
        if (cacheDir == null) {
            return null;
        }

        // 创建临时文件
        File tempFile = new File(cacheDir, UUID.randomUUID().toString().replace("-","")+".jpg");
        FileOutputStream outputStream = new FileOutputStream(tempFile);

        // 将 InputStream 写入临时文件
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }

        outputStream.close();
        inputStream.close();
        
        return tempFile;
    }
}
