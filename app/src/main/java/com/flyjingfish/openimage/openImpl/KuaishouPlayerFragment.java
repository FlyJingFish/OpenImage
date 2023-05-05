package com.flyjingfish.openimage.openImpl;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.flyjingfish.openimage.R;
import com.flyjingfish.openimagefulllib.VideoPlayerFragment;
import com.flyjingfish.openimagelib.photoview.PhotoView;
import com.flyjingfish.openimagelib.widget.LoadingView;

public class KuaishouPlayerFragment extends VideoPlayerFragment {

    protected KuaishouVideoPlayer friendVideoPlayer;
    private View rootView;
    private LinearLayout llBtn;
    private TextView commentTv;
    private TextView titleTv;

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
        llBtn = rootView.findViewById(R.id.ll_btn);
        commentTv = rootView.findViewById(R.id.tv_comment);
        titleTv = rootView.findViewById(R.id.tv_title);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        KuaishouViewModel kuaishouViewModel = new ViewModelProvider(requireActivity()).get(KuaishouViewModel.class);
        commentTv.setOnClickListener(v -> kuaishouViewModel.clickLikeLiveData.setValue(true));
        kuaishouViewModel.btnsTranslationYLiveData.observe(getViewLifecycleOwner(), aFloat -> {
            llBtn.setTranslationY(aFloat);
            titleTv.setTranslationY(aFloat);
        });
        kuaishouViewModel.btnsAlphaLiveData.observe(getViewLifecycleOwner(), aFloat -> {
            llBtn.setAlpha(aFloat);
            titleTv.setAlpha(aFloat);
        });
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
