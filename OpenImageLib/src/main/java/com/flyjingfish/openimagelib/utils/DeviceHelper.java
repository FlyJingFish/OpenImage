package com.flyjingfish.openimagelib.utils;


import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class DeviceHelper {
    private final static String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_FLYME_VERSION_NAME = "ro.build.display.id";
    private final static String FLYME = "flyme";
    private final static String MEIZUBOARD[] = {"m9", "M9", "mx", "MX"};

    private static String sMiuiVersionName;
    private static String sFlymeVersionName;
    private static final String BRAND = Build.BRAND.toLowerCase();
    private static boolean isInfoReaded = false;

    public static void close(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void checkReadInfo() {
        if (isInfoReaded) {
            return;
        }
        isInfoReaded = true;
        Properties properties = new Properties();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // android 8.0，读取 /system/uild.prop 会报 permission denied
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(new File(Environment.getRootDirectory(), "build.prop"));
                properties.load(fileInputStream);
            } catch (Exception e) {
            } finally {
                close(fileInputStream);
            }
        }

        Class<?> clzSystemProperties = null;
        try {
            clzSystemProperties = Class.forName("android.os.SystemProperties");
            Method getMethod = clzSystemProperties.getDeclaredMethod("get", String.class);
            // miui
            sMiuiVersionName = getLowerCaseName(properties, getMethod, KEY_MIUI_VERSION_NAME);
            //flyme
            sFlymeVersionName = getLowerCaseName(properties, getMethod, KEY_FLYME_VERSION_NAME);
        } catch (Exception e) {
        }
    }

    /**
     * 判断是否是flyme系统
     */
    private static OnceReadValue<Void, Boolean> isFlymeValue = new OnceReadValue<Void, Boolean>() {
        @Override
        protected Boolean read(Void param) {
            checkReadInfo();
            return !TextUtils.isEmpty(sFlymeVersionName) && sFlymeVersionName.contains(FLYME);
        }
    };

    public static boolean isFlyme() {
        return isFlymeValue.get(null);
    }

    /**
     * 判断是否是MIUI系统
     */
    public static boolean isMIUI() {
        checkReadInfo();
        return !TextUtils.isEmpty(sMiuiVersionName);
    }

    public static boolean isMIUIV5() {
        checkReadInfo();
        return "v5".equals(sMiuiVersionName);
    }

    public static boolean isMIUIV6() {
        checkReadInfo();
        return "v6".equals(sMiuiVersionName);
    }

    public static boolean isMIUIV7() {
        checkReadInfo();
        return "v7".equals(sMiuiVersionName);
    }

    public static boolean isMIUIV8() {
        checkReadInfo();
        return "v8".equals(sMiuiVersionName);
    }

    public static boolean isMIUIV9() {
        checkReadInfo();
        return "v9".equals(sMiuiVersionName);
    }

    public static boolean isFlymeLowerThan(int majorVersion) {
        return isFlymeLowerThan(majorVersion, 0, 0);
    }

    public static boolean isFlymeLowerThan(int majorVersion, int minorVersion, int patchVersion) {
        checkReadInfo();
        boolean isLower = false;
        if (sFlymeVersionName != null && !sFlymeVersionName.equals("")) {
            try {
                Pattern pattern = Pattern.compile("(\\d+\\.){2}\\d");
                Matcher matcher = pattern.matcher(sFlymeVersionName);
                if (matcher.find()) {
                    String versionString = matcher.group();
                    if (versionString.length() > 0) {
                        String[] version = versionString.split("\\.");
                        if (version.length >= 1) {
                            if (Integer.parseInt(version[0]) < majorVersion) {
                                isLower = true;
                            }
                        }

                        if (version.length >= 2 && minorVersion > 0) {
                            if (Integer.parseInt(version[1]) < majorVersion) {
                                isLower = true;
                            }
                        }

                        if (version.length >= 3 && patchVersion > 0) {
                            if (Integer.parseInt(version[2]) < majorVersion) {
                                isLower = true;
                            }
                        }
                    }
                }
            } catch (Throwable ignore) {

            }
        }
        return isMeizu() && isLower;
    }


    private static OnceReadValue<Void, Boolean> isMeizuValue = new OnceReadValue<Void, Boolean>() {
        @Override
        protected Boolean read(Void param) {
            checkReadInfo();
            return isPhone(MEIZUBOARD) || isFlyme();
        }
    };

    public static boolean isMeizu() {
        return isMeizuValue.get(null);
    }

    private static OnceReadValue<Void, Boolean> isEssentialPhoneValue = new OnceReadValue<Void, Boolean>() {
        @Override
        protected Boolean read(Void param) {
            return BRAND.contains("essential");
        }
    };

    public static boolean isEssentialPhone() {
        return isEssentialPhoneValue.get(null);
    }

    private static boolean isPhone(String[] boards) {
        checkReadInfo();
        final String board = android.os.Build.BOARD;
        if (board == null) {
            return false;
        }
        for (String board1 : boards) {
            if (board.equals(board1)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    private static String getLowerCaseName(Properties p, Method get, String key) {
        String name = p.getProperty(key);
        if (name == null) {
            try {
                name = (String) get.invoke(null, key);
            } catch (Exception ignored) {
            }
        }
        if (name != null) name = name.toLowerCase();
        return name;
    }
}