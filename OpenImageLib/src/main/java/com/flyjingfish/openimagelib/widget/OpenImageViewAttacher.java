package com.flyjingfish.openimagelib.widget;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.flyjingfish.openimagelib.OpenImageConfig;

public class OpenImageViewAttacher implements View.OnLayoutChangeListener {
    private final OpenImageView mImageView;
    private final Matrix mBaseMatrix = new Matrix();
    private final Matrix mDrawMatrix = new Matrix();
    private final Matrix mSuppMatrix = new Matrix();
    private final RectF mDisplayRect = new RectF();
    private OpenImageView.OpenScaleType mScaleType;

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
            mBaseMatrix.postTranslate(0,0);
            resetMatrix();
        } else if (mScaleType == OpenImageView.OpenScaleType.END_CROP) {
            mBaseMatrix.reset();
            float scale = Math.max(widthScale, heightScale);
            mBaseMatrix.postScale(scale, scale);
            mBaseMatrix.postTranslate((viewWidth - drawableWidth * scale),
                    (viewHeight - drawableHeight * scale));
            resetMatrix();
        } else {
            mImageView.setScaleType(OpenImageView.OpenScaleType.getScaleType(mScaleType));
        }


    }

    private void resetMatrix() {
        mSuppMatrix.reset();
        setImageViewMatrix(getDrawMatrix());
        checkMatrixBounds();
    }

    private Matrix getDrawMatrix() {
        mDrawMatrix.set(mBaseMatrix);
        mDrawMatrix.postConcat(mSuppMatrix);
        return mDrawMatrix;
    }

    public RectF getDisplayRect() {
        checkMatrixBounds();
        return getDisplayRect(getDrawMatrix());
    }

    private RectF getDisplayRect(Matrix matrix) {
        Drawable d = mImageView.getDrawable();
        if (d != null) {
            mDisplayRect.set(0, 0, d.getIntrinsicWidth(),
                    d.getIntrinsicHeight());
            matrix.mapRect(mDisplayRect);
            return mDisplayRect;
        }
        return null;
    }

    private boolean checkMatrixBounds() {
        final RectF rect = getDisplayRect(getDrawMatrix());
        if (rect == null) {
            return false;
        }
        final int viewWidth = getImageViewWidth(mImageView);
        final int viewHeight = getImageViewHeight(mImageView);
        final float height = rect.height(), width = rect.width();
        float deltaX = 0, deltaY = 0;
        if (height <= viewHeight) {
            deltaY = (viewHeight - height) / 2 - rect.top;
        } else if (rect.top > 0) {
            deltaY = -rect.top;
        } else if (rect.bottom < viewHeight) {
            deltaY = viewHeight - rect.bottom;
        }
        if (width <= viewWidth) {
            deltaX = (viewWidth - width) / 2 - rect.left;
        } else if (rect.left > 0) {
            deltaX = -rect.left;
        } else if (rect.right < viewWidth) {
            deltaX = viewWidth - rect.right;
        }
        mSuppMatrix.postTranslate(deltaX, deltaY);
        return true;
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
        return imageView.getWidth() - imageView.getPaddingLeft() - imageView.getPaddingRight();
    }

    private int getImageViewHeight(ImageView imageView) {
        return imageView.getHeight() - imageView.getPaddingTop() - imageView.getPaddingBottom();
    }

//    public ImageView.ScaleType getScaleType() {
//        return mScaleType;
//    }

    public Matrix getImageMatrix() {
        return mDrawMatrix;
    }
}
