package com.flyjingfish.openimagelib;

import androidx.annotation.FloatRange;

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
    private boolean disEnableTouchClose;
    private float touchCloseScale;
    private boolean isFixSharedAnimMemoryLeaks = true;

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

    /**
     * 这里是设置用于创建公共图片展示页面的类，设置后你可不必每次调用 {@link OpenImage#setImageFragmentCreate}
     * @param imageFragmentCreate 用于自定义图片展示页面
     */
    public void setImageFragmentCreate(ImageFragmentCreate imageFragmentCreate) {
        this.imageFragmentCreate = imageFragmentCreate;
    }

    public VideoFragmentCreate getVideoFragmentCreate() {
        return videoFragmentCreate;
    }

    /**
     * 这里是设置用于创建公共视频展示页面的类，设置后你可不必每次调用 {@link OpenImage#setVideoFragmentCreate}
     * @param videoFragmentCreate 用于自定义视频展示页面
     */
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

    public boolean isDisEnableTouchClose() {
        return disEnableTouchClose;
    }

    /**
     *
     * @param disEnableTouchClose 是否关闭拖动关闭功能
     */
    public void setDisEnableTouchClose(boolean disEnableTouchClose) {
        this.disEnableTouchClose = disEnableTouchClose;
    }

    public float getTouchCloseScale() {
        return touchCloseScale;
    }

    /**
     * @param touchCloseScale 拖动关闭百分比
     * @return
     */
    public void setTouchCloseScale(@FloatRange(from = .01f, to = .99f) float touchCloseScale) {
        this.touchCloseScale = touchCloseScale;
    }

    public boolean isFixSharedAnimMemoryLeaks() {
        return isFixSharedAnimMemoryLeaks;
    }

    /**
     *
     * @param fixSharedAnimMemoryLeaks 是否修复共享元素内存泄漏的bug
     */
    public void setFixSharedAnimMemoryLeaks(boolean fixSharedAnimMemoryLeaks) {
        isFixSharedAnimMemoryLeaks = fixSharedAnimMemoryLeaks;
    }
}
