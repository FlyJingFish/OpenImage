package com.flyjingfish.openimage.openImpl;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.flyjingfish.openimage.R;
import com.flyjingfish.openimagefulllib.VideoPlayerFragment;
import com.flyjingfish.openimagelib.photoview.PhotoView;
import com.flyjingfish.openimagelib.widget.LoadingView;

public class KuaishouPlayerFragment extends VideoPlayerFragment {

    protected KuaishouVideoPlayer friendVideoPlayer;
    private View rootView;

    @Override
    protected PhotoView getSmallCoverImageView() {
        return friendVideoPlayer.getSmallCoverImageView();
    }

    @Override
    protected PhotoView getPhotoView() {
        return friendVideoPlayer.getCoverImageView();
    }

    @Override
    protected View getItemClickableView() {
        return friendVideoPlayer.getTextureViewContainer();
    }

    @Override
    protected LoadingView getLoadingView() {
        return (LoadingView) friendVideoPlayer.getLoadingView();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_kuaishou_play,container,false);
        friendVideoPlayer = rootView.findViewById(R.id.video_player);
        videoPlayer = friendVideoPlayer;
        return rootView;
    }
    boolean isStartedTouch;
    @Override
    protected void onTouchScale(float scale) {
        if (scale == 1){
            if (isStartedTouch){
                videoPlayer.onVideoResume();
                isStartedTouch = false;
            }
        }else {
            isStartedTouch = true;
        }
        super.onTouchScale(scale);
    }
}
