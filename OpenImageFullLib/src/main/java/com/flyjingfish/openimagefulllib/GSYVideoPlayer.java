package com.flyjingfish.openimagefulllib;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.flyjingfish.openimagelib.photoview.PhotoView;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoViewBridge;

import java.util.UUID;

public class GSYVideoPlayer extends StandardGSYVideoPlayer {

    private String pageContextKey;
    private String uUKey;
    private boolean mute;//是否需要静音
    protected int showType = GSYVideoType.getShowType();

    boolean isUserInputResume = true;
    boolean isUserInputPause = true;
    boolean isPauseBeforeOnVideoPause = false;
    boolean isLossTransientAudio = false;
    boolean isHideCover = false;
    protected OpenImageGSYVideoHelper gsyVideoHelper;

    public GSYVideoPlayer(Context context) {
        this(context,null);
    }

    public GSYVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context,attrs);
    }

    void initAttrs(Context context,AttributeSet attrs) {
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
        builder.setEnlargeImageRes(R.drawable.video_enlarge);
        builder.setShrinkImageRes(R.drawable.video_shrink);
        builder.setAutoFullWithSize(true);
        builder.setShowFullAnimation(true);
        builder.setLockLand(true);
        builder.setCacheWithPlay(true);
        return playUrl(builder);
    }

    public OpenImageGSYVideoHelper playUrl(OpenImageGSYVideoHelper.GSYVideoHelperBuilder builder) {
        gsyVideoHelper = new OpenImageGSYVideoHelper(getContext(),this);
        gsyVideoHelper.setGsyVideoOptionBuilder(builder);

        if (getFullscreenButton() != null){
            getFullscreenButton().setOnClickListener(v -> {
                if (mThumbImageView instanceof PhotoView){
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
    protected void changeUiToPlayingShow() {
        super.changeUiToPlayingShow();
        if (!isHideCover){
            setViewShowState(mThumbImageViewLayout, VISIBLE);
        }
    }

    @Override
    protected void changeUiToPreparingShow() {
        super.changeUiToPreparingShow();
        if (!isHideCover){
            setViewShowState(mThumbImageViewLayout, VISIBLE);
        }
    }

    @Override
    protected void changeUiToPrepareingClear() {
        super.changeUiToPrepareingClear();
        if (!isHideCover){
            setViewShowState(mThumbImageViewLayout, VISIBLE);
        }
    }

    @Override
    public void onSurfaceSizeChanged(Surface surface, int width, int height) {
        super.onSurfaceSizeChanged(surface, width, height);
        if (mCurrentState == CURRENT_STATE_PLAYING){
            isHideCover = true;
            setViewShowState(mThumbImageViewLayout, INVISIBLE);
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
        if (isPauseBeforeOnVideoPause){
            isPauseBeforeOnVideoPause = false;
            return;
        }
        if (mCurrentState == CURRENT_STATE_PLAYING && this.getGSYVideoManager() != null && !this.getGSYVideoManager().isPlaying()){
            isUserInputPause = false;
            setStateAndUi(CURRENT_STATE_PAUSE);
        }
        boolean seek = true;
        if (this.getGSYVideoManager() != null){
            long currentPosition = this.getGSYVideoManager().getCurrentPosition();
            seek = currentPosition < mCurrentPosition;
        }
        isUserInputResume = false;
        super.onVideoResume(seek);
    }

    @Override
    public void onVideoPause() {
        isUserInputPause = false;
        if (mCurrentState == CURRENT_STATE_PAUSE && !isLossTransientAudio){
            isPauseBeforeOnVideoPause = true;
        }
        super.onVideoPause();
    }

    @Override
    protected void onLossTransientAudio() {
        isLossTransientAudio = true;
        super.onLossTransientAudio();
    }

    @Override
    protected void onLossAudio() {
        isLossTransientAudio = true;
        super.onLossAudio();
    }

    @Override
    protected void resolveUIState(int state) {
        if ((!isUserInputResume && state == CURRENT_STATE_PLAYING)||(!isUserInputPause && state == CURRENT_STATE_PAUSE)){
            isUserInputResume = true;
            isUserInputPause = true;
            return;
        }
        isUserInputResume = true;
        isUserInputPause = true;
        super.resolveUIState(state);
    }

    public int getShowType() {
        return showType;
    }

    public void setShowType(int showType) {
        this.showType = showType;
        GSYVideoType.setShowType(showType);
    }
}
