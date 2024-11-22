package com.flyjingfish.openimagefulllib;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.shuyu.gsyvideoplayer.listener.GSYStateUiListener;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView;

public class ScaleDrawable extends FrameLayout {
    private int intrinsicWidth;
    private int intrinsicHeight;
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
                gsyVideoPlayer.setOnVideoSizeChangedListener((videoWidth, videoHeight) -> {
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

                    Log.e("onStateChanged","intrinsicWidth="+intrinsicWidth+",intrinsicHeight="+intrinsicHeight);
                    setLayoutParams(layoutParams);

                    if (viewParent instanceof ScaleRelativeLayout){
                        ((ScaleRelativeLayout) viewParent).getAttacher().updateScaleConfig();
                    }
                });
            }
        });
    }


    public int getIntrinsicWidth(){
        return intrinsicWidth;
    }

    public int getIntrinsicHeight(){
        return intrinsicHeight;
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
