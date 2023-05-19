package com.flyjingfish.openimageglidelib;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.bumptech.glide.request.target.Target;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.InputStream;

public class BitmapUtils {
    private static final int ARGB_8888_MEMORY_BYTE = 4;
    private static final int MAX_BITMAP_SIZE = 100 * 1024 * 1024;   // 100 MB

    /**
     * 获取图片的缩放比例
     *
     * @param imageWidth  图片原始宽度
     * @param imageHeight 图片原始高度
     * @return
     */
    public static int[] getMaxImageSize(int imageWidth, int imageHeight) {
        int maxWidth = Target.SIZE_ORIGINAL, maxHeight = Target.SIZE_ORIGINAL;
        if (imageWidth == 0 && imageHeight == 0) {
            return new int[]{maxWidth, maxHeight};
        }
        int inSampleSize = BitmapUtils.computeSize(imageWidth, imageHeight);
        long totalMemory = getTotalMemory();
        boolean decodeAttemptSuccess = false;
        while (!decodeAttemptSuccess) {
            maxWidth = imageWidth / inSampleSize;
            maxHeight = imageHeight / inSampleSize;
            int bitmapSize = maxWidth * maxHeight * ARGB_8888_MEMORY_BYTE;
            if (bitmapSize > totalMemory) {
                inSampleSize *= 2;
                continue;
            }
            decodeAttemptSuccess = true;
        }
        return new int[]{maxWidth, maxHeight};
    }

    /**
     * 获取当前应用可用内存
     *
     * @return
     */
    public static long getTotalMemory() {
        long totalMemory = Runtime.getRuntime().totalMemory();
        return totalMemory > MAX_BITMAP_SIZE ? MAX_BITMAP_SIZE : totalMemory;
    }

    /**
     * 计算图片合适压缩比较
     *
     * @param srcWidth  资源宽度
     * @param srcHeight 资源高度
     * @return
     */
    public static int computeSize(int srcWidth, int srcHeight) {
        srcWidth = srcWidth % 2 == 1 ? srcWidth + 1 : srcWidth;
        srcHeight = srcHeight % 2 == 1 ? srcHeight + 1 : srcHeight;

        int longSide = Math.max(srcWidth, srcHeight);
        int shortSide = Math.min(srcWidth, srcHeight);

        float scale = ((float) shortSide / longSide);
        if (scale <= 1 && scale > 0.5625) {
            if (longSide < 1664) {
                return 1;
            } else if (longSide < 4990) {
                return 2;
            } else if (longSide > 4990 && longSide < 10240) {
                return 4;
            } else {
                return longSide / 1280;
            }
        } else if (scale <= 0.5625 && scale > 0.5) {
            return longSide / 1280 == 0 ? 1 : longSide / 1280;
        } else {
            return (int) Math.ceil(longSide / (1280.0 / scale));
        }
    }

    public static void close(@Nullable Closeable c) {
        // java.lang.IncompatibleClassChangeError: interface not implemented
        if (c instanceof Closeable) {
            try {
                c.close();
            } catch (Exception e) {
                // silence
            }
        }
    }
    public static boolean isContent(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        return url.startsWith("content://");
    }
    public static boolean isWeb(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        return url.startsWith("http://")|| url.startsWith("https://");
    }
    public static int[] getImageSize(Context context, String url) {
        int[] mediaExtraInfo = new int[2];
        InputStream inputStream = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            if (isContent(url)) {
                inputStream = PictureContentResolver.getContentResolverOpenInputStream(context, Uri.parse(url));
            } else {
                inputStream = new FileInputStream(url);
            }
            BitmapFactory.decodeStream(inputStream, null, options);
            mediaExtraInfo[0] = options.outWidth;
            mediaExtraInfo[1] = options.outHeight;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(inputStream);
        }
        return mediaExtraInfo;
    }

    public static String getImageTypeWithMime(Context context,String path) {
        InputStream inputStream = null;
        String type = "";
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            if (isContent(path)) {
                inputStream = PictureContentResolver.getContentResolverOpenInputStream(context, Uri.parse(path));
            } else {
                inputStream = new FileInputStream(path);
            }
            BitmapFactory.decodeStream(inputStream, null, options);
            type = options.outMimeType;
            Log.d("ImageUtil", "getImageTypeWithMime: path = " + path + ", type1 = " + type);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(inputStream);
        }
        String var10000;
        if (TextUtils.isEmpty(type)) {
            var10000 = "";
        } else {
            byte var5 = 6;
            var10000 = type.substring(var5);
        }
        type = var10000;
        Log.d("ImageUtil", "getImageTypeWithMime: path = " + path + ", type2 = " + type);
        return type;
    }
}