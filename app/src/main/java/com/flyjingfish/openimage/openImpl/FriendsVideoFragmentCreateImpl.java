package com.flyjingfish.openimage.openImpl;

import com.flyjingfish.openimagelib.BaseFragment;
import com.flyjingfish.openimagelib.listener.VideoFragmentCreate;

public class FriendsVideoFragmentCreateImpl implements VideoFragmentCreate {
    @Override
    public BaseFragment createVideoFragment() {
        return new FriendsPlayerFragment();
    }
}
