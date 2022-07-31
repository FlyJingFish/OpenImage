package com.flyjingfish.openimagelib;

import com.flyjingfish.openimagelib.listener.BigImageHelper;
import com.flyjingfish.openimagelib.listener.ImageFragmentCreate;
import com.flyjingfish.openimagelib.listener.VideoFragmentCreate;

public class OpenImageConfig {
    private static volatile OpenImageConfig mInstance;
    private BigImageHelper bigImageHelper;
    private ImageFragmentCreate imageFragmentCreate;
    private VideoFragmentCreate videoFragmentCreate;

    private static final float DEFAULT_READ_MODE_RULE = 2f;
    //阅读模式（长图的默认适宽显示）
    protected boolean isReadMode = true;
    //判定是否是长图的变量
    protected float readModeRule = DEFAULT_READ_MODE_RULE;

    private OpenImageConfig() {
    }

    public static OpenImageConfig getInstance() {
        if (mInstance == null) {
            synchronized (OpenImageConfig.class) {
                if (mInstance == null) {
                    mInstance = new OpenImageConfig();
                }
            }
        }
        return mInstance;
    }

    public BigImageHelper getBigImageHelper() {
        return bigImageHelper;
    }

    public void setBigImageHelper(BigImageHelper bigImageHelper) {
        this.bigImageHelper = bigImageHelper;
    }

    public ImageFragmentCreate getImageFragmentCreate() {
        return imageFragmentCreate;
    }

    public void setImageFragmentCreate(ImageFragmentCreate imageFragmentCreate) {
        this.imageFragmentCreate = imageFragmentCreate;
    }

    public VideoFragmentCreate getVideoFragmentCreate() {
        return videoFragmentCreate;
    }

    public void setVideoFragmentCreate(VideoFragmentCreate videoFragmentCreate) {
        this.videoFragmentCreate = videoFragmentCreate;
    }

    public boolean isReadMode() {
        return isReadMode;
    }

    public void setReadMode(boolean readMode) {
        isReadMode = readMode;
    }

    public float getReadModeRule() {
        return readModeRule;
    }

    public void setReadModeRule(float readModeRule) {
        this.readModeRule = readModeRule;
    }
}
