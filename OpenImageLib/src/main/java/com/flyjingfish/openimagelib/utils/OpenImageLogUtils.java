package com.flyjingfish.openimagelib.utils;

import android.content.Context;
import android.util.Log;

public class OpenImageLogUtils {
    private static boolean isApkInDebug;
    private static final String TAG = "OpenImage";
    public static boolean isApkInDebug() {
        return isApkInDebug;
    }
    private static boolean init;
    public static void init(Context context) {
        if (init){
            return;
        }
        isApkInDebug = ActivityCompatHelper.isApkInDebug(context);
        init = true;
    }
    public static void setApkInDebug(boolean apkInDebug) {
        isApkInDebug = apkInDebug;
    }
    public static void logD(String tag,String logText){
        if (isApkInDebug){
            Log.d(TAG,tag+"---->"+logText);
        }
    }
    public static void logE(String tag,String logText){
        if (isApkInDebug){
            Log.e(TAG,tag+"---->"+logText);
        }
    }
}
