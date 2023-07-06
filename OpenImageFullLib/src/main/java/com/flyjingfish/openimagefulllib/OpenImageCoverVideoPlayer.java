package com.flyjingfish.openimagefulllib;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.flyjingfish.openimagelib.photoview.PhotoView;


public class OpenImageCoverVideoPlayer extends GSYVideoPlayer {

    private PhotoView coverImageView;
    private PhotoView smallCoverImageView;

    public OpenImageCoverVideoPlayer(Context context) {
        this(context,null);
    }

    public OpenImageCoverVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        addCoverImageView(context);
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

    protected void addCoverImageView(Context context){
        coverImageView = new PhotoView(context);
        coverImageView.setId(R.id.iv_video_player_cover);
        mThumbImageView = coverImageView;
        coverImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        resolveThumbImage(mThumbImageView);
        if (mThumbImageViewLayout != null) {
            View oldSmallView = mThumbImageViewLayout.findViewById(R.id.iv_video_player_cover_small);
            if (oldSmallView != null){
                mThumbImageViewLayout.removeView(oldSmallView);
            }
            smallCoverImageView = new PhotoView(context);
            smallCoverImageView.setId(R.id.iv_video_player_cover_small);
            smallCoverImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            mThumbImageViewLayout.addView(smallCoverImageView,layoutParams);
        }
    }

}
