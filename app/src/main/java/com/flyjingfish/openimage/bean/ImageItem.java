package com.flyjingfish.openimage.bean;

import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.enums.MediaType;

import java.util.ArrayList;
import java.util.List;

public class ImageItem implements OpenImageUrl {
    public static final int IMAGE = 1;
    public static final int VIDEO = 2;
    public int type;
    public String text;
    public String coverUrl;
    public String videoUrl;
    public List<ImageEntity> images = new ArrayList<>();

    public int getViewType() {
        return type;
    }

    @Override
    public String getImageUrl() {
        return coverUrl;
    }

    @Override
    public String getVideoUrl() {
        return videoUrl;
    }

    @Override
    public String getCoverImageUrl() {
        return coverUrl;
    }

    @Override
    public MediaType getType() {
        return MediaType.VIDEO;
    }
}
