package com.flyjingfish.openimagefulllib;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.flyjingfish.openimagelib.photoview.OnMatrixChangedListener;
import com.flyjingfish.openimagelib.photoview.PhotoView;
import com.flyjingfish.openimagelib.photoview.PhotoViewAttacher;


public class ScaleRelativeLayout extends RelativeLayout {

    private final VideoPlayerAttacher attacher;
    private final RectF mDrawRect = new RectF();

    private GSYVideoPlayer gsyVideoPlayer;
    private ScaleDrawable scaleDrawable;
    private PhotoViewAttacher photoViewAttacher;
    private final OnMatrixChangedListener onPhotoMatrixChangedListener = this::onPhotoChange;
    private final OnMatrixChangedListener onVideoMatrixChangedListener = this::onVideoChange;

    public ScaleRelativeLayout(@NonNull Context context) {
        this(context,null);
    }

    public ScaleRelativeLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ScaleRelativeLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        attacher = new VideoPlayerAttacher(ScaleRelativeLayout.this);
        attacher.addOnMatrixChangeListener(onVideoMatrixChangedListener);
        initPlayer();
    }


    private void onVideoChange(RectF rect){
        if (gsyVideoPlayer != null && photoViewAttacher != null){
            if (gsyVideoPlayer.isShowingThumb()){
                return;
            }
            photoViewAttacher.removeOnMatrixChangeListener(onPhotoMatrixChangedListener);
            syncPhotoAttacher();
            photoViewAttacher.addOnMatrixChangeListener(onPhotoMatrixChangedListener);
        }
    }

    private void onPhotoChange(RectF rect){
        if (attacher == null){
            return;
        }
        if (!gsyVideoPlayer.isShowingThumb()){
            return;
        }
        attacher.removeOnMatrixChangeListener(onVideoMatrixChangedListener);
        syncAttacher();
        attacher.addOnMatrixChangeListener(onVideoMatrixChangedListener);


    }

    private void initPlayer(){
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                gsyVideoPlayer = Util.getVideoPlayer(ScaleRelativeLayout.this);
                if (gsyVideoPlayer instanceof ScaleOpenImageVideoPlayer scaleOpenImageVideoPlayer){
                    PhotoView coverImageView = scaleOpenImageVideoPlayer.getCoverImageView();
                    photoViewAttacher = coverImageView.getAttacher();
                    attacher.setOnChangedListener(() -> {
                        attacher.removeOnMatrixChangeListener(onVideoMatrixChangedListener);
                        photoViewAttacher.removeOnMatrixChangeListener(onPhotoMatrixChangedListener);
                        // 视频变了
                        syncAttacher();
                        attacher.addOnMatrixChangeListener(onVideoMatrixChangedListener);
                        photoViewAttacher.addOnMatrixChangeListener(onPhotoMatrixChangedListener);
                    });
                    photoViewAttacher.setOnChangedListener(() -> {
                        attacher.removeOnMatrixChangeListener(onVideoMatrixChangedListener);
                        photoViewAttacher.removeOnMatrixChangeListener(onPhotoMatrixChangedListener);
                        // 图片变了
                        syncPhotoAttacher();
                        attacher.addOnMatrixChangeListener(onVideoMatrixChangedListener);
                        photoViewAttacher.addOnMatrixChangeListener(onPhotoMatrixChangedListener);
                    });

                    photoViewAttacher.addOnMatrixChangeListener(onPhotoMatrixChangedListener);

                }
            }
        });
    }

    private void syncAttacher(){
        float scaleX = photoViewAttacher.getValue(Matrix.MSCALE_X);
        float translateX = photoViewAttacher.getValue(Matrix.MTRANS_X);
        float translateY = photoViewAttacher.getValue(Matrix.MTRANS_Y);
        attacher.setScaleIgnoreBounds(scaleX);
        attacher.postTranslate(translateX,translateY);
    }

    private void syncPhotoAttacher(){
        float scaleX = attacher.getValue(Matrix.MSCALE_X);
        float translateX = attacher.getValue(Matrix.MTRANS_X);
        float translateY = attacher.getValue(Matrix.MTRANS_Y);
        photoViewAttacher.setScaleIgnoreBounds(scaleX);
        photoViewAttacher.postTranslate(translateX,translateY);
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

    public void setImageRectF(RectF displayRect) {
        mDrawRect.set(displayRect.left,displayRect.top,displayRect.right,displayRect.bottom);
        invalidateLayout();
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
            if (l != null){
                l.onClick(v);
            }
        });
    }

    @Override
    public void setOnLongClickListener(@Nullable OnLongClickListener l) {
        attacher.setOnLongClickListener(v -> {
            if (l != null){
                return l.onLongClick(v);
            }
            return false;
        });
    }
}
