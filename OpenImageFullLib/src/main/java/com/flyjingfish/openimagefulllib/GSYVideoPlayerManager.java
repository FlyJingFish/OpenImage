package com.flyjingfish.openimagefulllib;

import com.shuyu.gsyvideoplayer.GSYVideoBaseManager;
import com.shuyu.gsyvideoplayer.player.IPlayerManager;

import tv.danmaku.ijk.media.exo2.Exo2PlayerManager;

public class GSYVideoPlayerManager extends GSYVideoBaseManager {
    public GSYVideoPlayerManager() {
        init();
    }

    @Override
    protected IPlayerManager getPlayManager() {
        return new Exo2PlayerManager();
    }

    public void pauseVideoPlayer() {
        if (listener() != null) listener().onVideoPause();
    }

    public void resumeVideoPlayer() {
        if (listener() != null) listener().onVideoResume();
    }
}
