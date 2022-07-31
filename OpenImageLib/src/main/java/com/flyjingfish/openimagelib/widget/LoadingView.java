package com.flyjingfish.openimagelib.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.flyjingfish.openimagelib.R;

public class LoadingView extends AppCompatImageView {

    private AnimatorSet loadingAnim;

    public LoadingView(@NonNull Context context) {
        this(context,null);
    }

    public LoadingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setImageResource(R.mipmap.icon_loading);
        initLoadingAnim();
    }
    private void initLoadingAnim(){
        stopLoadingAnim();
        ObjectAnimator loadingAnim1 = ObjectAnimator.ofFloat(this,"rotation",0,360);
        loadingAnim1.setRepeatCount(2);
        loadingAnim1.setRepeatMode(ValueAnimator.RESTART);
        loadingAnim1.setInterpolator(new LinearInterpolator());
        loadingAnim1.setDuration(500);

        ObjectAnimator loadingAnim2 = ObjectAnimator.ofFloat(this,"rotation",0,360);
        loadingAnim2.setRepeatCount(ValueAnimator.INFINITE);
        loadingAnim2.setRepeatMode(ValueAnimator.RESTART);
        loadingAnim2.setInterpolator(new LinearInterpolator());
        loadingAnim2.setDuration(800);

        loadingAnim = new AnimatorSet();
        loadingAnim.playSequentially(loadingAnim1,loadingAnim2);

        startLoadingAnim();
    }

    private void startLoadingAnim(){
        if (loadingAnim != null){
            loadingAnim.start();
        }
    }

    private void stopLoadingAnim(){
        if (loadingAnim != null){
            loadingAnim.removeAllListeners();
            loadingAnim.cancel();
        }
    }

    public void showLoading(){
        setVisibility(VISIBLE);
    }

    public void hideLoading(){
        setVisibility(GONE);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == VISIBLE){
            stopLoadingAnim();
            startLoadingAnim();
        }else {
            stopLoadingAnim();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startLoadingAnim();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopLoadingAnim();
    }
}
