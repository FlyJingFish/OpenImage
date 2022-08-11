package com.flyjingfish.openimage.videoplayer;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;

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

    @Override
    public GSYVideoViewBridge getGSYVideoManager() {
        GSYVideoPlayerManager manager = GSYVideoController.getGSYVideoPlayerManager(getVideoKey());
        manager.initContext(getContext().getApplicationContext());
        manager.setNeedMute(mute);
        return manager;
    }

    public String getVideoKey() {
        return pageContextKey + "$" + uUKey;
    }
}
