package com.flyjingfish.openimagefulllib;

import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.ImageView;

class Util {

    static void checkZoomLevels(float minZoom, float midZoom,
                                float maxZoom) {
        if (minZoom >= midZoom) {
            throw new IllegalArgumentException(
                    "Minimum zoom has to be less than Medium zoom. Call setMinimumZoom() with a more appropriate value");
        } else if (midZoom >= maxZoom) {
            throw new IllegalArgumentException(
                    "Medium zoom has to be less than Maximum zoom. Call setMaximumZoom() with a more appropriate value");
        }
    }

    static boolean hasDrawable(ScaleRelativeLayout imageView) {
        return imageView.getDrawable() != null;
    }

    static boolean isSupportedScaleType(final ImageView.ScaleType scaleType) {
        if (scaleType == null) {
            return false;
        }
        switch (scaleType) {
            case MATRIX:
                throw new IllegalStateException("Matrix scale type is not supported");
        }
        return true;
    }

    static int getPointerIndex(int action) {
        return (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
    }

    static GSYVideoPlayer getVideoPlayer(ViewParent parent){
        if (parent instanceof GSYVideoPlayer){
            return (GSYVideoPlayer) parent;
        }else {
            return getVideoPlayer(parent.getParent());
        }
    }

}
