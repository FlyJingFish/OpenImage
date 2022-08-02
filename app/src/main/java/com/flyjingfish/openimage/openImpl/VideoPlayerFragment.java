package com.flyjingfish.openimage.openImpl;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.flyjingfish.openimage.R;
import com.flyjingfish.openimage.databinding.FragmentVideoBinding;
import com.flyjingfish.openimage.videoplayer.GSYVideoController;
import com.flyjingfish.openimagelib.BaseImageFragment;
import com.flyjingfish.openimagelib.photoview.PhotoView;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;

import moe.codeest.enviews.ENDownloadView;

public class VideoPlayerFragment extends BaseImageFragment<ENDownloadView> {

    private FragmentVideoBinding binding;
    private String playerKey;


    @Override
    protected PhotoView getSmallCoverImageView() {
        return binding.videoPlayer.getSmallCoverImageView();
    }

    @Override
    protected PhotoView getPhotoView() {
        return binding.videoPlayer.getCoverImageView();
    }

    @Override
    protected ENDownloadView getLoadingView() {
        return (ENDownloadView) binding.videoPlayer.getLoadingView();
    }

    @Override
    protected void hideLoading(ENDownloadView pbLoading) {
        super.hideLoading(pbLoading);
        pbLoading.release();
        binding.videoPlayer.getStartButton().setVisibility(View.VISIBLE);
    }

    @Override
    protected void showLoading(ENDownloadView pbLoading) {
        super.showLoading(pbLoading);
        pbLoading.start();
        binding.videoPlayer.getStartButton().setVisibility(View.GONE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentVideoBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.videoPlayer.findViewById(R.id.back).setOnClickListener(v -> close());
        playerKey = binding.videoPlayer.getVideoKey();
        binding.videoPlayer.goneAllWidget();

    }

    @Override
    protected void onTouchClose(float scale) {
        super.onTouchClose(scale);
        binding.videoPlayer.findViewById(R.id.surface_container).setVisibility(View.GONE);
        binding.videoPlayer.goneAllWidget();
    }

    @Override
    protected void onTouchScale(float scale) {
        super.onTouchScale(scale);
        binding.videoPlayer.goneAllWidget();
        if (scale == 0){
            binding.videoPlayer.showAllWidget();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_DEFAULT);
        if (playerKey != null) {
            GSYVideoController.resumeByKey(playerKey);
        }
    }

    boolean isLoadImageFinish;
    @Override
    protected void loadImageFinish(boolean isLoadImageSuccess) {
        isLoadImageFinish = true;
        play();
    }

    private void play(){
        if (isTransitionEnd && isLoadImageFinish){
            binding.videoPlayer.playUrl(openImageBean.getVideoUrl());
            binding.videoPlayer.startPlayLogic();
        }
    }

    @Override
    protected void onTransitionEnd() {
        super.onTransitionEnd();
        play();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (playerKey != null) {
            GSYVideoController.pauseByPageKey(requireActivity().toString());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        GSYVideoController.cancelByPageKey(requireActivity().toString());
    }

    @Override
    public View getExitImageView() {
        binding.videoPlayer.getThumbImageViewLayout().setVisibility(View.VISIBLE);
        return super.getExitImageView();
    }
}
