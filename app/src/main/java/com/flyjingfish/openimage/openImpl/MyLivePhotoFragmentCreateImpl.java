package com.flyjingfish.openimage.openImpl;

import android.view.View;

import com.flyjingfish.openimagelib.BaseImageFragment;
import com.flyjingfish.openimagelib.listener.LivePhotoFragmentCreate;

public class MyLivePhotoFragmentCreateImpl implements LivePhotoFragmentCreate {
    @Override
    public BaseImageFragment<? extends View> createLivePhotoFragment() {
        return new MyLivePhotoFragment();
    }
}
