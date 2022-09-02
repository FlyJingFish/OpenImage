package com.flyjingfish.openimagelib.widget;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.flyjingfish.openimagelib.utils.ImageViewUtils;

public class OpenImageViewAttacher implements View.OnLayoutChangeListener {
    private final OpenImageView mImageView;
    private final Matrix mBaseMatrix = new Matrix();
    private final Matrix mDrawMatrix = new Matrix();
    private OpenImageView.OpenScaleType mScaleType;
    private float mAutoCropHeightWidthRatio;

    public OpenImageViewAttacher(OpenImageView imageView) {
        this.mImageView = imageView;
        imageView.addOnLayoutChangeListener(this);
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

    }

    public void update() {
        Drawable drawable = mImageView.getDrawable();
        updateBaseMatrix(drawable);
    }

    private void updateBaseMatrix(Drawable drawable) {
        if (drawable == null) {
            return;
        }
        final float viewWidth = getImageViewWidth(mImageView);
        final float viewHeight = getImageViewHeight(mImageView);
        final int drawableWidth = drawable.getIntrinsicWidth();
        final int drawableHeight = drawable.getIntrinsicHeight();
        final float widthScale = viewWidth / drawableWidth;
        final float heightScale = viewHeight / drawableHeight;
        if (mScaleType == OpenImageView.OpenScaleType.START_CROP) {
            mBaseMatrix.reset();
            float scale = Math.max(widthScale, heightScale);
            mBaseMatrix.postScale(scale, scale);
            mBaseMatrix.postTranslate(0, 0);
            resetMatrix();
        } else if (mScaleType == OpenImageView.OpenScaleType.END_CROP) {
            mBaseMatrix.reset();
            float scale = Math.max(widthScale, heightScale);
            mBaseMatrix.postScale(scale, scale);
            mBaseMatrix.postTranslate((viewWidth - drawableWidth * scale),
                    (viewHeight - drawableHeight * scale));
            resetMatrix();
        } else if (mScaleType == OpenImageView.OpenScaleType.AUTO_START_CENTER_CROP) {
            float imageHeightWidthRatio = drawableHeight * 1f / drawableWidth;
            float viewHeightWidthRatio = viewHeight / viewWidth;
            float ratio = imageHeightWidthRatio/viewHeightWidthRatio;
            mBaseMatrix.reset();
            float scale = Math.max(widthScale, heightScale);
            mBaseMatrix.postScale(scale, scale);
            if (ratio >= mAutoCropHeightWidthRatio) {
                mBaseMatrix.postTranslate(0, 0);
            } else {
                mBaseMatrix.postTranslate((viewWidth - drawableWidth * scale) / 2,
                        (viewHeight - drawableHeight * scale) / 2);
            }
            resetMatrix();
        } else if (mScaleType == OpenImageView.OpenScaleType.AUTO_END_CENTER_CROP) {
            float imageHeightWidthRatio = drawableHeight * 1f / drawableWidth;
            float viewHeightWidthRatio = viewHeight / viewWidth;
            float ratio = imageHeightWidthRatio/viewHeightWidthRatio;
            mBaseMatrix.reset();
            float scale = Math.max(widthScale, heightScale);
            mBaseMatrix.postScale(scale, scale);
            if (ratio >= mAutoCropHeightWidthRatio) {
                mBaseMatrix.postTranslate((viewWidth - drawableWidth * scale),
                        (viewHeight - drawableHeight * scale));
            } else {
                mBaseMatrix.postTranslate((viewWidth - drawableWidth * scale) / 2,
                        (viewHeight - drawableHeight * scale) / 2);
            }
            resetMatrix();
        } else {
            ImageView.ScaleType scaleType = OpenImageView.OpenScaleType.getScaleType(mScaleType);
            if (scaleType != null){
                mImageView.setScaleType(scaleType);
            }
        }


    }

    private void resetMatrix() {
        setImageViewMatrix(getDrawMatrix());
    }

    private Matrix getDrawMatrix() {
        mDrawMatrix.set(mBaseMatrix);
        return mDrawMatrix;
    }

    private void setImageViewMatrix(Matrix matrix) {
        mImageView.setImageMatrix(matrix);
    }

    public void setScaleType(OpenImageView.OpenScaleType scaleType) {
        if (scaleType != mScaleType) {
            mScaleType = scaleType;
            update();
        }
    }

    public OpenImageView.OpenScaleType getOpenScaleType() {
        return mScaleType;
    }

    private int getImageViewWidth(ImageView imageView) {
        return imageView.getWidth() - ImageViewUtils.getViewPaddingLeft(imageView) - ImageViewUtils.getViewPaddingRight(imageView);
    }

    private int getImageViewHeight(ImageView imageView) {
        return imageView.getHeight() - imageView.getPaddingTop() - imageView.getPaddingBottom();
    }

    public Matrix getImageMatrix() {
        return mDrawMatrix;
    }

    public void setAutoCropHeightWidthRatio(float autoCropHeightWidthRatio) {
        this.mAutoCropHeightWidthRatio = autoCropHeightWidthRatio;
    }
}
