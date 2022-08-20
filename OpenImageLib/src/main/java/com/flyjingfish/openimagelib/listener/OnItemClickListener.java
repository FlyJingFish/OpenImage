package com.flyjingfish.openimagelib.listener;

import androidx.fragment.app.Fragment;

import com.flyjingfish.openimagelib.beans.OpenImageUrl;

public interface OnItemClickListener {
    void onItemClick(Fragment fragment, OpenImageUrl openImageUrl,int position);
}
