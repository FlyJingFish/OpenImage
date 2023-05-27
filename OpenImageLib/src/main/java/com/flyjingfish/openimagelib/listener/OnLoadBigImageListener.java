package com.flyjingfish.openimagelib.listener;


import android.graphics.drawable.Drawable;

public interface OnLoadBigImageListener {
    /**
     * 加载图片成功回调
     * @param drawable 这个图必须保证不可以OOM，如果是超大图要处理好
     * @param filePath 图片下载的本地路径
     */
    void onLoadImageSuccess(Drawable drawable, String filePath);

    /**
     * 加载图片失败回调
     */
    void onLoadImageFailed();
}
