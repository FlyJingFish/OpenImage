package com.flyjingfish.openimagefulllib;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.flyjingfish.openimagelib.utils.ScreenUtils;


public class ScaleOpenImageVideoPlayer extends OpenImageCoverVideoPlayer {

    private ScaleRelativeLayout scaleRelativeLayout;

    public ScaleOpenImageVideoPlayer(Context context) {
        this(context,null);
    }

    public ScaleOpenImageVideoPlayer(Context context, AttributeSet attrs) {
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
        if (v.getId() == R.id.surface_container) {
            onClickUiToggle(null);
        }
    }

    public VideoPlayerAttacher getAttacher(){
        return scaleRelativeLayout.getAttacher();
    }
}
