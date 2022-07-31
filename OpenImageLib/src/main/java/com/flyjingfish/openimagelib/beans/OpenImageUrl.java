package com.flyjingfish.openimagelib.beans;

import com.flyjingfish.openimagelib.enums.MediaType;

import java.io.Serializable;

public interface OpenImageUrl extends Serializable {
    /**
     * @return 返回应该浏览的大图获取视频的大图封面
     */
    String getImageUrl();

    /**
     * @return 返回要播放的视频链接
     */
    String getVideoUrl();

    /**
     * @return 返回点击的列表展示的图片链接或者视频的小图封面
     */
    String getCoverImageUrl();

    /**
     * @return 返回是视频还是图片
     */
    MediaType getType();
}
