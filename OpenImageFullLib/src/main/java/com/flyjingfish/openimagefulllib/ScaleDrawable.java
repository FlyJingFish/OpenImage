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
    private final Rect mTempRect = new Rect();
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
                mTempRect.set(0,0,getWidth(),getHeight());
                GSYVideoPlayer gsyVideoPlayer = Util.getVideoPlayer(getParent());
                gsyVideoPlayer.setOnVideoSizeChangedListener((videoWidth, videoHeight) -> {
                    ViewGroup.LayoutParams layoutParams = getLayoutParams();
                    float scale = videoHeight*1f/videoWidth;
                    layoutParams.height = (int) (getWidth()*scale);
                    intrinsicWidth = getWidth();
                    intrinsicHeight = layoutParams.height;
                    Log.e("onStateChanged","intrinsicWidth="+intrinsicWidth+",intrinsicHeight="+intrinsicHeight);
                    setLayoutParams(layoutParams);
                    ViewParent viewParent = ScaleDrawable.this.getParent();
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
