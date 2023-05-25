package com.flyjingfish.openimagelib.beans;

import android.widget.FrameLayout;

import androidx.annotation.DrawableRes;

import com.flyjingfish.openimagelib.R;
import com.flyjingfish.openimagelib.enums.MoreViewShowType;


public class CloseParams {
    @DrawableRes
    private int closeSrc = R.drawable.ic_open_image_close;
    private boolean touchingHide = true;
    private FrameLayout.LayoutParams closeLayoutParams;
    private MoreViewShowType moreViewShowType = MoreViewShowType.IMAGE;

    /**
     *
     * @param closeSrc 下载按钮资源图
     * @return
     */
    public CloseParams setCloseSrc(@DrawableRes int closeSrc) {
        this.closeSrc = closeSrc;
        return this;
    }

    /**
     *
     * @param touchingHide 触摸时是否隐藏下载按钮
     * @return
     */
    public CloseParams setTouchingHide(boolean touchingHide) {
        this.touchingHide = touchingHide;
        return this;
    }

    /**
     *
     * @param closeLayoutParams 下载按钮布局参数
     * @return
     */
    public CloseParams setCloseLayoutParams(FrameLayout.LayoutParams closeLayoutParams) {
        this.closeLayoutParams = closeLayoutParams;
        return this;
    }

    /**
     *
     * @param moreViewShowType 展示类型
     * @return
     */
    public CloseParams setMoreViewShowType(MoreViewShowType moreViewShowType) {
        this.moreViewShowType = moreViewShowType;
        return this;
    }

    public MoreViewShowType getMoreViewShowType() {
        return moreViewShowType;
    }

    public int getCloseSrc() {
        return closeSrc;
    }

    public boolean isTouchingHide() {
        return touchingHide;
    }

    public FrameLayout.LayoutParams getCloseLayoutParams() {
        return closeLayoutParams;
    }
}
