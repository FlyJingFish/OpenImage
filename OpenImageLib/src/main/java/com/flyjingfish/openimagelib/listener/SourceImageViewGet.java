package com.flyjingfish.openimagelib.listener;

import android.widget.ImageView;

import androidx.annotation.IdRes;

import com.flyjingfish.openimagelib.beans.OpenImageUrl;

public interface SourceImageViewGet<T extends OpenImageUrl> {
    ImageView getImageView(T data, int position);
}
