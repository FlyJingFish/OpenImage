package com.flyjingfish.openimage.openImpl;

import android.view.View;

import com.flyjingfish.openimagelib.BaseImageFragment;
import com.flyjingfish.openimagelib.listener.VideoFragmentCreate;

public class KuaishouVideoFragmentCreateImpl implements VideoFragmentCreate {
    @Override
    public BaseImageFragment<? extends View> createVideoFragment() {
        return new KuaishouPlayerFragment();
    }
}
