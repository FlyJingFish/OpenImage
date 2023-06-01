package com.flyjingfish.openimagelib;

import android.Manifest;
import android.content.Context;
import android.os.Build;

class PermissionConfig {
    public static final int TIRAMISU = 33;
    public static final String READ_MEDIA_AUDIO = "android.permission.READ_MEDIA_AUDIO";
    public static final String READ_MEDIA_IMAGES = "android.permission.READ_MEDIA_IMAGES";
    public static final String READ_MEDIA_VIDEO = "android.permission.READ_MEDIA_VIDEO";
    public static final String READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    public static final String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    public static boolean isTIRAMISU() {
        return Build.VERSION.SDK_INT >= TIRAMISU;
    }

    /**
     * 获取外部读取权限
     */
    public static String[] getReadPermissionArray() {
        if (isTIRAMISU()) {
            return new String[]{READ_MEDIA_IMAGES, READ_MEDIA_VIDEO};
        }
        return new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE};
    }

}
