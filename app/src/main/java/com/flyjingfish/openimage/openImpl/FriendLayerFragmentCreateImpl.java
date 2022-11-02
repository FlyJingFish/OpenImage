package com.flyjingfish.openimage.openImpl;

import androidx.fragment.app.Fragment;

import com.flyjingfish.openimagelib.listener.UpperLayerFragmentCreate;

public class FriendLayerFragmentCreateImpl implements UpperLayerFragmentCreate {
    @Override
    public Fragment createVideoFragment() {
        return new FriendsFragment();
    }
}
