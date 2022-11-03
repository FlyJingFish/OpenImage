package com.flyjingfish.openimagefulllib;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoViewBridge;

import java.util.UUID;

public class GSYVideoPlayer extends StandardGSYVideoPlayer {

    private String pageContextKey;
    private String uUKey;
    private boolean mute;//是否需要静音

    public GSYVideoPlayer(Context context) {
        this(context,null);
    }

    public GSYVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context);
    }

    void initAttrs(Context context) {
        pageContextKey = context.toString();
        uUKey = UUID.randomUUID().toString();
    }

    public void playUrl(String videoUrl) {
        setUp(videoUrl, true, "");
    }

    @Override
    public GSYVideoViewBridge getGSYVideoManager() {
        GSYVideoPlayerManager manager = GSYVideoController.getGSYVideoPlayerManager(getVideoKey());
        manager.initContext(getContext().getApplicationContext());
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

    public String getVideoKey() {
        return pageContextKey + "$" + uUKey;
    }
}
