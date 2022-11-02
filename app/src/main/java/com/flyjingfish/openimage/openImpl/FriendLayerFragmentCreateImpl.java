package com.flyjingfish.openimage.openImpl;


import com.flyjingfish.openimagelib.BaseUpperLayerFragment;
import com.flyjingfish.openimagelib.listener.UpperLayerFragmentCreate;

public class FriendLayerFragmentCreateImpl implements UpperLayerFragmentCreate {
    @Override
    public BaseUpperLayerFragment createVideoFragment() {
        return new FriendsFragment();
    }
}
