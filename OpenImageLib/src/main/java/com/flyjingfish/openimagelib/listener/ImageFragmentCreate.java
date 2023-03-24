package com.flyjingfish.openimagelib.listener;

import android.view.View;

import com.flyjingfish.openimagelib.BaseImageFragment;

public interface ImageFragmentCreate {
    BaseImageFragment<? extends View> createImageFragment();
}
