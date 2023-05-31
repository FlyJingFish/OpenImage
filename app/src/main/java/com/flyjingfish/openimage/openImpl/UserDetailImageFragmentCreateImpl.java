package com.flyjingfish.openimage.openImpl;

import android.view.View;

import com.flyjingfish.openimagelib.BaseImageFragment;
import com.flyjingfish.openimagelib.listener.ImageFragmentCreate;
import com.flyjingfish.openimagelib.listener.VideoFragmentCreate;

public class UserDetailImageFragmentCreateImpl implements ImageFragmentCreate {

    @Override
    public BaseImageFragment<? extends View> createImageFragment() {
        return new UserDetailImageFragment();
    }
}
