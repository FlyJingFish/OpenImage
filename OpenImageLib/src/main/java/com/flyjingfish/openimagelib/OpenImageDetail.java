package com.flyjingfish.openimagelib;


import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.enums.MediaType;

class OpenImageDetail implements OpenImageUrl {
    public OpenImageUrl openImageUrl;
    public int srcWidth;
    public int srcHeight;
    public int dataPosition;
    public int viewPosition;
    private long id;

    public OpenImageDetail() {
        
    }
    
    public static OpenImageDetail getNewOpenImageDetail(){
        OpenImageDetail openImageDetail = new OpenImageDetail();
        openImageDetail.id = ImageLoadUtils.getInstance().getUniqueId();
        return openImageDetail;
    }

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

    public long getId() {
        return id;
    }
}
