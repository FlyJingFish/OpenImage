package com.flyjingfish.openimagefulllib;

import android.view.View;

import com.flyjingfish.openimagelib.BaseImageFragment;
import com.flyjingfish.openimagelib.listener.LivePhotoFragmentCreate;

public class LivePhotoFragmentCreateImpl implements LivePhotoFragmentCreate {
    @Override
    public BaseImageFragment<? extends View> createLivePhotoFragment() {
        return new LivePhotoPlayerFragment();
    }
}
