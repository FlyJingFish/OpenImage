package com.flyjingfish.openimagefulllib;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.flyjingfish.openimagelib.photoview.PhotoView;
import com.flyjingfish.openimagelib.utils.ScreenUtils;


public class OpenImageVideoPlayer extends GSYVideoPlayer {


    private PhotoView coverImageView;
    private PhotoView smallCoverImageView;

    public OpenImageVideoPlayer(Context context) {
        super(context);
    }

    public OpenImageVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        coverImageView = new PhotoView(context);
        coverImageView.setId(R.id.iv_video_player_cover);
        mThumbImageView = coverImageView;
        coverImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        resolveThumbImage(mThumbImageView);
        smallCoverImageView = findViewById(R.id.iv_small_cover);
        if (mTopContainer != null){
            mTopContainer.setPadding(0, ScreenUtils.getStatusBarHeight(context)+mTopContainer.getPaddingTop(), 0, 0);
        }
        changeUiToNormal();
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
        return R.layout.open_image_layout_videos;
    }

    @Override
    protected void setViewShowState(View view, int visibility) {
        super.setViewShowState(view, visibility);
        if (view == mThumbImageViewLayout && smallCoverImageView != null){
            smallCoverImageView.setVisibility(visibility);
        }
    }

}
