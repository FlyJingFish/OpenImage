package com.flyjingfish.openimage.openImpl;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.flyjingfish.openimage.R;
import com.flyjingfish.openimage.databinding.FragmentVideoBinding;
import com.flyjingfish.openimage.videoplayer.GSYVideoController;
import com.flyjingfish.openimagelib.BaseImageFragment;
import com.flyjingfish.openimagelib.photoview.PhotoView;

import moe.codeest.enviews.ENDownloadView;

public class VideoPlayerFragment extends BaseImageFragment<ENDownloadView> {

    protected FragmentVideoBinding binding;
    protected String playerKey;
    protected boolean isPlayed;
    protected boolean isLoadImageFinish;

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
        isPlayed = false;
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
    protected void loadImageFinish(boolean isLoadImageSuccess) {
        isLoadImageFinish = true;
        play();
    }

    private void play(){
        if (isTransitionEnd && isLoadImageFinish && !isPlayed){
            if (getLifecycle().getCurrentState() == Lifecycle.State.RESUMED){
                toPlay4Resume();
            }else {
                getLifecycle().addObserver(new LifecycleEventObserver() {
                    @Override
                    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                        if (event == Lifecycle.Event.ON_RESUME){
                            toPlay4Resume();
                            source.getLifecycle().removeObserver(this);
                        }
                    }
                });
            }

            isPlayed = true;
        }
    }

    protected void toPlay4Resume(){
        binding.videoPlayer.playUrl(openImageUrl.getVideoUrl());
        binding.videoPlayer.startPlayLogic();
    }

    @Override
    protected void onTransitionEnd() {
        super.onTransitionEnd();
        play();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (playerKey != null) {
            GSYVideoController.resumeByKey(playerKey);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (playerKey != null) {
            GSYVideoController.pauseByKey(playerKey);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (playerKey != null) {
            GSYVideoController.cancelByKeyAndDeleteKey(playerKey);
        }
    }

    @Override
    public View getExitImageView() {
        binding.videoPlayer.getThumbImageViewLayout().setVisibility(View.VISIBLE);
        return super.getExitImageView();
    }
}
