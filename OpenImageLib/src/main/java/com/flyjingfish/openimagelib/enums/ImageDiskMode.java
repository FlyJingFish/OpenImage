package com.flyjingfish.openimagelib.enums;


public enum ImageDiskMode {
    CONTAIN_ORIGINAL,//包含原图
    RESULT,//只保存目标图片大小，如果选择这个可能出现小图扩大到大图的效果
    NONE//没有缓存
}
