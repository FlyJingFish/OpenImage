package com.flyjingfish.openimagelib;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

class PermissionChecker {

    /**
     * 检查是否有某个权限
     *
     * @param ctx
     * @param permissions
     */
    public static boolean checkSelfPermission(Context ctx, String[] permissions) {
        boolean isAllGranted = true;
        if (permissions != null) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(ctx.getApplicationContext(), permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }
        }
        return isAllGranted;
    }

    /**
     * 检查读写权限是否存在
     */
    public static boolean isCheckWriteReadStorage(Context context) {
        if (PermissionConfig.isTIRAMISU()) {
            return PermissionChecker.isCheckReadImages(context) && PermissionChecker.isCheckReadVideo(context);
        } else {
            return PermissionChecker.isCheckWriteExternalStorage(context);
        }
    }


    /**
     * 检查读取图片权限是否存在
     */
    @RequiresApi(api = 33)
    public static boolean isCheckReadImages(Context context) {
        return PermissionChecker.checkSelfPermission(context,
                new String[]{PermissionConfig.READ_MEDIA_IMAGES});
    }

    /**
     * 检查读取视频权限是否存在
     */
    @RequiresApi(api = 33)
    public static boolean isCheckReadVideo(Context context) {
        return PermissionChecker.checkSelfPermission(context,
                new String[]{PermissionConfig.READ_MEDIA_VIDEO});
    }

    /**
     * 检查写入权限是否存在
     */
    public static boolean isCheckWriteExternalStorage(Context context) {
        return PermissionChecker.checkSelfPermission(context,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }


}
