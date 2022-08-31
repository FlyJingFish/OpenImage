package com.flyjingfish.openimagelib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.flyjingfish.openimagelib.R;

import java.io.Serializable;


public class OpenImageView extends AppCompatImageView {
    private OpenImageViewAttacher attacher;
    private OpenScaleType pendingScaleType;

    public OpenImageView(Context context) {
        this(context, null);
    }

    public OpenImageView(Context context, AttributeSet attr) {
        this(context, attr, 0);
    }

    public OpenImageView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        TypedArray typedArray = context.obtainStyledAttributes(attr, R.styleable.OpenImageView);
        pendingScaleType = OpenScaleType.getType(typedArray.getInt(R.styleable.OpenImageView_openScaleType, 0));
        typedArray.recycle();
        init();
    }

    private void init() {
        attacher = new OpenImageViewAttacher(this);
        super.setScaleType(ScaleType.MATRIX);
        if (pendingScaleType != null) {
            setOpenScaleType(pendingScaleType);
            pendingScaleType = null;
        }
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        super.setScaleType(scaleType);
    }

    @Override
    public Matrix getImageMatrix() {
        return attacher.getImageMatrix();
    }

    public void setOpenScaleType(OpenScaleType scaleType) {
        if (attacher == null) {
            pendingScaleType = scaleType;
        } else {
            attacher.setScaleType(scaleType);
        }
    }

    public OpenScaleType getOpenScaleType() {
        return attacher.getOpenScaleType();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        // setImageBitmap calls through to this method
        if (attacher != null) {
            attacher.update();
        }
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
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        boolean changed = super.setFrame(l, t, r, b);
        if (changed) {
            attacher.update();
        }
        return changed;
    }

    public static class OpenScaleType implements Serializable {
        public static final OpenScaleType FIT_XY = new OpenScaleType(1);
        public static final OpenScaleType FIT_START = new OpenScaleType(2);
        public static final OpenScaleType FIT_CENTER = new OpenScaleType(3);
        public static final OpenScaleType FIT_END = new OpenScaleType(4);
        public static final OpenScaleType CENTER = new OpenScaleType(5);
        public static final OpenScaleType CENTER_CROP = new OpenScaleType(6);
        public static final OpenScaleType CENTER_INSIDE = new OpenScaleType(7);
        public static final OpenScaleType START_CROP = new OpenScaleType(8);
        public static final OpenScaleType END_CROP = new OpenScaleType(9);

        OpenScaleType(int ni) {
            type = ni;
        }

        public static OpenScaleType getType(int ni){
            if (ni == 1){
                return FIT_XY;
            }else if (ni == 2){
                return FIT_START;
            }else if (ni == 3){
                return FIT_CENTER;
            }else if (ni == 4){
                return FIT_END;
            }else if (ni == 5){
                return CENTER;
            }else if (ni == 6){
                return CENTER_CROP;
            }else if (ni == 7){
                return CENTER_INSIDE;
            }else if (ni == 8){
                return START_CROP;
            }else if (ni == 9){
                return END_CROP;
            }else {
                return null;
            }
        }

        public static OpenScaleType getType(ScaleType scaleType){
            if (scaleType == ScaleType.FIT_XY){
                return FIT_XY;
            }else if (scaleType == ScaleType.FIT_START){
                return FIT_START;
            }else if (scaleType == ScaleType.FIT_CENTER){
                return FIT_CENTER;
            }else if (scaleType == ScaleType.FIT_END){
                return FIT_END;
            }else if (scaleType == ScaleType.CENTER){
                return CENTER;
            }else if (scaleType == ScaleType.CENTER_CROP){
                return CENTER_CROP;
            }else if (scaleType == ScaleType.CENTER_INSIDE){
                return CENTER_INSIDE;
            }else {
                return null;
            }
        }
        public static ScaleType getScaleType(OpenScaleType scaleType){
            if (scaleType == OpenScaleType.FIT_XY){
                return ScaleType.FIT_XY;
            }else if (scaleType == OpenScaleType.FIT_START){
                return ScaleType.FIT_START;
            }else if (scaleType == OpenScaleType.FIT_CENTER){
                return ScaleType.FIT_CENTER;
            }else if (scaleType == OpenScaleType.FIT_END){
                return ScaleType.FIT_END;
            }else if (scaleType == OpenScaleType.CENTER){
                return ScaleType.CENTER;
            }else if (scaleType == OpenScaleType.CENTER_CROP){
                return ScaleType.CENTER_CROP;
            }else if (scaleType == OpenScaleType.CENTER_INSIDE){
                return ScaleType.CENTER_INSIDE;
            }else {
                return null;
            }
        }
        public static ScaleType getScaleType(int ni){
            if (ni == 1){
                return ScaleType.FIT_XY;
            }else if (ni == 2){
                return ScaleType.FIT_START;
            }else if (ni == 3){
                return ScaleType.FIT_CENTER;
            }else if (ni == 4){
                return ScaleType.FIT_END;
            }else if (ni == 5){
                return ScaleType.CENTER;
            }else if (ni == 6){
                return ScaleType.CENTER_CROP;
            }else if (ni == 7){
                return ScaleType.CENTER_INSIDE;
            }else {
                return null;
            }
        }

        final int type;

        public int getType() {
            return type;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj instanceof OpenScaleType){
                return this.getType() == ((OpenScaleType) obj).getType();
            }
            return super.equals(obj);
        }
    }
//    public static class OpenScaleType {
//        public static final ImageView.ScaleType FIT_XY = ScaleType.FIT_XY;
//        public static final ImageView.ScaleType FIT_START = ScaleType.FIT_START;
//        public static final ImageView.ScaleType FIT_CENTER = ScaleType.FIT_CENTER;
//        public static final ImageView.ScaleType FIT_END = ScaleType.FIT_END;
//        public static final ImageView.ScaleType CENTER = ScaleType.CENTER;
//        public static final ImageView.ScaleType CENTER_CROP = ScaleType.CENTER_CROP;
//        public static final ImageView.ScaleType CENTER_INSIDE = ScaleType.CENTER_INSIDE;
//        public static final OpenScaleType START_CROP = new OpenScaleType(8);
//        public static final OpenScaleType END_CROP = new OpenScaleType(9);
//
//        OpenScaleType(int ni) {
//            nativeInt = ni;
//        }
//
//        public static OpenScaleType getType(int ni){
//            if (ni == 8){
//                return START_CROP;
//            }else if (ni == 9){
//                return END_CROP;
//            }else {
//                return null;
//            }
//        }
//
//        final int nativeInt;
//    }


}
