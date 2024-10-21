package com.flyjingfish.openimagelib.utils;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import android.media.ExifInterface;

public class ExifHelper {
    public static ExifInterface getExifInterface(Context context, String filePath) {
        ExifInterface exifInterface = null;
        try {
            if (filePath.startsWith("file:///android_asset/")){
                String fileName = filePath.replace("file:///android_asset/", "");
                exifInterface = ExifHelper.getExifFromAssets(context,fileName);
            }else {
                exifInterface = new ExifInterface(filePath);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return exifInterface;
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
