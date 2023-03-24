package com.flyjingfish.openimagelib.listener;

import com.flyjingfish.openimagelib.BaseInnerFragment;
import com.flyjingfish.openimagelib.beans.OpenImageUrl;

public interface OnItemClickListener {
    void onItemClick(BaseInnerFragment fragment, OpenImageUrl openImageUrl, int position);
}
