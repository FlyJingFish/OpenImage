package com.flyjingfish.openimagelib.widget;


import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;


public class PercentImageView extends AppCompatImageView{
    private final RectF mRectF = new RectF();
    private final RectF mLayerRectF = new RectF();
    private final PorterDuffXfermode mDstInXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    private final Paint mImagePaint;
    private float percent;
    private ColorStateList percentColor;
    private int curPercentColor = Color.GREEN;

    public PercentImageView(@NonNull Context context) {
        this(context, null);
    }

    public PercentImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PercentImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        percentColor = ColorStateList.valueOf(Color.GREEN);
        mImagePaint = new Paint();
        mImagePaint.setColor(curPercentColor);
        mImagePaint.setAntiAlias(true);
        mImagePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mImagePaint.setXfermode(mDstInXfermode);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int left = 0;
        int top = (int) (getHeight() - percent * getHeight());
        int right = getWidth();
        int bottom = getHeight();

        mImagePaint.setXfermode(null);
        mRectF.set(left, top, right, bottom);
        mLayerRectF.set(0, 0, right, bottom);
        canvas.saveLayer(mLayerRectF, mImagePaint, Canvas.ALL_SAVE_FLAG);

        canvas.drawRect(mRectF, mImagePaint);

        mImagePaint.setXfermode(mDstInXfermode);
        canvas.saveLayer(mLayerRectF, mImagePaint, Canvas.ALL_SAVE_FLAG);

        super.onDraw(canvas);
    }
    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        updateColor();
    }
    public void setPercent(float percent) {
        this.percent = percent;
        invalidate();
    }

    public void setPercentColor(@ColorInt int color){
        setPercentColors(ColorStateList.valueOf(color));
    }

    public void setPercentColors(ColorStateList color){
        percentColor = color;
        updateColor();
    }

    private void updateColor(){
        boolean inval = false;
        final int[] drawableState = getDrawableState();
        int highlightColor = percentColor.getColorForState(drawableState, 0);
        if (highlightColor != curPercentColor) {
            curPercentColor = highlightColor;
            inval = true;
        }
        mImagePaint.setColor(curPercentColor);
        if (inval){
            invalidate();
        }
    }

}
