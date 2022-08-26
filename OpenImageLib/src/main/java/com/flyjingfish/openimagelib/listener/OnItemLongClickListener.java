package com.flyjingfish.openimagelib.listener;


import com.flyjingfish.openimagelib.BaseFragment;
import com.flyjingfish.openimagelib.beans.OpenImageUrl;

public interface OnItemLongClickListener {
    void onItemLongClick(BaseFragment fragment, OpenImageUrl openImageUrl, int position);
}
