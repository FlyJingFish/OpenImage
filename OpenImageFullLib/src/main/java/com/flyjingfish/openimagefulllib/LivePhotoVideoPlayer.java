package com.flyjingfish.openimagefulllib;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;


public class LivePhotoVideoPlayer extends ScaleOpenImageVideoPlayer {

    public LivePhotoVideoPlayer(Context context) {
        super(context);
    }

    public LivePhotoVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getLayoutId() {
        return R.layout.open_image_layout_live_photo;
    }


//    @Override
//    protected void touchSurfaceMove(float deltaX, float deltaY, float y) {
//        super.touchSurfaceMove(deltaX, deltaY, y);
//    }

    boolean notShowLoading = false;

    @Override
    protected void setViewShowState(View view, int visibility) {
        if (notShowLoading && view == mLoadingProgressBar){
            return;
        }
        super.setViewShowState(view, visibility);
    }

    @Override
    protected void setStateAndUi(int state) {
        super.setStateAndUi(state);
        if (mCurrentState == CURRENT_STATE_AUTO_COMPLETE){
            notShowLoading = true;
        }
    }
}
