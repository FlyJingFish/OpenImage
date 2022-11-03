package com.flyjingfish.openimagelib;

import com.flyjingfish.openimagelib.listener.UpperLayerFragmentCreate;

class UpperLayerOption {
    private UpperLayerFragmentCreate upperLayerFragmentCreate;
    private boolean followTouch;

    public UpperLayerOption(UpperLayerFragmentCreate upperLayerFragmentCreate, boolean followTouch) {
        this.upperLayerFragmentCreate = upperLayerFragmentCreate;
        this.followTouch = followTouch;
    }

    public UpperLayerFragmentCreate getUpperLayerFragmentCreate() {
        return upperLayerFragmentCreate;
    }

    public void setUpperLayerFragmentCreate(UpperLayerFragmentCreate upperLayerFragmentCreate) {
        this.upperLayerFragmentCreate = upperLayerFragmentCreate;
    }

    public boolean isFollowTouch() {
        return followTouch;
    }

    public void setFollowTouch(boolean followTouch) {
        this.followTouch = followTouch;
    }
}
