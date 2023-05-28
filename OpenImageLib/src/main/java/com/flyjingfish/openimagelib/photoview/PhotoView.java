/*
 Copyright 2011, 2012 Chris Banes.
 <p>
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 <p>
 http://www.apache.org/licenses/LICENSE-2.0
 <p>
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package com.flyjingfish.openimagelib.photoview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.LayoutDirection;
import android.view.GestureDetector;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.text.TextUtilsCompat;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;

import com.flyjingfish.openimagelib.OpenImageConfig;
import com.flyjingfish.shapeimageviewlib.ShapeImageView;

import java.util.Locale;


/**
 * A zoomable ImageView. See {@link PhotoViewAttacher} for most of the details on how the zooming
 * is accomplished
 */
@SuppressWarnings("unused")
public class PhotoView extends AppCompatImageView {

    private PhotoViewAttacher attacher;
    private ScaleType pendingScaleType;
    private OnMatrixChangedListener onMatrixChangedListener;
    private Bitmap subsamplingScaleBitmap;
    private PhotoViewSuperBigImageHelper photoViewSuperBigImageHelper;

    public PhotoView(Context context) {
        this(context, null);
    }

    public PhotoView(Context context, AttributeSet attr) {
        this(context, attr, 0);
    }

    public PhotoView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        init();
        isRtl = TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == LayoutDirection.RTL;
        mImagePaint = new Paint();
        mImagePaint.setXfermode(null);
        mRoundPaint = new Paint();
        mRoundPaint.setColor(Color.WHITE);
        mRoundPaint.setAntiAlias(true);
        mRoundPaint.setStyle(Paint.Style.FILL);
        mRoundPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
    }

    private void init() {
        attacher = new PhotoViewAttacher(this);
        photoViewSuperBigImageHelper = new PhotoViewSuperBigImageHelper(this);
        attacher.setOnMatrixChangeListener(rect -> {
            if (onMatrixChangedListener != null){
                onMatrixChangedListener.onMatrixChanged(rect);
            }
            if (!attacher.isExitMode()){
                photoViewSuperBigImageHelper.onMatrixChanged(rect);
            }
        });
        //We always pose as a Matrix scale type, though we can change to another scale type
        //via the attacher
        super.setScaleType(ScaleType.MATRIX);
        //apply the previously applied scale type
        if (pendingScaleType != null) {
            setScaleType(pendingScaleType);
            pendingScaleType = null;
        }
    }

    /**
     * Get the current {@link PhotoViewAttacher} for this view. Be wary of holding on to references
     * to this attacher, as it has a reference to this view, which, if a reference is held in the
     * wrong place, can cause memory leaks.
     *
     * @return the attacher.
     */
    public PhotoViewAttacher getAttacher() {
        return attacher;
    }

    @Override
    public ScaleType getScaleType() {
        return attacher.getScaleType();
    }

    @Override
    public Matrix getImageMatrix() {
        return attacher.getImageMatrix();
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        attacher.setOnLongClickListener(l);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        if (attacher.isZoomable()){
            attacher.setOnClickListener(l);
        }else {
            super.setOnClickListener(l);
        }
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (attacher == null) {
            pendingScaleType = scaleType;
        } else {
            attacher.setScaleType(scaleType);
        }
    }
    public void setSrcScaleType(ShapeImageView.ShapeScaleType scaleType) {
        if (attacher != null) {
            attacher.setSrcScaleType(scaleType);
        }
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        // setImageBitmap calls through to this method
        if (attacher != null) {
            attacher.update();
        }
        startGif();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        if (attacher != null) {
            attacher.update();
        }
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        if (attacher != null) {
            attacher.update();
        }
        startGif();
    }

    private void startGif(){
        Drawable drawable = getDrawable();
        if (drawable instanceof Animatable2Compat) {
            post(() -> {
                drawable.setVisible(true,false);
                if (!((Animatable2Compat) drawable).isRunning()){
                    ((Animatable2Compat) drawable).start();
                }
            });
        }
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        boolean changed = super.setFrame(l, t, r, b);
        if (changed) {
            attacher.update();
        }
        return changed;
    }

    public void setRotationTo(float rotationDegree) {
        attacher.setRotationTo(rotationDegree);
    }

    public void setRotationBy(float rotationDegree) {
        attacher.setRotationBy(rotationDegree);
    }

    public boolean isZoomable() {
        return attacher.isZoomable();
    }

    public void setZoomable(boolean zoomable) {
        attacher.setZoomable(zoomable);
    }

    public RectF getDisplayRect() {
        return attacher.getDisplayRect();
    }

    public void getDisplayMatrix(Matrix matrix) {
        attacher.getDisplayMatrix(matrix);
    }

    @SuppressWarnings("UnusedReturnValue") public boolean setDisplayMatrix(Matrix finalRectangle) {
        return attacher.setDisplayMatrix(finalRectangle);
    }

    public void getSuppMatrix(Matrix matrix) {
        attacher.getSuppMatrix(matrix);
    }

    public boolean setSuppMatrix(Matrix matrix) {
        return attacher.setDisplayMatrix(matrix);
    }

    public float getMinimumScale() {
        return attacher.getMinimumScale();
    }

    public float getMediumScale() {
        return attacher.getMediumScale();
    }

    public float getMaximumScale() {
        return attacher.getMaximumScale();
    }

    public float getScale() {
        return attacher.getScale();
    }

    public void setAllowParentInterceptOnEdge(boolean allow) {
        attacher.setAllowParentInterceptOnEdge(allow);
    }

    public void setMinimumScale(float minimumScale) {
        attacher.setMinimumScale(minimumScale);
    }

    public void setMediumScale(float mediumScale) {
        attacher.setMediumScale(mediumScale);
    }

    public void setMaximumScale(float maximumScale) {
        attacher.setMaximumScale(maximumScale);
    }

    public void setScaleLevels(float minimumScale, float mediumScale, float maximumScale) {
        attacher.setScaleLevels(minimumScale, mediumScale, maximumScale);
    }

    public void setOnMatrixChangeListener(OnMatrixChangedListener listener) {
        onMatrixChangedListener = listener;
    }

    public void setOnPhotoTapListener(OnPhotoTapListener listener) {
        attacher.setOnPhotoTapListener(listener);
    }

    public void setOnOutsidePhotoTapListener(OnOutsidePhotoTapListener listener) {
        attacher.setOnOutsidePhotoTapListener(listener);
    }

    public void setOnViewTapListener(OnViewTapListener listener) {
        attacher.setOnViewTapListener(listener);
    }

    public void setOnViewDragListener(OnViewDragListener listener) {
        attacher.setOnViewDragListener(listener);
    }

    public void setScale(float scale) {
        attacher.setScale(scale);
    }

    public void setScale(float scale, boolean animate) {
        attacher.setScale(scale, animate);
    }

    public void setScale(float scale, float focalX, float focalY, boolean animate) {
        attacher.setScale(scale, focalX, focalY, animate);
    }

    public void setZoomTransitionDuration(int milliseconds) {
        attacher.setZoomTransitionDuration(milliseconds);
    }

    public void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener onDoubleTapListener) {
        attacher.setOnDoubleTapListener(onDoubleTapListener);
    }

    public void setOnScaleChangeListener(OnScaleChangedListener onScaleChangedListener) {
        attacher.setOnScaleChangeListener(onScaleChangedListener);
    }

    public void setOnSingleFlingListener(OnSingleFlingListener onSingleFlingListener) {
        attacher.setOnSingleFlingListener(onSingleFlingListener);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (attacher != null){
            attacher.unRegisterDisplayListener();
        }
        Drawable drawable = getDrawable();
        if (drawable instanceof Animatable2Compat){
            ((Animatable2Compat) drawable).stop();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (attacher != null){
            attacher.registerDisplayListener();
        }
        Drawable drawable = getDrawable();
        if (drawable instanceof Animatable2Compat){
            ((Animatable2Compat) drawable).start();
        }
    }

    public void setStartWidth(float mStartWidth) {
        attacher.setStartWidth(mStartWidth);
    }

    public void setStartHeight(float mStartHeight) {
        attacher.setStartHeight(mStartHeight);
    }
    public void setExitMode(boolean mode) {
        attacher.setExitMode(mode);
        if (mode){
            subsamplingScaleBitmap = null;
            clearBitmap();
        }
    }


    public void setNoneClickView(boolean noneClickView) {
        attacher.setNoneClickView(noneClickView);
    }

    public void setExitFloat(float exitFloat) {
        attacher.setExitFloat(exitFloat);
    }

    public void setAutoCropHeightWidthRatio(float autoCropHeightWidthRatio) {
        this.attacher.setAutoCropHeightWidthRatio(autoCropHeightWidthRatio);
    }

    private ShapeImageView.ShapeType shapeType;
    private float leftTopRadius;
    private float leftBottomRadius;
    private float rightTopRadius;
    private float rightBottomRadius;
    private boolean isRtl;
    private final Paint mImagePaint;
    private final Paint mRoundPaint;
    private float startTopRadius;
    private float startBottomRadius;
    private float endTopRadius;
    private float endBottomRadius;
    private Rect showRect;
    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        if (attacher != null && attacher.isExitMode() && shapeType == ShapeImageView.ShapeType.OVAL) {
            canvas.saveLayer(new RectF(0, 0, getWidth(), getHeight()), mImagePaint, Canvas.ALL_SAVE_FLAG);
            super.onDraw(canvas);
            drawSuperBigImage(canvas);
            drawOval(canvas);
            canvas.restore();
        } else if (attacher != null && attacher.isExitMode() && shapeType == ShapeImageView.ShapeType.RECTANGLE) {
            canvas.saveLayer(new RectF(0, 0, getWidth(), getHeight()), mImagePaint, Canvas.ALL_SAVE_FLAG);
            super.onDraw(canvas);
            drawSuperBigImage(canvas);
            drawRectangle(canvas);
            canvas.restore();
        } else {
            super.onDraw(canvas);
            drawSuperBigImage(canvas);
        }

    }

    private void drawSuperBigImage(Canvas canvas){
        if (subsamplingScaleBitmap != null && showRect != null){
            canvas.drawBitmap(subsamplingScaleBitmap,new Rect(0,0,subsamplingScaleBitmap.getWidth(),subsamplingScaleBitmap.getHeight()),showRect,mImagePaint);
        }
    }

    private void drawOval(Canvas canvas) {
        drawTopLeft(canvas);
        drawTopRight(canvas);
        drawBottomLeft(canvas);
        drawBottomRight(canvas);
    }

    private void drawRectangle(Canvas canvas) {
        if (ViewUtils.getRtlValue(isRtl ? endTopRadius : startTopRadius, this.leftTopRadius) > 0) {
            drawTopLeft(canvas);
        }
        if (ViewUtils.getRtlValue(isRtl ? startTopRadius : endTopRadius, this.rightTopRadius) > 0) {
            drawTopRight(canvas);
        }
        if (ViewUtils.getRtlValue(isRtl ? endBottomRadius : startBottomRadius, this.leftBottomRadius) > 0) {
            drawBottomLeft(canvas);
        }
        if (ViewUtils.getRtlValue(isRtl ? startBottomRadius : endBottomRadius, this.rightBottomRadius) > 0) {
            drawBottomRight(canvas);
        }
    }

    private void drawTopLeft(Canvas canvas) {
        Path path = new Path();
        int paddingLeft = ViewUtils.getViewPaddingLeft(this);
        int paddingRight = ViewUtils.getViewPaddingRight(this);
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        if (shapeType == ShapeImageView.ShapeType.OVAL) {
            int height = getHeight();
            int width = getWidth();
            path.moveTo(paddingLeft, (height - paddingTop - paddingBottom) / 2 + paddingTop);
            path.lineTo(paddingLeft, paddingTop);
            path.lineTo((width - paddingLeft - paddingRight) / 2 + paddingLeft, paddingTop);
            path.arcTo(new RectF(paddingLeft, paddingTop, width - paddingRight, height - paddingBottom), -90, -90);
        } else {
            float leftTopRadius = ViewUtils.getRtlValue(isRtl ? endTopRadius : startTopRadius, this.leftTopRadius);

            path.moveTo(paddingLeft, paddingTop + leftTopRadius);
            path.lineTo(paddingLeft, paddingTop);
            path.lineTo(paddingLeft + leftTopRadius, paddingTop);
            path.arcTo(new RectF(paddingLeft, paddingTop, paddingLeft + leftTopRadius * 2, paddingTop + leftTopRadius * 2), -90, -90);
        }
        path.close();
        canvas.drawPath(path, mRoundPaint);
    }

    private void drawTopRight(Canvas canvas) {
        int width = getWidth();
        Path path = new Path();
        int paddingLeft = ViewUtils.getViewPaddingLeft(this);
        int paddingRight = ViewUtils.getViewPaddingRight(this);
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        if (shapeType == ShapeImageView.ShapeType.OVAL) {
            int height = getHeight();
            path.moveTo((width - paddingLeft - paddingRight) / 2 + paddingLeft, paddingTop);
            path.lineTo(width - paddingRight, paddingTop);
            path.lineTo(width - paddingRight, (height - paddingTop - paddingBottom) / 2 + paddingTop);
            path.arcTo(new RectF(paddingLeft, paddingTop, width - paddingRight, height - paddingBottom), 0, -90);
        } else {
            float rightTopRadius = ViewUtils.getRtlValue(isRtl ? startTopRadius : endTopRadius, this.rightTopRadius);
            path.moveTo(width - rightTopRadius - paddingRight, paddingTop);
            path.lineTo(width - paddingRight, paddingTop);
            path.lineTo(width - paddingRight, paddingTop + rightTopRadius);
            path.arcTo(new RectF(width - paddingRight - 2 * rightTopRadius, paddingTop, width - paddingRight, paddingTop + rightTopRadius * 2), 0, -90);
        }
        path.close();
        canvas.drawPath(path, mRoundPaint);
    }

    private void drawBottomLeft(Canvas canvas) {
        int height = getHeight();
        Path path = new Path();
        int paddingLeft = ViewUtils.getViewPaddingLeft(this);
        int paddingRight = ViewUtils.getViewPaddingRight(this);
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        if (shapeType == ShapeImageView.ShapeType.OVAL) {
            int width = getWidth();
            path.moveTo(paddingLeft, (height - paddingTop - paddingBottom) / 2 + paddingTop);
            path.lineTo(paddingLeft, height - paddingBottom);
            path.lineTo((width - paddingLeft - paddingRight) / 2 + paddingLeft, height - paddingBottom);
            path.arcTo(new RectF(paddingLeft, paddingTop, width - paddingRight, height - paddingBottom), 90, 90);
        } else {
            float leftBottomRadius = ViewUtils.getRtlValue(isRtl ? endBottomRadius : startBottomRadius, this.leftBottomRadius);
            path.moveTo(paddingLeft, height - paddingBottom - leftBottomRadius);
            path.lineTo(paddingLeft, height - paddingBottom);
            path.lineTo(paddingLeft + leftBottomRadius, height - paddingBottom);
            path.arcTo(new RectF(paddingLeft, height - paddingBottom - 2 * leftBottomRadius, paddingLeft + leftBottomRadius * 2, height - paddingBottom), 90, 90);
        }
        path.close();
        canvas.drawPath(path, mRoundPaint);
    }

    private void drawBottomRight(Canvas canvas) {
        int height = getHeight();
        int width = getWidth();
        Path path = new Path();
        int paddingLeft = ViewUtils.getViewPaddingLeft(this);
        int paddingRight = ViewUtils.getViewPaddingRight(this);
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        if (shapeType == ShapeImageView.ShapeType.OVAL) {
            path.moveTo((width - paddingLeft - paddingRight) / 2 + paddingLeft, height - paddingBottom);
            path.lineTo(width - paddingRight, height - paddingBottom);
            path.lineTo(width - paddingRight, (height - paddingTop - paddingBottom) / 2 + paddingTop);
            path.arcTo(new RectF(paddingLeft, paddingTop, width - paddingRight, height - paddingBottom), 0, 90);
        } else {
            float rightBottomRadius = ViewUtils.getRtlValue(isRtl ? startBottomRadius : endBottomRadius, this.rightBottomRadius);
            path.moveTo(width - paddingRight - rightBottomRadius, height - paddingBottom);
            path.lineTo(width - paddingRight, height - paddingBottom);
            path.lineTo(width - paddingRight, height - paddingBottom - rightBottomRadius);
            path.arcTo(new RectF(width - paddingRight - 2 * rightBottomRadius, height - paddingBottom - 2 * rightBottomRadius, width - paddingRight, height - paddingBottom), 0, 90);
        }
        path.close();
        canvas.drawPath(path, mRoundPaint);
    }

    public void setRadius(int leftTopRadius,int rightTopRadius,int rightBottomRadius,int leftBottomRadius) {
        this.leftTopRadius = leftTopRadius;
        this.rightTopRadius = rightTopRadius;
        this.rightBottomRadius = rightBottomRadius;
        this.leftBottomRadius = leftBottomRadius;
        invalidate();
    }
    public void setRelativeRadius(int startTopRadius,int endTopRadius,int endBottomRadius,int startBottomRadius) {
        this.startTopRadius = startTopRadius;
        this.endTopRadius = endTopRadius;
        this.endBottomRadius = endBottomRadius;
        this.startBottomRadius = startBottomRadius;
        invalidate();
    }

    public void setShapeType(ShapeImageView.ShapeType shapeType) {
        this.shapeType = shapeType;
    }

    /**
     * 设置之后才可以支持超大图,必须在设置图片之后才可以调用，否则无作用
     * @param filePath 图片本地路径
     */
    public void setImageFilePath(String filePath) {
        if (photoViewSuperBigImageHelper != null && OpenImageConfig.getInstance().isSupportSuperBigImage()){
            photoViewSuperBigImageHelper.setImageFilePath(filePath);
        }
    }

    void setSubsamplingScaleBitmap(Bitmap bitmap,Rect rect) {
        this.showRect = rect;
        if (!attacher.isExitMode()){
            this.subsamplingScaleBitmap = bitmap;
        }
        invalidate();
    }

    Bitmap getSubsamplingScaleBitmap() {
        return subsamplingScaleBitmap;
    }

    void setShowRect(Rect rect) {
        this.showRect = rect;
        invalidate();
    }

    void moving(){
        if (photoViewSuperBigImageHelper != null){
            photoViewSuperBigImageHelper.moving();
        }
    }

    void clearBitmap(){
        setSubsamplingScaleBitmap(null,null);
    }
}
