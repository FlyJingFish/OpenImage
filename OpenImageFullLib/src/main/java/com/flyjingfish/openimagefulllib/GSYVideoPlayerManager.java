package com.flyjingfish.openimagefulllib;

import static androidx.media3.exoplayer.DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER;

import androidx.annotation.OptIn;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.DefaultLoadControl;
import androidx.media3.exoplayer.DefaultRenderersFactory;
import androidx.media3.exoplayer.LoadControl;

import com.shuyu.gsyvideoplayer.GSYVideoBaseManager;
import com.shuyu.gsyvideoplayer.player.IPlayerManager;

import tv.danmaku.ijk.media.exo2.Exo2PlayerManager;
import tv.danmaku.ijk.media.exo2.IjkExo2MediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer;

public class GSYVideoPlayerManager extends GSYVideoBaseManager {

    private Exo2PlayerManager exo2PlayerManager;

    public GSYVideoPlayerManager() {
        init();
    }

    @Override
    protected IPlayerManager getPlayManager() {
        exo2PlayerManager = new Exo2PlayerManager();
        return exo2PlayerManager;
    }

    @OptIn(markerClass = UnstableApi.class) @Override
    public void setNeedMute(boolean needMute) {
        super.setNeedMute(needMute);
        IMediaPlayer iMediaPlayer;
        if (exo2PlayerManager != null && ((iMediaPlayer = exo2PlayerManager.getMediaPlayer()) instanceof IjkExo2MediaPlayer)){
            IjkExo2MediaPlayer ijkMediaPlayer = (IjkExo2MediaPlayer) iMediaPlayer;
            LoadControl loadControl = ijkMediaPlayer.getLoadControl();
            if (loadControl == null){
                loadControl = new DefaultLoadControl.Builder()
                        .setBufferDurationsMs(5_000,5_000,250,500)
                        .build();
                ijkMediaPlayer.setLoadControl(loadControl);

            }

            DefaultRenderersFactory rendererFactory = ijkMediaPlayer.getRendererFactory();
            if (rendererFactory == null){
                rendererFactory = new DefaultRenderersFactory(context);
                rendererFactory.setExtensionRendererMode(EXTENSION_RENDERER_MODE_PREFER).setEnableDecoderFallback(true);
                ijkMediaPlayer.setRendererFactory(rendererFactory);
            }
        }
    }

    public void pauseVideoPlayer() {
        if (listener() != null) listener().onVideoPause();
    }

    public void resumeVideoPlayer() {
        if (listener() != null) listener().onVideoResume();
    }
}
