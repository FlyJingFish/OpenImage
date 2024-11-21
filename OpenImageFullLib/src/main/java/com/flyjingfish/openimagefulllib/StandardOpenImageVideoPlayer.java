package com.flyjingfish.openimagefulllib;

import android.content.Context;
import android.util.AttributeSet;


public class StandardOpenImageVideoPlayer extends OpenImageVideoPlayer {


    public StandardOpenImageVideoPlayer(Context context) {
        this(context,null);
    }

    public StandardOpenImageVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getLayoutId() {
        return R.layout.open_image_layout_videos;
    }

}
