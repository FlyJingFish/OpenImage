package com.flyjingfish.openimagefulllib;

import android.content.Context;
import android.content.res.TypedArray;
import android.media.AudioManager;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.flyjingfish.openimagelib.photoview.PhotoView;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoViewBridge;

import java.util.UUID;

public class GSYVideoPlayer extends StandardGSYVideoPlayer {

    private String pageContextKey;
    private String uUKey;
    private boolean mute;//是否需要静音
    protected int showType = GSYVideoType.getShowType();

    protected LifecycleOwner lifecycleOwner;
    boolean isUserInputPause = false;
    boolean isUserInput = false;
    protected OpenImageGSYVideoHelper gsyVideoHelper;
    private int mOldState;

    public GSYVideoPlayer(Context context) {
        this(context, null);
    }

    public GSYVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    public void setLifecycleOwner(LifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;
    }

    long touchSurfaceDownTime;

    @Override
    protected void touchSurfaceDown(float x, float y) {
        super.touchSurfaceDown(x, y);
        touchSurfaceDownTime = SystemClock.uptimeMillis();
    }

    @Override
    protected void touchSurfaceUp() {
        super.touchSurfaceUp();
        long time = SystemClock.uptimeMillis();
        if (time - touchSurfaceDownTime < 500){
            if (onClickListener != null){
                onClickListener.onClick(getTextureViewContainer());
            }
        }
    }
    private OnClickListener onClickListener;
    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        if (getTextureViewContainer() != null){
            onClickListener = l;
        }else {
            super.setOnClickListener(l);
        }
    }

    @Override
    public void setOnLongClickListener(@Nullable OnLongClickListener l) {
        if (getTextureViewContainer() != null){
            getTextureViewContainer().setOnLongClickListener(l);
        }else {
            super.setOnLongClickListener(l);
        }
    }

    private static class MyOnAudioFocusChangeListener implements AudioManager.OnAudioFocusChangeListener{
        @Override
        public void onAudioFocusChange(int focusChange) {

        }
    }

    private final AudioManager.OnAudioFocusChangeListener EMPTY = new MyOnAudioFocusChangeListener();
    private final AudioManager.OnAudioFocusChangeListener onStandardAudioFocusChangeListener = onAudioFocusChangeListener;

    @Override
    protected void init(Context context) {
        super.init(context);
        onAudioFocusChangeListener = EMPTY;
    }

    public void requestAudioFocus(){
        abandonAudioFocus();
        if (mAudioManager != null && !mReleaseWhenLossAudio) {
            onAudioFocusChangeListener = onStandardAudioFocusChangeListener;
            mAudioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        }
    }

    public void abandonAudioFocus(){
        if (mAudioManager != null) {
            mAudioManager.abandonAudioFocus(onAudioFocusChangeListener);
        }
    }


    void initAttrs(Context context, AttributeSet attrs) {
        pageContextKey = context.toString();
        uUKey = UUID.randomUUID().toString();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GSYVideoPlayer);
        showType = a.getInt(R.styleable.GSYVideoPlayer_gsy_showType, GSYVideoType.getShowType());
        a.recycle();

        setShowType(showType);
    }

    public String getVideoKey() {
        return pageContextKey + "$" + uUKey;
    }

    public OpenImageGSYVideoHelper playUrl(String videoUrl) {
        OpenImageGSYVideoHelper.GSYVideoHelperBuilder builder = new OpenImageGSYVideoHelper.GSYVideoHelperBuilder();
        builder.setHideActionBar(true);
        builder.setHideStatusBar(true);
        builder.setHideKey(true);
        builder.setUrl(videoUrl);
        builder.setEnlargeImageRes(getEnlargeImageRes());
        builder.setShrinkImageRes(getShrinkImageRes());
        builder.setAutoFullWithSize(true);
        builder.setShowFullAnimation(true);
        builder.setLockLand(true);
        builder.setReleaseWhenLossAudio(false);
        builder.setCacheWithPlay(true);
        builder.setLooping(isLooping());
        return playUrl(builder);
    }

    public OpenImageGSYVideoHelper playUrl(OpenImageGSYVideoHelper.GSYVideoHelperBuilder builder) {
        gsyVideoHelper = new OpenImageGSYVideoHelper(getContext(), this);
        gsyVideoHelper.setGsyVideoOptionBuilder(builder);

        if (getFullscreenButton() != null) {
            getFullscreenButton().setOnClickListener(v -> {
                if (mThumbImageView instanceof PhotoView) {
                    PhotoView photoImageView = (PhotoView) mThumbImageView;
                    photoImageView.getAttacher().setScreenOrientationChange(true);
                }
                gsyVideoHelper.doFullBtnLogic();
            });
        }
        gsyVideoHelper.readyPlay();
        return gsyVideoHelper;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    @Override
    public GSYVideoViewBridge getGSYVideoManager() {
        GSYVideoPlayerManager manager = GSYVideoController.getGSYVideoPlayerManager(getVideoKey());
        manager.initContext(getContext());
        manager.setNeedMute(mute);
        return manager;
    }

    public ViewGroup getTextureViewContainer() {
        return mTextureViewContainer;
    }

    public ImageView getBackButton() {
        return mBackButton;
    }

    public void goneAllWidget() {
        hideAllWidget();
    }

    public void showAllWidget() {
        if (mCurrentState == CURRENT_STATE_NORMAL) {
            changeUiToNormal();
        } else if (mCurrentState == CURRENT_STATE_PAUSE) {
            changeUiToPauseShow();
        } else if (mCurrentState == CURRENT_STATE_AUTO_COMPLETE) {
            changeUiToCompleteShow();
        } else if (mCurrentState == CURRENT_STATE_ERROR) {
            changeUiToError();
        }
    }

    @Override
    public void onSurfaceUpdated(Surface surface) {
        super.onSurfaceUpdated(surface);
        if (mThumbImageViewLayout != null && mThumbImageViewLayout.getVisibility() == VISIBLE) {
            mThumbImageViewLayout.setVisibility(INVISIBLE);
        }
    }

    @Override
    protected void setViewShowState(View view, int visibility) {
        if (view == mThumbImageViewLayout && visibility != VISIBLE) {
            return;
        }
        if (view != null){
            LifecycleEventObserver observer = (LifecycleEventObserver) view.getTag(R.id.gsy_player_view_visibility);
            if (observer != null){
                lifecycleOwner.getLifecycle().removeObserver(observer);
            }
        }
        if ((view == mTopContainer || view == mBottomContainer || view == mStartButton || view == mBottomProgressBar) && visibility == VISIBLE){
            if (lifecycleOwner == null || lifecycleOwner.getLifecycle().getCurrentState() == Lifecycle.State.RESUMED){
                super.setViewShowState(view, visibility);
            }else if (lifecycleOwner != null && view != null){
                LifecycleEventObserver observer = new LifecycleEventObserver() {
                    @Override
                    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                        if (event == Lifecycle.Event.ON_RESUME){
                            source.getLifecycle().removeObserver(this);
                            GSYVideoPlayer.super.setViewShowState(view, visibility);
                        }
                    }
                };
                view.setTag(R.id.gsy_player_view_visibility,observer);
                lifecycleOwner.getLifecycle().addObserver(observer);
            }
        }else {
            super.setViewShowState(view, visibility);
        }
    }

    @Override
    public void onSurfaceAvailable(Surface surface) {
        super.onSurfaceAvailable(surface);
        if (GSYVideoType.getRenderType() != GSYVideoType.TEXTURE) {
            if (mThumbImageViewLayout != null && mThumbImageViewLayout.getVisibility() == VISIBLE) {
                mThumbImageViewLayout.setVisibility(INVISIBLE);
            }
        }
    }

    @Override
    protected void changeUiToPrepareingClear() {
        View progressView = mLoadingProgressBar;
        if (mCurrentState == CURRENT_STATE_PREPAREING || mCurrentState == CURRENT_STATE_PLAYING_BUFFERING_START){
            mLoadingProgressBar = null;
        }
        super.changeUiToPrepareingClear();
        mLoadingProgressBar = progressView;
    }

    @Override
    public void startAfterPrepared() {
        super.startAfterPrepared();
        if (mCurrentState == CURRENT_STATE_PAUSE) {
            updateStartImage();
            startDismissControlViewTimer();
        }
    }

    @Override
    public int getCurrentVideoHeight() {
        GSYVideoType.setShowType(showType);
        return super.getCurrentVideoHeight();
    }

    @Override
    protected int getTextureParams() {
        GSYVideoType.setShowType(showType);
        return super.getTextureParams();
    }

    @Override
    protected void addTextureView() {
        GSYVideoType.setShowType(showType);
        super.addTextureView();
    }

    @Override
    public void onVideoResume() {
        if (isUserInputPause) {
            return;
        }
        boolean seek = true;
        if (this.getGSYVideoManager() != null) {
            long currentPosition = this.getGSYVideoManager().getCurrentPosition();
            seek = currentPosition < mCurrentPosition;
        }
        onVideoResume(seek);
    }

    @Override
    public void onVideoResume(boolean seek) {
//        super.onVideoResume(seek);
        mPauseBeforePrepared = false;
        if (mCurrentState == CURRENT_STATE_PAUSE || mCurrentState == CURRENT_STATE_PLAYING_BUFFERING_START) {
            try {
                if (mCurrentPosition >= 0 && getGSYVideoManager() != null) {
                    if (seek) {
                        getGSYVideoManager().seekTo(mCurrentPosition);
                    }
                    if (mCurrentState == CURRENT_STATE_PLAYING_BUFFERING_START){
                        if (!getGSYVideoManager().isPlaying()){
                            getGSYVideoManager().start();
                        }
                    }else {
                        getGSYVideoManager().start();
                    }
                    setStateAndUi(CURRENT_STATE_PLAYING);
                    requestAudioFocus();
                    mCurrentPosition = 0;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onVideoPause() {
        super.onVideoPause();
        abandonAudioFocus();
    }

    @Override
    protected void resolveUIState(int state) {
        int oldState = mOldState;
        boolean userInput = isUserInput;
        mOldState = state;
        isUserInput = false;
        if (!userInput && ((oldState == CURRENT_STATE_PLAYING && state == CURRENT_STATE_PAUSE)||(oldState == CURRENT_STATE_PAUSE && state == CURRENT_STATE_PLAYING))){
            if (state == CURRENT_STATE_PLAYING){
                updateStartImage();
                startDismissControlViewTimer();
            }
            return;
        }
        super.resolveUIState(state);
    }

    @Override
    protected void prepareVideo() {
        super.prepareVideo();
        isUserInput = false;
        isUserInputPause = false;
    }

    @Override
    protected void clickStartIcon() {
        isUserInput = true;
        isUserInputPause = !TextUtils.isEmpty(mUrl) && mCurrentState == CURRENT_STATE_PLAYING;
        super.clickStartIcon();
    }

    public int getShowType() {
        return showType;
    }

    public void setShowType(int showType) {
        this.showType = showType;
        GSYVideoType.setShowType(showType);
    }
}
