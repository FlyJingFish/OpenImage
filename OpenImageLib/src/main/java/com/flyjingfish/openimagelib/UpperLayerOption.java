package com.flyjingfish.openimagelib;

import com.flyjingfish.openimagelib.listener.UpperLayerFragmentCreate;

class UpperLayerOption {
    private UpperLayerFragmentCreate upperLayerFragmentCreate;
    private boolean followTouch;
    private boolean touchingHide;

    public UpperLayerOption(UpperLayerFragmentCreate upperLayerFragmentCreate, boolean followTouch) {
        this.upperLayerFragmentCreate = upperLayerFragmentCreate;
        this.followTouch = followTouch;
    }

    public UpperLayerOption(UpperLayerFragmentCreate upperLayerFragmentCreate, boolean followTouch, boolean touchingHide) {
        this.upperLayerFragmentCreate = upperLayerFragmentCreate;
        this.followTouch = followTouch;
        this.touchingHide = touchingHide;
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

    public boolean isTouchingHide() {
        return touchingHide;
    }

    public void setTouchingHide(boolean touchingHide) {
        this.touchingHide = touchingHide;
    }
}
