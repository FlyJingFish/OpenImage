package com.flyjingfish.openimage.openImpl;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class MyImageView extends AppCompatImageView {
    public MyImageView(@NonNull Context context) {
        super(context);
    }

    public MyImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setFocusable(int focusable) {
//        super.setFocusable(focusable);
    }

    @Override
    public void setHovered(boolean hovered) {
//        super.setHovered(hovered);
    }

    @Override
    public void setTranslationZ(float translationZ) {
//        super.setTranslationZ(translationZ);
    }

    @Override
    public void setHasTransientState(boolean hasTransientState) {
        super.setHasTransientState(hasTransientState);
    }
}
