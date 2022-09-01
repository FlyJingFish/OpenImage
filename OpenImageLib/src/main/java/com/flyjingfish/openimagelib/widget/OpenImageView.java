package com.flyjingfish.openimagelib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatImageView;

import com.flyjingfish.openimagelib.R;
import com.flyjingfish.openimagelib.utils.ImageViewUtils;

import java.io.Serializable;


public class OpenImageView extends AppCompatImageView {
    private OpenImageViewAttacher mAttacher;
    private OpenScaleType mPendingScaleType;
    private float mAutoCropHeightWidthRatio;
    private int leftTopRadius;
    private int leftBottomRadius;
    private int rightTopRadius;
    private int rightBottomRadius;
    private final Paint mImagePaint;
    private final Paint mRoundPaint;
    private ShapeType shapeType;

    public OpenImageView(Context context) {
        this(context, null);
    }

    public OpenImageView(Context context, AttributeSet attr) {
        this(context, attr, 0);
    }

    public OpenImageView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        TypedArray a = context.obtainStyledAttributes(attr, R.styleable.OpenImageView);
        mPendingScaleType = OpenScaleType.getType(a.getInt(R.styleable.OpenImageView_openScaleType, 0));
        mAutoCropHeightWidthRatio = a.getFloat(R.styleable.OpenImageView_autoCrop_height_width_ratio, 2f);
        int radius = a.getDimensionPixelSize(R.styleable.OpenImageView_openImage_radius, 0);
        leftTopRadius = a.getDimensionPixelSize(R.styleable.OpenImageView_openImage_left_top_radius, radius);
        leftBottomRadius = a.getDimensionPixelSize(R.styleable.OpenImageView_openImage_left_bottom_radius, radius);
        rightTopRadius = a.getDimensionPixelSize(R.styleable.OpenImageView_openImage_right_top_radius, radius);
        rightBottomRadius = a.getDimensionPixelSize(R.styleable.OpenImageView_openImage_right_bottom_radius, radius);
        shapeType = ShapeType.getType(a.getInt(R.styleable.OpenImageView_openImage_shape, 0));
        a.recycle();

        mImagePaint = new Paint();
        mImagePaint.setXfermode(null);
        mRoundPaint = new Paint();
        mRoundPaint.setColor(Color.WHITE);
        mRoundPaint.setAntiAlias(true);
        mRoundPaint.setStyle(Paint.Style.FILL);
        mRoundPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

        init();
    }

    private void init() {
        mAttacher = new OpenImageViewAttacher(this);
        mAttacher.setAutoCropHeightWidthRatio(mAutoCropHeightWidthRatio);
        if (mPendingScaleType != null) {
            super.setScaleType(ScaleType.MATRIX);
            setOpenScaleType(mPendingScaleType);
            mPendingScaleType = null;
        }else {
            setOpenScaleType(OpenScaleType.getType(getScaleType()));
        }
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        super.setScaleType(scaleType);
    }

    @Override
    public Matrix getImageMatrix() {
        return mAttacher.getImageMatrix();
    }

    public void setOpenScaleType(OpenScaleType scaleType) {
        if (mAttacher == null) {
            mPendingScaleType = scaleType;
        } else {
            mAttacher.setScaleType(scaleType);
        }
    }

    public float getAutoCropHeightWidthRatio() {
        return mAutoCropHeightWidthRatio;
    }

    public void setAutoCropHeightWidthRatio(float autoCropHeightWidthRatio) {
        this.mAutoCropHeightWidthRatio = autoCropHeightWidthRatio;
        if (mAttacher != null) {
            mAttacher.setAutoCropHeightWidthRatio(autoCropHeightWidthRatio);
            mAttacher.update();
        }

    }

    public OpenScaleType getOpenScaleType() {
        return mAttacher.getOpenScaleType();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        // setImageBitmap calls through to this method
        if (mAttacher != null) {
            mAttacher.update();
        }
    }


    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        if (mAttacher != null) {
            mAttacher.update();
        }
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        if (mAttacher != null) {
            mAttacher.update();
        }
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        boolean changed = super.setFrame(l, t, r, b);
        if (changed) {
            mAttacher.update();
        }
        return changed;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        OpenScaleType openScaleType = mAttacher.getOpenScaleType();
        boolean isOpenCrop = openScaleType == OpenScaleType.START_CROP
                || openScaleType == OpenScaleType.END_CROP
                || openScaleType == OpenScaleType.AUTO_START_CENTER_CROP
                || openScaleType == OpenScaleType.AUTO_END_CENTER_CROP;
        if (shapeType == ShapeType.OVAL){
            canvas.saveLayer(new RectF(0, 0, canvas.getWidth(), canvas.getHeight()), mImagePaint, Canvas.ALL_SAVE_FLAG);
            super.onDraw(canvas);
            drawOval(canvas);
            if (isOpenCrop) {
                drawPadding(canvas);
            }
            canvas.restore();
        }else if (leftTopRadius > 0 || leftBottomRadius > 0 || rightTopRadius > 0 || rightBottomRadius > 0){
            canvas.saveLayer(new RectF(0, 0, canvas.getWidth(), canvas.getHeight()), mImagePaint, Canvas.ALL_SAVE_FLAG);
            super.onDraw(canvas);
            drawRectangle(canvas);
            if (isOpenCrop) {
                drawPadding(canvas);
            }
            canvas.restore();
        }else {
            if (isOpenCrop) {
                canvas.saveLayer(new RectF(0, 0, canvas.getWidth(), canvas.getHeight()), mImagePaint, Canvas.ALL_SAVE_FLAG);
                super.onDraw(canvas);
                drawPadding(canvas);
                canvas.restore();
            } else {
                super.onDraw(canvas);
            }
        }



    }

    private void drawPadding(Canvas canvas){
        drawLeft(canvas);
        drawTop(canvas);
        drawRight(canvas);
        drawBottom(canvas);
    }

    private int getImageViewWidth(ImageView imageView) {
        return imageView.getWidth() - imageView.getPaddingLeft() - imageView.getPaddingRight();
    }

    private int getImageViewHeight(ImageView imageView) {
        return imageView.getHeight() - imageView.getPaddingTop() - imageView.getPaddingBottom();
    }
    private void drawOval(Canvas canvas){
        drawTopLeft(canvas);
        drawTopRight(canvas);
        drawBottomLeft(canvas);
        drawBottomRight(canvas);
    }
    private void drawRectangle(Canvas canvas){
        if (leftTopRadius > 0) {
            drawTopLeft(canvas);
        }
        if (rightTopRadius > 0) {
            drawTopRight(canvas);
        }
        if (leftBottomRadius > 0) {
            drawBottomLeft(canvas);
        }
        if (rightBottomRadius > 0) {
            drawBottomRight(canvas);
        }
    }

    private void drawTopLeft(Canvas canvas) {
        Path path = new Path();
        if (shapeType == ShapeType.OVAL){
            int height = getHeight();
            int width = getWidth();
            path.moveTo(0, height/2);
            path.lineTo(0, 0);
            path.lineTo(width/2, 0);
            path.arcTo(new RectF(0, 0, width, height), -90, -90);
        }else {
            path.moveTo(0, leftTopRadius);
            path.lineTo(0, 0);
            path.lineTo(leftTopRadius, 0);
            path.arcTo(new RectF(0, 0, leftTopRadius * 2, leftTopRadius * 2), -90, -90);
        }
        path.close();
        canvas.drawPath(path, mRoundPaint);
    }

    private void drawTopRight(Canvas canvas) {
        int width = getWidth();
        Path path = new Path();
        if (shapeType == ShapeType.OVAL){
            int height = getHeight();
            path.moveTo(width/2, 0);
            path.lineTo(width, 0);
            path.lineTo(width, height/2);
            path.arcTo(new RectF(0, 0, width, height), 0, -90);

        }else {
            path.moveTo(width - rightTopRadius, 0);
            path.lineTo(width, 0);
            path.lineTo(width, rightTopRadius);
            path.arcTo(new RectF(width - 2 * rightTopRadius, 0, width, rightTopRadius * 2), 0, -90);
        }
        path.close();
        canvas.drawPath(path, mRoundPaint);
    }

    private void drawBottomLeft(Canvas canvas) {
        int height = getHeight();
        Path path = new Path();
        if (shapeType == ShapeType.OVAL){
            int width = getWidth();
            path.moveTo(0, height/2);
            path.lineTo(0, height);
            path.lineTo(width/2, height);
            path.arcTo(new RectF(0, 0, width, height), 90, 90);
        }else {
            path.moveTo(0, height - leftBottomRadius);
            path.lineTo(0, height);
            path.lineTo(leftBottomRadius, height);
            path.arcTo(new RectF(0, height - 2 * leftBottomRadius, leftBottomRadius * 2, height), 90, 90);
        }
        path.close();
        canvas.drawPath(path, mRoundPaint);
    }

    private void drawBottomRight(Canvas canvas) {
        int height = getHeight();
        int width = getWidth();
        Path path = new Path();
        if (shapeType == ShapeType.OVAL){
            path.moveTo(width/2, height);
            path.lineTo(width, height);
            path.lineTo(width, height/2);
            path.arcTo(new RectF(0, 0, width, height), 0, 90);
        }else {
            path.moveTo(width - rightBottomRadius, height);
            path.lineTo(width, height);
            path.lineTo(width, height - rightBottomRadius);
            path.arcTo(new RectF(width - 2 * rightBottomRadius, height - 2 * rightBottomRadius, width, height), 0, 90);
        }
        path.close();
        canvas.drawPath(path, mRoundPaint);
    }

    private void drawLeft(Canvas canvas) {
        Path path = new Path();
        int height = getHeight();
        int paddingLeft = ImageViewUtils.getViewPaddingLeft(this);
        path.moveTo(0, height);
        path.lineTo(0, 0);
        path.lineTo(paddingLeft, 0);
        path.lineTo(paddingLeft, height);
        path.close();
        canvas.drawPath(path, mRoundPaint);
    }

    private void drawTop(Canvas canvas) {
        Path path = new Path();
        int height = getHeight();
        int width = getWidth();
        int paddingTop = getPaddingTop();
        path.moveTo(0, 0);
        path.lineTo(width, 0);
        path.lineTo(width, paddingTop);
        path.lineTo(0, paddingTop);
        path.close();
        canvas.drawPath(path, mRoundPaint);
    }

    private void drawRight(Canvas canvas) {
        Path path = new Path();
        int height = getHeight();
        int width = getWidth();
        int paddingRight = ImageViewUtils.getViewPaddingRight(this);
        path.moveTo(width, 0);
        path.lineTo(width, height);
        path.lineTo(width - paddingRight, height);
        path.lineTo(width - paddingRight, 0);
        path.close();
        canvas.drawPath(path, mRoundPaint);
    }

    private void drawBottom(Canvas canvas) {
        Path path = new Path();
        int height = getHeight();
        int width = getWidth();
        int paddingBottom = getPaddingBottom();
        path.moveTo(0, height);
        path.lineTo(width, height);
        path.lineTo(width, height - paddingBottom);
        path.lineTo(0, height - paddingBottom);
        path.close();
        canvas.drawPath(path, mRoundPaint);
    }

    public enum OpenScaleType implements Serializable {
        FIT_XY(1), FIT_START(2), FIT_CENTER(3), FIT_END(4),
        CENTER(5), CENTER_CROP(6), CENTER_INSIDE(7), START_CROP(8),
        END_CROP(9), AUTO_START_CENTER_CROP(10), AUTO_END_CENTER_CROP(11);

        OpenScaleType(int ni) {
            type = ni;
        }

        public static OpenScaleType getType(int ni) {
            if (ni == 1) {
                return FIT_XY;
            } else if (ni == 2) {
                return FIT_START;
            } else if (ni == 3) {
                return FIT_CENTER;
            } else if (ni == 4) {
                return FIT_END;
            } else if (ni == 5) {
                return CENTER;
            } else if (ni == 6) {
                return CENTER_CROP;
            } else if (ni == 7) {
                return CENTER_INSIDE;
            } else if (ni == 8) {
                return START_CROP;
            } else if (ni == 9) {
                return END_CROP;
            } else if (ni == 10) {
                return AUTO_START_CENTER_CROP;
            } else if (ni == 11) {
                return AUTO_END_CENTER_CROP;
            } else {
                return null;
            }
        }

        public static OpenScaleType getType(ScaleType scaleType) {
            if (scaleType == ScaleType.FIT_XY) {
                return FIT_XY;
            } else if (scaleType == ScaleType.FIT_START) {
                return FIT_START;
            } else if (scaleType == ScaleType.FIT_CENTER) {
                return FIT_CENTER;
            } else if (scaleType == ScaleType.FIT_END) {
                return FIT_END;
            } else if (scaleType == ScaleType.CENTER) {
                return CENTER;
            } else if (scaleType == ScaleType.CENTER_CROP) {
                return CENTER_CROP;
            } else if (scaleType == ScaleType.CENTER_INSIDE) {
                return CENTER_INSIDE;
            } else {
                return null;
            }
        }

        public static ScaleType getScaleType(OpenScaleType scaleType) {
            if (scaleType == OpenScaleType.FIT_XY) {
                return ScaleType.FIT_XY;
            } else if (scaleType == OpenScaleType.FIT_START) {
                return ScaleType.FIT_START;
            } else if (scaleType == OpenScaleType.FIT_CENTER) {
                return ScaleType.FIT_CENTER;
            } else if (scaleType == OpenScaleType.FIT_END) {
                return ScaleType.FIT_END;
            } else if (scaleType == OpenScaleType.CENTER) {
                return ScaleType.CENTER;
            } else if (scaleType == OpenScaleType.CENTER_CROP) {
                return ScaleType.CENTER_CROP;
            } else if (scaleType == OpenScaleType.CENTER_INSIDE) {
                return ScaleType.CENTER_INSIDE;
            } else {
                return null;
            }
        }

        final int type;

        public int getType() {
            return type;
        }

    }

    public enum ShapeType{
        RECTANGLE(0),OVAL(1);

        ShapeType(int type) {
            this.type = type;
        }

        final int type;

        public int getType() {
            return type;
        }

        public static ShapeType getType(int type) {
            if (type == 1) {
                return OVAL;
            } {
                return RECTANGLE;
            }
        }
    }

    public int getLeftTopRadius() {
        return leftTopRadius;
    }

    public void setLeftTopRadius(int leftTopRadius) {
        this.leftTopRadius = leftTopRadius;
        invalidate();
    }

    public int getLeftBottomRadius() {
        return leftBottomRadius;
    }

    public void setLeftBottomRadius(int leftBottomRadius) {
        this.leftBottomRadius = leftBottomRadius;
        invalidate();
    }

    public int getRightTopRadius() {
        return rightTopRadius;
    }

    public void setRightTopRadius(int rightTopRadius) {
        this.rightTopRadius = rightTopRadius;
        invalidate();
    }

    public int getRightBottomRadius() {
        return rightBottomRadius;
    }

    public void setRightBottomRadius(int rightBottomRadius) {
        this.rightBottomRadius = rightBottomRadius;
        invalidate();
    }

    public void setRadius(int radius) {
        this.leftTopRadius = radius;
        this.rightTopRadius = radius;
        this.leftBottomRadius = radius;
        this.rightBottomRadius = radius;
        invalidate();
    }

    public ShapeType getShapeType() {
        return shapeType;
    }

    public void setShapeType(ShapeType shapeType) {
        this.shapeType = shapeType;
        invalidate();
    }
}
