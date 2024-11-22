package com.flyjingfish.openimagelib.photoview;

import android.view.View;

/**
 * Callback when the user tapped outside of the photo
 */
public interface OnOutsidePhotoTapListener {

    /**
     * The outside of the photo has been tapped
     */
    void onOutsidePhotoTap(View imageView);
}
