package com.flyjingfish.openimagelib.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences 工具类
 * 使用示例：
 * SPUtils.put(context, "key", "value");
 * String value = SPUtils.getString(context, "key", "default");
 */
public class SPUtils {

    private static final String SP_NAME = "open_image_prefs";

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    /** ================== 保存数据 ================== **/

    public static void put(Context context, String key, String value) {
        getPreferences(context).edit().putString(key, value).apply();
    }

    public static void put(Context context, String key, int value) {
        getPreferences(context).edit().putInt(key, value).apply();
    }

    public static void put(Context context, String key, long value) {
        getPreferences(context).edit().putLong(key, value).apply();
    }

    public static void put(Context context, String key, float value) {
        getPreferences(context).edit().putFloat(key, value).apply();
    }

    public static void put(Context context, String key, boolean value) {
        getPreferences(context).edit().putBoolean(key, value).apply();
    }

    /** ================== 获取数据 ================== **/

    public static String getString(Context context, String key, String defaultValue) {
        return getPreferences(context).getString(key, defaultValue);
    }

    public static int getInt(Context context, String key, int defaultValue) {
        return getPreferences(context).getInt(key, defaultValue);
    }

    public static long getLong(Context context, String key, long defaultValue) {
        return getPreferences(context).getLong(key, defaultValue);
    }

    public static float getFloat(Context context, String key, float defaultValue) {
        return getPreferences(context).getFloat(key, defaultValue);
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        return getPreferences(context).getBoolean(key, defaultValue);
    }

    /** ================== 删除 / 清空 ================== **/

    public static void remove(Context context, String key) {
        getPreferences(context).edit().remove(key).apply();
    }

    public static void clear(Context context) {
        getPreferences(context).edit().clear().apply();
    }
}
