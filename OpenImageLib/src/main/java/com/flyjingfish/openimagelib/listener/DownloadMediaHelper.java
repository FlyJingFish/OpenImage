package com.flyjingfish.openimagelib.listener;

import androidx.fragment.app.FragmentActivity;

import com.flyjingfish.openimagelib.beans.OpenImageUrl;

public interface DownloadMediaHelper {
    void download(FragmentActivity activity,OpenImageUrl openImageUrl, OnDownloadMediaListener onDownloadMediaListener);
}
