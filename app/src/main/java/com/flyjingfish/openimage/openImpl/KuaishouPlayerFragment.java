package com.flyjingfish.openimage.openImpl;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;

import com.flyjingfish.openimage.R;
import com.flyjingfish.openimage.bean.MessageBean;
import com.flyjingfish.openimagefulllib.VideoPlayerFragment;
import com.flyjingfish.openimagelib.photoview.PhotoView;
import com.flyjingfish.openimagelib.widget.LoadingView;

public class KuaishouPlayerFragment extends VideoPlayerFragment {

    protected KuaishouVideoPlayer kuaishouVideoPlayer;
    private View rootView;
    private LinearLayout llBtn;
    private TextView commentTv;
    private TextView titleTv;
    private boolean isOpenSlide;
    private KuaishouViewModel kuaishouViewModel;

    @Override
    protected PhotoView getSmallCoverImageView() {
        return kuaishouVideoPlayer.getSmallCoverImageView();
    }

    @Override
    protected PhotoView getPhotoView() {
        return kuaishouVideoPlayer.getCoverImageView();
    }

    @Override
    protected View getItemClickableView() {
        return kuaishouVideoPlayer;
    }

    @Override
    protected LoadingView getLoadingView() {
        return (LoadingView) kuaishouVideoPlayer.getLoadingView();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_kuaishou_play,container,false);
        kuaishouVideoPlayer = rootView.findViewById(R.id.video_player);
        videoPlayer = kuaishouVideoPlayer;
        llBtn = rootView.findViewById(R.id.ll_btn);
        commentTv = rootView.findViewById(R.id.tv_comment);
        titleTv = rootView.findViewById(R.id.tv_title);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        videoPlayer.setLooping(true);
        kuaishouViewModel = new ViewModelProvider(requireActivity()).get(KuaishouViewModel.class);
        commentTv.setOnClickListener(v -> kuaishouViewModel.clickLikeLiveData.setValue(true));
        kuaishouViewModel.btnsTranslationYLiveData.observe(getViewLifecycleOwner(), aFloat -> {
            llBtn.setTranslationY(aFloat);
            titleTv.setTranslationY(aFloat);
        });
        kuaishouViewModel.btnsAlphaLiveData.observe(getViewLifecycleOwner(), aFloat -> {
            llBtn.setAlpha(aFloat);
            titleTv.setAlpha(aFloat);
        });
        kuaishouViewModel.slidingLiveData.observe(getViewLifecycleOwner(), aFloat -> {
            llBtn.setAlpha(1-aFloat);
            titleTv.setAlpha(1-aFloat);
            float minScale = 0.8f;
            llBtn.getWidth();
            float scale = (1-aFloat)*(1-minScale) + minScale;
            llBtn.setScaleX(scale);
            llBtn.setScaleY(scale);
            titleTv.setScaleX(scale);
            titleTv.setScaleY(scale);
        });
        kuaishouViewModel.slideStatusLiveData.observe(getViewLifecycleOwner(), aBoolean -> isOpenSlide = aBoolean);
        kuaishouVideoPlayer.setOnSurfaceTouchListener(() -> {
            kuaishouViewModel.closeSlideLiveData.setValue(true);
            return isOpenSlide;
        });
        kuaishouViewModel.pausePlayLiveData.observe(getViewLifecycleOwner(), playState -> {
            if (playState.position == getShowPosition() && !playState.consume){
                kuaishouVideoPlayer.playPause();
                playState.consume = true;
            }
        });
        kuaishouVideoPlayer.setGSYStateUiListener(state -> {
            if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED){
                kuaishouViewModel.playStateLiveData.setValue(new PlayState(state,getShowPosition()));
            }
        });
        titleTv.setText(((MessageBean) openImageUrl).text);
    }

    @Override
    public void onResume() {
        super.onResume();
        kuaishouViewModel.playStateLiveData.setValue(new PlayState(kuaishouVideoPlayer.getCurrentState(),getShowPosition()));
    }

    boolean isStartedTouch;
    @Override
    protected void onTouchScale(float scale) {
        if (scale == 1){
            if (isStartedTouch){
                if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED){
                    videoPlayer.onVideoResume();
                }
                isStartedTouch = false;
            }
        }else {
            isStartedTouch = true;
        }
        super.onTouchScale(scale);
    }
}
