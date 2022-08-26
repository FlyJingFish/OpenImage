package com.flyjingfish.openimagelib.utils;


import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.view.Window;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;


public class ActivityCompatHelper {
    private static final int MIN_FRAGMENT_COUNT = 1;

    public static boolean isDestroy(Activity activity) {
        if (activity == null) {
            return true;
        }
        return activity.isFinishing() || activity.isDestroyed();
    }


    /**
     * 验证Fragment是否已存在
     *
     * @param fragmentTag Fragment标签
     * @return
     */
    public static boolean checkFragmentNonExits(FragmentActivity activity, String fragmentTag) {
        if (isDestroy(activity)) {
            return false;
        }
        Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag(fragmentTag);
        return fragment == null;
    }


    public static boolean assertValidRequest(Context context) {
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            return !isDestroy(activity);
        } else if (context instanceof ContextWrapper) {
            ContextWrapper contextWrapper = (ContextWrapper) context;
            if (contextWrapper.getBaseContext() instanceof Activity) {
                Activity activity = (Activity) contextWrapper.getBaseContext();
                return !isDestroy(activity);
            }
        }
        return true;
    }

    /**
     * 验证当前是否是根Fragment
     *
     * @param activity
     * @return
     */
    public static boolean checkRootFragment(FragmentActivity activity) {
        if (ActivityCompatHelper.isDestroy(activity)) {
            return false;
        }
        return activity.getSupportFragmentManager().getBackStackEntryCount() == MIN_FRAGMENT_COUNT;
    }

    public static Window getWindow(Context context) {
        Activity activity = getActivity(context);
        if (activity != null){
            return activity.getWindow();
        }
        return null;
    }

    public static Activity getActivity(Context context) {
        return (Activity) context;
    }

    public static FragmentActivity getFragmentActivity(Context context) {
        if (context instanceof FragmentActivity) {
            return (FragmentActivity) context;
        }
        return null;
    }
}
