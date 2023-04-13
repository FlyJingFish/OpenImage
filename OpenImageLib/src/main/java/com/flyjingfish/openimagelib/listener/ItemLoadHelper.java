package com.flyjingfish.openimagelib.listener;

import android.content.Context;
import android.widget.ImageView;

import com.flyjingfish.openimagelib.beans.OpenImageUrl;

/**
 * 已被废弃请不要调用
 */
@Deprecated
public interface ItemLoadHelper {
    /**
     *
     * @param context
     * @param openImageUrl 当初传入的其中一个数据
     * @param imageUrl
     * @param imageView
     * @param overrideWidth 图片大小（如果Glide只保存目标大小必须在加载图片时传入）
     * @param overrideHeight 图片大小（如果Glide只保存目标大小必须在加载图片时传入）
     * @param onLoadCoverImageListener 回调成功失败
     */
    void loadImage(Context context, OpenImageUrl openImageUrl, String imageUrl, ImageView imageView, int overrideWidth, int overrideHeight, OnLoadCoverImageListener onLoadCoverImageListener);
}
