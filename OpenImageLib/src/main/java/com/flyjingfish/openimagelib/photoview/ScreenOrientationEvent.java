package com.flyjingfish.openimagelib.photoview;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Handler;
import android.os.Looper;

class ScreenOrientationEvent {
    private OnOrientationListener onOrientationListener;
    private DisplayManager mDisplayManager;

    public ScreenOrientationEvent(Context context) {
        mDisplayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
    }

    DisplayManager.DisplayListener mDisplayListener = new DisplayManager.DisplayListener() {
        @Override
        public void onDisplayAdded(int displayId) {
            android.util.Log.i("mDisplayListener", "Display #" + displayId + " added.");
        }

        @Override
        public void onDisplayChanged(int displayId) {
            android.util.Log.i("mDisplayListener", "Display #" + displayId + " changed.");
            if (onOrientationListener != null){
                onOrientationListener.onOrientationChanged();
            }
        }

        @Override
        public void onDisplayRemoved(int displayId) {
            android.util.Log.i("mDisplayListener", "Display #" + displayId + " removed.");
        }
    };


    public interface OnOrientationListener{
        void onOrientationChanged();
    }

    public void registerDisplayListener(OnOrientationListener onOrientationListener){
        mDisplayManager.registerDisplayListener(mDisplayListener, new Handler(Looper.getMainLooper()));
        this.onOrientationListener = onOrientationListener;
    }

    public void unRegisterDisplayListener(){
        mDisplayManager.unregisterDisplayListener(mDisplayListener);
        onOrientationListener = null;
    }

}
