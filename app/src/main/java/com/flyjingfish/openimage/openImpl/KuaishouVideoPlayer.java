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
import com.flyjingfish.openimagefulllib.OpenImageCoverVideoPlayer;

import moe.codeest.enviews.ENPlayView;

public class KuaishouVideoPlayer extends OpenImageCoverVideoPlayer {


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
        seekBar = findViewById(R.id.progress2);
        startBtn = findViewById(R.id.start_btn);
        changeUiToNormal();
        seekBar.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_MOVE){
                seekBar.setAlpha(1f);
            }
            return false;
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
    protected void clickStartIcon() {
        super.clickStartIcon();
        //以下是为了缓冲时还能暂停
        if (mCurrentState == CURRENT_STATE_PREPAREING || mCurrentState == CURRENT_STATE_PLAYING_BUFFERING_START){
            if (mLoadingProgressBar.getVisibility() == VISIBLE){
                onVideoPause();
                setViewShowState(startBtn,VISIBLE);
                setViewShowState(mLoadingProgressBar,GONE);
                if (mGsyStateUiListener != null) {
                    mGsyStateUiListener.onStateChanged(CURRENT_STATE_PAUSE);
                }
            }else {
                onVideoResume();
                setViewShowState(startBtn,GONE);
                setViewShowState(mLoadingProgressBar,VISIBLE);
                if (mGsyStateUiListener != null) {
                    mGsyStateUiListener.onStateChanged(mCurrentState);
                }
            }
        }
    }

    @Override
    protected void changeUiToNormal() {
        super.changeUiToNormal();
        setViewShowState(startBtn,GONE);
    }

    @Override
    protected void changeUiToPlayingShow() {
        super.changeUiToPlayingShow();
        if (!mPauseBeforePrepared){
            setViewShowState(startBtn,GONE);
        }
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
        if (view == mBottomProgressBar){
            mBottomProgressBar.setVisibility(VISIBLE);
        }
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
