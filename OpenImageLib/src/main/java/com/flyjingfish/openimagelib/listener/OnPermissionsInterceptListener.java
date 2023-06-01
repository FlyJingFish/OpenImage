package com.flyjingfish.openimagelib.listener;

import com.flyjingfish.openimagelib.OpenImageActivity;

public interface OnPermissionsInterceptListener {
    /**
     * Custom Permissions management
     *
     * @param activity
     * @param permissionArray Permissions array
     * @param call
     */
    void requestPermission(OpenImageActivity activity, String[] permissionArray, OnRequestPermissionListener call);

    /**
     * Verify permission application status
     *
     * @param activity
     * @param permissionArray
     * @return
     */
    boolean hasPermissions(OpenImageActivity activity, String[] permissionArray);
}