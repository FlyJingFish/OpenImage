package com.flyjingfish.openimagefulllib;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ScaleDrawable extends FrameLayout {
    private int intrinsicWidth;
    private int intrinsicHeight;
    private int lastVideoWidth;
    private int lastVideoHeight;
    public ScaleDrawable(@NonNull Context context) {
        this(context,null);
    }

    public ScaleDrawable(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ScaleDrawable(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                GSYVideoPlayer gsyVideoPlayer = Util.getVideoPlayer(getParent());
                gsyVideoPlayer.addOnVideoSizeChangedListener((videoWidth, videoHeight) -> {
                    setVideoWidthHeight(videoWidth,videoHeight);
                });
            }
        });
    }

    public boolean isReady(){
        return intrinsicWidth != 0 && intrinsicHeight!= 0;
    }

    public int getIntrinsicWidth(){
        return intrinsicWidth;
    }

    public int getIntrinsicHeight(){
        return intrinsicHeight;
    }

    public void setVideoWidthHeight(int videoWidth, int videoHeight) {
        if (lastVideoHeight == videoHeight && lastVideoWidth == videoWidth){
            return;
        }
        ViewParent viewParent = ScaleDrawable.this.getParent();
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        int viewWidth = ((ViewGroup) viewParent).getWidth();
        int viewHeight = ((ViewGroup) viewParent).getHeight();
        float viewScale = viewHeight*1f/viewWidth;
        float videoScale = videoHeight*1f/videoWidth;
        int drawableWidth;
        int drawableHeight;
        if (videoScale > viewScale){
            drawableWidth = (int) (viewHeight / videoScale);
            drawableHeight = viewHeight;
            layoutParams.width = drawableWidth;
        }else {
            drawableWidth = viewWidth;
            drawableHeight = (int) (viewWidth * videoScale);
            layoutParams.height = drawableHeight;
        }
        intrinsicWidth = drawableWidth;
        intrinsicHeight = drawableHeight;

        setLayoutParams(layoutParams);

        if (viewParent instanceof ScaleRelativeLayout){
            ((ScaleRelativeLayout) viewParent).getAttacher().updateScaleConfig();
            ((ScaleRelativeLayout) viewParent).getAttacher().update();
        }
        lastVideoWidth = videoWidth;
        lastVideoHeight = videoHeight;

    }


    @Override
    public void setOnTouchListener(OnTouchListener l) {
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {

    }

    @Override
    public void setOnLongClickListener(@Nullable OnLongClickListener l) {

    }
}
