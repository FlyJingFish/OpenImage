package com.flyjingfish.openimagelib.enums;

/**
 * 这项这是已被废弃请不要调用
 */
@Deprecated
public enum ImageDiskMode {
    /**
     * 硬盘缓存：包含原图
     */
    CONTAIN_ORIGINAL,
    /**
     * 硬盘缓存：只保存目标图片大小，如果选择这个可能出现小图扩大到大图的效果
     */
    RESULT,
    /**
     * 硬盘缓存：没有缓存
     */
    NONE//没有缓存
}
