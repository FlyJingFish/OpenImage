package com.flyjingfish.openimage.openImpl;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.NonNull;

import com.flyjingfish.openimage.R;
import com.flyjingfish.openimagefulllib.GSYVideoPlayer;
import com.flyjingfish.openimagelib.photoview.PhotoView;

import moe.codeest.enviews.ENPlayView;

public class KuaishouVideoPlayer extends GSYVideoPlayer {


    private PhotoView coverImageView;
    private PhotoView smallCoverImageView;
    private ENPlayView startBtn;
    private SeekBar seekBar;
    private final Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            seekBar.setAlpha(0f);
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacksAndMessages(null);
    }

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
        seekBar = findViewById(R.id.progress2);
        startBtn = findViewById(R.id.start_btn);
        changeUiToNormal();
        seekBar.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE){
                    seekBar.setAlpha(1f);
                }
                return false;
            }
        });
        seekBar.setOnSeekBarChangeListener(this);
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
        handler.removeMessages(0);
        handler.sendEmptyMessageDelayed(0,1000);
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
        if (view == mBottomProgressBar){
            mBottomProgressBar.setVisibility(VISIBLE);
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

    @Override
    protected void setProgressAndTime(long progress, long secProgress, long currentTime, long totalTime, boolean forceChange) {
        super.setProgressAndTime(progress, secProgress, currentTime, totalTime, forceChange);
        if (!mTouchingProgressBar && seekBar != null) {
            if (progress != 0 || forceChange) seekBar.setProgress((int)progress);
        }
    }

    @Override
    protected void setSecondaryProgress(long secProgress) {
        super.setSecondaryProgress(secProgress);
        if (seekBar != null) {
            if (secProgress != 0 && !getGSYVideoManager().isCacheFile()) {
                seekBar.setSecondaryProgress((int)secProgress);
            }
        }
    }

    @Override
    protected void resetProgressAndTime() {
        super.resetProgressAndTime();
        if (seekBar != null){
            seekBar.setProgress(0);
            seekBar.setSecondaryProgress(0);
        }
    }

    @Override
    protected void loopSetProgressAndTime() {
        super.loopSetProgressAndTime();
        if (seekBar != null){
            seekBar.setProgress(0);
            seekBar.setSecondaryProgress(0);
        }
    }
}
