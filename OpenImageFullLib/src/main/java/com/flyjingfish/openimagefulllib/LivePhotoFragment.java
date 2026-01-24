package com.flyjingfish.openimagefulllib;

import android.graphics.RectF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.flyjingfish.openimagelib.utils.OpenImageLogUtils;
import com.flyjingfish.openimagelib.utils.SPUtils;
import com.flyjingfish.openimagelib.utils.ScreenUtils;
import com.flyjingfish.openimagelib.utils.StatusBarHelper;
import com.flyjingfish.openimagelib.widget.LoadingView;

public class LivePhotoFragment extends VideoPlayerFragment {

    protected View live;
    protected boolean isOpenLive = true;
    protected TextView tvSwitch;
    protected ImageView ivSwitch;
    protected ImageView ivLive;
    protected View livePop;
    protected View ivLiveDown;
    protected View llSwitch;
    protected View replay;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.open_image_fragment_live_photo,container,false);
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

    protected void initLive(){
        if (isOpenLive){
            tvSwitch.setText(closeLive);
            ivSwitch.setImageResource(R.drawable.open_image_live_close_black);
            ivLive.setImageResource(R.drawable.open_image_live_open_white);
        }else{
            tvSwitch.setText(openLive);
            ivSwitch.setImageResource(R.drawable.open_image_live_open_black);
            ivLive.setImageResource(R.drawable.open_image_live_close_white);
        }
    }

    protected static final String OPEN_LIVE = "open_live";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isOpenLive = SPUtils.getBoolean(requireContext(),OPEN_LIVE,true);
        addOnItemClickListener((fragment, openImageUrl, position) -> {
            if (position == getShowPosition()){
                livePop.setVisibility(View.GONE);
            }
        });
        replay.setOnClickListener(v -> {
            startPlay();
            livePop.setVisibility(View.GONE);
            ivLiveDown.setRotation(0);
        });
        live.setOnClickListener(v -> {
            if (livePop.getVisibility()==View.VISIBLE){
                livePop.setVisibility(View.GONE);
                ivLiveDown.setRotation(0);
            }else{
                livePop.setVisibility(View.VISIBLE);
                ivLiveDown.setRotation(180);
            }
        });
//        liveOut.setOnClickListener(v -> livePop.setVisibility(View.GONE));

        setOnListener();
        llSwitch.setOnClickListener(v -> {
            isOpenLive = !isOpenLive;
            initLive();
            SPUtils.put(requireContext(),OPEN_LIVE,isOpenLive);
            livePop.setVisibility(View.GONE);
            ivLiveDown.setRotation(0);
        });
        initLive();
    }

    @Override
    protected void onTouchClose(float scale) {
        super.onTouchClose(scale);
        if (isLoadImageFinish){
            live.setVisibility(View.GONE);
        }
    }

    @Override
    protected void loadImageFinish(boolean isLoadImageSuccess) {
        super.loadImageFinish(isLoadImageSuccess);
        showLive();
    }

    @Override
    protected void onTransitionEnd() {
        super.onTransitionEnd();
        showLive();
    }

    @Override
    protected void play() {
//        super.play();
    }

    protected void toPlayVideo(){
        super.play();
    }

    protected void showLive(){
        if (isTransitionEnd && isLoadImageFinish){
            rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mHandler.post(()->{
                        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) live.getLayoutParams();
                        int topMargin = (int) (StatusBarHelper.getStatusbarHeight(requireContext())+ ScreenUtils.dp2px(requireContext(),10));
                        RectF rectF = photoImageView.getDisplayRect();
                        layoutParams.topMargin = (int) Math.max(rectF.top+ScreenUtils.dp2px(requireContext(),10),topMargin);
                        live.setLayoutParams(layoutParams);
                    });
                    mHandler.post(()->{
                        if (isOpenLive){
                            toPlayVideo();
                        }else{
                            live.setVisibility(View.VISIBLE);
                        }
                    });
                }
            });
        }
    }


    @Override
    protected void onTouchScale(float scale) {
        super.onTouchScale(scale);
        if (isLoadImageFinish){
            live.setVisibility(View.GONE);
            livePop.setVisibility(View.GONE);
            ivLiveDown.setRotation(0);
            if (scale == 1){
                if (isPlayed){
                    live.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    @Override
    protected void onStateChanged(int state) {
        super.onStateChanged(state);
        if (isPlayed){

            if (state == GSYVideoPlayer.CURRENT_STATE_AUTO_COMPLETE){
                live.setVisibility(View.VISIBLE);
            }else{
                live.setVisibility(View.GONE);
            }
        }
        OpenImageLogUtils.logD("loadImageFinish2","onStateChanged===>"+state);
    }
}
