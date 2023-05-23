package com.flyjingfish.openimagelib.beans;

import android.content.res.ColorStateList;
import android.widget.FrameLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;

import com.flyjingfish.openimagelib.R;


public class DownloadParams {
    @DrawableRes
    private int downloadSrc = R.drawable.ic_open_image_download;
    private ColorStateList percentColors;
    private boolean touchingHide = true;
    private FrameLayout.LayoutParams downloadLayoutParams;

    /**
     *
     * @param downloadSrc 下载按钮资源图
     * @return
     */
    public DownloadParams setDownloadSrc(@DrawableRes int downloadSrc) {
        this.downloadSrc = downloadSrc;
        return this;
    }

    /**
     *
     * @param percentColor 下载按钮进度颜色
     * @return
     */
    public DownloadParams setPercentColor(@ColorInt int percentColor) {
        this.percentColors = ColorStateList.valueOf(percentColor);
        return this;
    }

    /**
     *
     * @param percentColors 下载按钮进度颜色
     * @return
     */
    public DownloadParams setPercentColors(ColorStateList percentColors) {
        this.percentColors = percentColors;
        return this;
    }

    /**
     *
     * @param touchingHide 触摸时是否隐藏下载按钮
     * @return
     */
    public DownloadParams setTouchingHide(boolean touchingHide) {
        this.touchingHide = touchingHide;
        return this;
    }

    /**
     *
     * @param downloadLayoutParams 下载按钮布局参数
     * @return
     */
    public DownloadParams setDownloadLayoutParams(FrameLayout.LayoutParams downloadLayoutParams) {
        this.downloadLayoutParams = downloadLayoutParams;
        return this;
    }

    public int getDownloadSrc() {
        return downloadSrc;
    }

    public ColorStateList getPercentColors() {
        return percentColors;
    }

    public boolean isTouchingHide() {
        return touchingHide;
    }

    public FrameLayout.LayoutParams getDownloadLayoutParams() {
        return downloadLayoutParams;
    }
}
