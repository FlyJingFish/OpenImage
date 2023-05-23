package com.flyjingfish.openimagelib.listener;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;

import com.flyjingfish.openimagelib.beans.OpenImageUrl;

public interface DownloadMediaHelper {
    /**
     *
     * @param activity 上下文
     * @param lifecycleOwner 生命周期拥有者{@link LifecycleOwner} ,
     *                       在{@link androidx.fragment.app.Fragment} 中请调用{@link Fragment#getViewLifecycleOwner()},
     *                       在{@link FragmentActivity}中则传其自身，这个是为了防止内存泄漏
     * @param openImageUrl 下载的项目
     * @param onDownloadMediaListener 下载监听
     */
    void download(FragmentActivity activity, LifecycleOwner lifecycleOwner, OpenImageUrl openImageUrl, OnDownloadMediaListener onDownloadMediaListener);
}
