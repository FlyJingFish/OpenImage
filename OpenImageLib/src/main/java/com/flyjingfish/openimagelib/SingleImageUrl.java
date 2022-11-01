package com.flyjingfish.openimagelib;

import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.enums.MediaType;

class SingleImageUrl implements OpenImageUrl {
    private String url;
    private MediaType mediaType;

    public SingleImageUrl(String url, MediaType mediaType) {
        this.url = url;
        this.mediaType = mediaType;
    }

    @Override
    public String getImageUrl() {
        return url;
    }

    @Override
    public String getVideoUrl() {
        return url;
    }

    @Override
    public String getCoverImageUrl() {
        return url;
    }

    @Override
    public MediaType getType() {
        return mediaType;
    }
}
