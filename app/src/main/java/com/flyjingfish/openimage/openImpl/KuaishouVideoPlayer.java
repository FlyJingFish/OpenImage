package com.flyjingfish.openimage.openImpl;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.flyjingfish.openimage.R;
import com.flyjingfish.openimagefulllib.GSYVideoPlayer;
import com.flyjingfish.openimagelib.photoview.PhotoView;

public class KuaishouVideoPlayer extends GSYVideoPlayer {


    private PhotoView coverImageView;
    private PhotoView smallCoverImageView;

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
        changeUiToNormal();
        mTextureViewContainer.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    clickStartIcon();
                }
                return false;
            }
        });
        mTextureViewContainer.setOnClickListener(v -> {
            clickStartIcon();
        });
    }

    @Override
    protected void changeUiToNormal() {
        super.changeUiToNormal();
        mStartButton.setVisibility(GONE);
    }

    @Override
    protected void changeUiToPlayingShow() {
        super.changeUiToPlayingShow();
        mStartButton.setVisibility(GONE);
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

}
