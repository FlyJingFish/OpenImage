package com.flyjingfish.openimagelib.listener;

import androidx.annotation.IdRes;

import com.flyjingfish.openimagelib.beans.OpenImageUrl;

public interface SourceImageViewIdGet<T extends OpenImageUrl> {
    @IdRes int getImageViewId(T data, int position);
}
