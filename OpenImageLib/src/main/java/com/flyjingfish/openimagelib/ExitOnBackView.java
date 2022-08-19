package com.flyjingfish.openimagelib;

import android.view.View;

class ExitOnBackView implements ImageLoadUtils.OnBackView {
    private final View transitionView;
    private final float transitionViewStartAlpha;
    private final int transitionViewStartVisibility;
    private boolean isTouchClose;

    public ExitOnBackView(View transitionView) {
        this.transitionView = transitionView;
        if (transitionView != null){
            this.transitionViewStartAlpha = transitionView.getAlpha();
            this.transitionViewStartVisibility = transitionView.getVisibility();
        }else {
            this.transitionViewStartAlpha = 1f;
            this.transitionViewStartVisibility = View.VISIBLE;
        }
    }

    @Override
    public boolean onBack(int showPosition) {
        return false;
    }

    @Override
    public void onTouchClose(boolean isTouchClose) {
        this.isTouchClose = isTouchClose;
    }

    @Override
    public void onScrollPos(int pos) {

    }

    @Override
    public void onStartTouchScale(int showPosition) {
        if (transitionView != null){
            transitionView.setVisibility(transitionViewStartVisibility);
            transitionView.setAlpha(transitionViewStartAlpha);
        }
    }
}