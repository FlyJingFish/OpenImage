package com.flyjingfish.openimage.openImpl;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.flyjingfish.openimage.R;
import com.flyjingfish.openimagefulllib.GSYVideoPlayer;
import com.flyjingfish.openimagelib.photoview.PhotoView;

public class FriendsVideoPlayer extends GSYVideoPlayer {


    private PhotoView coverImageView;
    private PhotoView smallCoverImageView;

    public FriendsVideoPlayer(Context context) {
        super(context);
    }

    public FriendsVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        coverImageView = new PhotoView(context);
        coverImageView.setId(R.id.iv_video_player_cover);
        mThumbImageView = coverImageView;
        resolveThumbImage(mThumbImageView);
        smallCoverImageView = findViewById(R.id.iv_small_cover);
        changeUiToNormal();
        mTextureViewContainer.setOnClickListener(null);
        mTextureViewContainer.setOnTouchListener(null);
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
        return R.layout.layout_friends_player;
    }
}
