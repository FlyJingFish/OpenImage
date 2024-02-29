package com.flyjingfish.openimagefulllib;

import static com.google.android.exoplayer2.DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.mediacodec.MediaCodecInfo;
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.util.MimeTypes;
import com.shuyu.gsyvideoplayer.GSYVideoBaseManager;
import com.shuyu.gsyvideoplayer.player.IPlayerManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    @Override
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
