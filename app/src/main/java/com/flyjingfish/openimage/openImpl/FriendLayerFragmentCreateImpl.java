package com.flyjingfish.openimage.openImpl;


import com.flyjingfish.openimagelib.BaseInnerFragment;
import com.flyjingfish.openimagelib.listener.UpperLayerFragmentCreate;

public class FriendLayerFragmentCreateImpl implements UpperLayerFragmentCreate {
    @Override
    public BaseInnerFragment createVideoFragment() {
        return new FriendsFragment();
    }
}
