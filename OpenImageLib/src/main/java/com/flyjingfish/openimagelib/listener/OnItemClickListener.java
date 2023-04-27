package com.flyjingfish.openimagelib.listener;

import com.flyjingfish.openimagelib.BaseInnerFragment;
import com.flyjingfish.openimagelib.beans.OpenImageUrl;

public interface OnItemClickListener {
    /**
     *
     * @param fragment 点击的图片所在的 fragment
     * @param openImageUrl 点击的图片的数据实体类
     * @param position 点击图片的位置
     */
    void onItemClick(BaseInnerFragment fragment, OpenImageUrl openImageUrl, int position);
}
