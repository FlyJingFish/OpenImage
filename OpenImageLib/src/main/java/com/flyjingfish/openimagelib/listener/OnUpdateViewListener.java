package com.flyjingfish.openimagelib.listener;

import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.enums.UpdateViewType;

import java.util.Collection;

public interface OnUpdateViewListener {
    UpdateViewType onUpdate(Collection<? extends OpenImageUrl> data,UpdateViewType updateViewType);
}
