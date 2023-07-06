package com.flyjingfish.openimagefulllib;

import android.content.Context;
import android.util.AttributeSet;

import com.flyjingfish.openimagelib.utils.ScreenUtils;


public class OpenImageVideoPlayer extends OpenImageCoverVideoPlayer {

    public OpenImageVideoPlayer(Context context) {
        super(context);
    }

    public OpenImageVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (mTopContainer != null){
            mTopContainer.setPadding(0, ScreenUtils.getStatusBarHeight(context)+mTopContainer.getPaddingTop(), 0, 0);
        }
        changeUiToNormal();
    }

    @Override
    public int getLayoutId() {
        return R.layout.open_image_layout_videos;
    }

}
