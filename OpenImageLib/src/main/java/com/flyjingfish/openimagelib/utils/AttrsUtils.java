package com.flyjingfish.openimagelib.utils;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.TypedValue;

public class AttrsUtils {

    public static int getTypeValueColor(Context context, int attr) {
        return getTypeValueColor(context, attr,0);
    }

    public static int getTypeValueColor(Context context, int attr,int defaultValue) {
        try {
            TypedValue typedValue = new TypedValue();
            int[] attribute = new int[]{attr};
            TypedArray array = context.obtainStyledAttributes(typedValue.resourceId, attribute);
            int color = array.getColor(0, defaultValue);
            array.recycle();
            return color;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    public static int getTypeValueInt(Context context, int attr) {
        try {
            TypedValue typedValue = new TypedValue();
            int[] attribute = new int[]{attr};
            TypedArray array = context.obtainStyledAttributes(typedValue.resourceId, attribute);
            int intValue = array.getInt(0, 0);
            array.recycle();
            return intValue;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static float getTypeValueDimension(Context context, int attr) {
        return getTypeValueDimension(context, attr,0);
    }

    public static float getTypeValueDimension(Context context, int attr,float defaultValue) {
        try {
            TypedValue typedValue = new TypedValue();
            int[] attribute = new int[]{attr};
            TypedArray array = context.obtainStyledAttributes(typedValue.resourceId, attribute);
            float intValue = array.getDimension(0, defaultValue);
            array.recycle();
            return intValue;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    public static CharSequence getTypeValueText(Context context, int attr) {
        try {
            TypedValue typedValue = new TypedValue();
            int[] attribute = new int[]{attr};
            TypedArray array = context.obtainStyledAttributes(typedValue.resourceId, attribute);
            CharSequence strValue = array.getText(0);
            array.recycle();
            return strValue;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean getTypeValueBoolean(Context context, int attr) {
        try {
            TypedValue typedValue = new TypedValue();
            int[] attribute = new int[]{attr};
            TypedArray array = context.obtainStyledAttributes(typedValue.resourceId, attribute);
            boolean statusFont = array.getBoolean(0, false);
            array.recycle();
            return statusFont;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int getTypeValueResourceId(Context context, int attr) {
        try {
            TypedValue typedValue = new TypedValue();
            int[] attribute = new int[]{attr};
            TypedArray array = context.obtainStyledAttributes(typedValue.resourceId, attribute);
            int drawable = array.getResourceId(0,0);
            array.recycle();
            return drawable;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
