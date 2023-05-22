package com.flyjingfish.openimagelib;


import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.view.View;

class AttrsUtils {

    public static int getTypeValueColor(Context context, int themeRes, int attr) {
        return getTypeValueColor(context, themeRes, attr, 0);
    }

    public static int getTypeValueColor(Context context, int themeRes, int attr, int defaultValue) {
        try {
            int[] attribute = new int[]{attr};
            TypedArray array = context.obtainStyledAttributes(themeRes, attribute);
            int color = array.getColor(0, defaultValue);
            array.recycle();
            return color;
        } catch (Exception ignored) {
        }
        return defaultValue;
    }

    public static int getTypeValueInt(Context context, int themeRes, int attr, int defaultValue) {
        try {
            int[] attribute = new int[]{attr};
            TypedArray array = context.obtainStyledAttributes(themeRes, attribute);
            int intValue = array.getInt(0, defaultValue);
            array.recycle();
            return intValue;
        } catch (Exception ignored) {
        }
        return defaultValue;
    }

    public static int getTypeValueInt(Context context, int themeRes, int attr) {
        return getTypeValueInt(context, themeRes, attr,0);
    }

    public static float getTypeValueDimension(Context context, int themeRes, int attr) {
        return getTypeValueDimension(context, themeRes, attr, 0);
    }

    public static float getTypeValueDimension(Context context, int themeRes, int attr, float defaultValue) {
        try {
            int[] attribute = new int[]{attr};
            TypedArray array = context.obtainStyledAttributes(themeRes, attribute);
            float intValue = array.getDimension(0, defaultValue);
            array.recycle();
            return intValue;
        } catch (Exception ignored) {
        }
        return defaultValue;
    }

    public static CharSequence getTypeValueText(Context context, int themeRes, int attr) {
        try {
            int[] attribute = new int[]{attr};
            TypedArray array = context.obtainStyledAttributes(themeRes, attribute);
            CharSequence strValue = array.getText(0);
            array.recycle();
            return strValue;
        } catch (Exception ignored) {
        }
        return null;
    }

    public static boolean getTypeValueBoolean(Context context, int themeRes, int attr) {
        return getTypeValueBoolean(context, themeRes, attr, false);
    }

    public static boolean getTypeValueBoolean(Context context, int themeRes, int attr, boolean defaultValue) {
        try {
            int[] attribute = new int[]{attr};
            TypedArray array = context.obtainStyledAttributes(themeRes, attribute);
            boolean statusFont = array.getBoolean(0, defaultValue);
            array.recycle();
            return statusFont;
        } catch (Exception ignored) {
        }
        return defaultValue;
    }

    public static int getTypeValueResourceId(Context context, int themeRes, int attr) {
        try {
            int[] attribute = new int[]{attr};
            TypedArray array = context.obtainStyledAttributes(themeRes, attribute);
            int drawable = array.getResourceId(0, 0);
            array.recycle();
            return drawable;
        } catch (Exception ignored) {
        }
        return 0;
    }

    public static Drawable getTypeValueDrawable(Context context, int themeRes, int attr) {
        try {
            int[] attribute = new int[]{attr};
            TypedArray array = context.obtainStyledAttributes(themeRes, attribute);
            Drawable drawable = array.getDrawable(0);
            array.recycle();
            return drawable;
        } catch (Exception ignored) {
        }
        return null;
    }

    public static void setBackgroundResourceOrColor(Context context, int themeRes, int attr, View view) {
        Drawable drawable = getTypeValueDrawable(context, themeRes, attr);
        if (drawable != null) {
            view.setBackground(drawable);
        }
    }

    public static ColorStateList getTypeValueColorStateList(Context context, int themeRes, int attr) {
        try {
            int[] attribute = new int[]{attr};
            TypedArray array = context.obtainStyledAttributes(themeRes, attribute);
            ColorStateList color = array.getColorStateList(0);
            array.recycle();
            return color;
        } catch (Exception ignored) {
        }
        return null;
    }
}
