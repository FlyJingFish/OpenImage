package com.flyjingfish.openimagefulllib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class ScaleRelativeLayout extends RelativeLayout {

    private PhotoViewAttacher attacher;
    private final RectF mDrawRect = new RectF();

    private GSYVideoPlayer gsyVideoPlayer;

    public ScaleRelativeLayout(@NonNull Context context) {
        this(context,null);
    }

    public ScaleRelativeLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ScaleRelativeLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        attacher = new PhotoViewAttacher(ScaleRelativeLayout.this);
        attacher.setOnMatrixChangeListener(rect -> {
            mDrawRect.set(rect.left,rect.top,rect.right,rect.bottom);
            invalidateLayout();

        });

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
                    attacher.setOnProxyTouchListener((v, event) -> {
                        scaleOpenImageVideoPlayer.getCoverImageView().getAttacher().onTouch(ScaleRelativeLayout.this,event);
                        return false;
                    });

                    scaleOpenImageVideoPlayer.getCoverImageView().getAttacher().setOnProxyTouchListener((v, event) -> {
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
        initPlayer();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        gsyVideoPlayer = null;
    }

    public PhotoViewAttacher getAttacher() {
        return attacher;
    }

    public ScaleDrawable getDrawable(){
        return findViewById(R.id.surface_container);
    }

    public void setImageMatrix(Matrix matrix) {
    }


    public void invalidateLayout(){

        ScaleDrawable drawable = getDrawable();
        if (drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0){
            return;
        }
        float scaleX = mDrawRect.width()*1f/drawable.getIntrinsicWidth();
        float scaleY = mDrawRect.height()*1f/drawable.getIntrinsicHeight();

        float centerX = mDrawRect.centerX();
        float centerY = mDrawRect.centerY();
        float x = centerX - getWidth()/2;
        float y = centerY - getHeight()/2;
        Log.e("invalidateLayout", mDrawRect +",centerX="+mDrawRect.centerX()+",centerY="+mDrawRect.centerY());
        drawable.setTranslationX(x);
        drawable.setTranslationY(y);
        drawable.setScaleX(scaleX);
        drawable.setScaleY(scaleY);
    }
//    private OnTouchListener onTouchListener;
//    @Override
//    public void setOnTouchListener(OnTouchListener l) {
//        onTouchListener = l;
////        gsyVideoPlayer.setOnTouchListener(l);
//        if (gsyVideoPlayer != null){
//            gsyVideoPlayer.setProxyOnTouchListener(onTouchListener);
//        }
//    }


    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        attacher.setOnClickListener(v -> {
            if (l != null){
                l.onClick(v);
            }
            gsyVideoPlayer.onClick(getDrawable());
        });
    }

    @Override
    public void setOnLongClickListener(@Nullable OnLongClickListener l) {
        attacher.setOnLongClickListener(l);
    }
}
