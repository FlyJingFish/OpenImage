package com.flyjingfish.openimagelib;


import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.enums.MediaType;

class OpenImageDetail implements OpenImageUrl {
    public OpenImageUrl openImageUrl;
    public int srcWidth;
    public int srcHeight;
    public int dataPosition;
    public int viewPosition;

    @Override
    public String getImageUrl() {
        return openImageUrl.getImageUrl();
    }

    @Override
    public String getVideoUrl() {
        return openImageUrl.getVideoUrl();
    }

    @Override
    public String getCoverImageUrl() {
        return openImageUrl.getCoverImageUrl();
    }

    @Override
    public MediaType getType() {
        return openImageUrl.getType();
    }

}
