package com.flyjingfish.openimagefulllib;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.flyjingfish.openimagelib.BaseImageFragment;
import com.flyjingfish.openimagelib.photoview.PhotoView;
import com.flyjingfish.openimagelib.widget.LoadingView;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.utils.GSYVideoHelper;

public class VideoPlayerFragment extends BaseImageFragment<LoadingView> {

    protected String playerKey;
    protected boolean isPlayed;
    protected boolean isLoadImageFinish;
    protected GSYVideoPlayer videoPlayer;
    protected View rootView;
    protected PhotoView smallImageView;
    protected PhotoView photoImageView;
    protected LoadingView loadingView;
    private GSYVideoHelper gsyVideoHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.open_image_fragment_video,container,false);
        OpenImageVideoPlayer videoPlayer = rootView.findViewById(R.id.video_player);
        this.videoPlayer = videoPlayer;
        smallImageView = videoPlayer.getSmallCoverImageView();
        photoImageView = videoPlayer.getCoverImageView();
        loadingView = (LoadingView) videoPlayer.getLoadingView();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (videoPlayer.getBackButton() != null){
            videoPlayer.getBackButton().setOnClickListener(v -> close());
        }
        playerKey = videoPlayer.getVideoKey();
        videoPlayer.goneAllWidget();
        isPlayed = false;
    }

    @Override
    protected PhotoView getSmallCoverImageView() {
        return smallImageView;
    }

    @Override
    protected PhotoView getPhotoView() {
        return photoImageView;
    }

    @Override
    protected View getItemClickableView() {
        return videoPlayer;
    }

    @Override
    protected LoadingView getLoadingView() {
        return loadingView;
    }

    @Override
    protected void hideLoading(LoadingView pbLoading) {
        super.hideLoading(pbLoading);
        if (videoPlayer.getStartButton() != null){
            videoPlayer.getStartButton().setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void showLoading(LoadingView pbLoading) {
        super.showLoading(pbLoading);
        if (videoPlayer.getStartButton() != null){
            videoPlayer.getStartButton().setVisibility(View.GONE);
        }
    }

    @Override
    protected void onTouchClose(float scale) {
        super.onTouchClose(scale);
        if (videoPlayer.getTextureViewContainer() != null){
            videoPlayer.getTextureViewContainer().setVisibility(View.GONE);
        }
        videoPlayer.goneAllWidget();
    }

    @Override
    protected void onTouchScale(float scale) {
        super.onTouchScale(scale);
        videoPlayer.goneAllWidget();
        if (scale == 1){
            videoPlayer.showAllWidget();
        }
    }

    @Override
    protected void loadImageFinish(boolean isLoadImageSuccess) {
        isLoadImageFinish = true;
        play();
    }

    @Override
    protected void onTransitionEnd() {
        super.onTransitionEnd();
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
        GSYVideoHelper.GSYVideoHelperBuilder builder = new GSYVideoHelper.GSYVideoHelperBuilder();
        builder.setHideActionBar(true);
        builder.setHideStatusBar(true);
        builder.setUrl(openImageUrl.getVideoUrl());
        builder.setEnlargeImageRes(R.drawable.video_enlarge);
        builder.setShrinkImageRes(R.drawable.video_shrink);
        builder.setAutoFullWithSize(true);
        builder.setShowFullAnimation(true);
        builder.setLockLand(true);
        gsyVideoHelper = new GSYVideoHelper(requireContext(),videoPlayer);
        gsyVideoHelper.setGsyVideoOptionBuilder(builder);
        builder.setVideoAllCallBack(new GSYSampleCallBack(){
            @Override
            public void onQuitFullscreen(String url, Object... objects) {
                super.onQuitFullscreen(url, objects);
                videoPlayer.getBackButton().setVisibility(View.VISIBLE);
            }
        });
//        gsyVideoHelper.startPlay();
        if (videoPlayer.getFullscreenButton() != null){
            videoPlayer.getFullscreenButton().setOnClickListener(v -> {
                photoImageView.getAttacher().setScreenOrientationChange(true);
                gsyVideoHelper.doFullBtnLogic();
            });
        }
        videoPlayer.playUrl(openImageUrl.getVideoUrl());
        videoPlayer.startPlayLogic();
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
    public boolean onKeyBackDown() {
        if (gsyVideoHelper != null){
            return gsyVideoHelper.isFull();
        }
        return super.onKeyBackDown();
    }
}
