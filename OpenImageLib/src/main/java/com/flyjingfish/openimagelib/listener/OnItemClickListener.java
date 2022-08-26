package com.flyjingfish.openimagelib.listener;

import com.flyjingfish.openimagelib.BaseFragment;
import com.flyjingfish.openimagelib.beans.OpenImageUrl;

public interface OnItemClickListener {
    void onItemClick(BaseFragment fragment, OpenImageUrl openImageUrl, int position);
}
