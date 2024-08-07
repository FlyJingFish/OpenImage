package com.flyjingfish.openimage.bean;

import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.enums.MediaType;

public class ImageEntity implements OpenImageUrl {
    public String url;
    public String coverUrl;
    public TestBean testBean;
    public ImageEntity(String url) {
        this.url = url;
    }

    public ImageEntity() {
    }

    @Override
    public String getImageUrl() {
        return url;
    }

    @Override
    public String getVideoUrl() {
        return null;
    }

    @Override
    public String getCoverImageUrl() {
        return coverUrl != null?coverUrl:url;
    }

    @Override
    public MediaType getType() {
        return MediaType.IMAGE;
    }
}
