package com.flyjingfish.openimagefulllib;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.flyjingfish.openimagelib.utils.ScreenUtils;
import com.shuyu.gsyvideoplayer.utils.Debuger;


public class OpenImageVideoPlayer extends OpenImageCoverVideoPlayer {

    private ScaleRelativeLayout scaleRelativeLayout;

    public OpenImageVideoPlayer(Context context) {
        this(context,null);
    }

    public OpenImageVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        scaleRelativeLayout = findViewById(R.id.rl_video_player_root);
        if (mTopContainer != null){
            mTopContainer.setPadding(0, ScreenUtils.getStatusBarHeight(context)+mTopContainer.getPaddingTop(), 0, 0);
        }
        changeUiToNormal();
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        mTextureViewContainer = findViewById(R.id.surface_container_drawable);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        scaleRelativeLayout.setOnClickListener(l);
    }

    @Override
    public void setOnLongClickListener(@Nullable OnLongClickListener l) {
        scaleRelativeLayout.setOnLongClickListener(l);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.surface_container_drawable && mCurrentState == CURRENT_STATE_ERROR) {
            if (!mSurfaceErrorPlay) {
                onClickUiToggle(null);
                return;
            }
            if (mVideoAllCallBack != null) {
                Debuger.printfLog("onClickStartError");
                mVideoAllCallBack.onClickStartError(mOriginUrl, mTitle, this);
            }
            prepareVideo();
        }else if (v.getId() == R.id.surface_container_drawable) {
            onClickUiToggle(null);
            if (mVideoAllCallBack != null && isCurrentMediaListener()) {
                if (mIfCurrentIsFullscreen) {
                    Debuger.printfLog("onClickBlankFullscreen");
                    mVideoAllCallBack.onClickBlankFullscreen(mOriginUrl, mTitle, OpenImageVideoPlayer.this);
                } else {
                    Debuger.printfLog("onClickBlank");
                    mVideoAllCallBack.onClickBlank(mOriginUrl, mTitle, OpenImageVideoPlayer.this);
                }
            }
            startDismissControlViewTimer();
        }
    }
}
