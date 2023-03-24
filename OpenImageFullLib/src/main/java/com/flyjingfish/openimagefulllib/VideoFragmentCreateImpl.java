package com.flyjingfish.openimagefulllib;

import android.view.View;

import com.flyjingfish.openimagelib.BaseImageFragment;
import com.flyjingfish.openimagelib.listener.VideoFragmentCreate;

public class VideoFragmentCreateImpl implements VideoFragmentCreate {
    @Override
    public BaseImageFragment<? extends View> createVideoFragment() {
        return new VideoPlayerFragment();
    }
}
