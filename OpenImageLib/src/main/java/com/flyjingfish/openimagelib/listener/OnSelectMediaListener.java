package com.flyjingfish.openimagelib.listener;

import com.flyjingfish.openimagelib.beans.OpenImageUrl;

public interface OnSelectMediaListener{
    /**
     *
     * @param openImageUrl 正在看的所传入的数据
     * @param position 所传入数据list中的位置
     */
    void onSelect(OpenImageUrl openImageUrl,int position);
}
