package com.flyjingfish.openimagelib;

import android.os.SystemClock;

class OpenImageDetailIdUtil {

    private static long mCurrentId = timeGen();

    private OpenImageDetailIdUtil() {
    }

    public synchronized static long nextId() {
        return mCurrentId++;
    }
 
    private static long timeGen(){
        return SystemClock.uptimeMillis();
    }
}