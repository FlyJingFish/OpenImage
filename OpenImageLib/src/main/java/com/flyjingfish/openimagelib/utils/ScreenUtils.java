package com.flyjingfish.openimagelib.utils;


import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

public class ScreenUtils {
    private static int screenWidth;
    private static int screenHeight;
    public static float dp2px(Context context,float dp){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,context.getResources().getDisplayMetrics()) + 0.5f;
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRealMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRealMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    public static int getScreenWidth2Cache(Context context) {
        if (screenWidth == 0){
            screenWidth = getScreenWidth(context);
        }
        return screenWidth;
    }

    public static int getScreenHeight2Cache(Context context) {
        if (screenHeight == 0){
            screenHeight = getScreenHeight(context);
        }
        return screenHeight;
    }

    public static int getStatusBarHeight(Context context) {
        return StatusBarHelper.getStatusbarHeight(context);
    }
}
