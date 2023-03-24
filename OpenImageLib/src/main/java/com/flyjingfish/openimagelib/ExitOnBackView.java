package com.flyjingfish.openimagelib;

import android.view.View;
import android.widget.ImageView;

class ExitOnBackView implements ImageLoadUtils.OnBackView {
    private final View transitionView;
    private final float transitionViewStartAlpha;
    private final int transitionViewStartVisibility;
    protected boolean isTouchClose;

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
    public ShareExitViewBean onBack(int showPosition) {
        return new ShareExitViewBean(BackViewType.NO_SHARE,null);
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

    @Override
    public void onEndTouchScale(int showPosition) {

    }

    public static class ShareExitViewBean {
        protected BackViewType backViewType;
        protected ImageView shareExitView;
        protected boolean isClipSrcImageView = true;

        public ShareExitViewBean(BackViewType backViewType, ImageView shareExitView) {
            this.backViewType = backViewType;
            this.shareExitView = shareExitView;
        }
    }
}
