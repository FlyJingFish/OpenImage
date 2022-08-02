package com.flyjingfish.openimage.videoplayer;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.flyjingfish.openimage.R;
import com.flyjingfish.openimagelib.photoview.PhotoView;
import com.flyjingfish.openimagelib.utils.ScreenUtils;


public class MyVideoPlayer extends GSYVideoPlayer {


    private PhotoView coverImageView;
    private PhotoView smallCoverImageView;

    public MyVideoPlayer(Context context) {
        super(context);
    }

    public MyVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        coverImageView = new PhotoView(context);
        coverImageView.setId(R.id.iv_video_player_cover);
        mThumbImageView = coverImageView;
        resolveThumbImage(mThumbImageView);
        smallCoverImageView = findViewById(R.id.iv_small_cover);
        mTopContainer.setPadding(0, ScreenUtils.getStatusBarHeight(context), 0, 0);
        changeUiToNormal();
    }

    public void playUrl(String videoUrl) {
        setUp(videoUrl, true, "");
    }

    public PhotoView getCoverImageView() {
        return coverImageView;
    }

    public PhotoView getSmallCoverImageView() {
        return smallCoverImageView;
    }

    public View getLoadingView() {
        return mLoadingProgressBar;
    }


    @Override
    public int getLayoutId() {
        return R.layout.layout_videos;
    }

//    public void goneAllWidget() {
//        cancelDismissControlViewTimer();
//        mPostDismiss = true;
//        if (mCurrentState != CURRENT_STATE_NORMAL
//                && mCurrentState != CURRENT_STATE_ERROR
//                && mCurrentState != CURRENT_STATE_AUTO_COMPLETE) {
//            if (getActivityContext() != null) {
//                hideAllWidget();
//                setViewShowState(mLockScreen, GONE);
//                if (mHideKey && mIfCurrentIsFullscreen && mShowVKey) {
//                    hideNavKey(mContext);
//                }
//            }
//            if (mPostDismiss) {
//                startDismissControlViewTimer();
//            }
//        }
//    }

    public void goneAllWidget() {
//        cancelDismissControlViewTimer();
//        mPostDismiss = true;
        hideAllWidget();
//        startDismissControlViewTimer();
    }

    public void showAllWidget() {
        if (mCurrentState == CURRENT_STATE_NORMAL){
            changeUiToNormal();
        }else if (mCurrentState == CURRENT_STATE_PAUSE){
            changeUiToPauseShow();
        }else if (mCurrentState == CURRENT_STATE_AUTO_COMPLETE){
            changeUiToCompleteShow();
        }else if (mCurrentState == CURRENT_STATE_ERROR){
            changeUiToError();
        }
    }


    @Override
    protected void changeUiToPreparingShow() {
        super.changeUiToPreparingShow();
        setViewShowState(mThumbImageViewLayout, VISIBLE);
    }
}
