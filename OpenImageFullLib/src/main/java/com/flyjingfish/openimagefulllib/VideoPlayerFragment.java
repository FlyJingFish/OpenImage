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

public class VideoPlayerFragment extends BaseImageFragment<LoadingView> {

    protected String playerKey;
    protected boolean isPlayed;
    protected boolean isLoadImageFinish;
    protected GSYVideoPlayer videoPlayer;
    protected View rootView;
    protected PhotoView smallImageView;
    protected PhotoView photoImageView;
    protected LoadingView loadingView;
    private OpenImageGSYVideoHelper gsyVideoHelper;

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
        setBackListener();
        playerKey = videoPlayer.getVideoKey();
        videoPlayer.goneAllWidget();
        isPlayed = false;
        RecordPlayerPosition.INSTANCE.clearRecord(requireActivity());
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
            startPlay();
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

    /**
     * 从2.0.3开始播放逻辑从这里改为从{@link VideoPlayerFragment#startPlay()}播放,使用本库中的播放器可以预加载，保持最快速度播放
     */
    protected void toPlay4Resume(){

    }

    /**
     * 从2.0.3新增此方法；开始播放，如果生命周期 在 onResume 下立刻开始播放，否则开始预加载，加载完毕后自动暂停，当 生命周期回到 onResume 下可实现快速开始播放
     *
     */
    protected void startPlay(){
        readyPlay();
        videoPlayer.setSeekOnStart(RecordPlayerPosition.INSTANCE.getPlayPosition(requireActivity(),beanId));
        videoPlayer.startPlayLogic();
        if (getLifecycle().getCurrentState() != Lifecycle.State.RESUMED){
            videoPlayer.onVideoPause();
        }
    }

    /**
     * 准备播放的参数
     */
    protected void readyPlay(){
        gsyVideoHelper = videoPlayer.playUrl(openImageUrl.getVideoUrl());
        if (gsyVideoHelper.getGsyVideoOptionBuilder() != null){
            gsyVideoHelper.getGsyVideoOptionBuilder().setVideoAllCallBack(new GSYSampleCallBack(){
                @Override
                public void onQuitFullscreen(String url, Object... objects) {
                    super.onQuitFullscreen(url, objects);
                    setBackListener();
                }
            });
        }
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
            RecordPlayerPosition.INSTANCE.setPlayPosition(requireActivity(),beanId,videoPlayer.getCurrentPositionWhenPlaying());
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
            boolean isFull = gsyVideoHelper.isFull();
            if (isFull){
                gsyVideoHelper.doFullBtnLogic();
            }
            return !isFull;
        }
        return super.onKeyBackDown();
    }

    protected void setBackListener(){
        if (videoPlayer.getBackButton() != null){
            videoPlayer.getBackButton().setOnClickListener(v -> close());
        }
    }
}
