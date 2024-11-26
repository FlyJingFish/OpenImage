package com.flyjingfish.openimagefulllib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.flyjingfish.openimagelib.photoview.PhotoViewAttacher;


public class ScaleRelativeLayout extends RelativeLayout {

    private final VideoPlayerAttacher attacher;
    private final RectF mDrawRect = new RectF();

    private GSYVideoPlayer gsyVideoPlayer;
    private ScaleDrawable scaleDrawable;

    public ScaleRelativeLayout(@NonNull Context context) {
        this(context,null);
    }

    public ScaleRelativeLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ScaleRelativeLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        attacher = new VideoPlayerAttacher(ScaleRelativeLayout.this);
        attacher.setOnMatrixChangeListener(rect -> {
            mDrawRect.set(rect.left,rect.top,rect.right,rect.bottom);
            invalidateLayout();

        });
        initPlayer();
    }
    private void initPlayer(){
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                gsyVideoPlayer = Util.getVideoPlayer(ScaleRelativeLayout.this);
                if (gsyVideoPlayer instanceof ScaleOpenImageVideoPlayer){
                    ScaleOpenImageVideoPlayer scaleOpenImageVideoPlayer = (ScaleOpenImageVideoPlayer) gsyVideoPlayer;
                    PhotoViewAttacher photoViewAttacher = scaleOpenImageVideoPlayer.getCoverImageView().getAttacher();
                    attacher.setOnChangedListener(() -> {
                        // 视频变了
                        float scaleX = photoViewAttacher.getValue(Matrix.MSCALE_X);
                        float translateX = photoViewAttacher.getValue(Matrix.MTRANS_X);
                        float translateY = photoViewAttacher.getValue(Matrix.MTRANS_Y);
                        attacher.setScale(scaleX);
                        attacher.postTranslate(translateX,translateY);
                    });
                    scaleOpenImageVideoPlayer.getCoverImageView().getAttacher().setOnChangedListener(() -> {
                        // 图片变了
                        float scaleX = attacher.getValue(Matrix.MSCALE_X);
                        float translateX = attacher.getValue(Matrix.MTRANS_X);
                        float translateY = attacher.getValue(Matrix.MTRANS_Y);
                        photoViewAttacher.setScale(scaleX);
                        photoViewAttacher.postTranslate(translateX,translateY);
                    });

                    attacher.setOnProxyTouchListener((v, event) -> {
                        photoViewAttacher.onTouch(ScaleRelativeLayout.this,event);
                        return false;
                    });
                    photoViewAttacher.setOnProxyTouchListener((v, event) -> {
                        attacher.onTouch(v,event);
                        return false;
                    });

                }
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (attacher != null){
            attacher.registerDisplayListener();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (attacher != null){
            attacher.unRegisterDisplayListener();
        }
    }

    public VideoPlayerAttacher getAttacher() {
        return attacher;
    }

    public ScaleDrawable getDrawable(){
        if (scaleDrawable == null){
            scaleDrawable = findViewById(R.id.surface_container);
        }
        return scaleDrawable;
    }

    public void setImageMatrix(Matrix matrix) {
    }


    private void invalidateLayout(){

        ScaleDrawable drawable = getDrawable();
        if (drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0){
            return;
        }
        float scaleX = mDrawRect.width() /drawable.getIntrinsicWidth();
        float scaleY = mDrawRect.height() /drawable.getIntrinsicHeight();

        float centerX = mDrawRect.centerX();
        float centerY = mDrawRect.centerY();
        float x = centerX - getWidth()/2f;
        float y = centerY - getHeight()/2f;
        drawable.setTranslationX(x);
        drawable.setTranslationY(y);
        drawable.setScaleX(scaleX);
        drawable.setScaleY(scaleY);
    }


    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        attacher.setOnClickListener(v -> {
            gsyVideoPlayer.onClick(getDrawable());
        });
    }

}
