package com.flyjingfish.openimage.bean;

import android.text.TextUtils;

import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.enums.MediaType;

public class MessageBean implements OpenImageUrl {
    public static final int TEXT = 0;
    public static final int IMAGE = 1;
    public static final int VIDEO = 2;
    public int type;//0文本，1图片，2视频
    public String text;
    public String imageUrl;
    public String videoUrl;
    public String coverUrl;
    public String smallCoverUrl;
    @Override
    public String getImageUrl() {
        return type == IMAGE?imageUrl:coverUrl;
    }

    @Override
    public String getVideoUrl() {
        return videoUrl;
    }

    @Override
    public String getCoverImageUrl() {
        return TextUtils.isEmpty(smallCoverUrl)?coverUrl:smallCoverUrl;
    }

    @Override
    public MediaType getType() {
        if (type == IMAGE){
            return MediaType.IMAGE;
        }else if (type == VIDEO){
            return MediaType.VIDEO;
        }else {
            return MediaType.NONE;
        }
    }
}
