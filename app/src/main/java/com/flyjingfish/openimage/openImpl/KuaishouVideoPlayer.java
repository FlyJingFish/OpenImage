package com.flyjingfish.openimage.openImpl;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.flyjingfish.openimage.R;
import com.flyjingfish.openimagefulllib.GSYVideoPlayer;
import com.flyjingfish.openimagelib.photoview.PhotoView;

import moe.codeest.enviews.ENPlayView;

public class KuaishouVideoPlayer extends GSYVideoPlayer {


    private PhotoView coverImageView;
    private PhotoView smallCoverImageView;
    private ENPlayView startBtn;

    public KuaishouVideoPlayer(Context context) {
        super(context);
    }

    public KuaishouVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        coverImageView = new PhotoView(context);
        coverImageView.setId(R.id.iv_video_player_cover);
        mThumbImageView = coverImageView;
        resolveThumbImage(mThumbImageView);
        smallCoverImageView = findViewById(R.id.iv_small_cover);
        startBtn = findViewById(R.id.start_btn);
        changeUiToNormal();
    }


    @Override
    protected void touchSurfaceMove(float deltaX, float deltaY, float y) {
//        super.touchSurfaceMove(deltaX, deltaY, y);
    }

    @Override
    protected void touchSurfaceUp() {
        if (!mChangePosition) {
            if (onSurfaceTouchListener != null && onSurfaceTouchListener.onTouchUp()){
                return;
            }
            clickStartIcon();
        }
        super.touchSurfaceUp();
    }

    public void playPause(){
        clickStartIcon();
    }

    @Override
    protected void changeUiToNormal() {
        super.changeUiToNormal();
        setViewShowState(startBtn,GONE);
    }

    @Override
    protected void changeUiToPlayingShow() {
        super.changeUiToPlayingShow();
        setViewShowState(startBtn,GONE);
    }

    @Override
    protected void changeUiToPauseShow() {
        super.changeUiToPauseShow();
        setViewShowState(startBtn,VISIBLE);
    }

    @Override
    protected void setViewShowState(View view, int visibility) {
        super.setViewShowState(view, visibility);
        if (view == mThumbImageViewLayout && smallCoverImageView != null){
            smallCoverImageView.setVisibility(visibility);
        }
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
        return R.layout.layout_kuaishou_player;
    }

    public interface OnSurfaceTouchListener{
        boolean onTouchUp();
    }

    private OnSurfaceTouchListener onSurfaceTouchListener;

    public OnSurfaceTouchListener getOnSurfaceTouchListener() {
        return onSurfaceTouchListener;
    }

    public void setOnSurfaceTouchListener(OnSurfaceTouchListener onSurfaceTouchListener) {
        this.onSurfaceTouchListener = onSurfaceTouchListener;
    }
}
