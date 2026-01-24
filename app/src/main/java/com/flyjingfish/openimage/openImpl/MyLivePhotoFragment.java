package com.flyjingfish.openimage.openImpl;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.flyjingfish.openimage.R;
import com.flyjingfish.openimagefulllib.LivePhotoFragment;
import com.flyjingfish.openimagefulllib.OpenImageCoverVideoPlayer;
import com.flyjingfish.openimagefulllib.VideoPlayerFragment;
import com.flyjingfish.openimagelib.widget.LoadingView;

public class MyLivePhotoFragment extends LivePhotoFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.my_open_image_fragment_live_photo,container,false);
        OpenImageCoverVideoPlayer videoPlayer = rootView.findViewById(R.id.video_player);
        live = rootView.findViewById(R.id.ll_live);
        llSwitch = rootView.findViewById(R.id.ll_switch_live);
        ivLiveDown = rootView.findViewById(R.id.iv_live_down);
        tvSwitch = rootView.findViewById(R.id.tv_switch_live);
        ivSwitch = rootView.findViewById(R.id.iv_switch_live);
        ivLive = rootView.findViewById(R.id.iv_live);
        livePop = rootView.findViewById(R.id.ll_live_pop);
        replay = rootView.findViewById(R.id.ll_replay);

        this.videoPlayer = videoPlayer;
        smallImageView = videoPlayer.getSmallCoverImageView();
        photoImageView = videoPlayer.getCoverImageView();
        loadingView = (LoadingView) videoPlayer.getLoadingView();
        photoImageView.setZoomable(true);

        return rootView;
    }

}
