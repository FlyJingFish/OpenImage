package com.flyjingfish.openimagelib;

import androidx.annotation.FloatRange;
import androidx.annotation.IntDef;
import androidx.annotation.IntRange;

import com.flyjingfish.openimagelib.listener.BigImageHelper;
import com.flyjingfish.openimagelib.listener.DownloadMediaHelper;
import com.flyjingfish.openimagelib.listener.ImageFragmentCreate;
import com.flyjingfish.openimagelib.listener.VideoFragmentCreate;

/**
 * 全局设置类
 */
public class OpenImageConfig {
    private static volatile OpenImageConfig mInstance;
    private BigImageHelper bigImageHelper;
    private DownloadMediaHelper downloadMediaHelper;
    private ImageFragmentCreate imageFragmentCreate;
    private VideoFragmentCreate videoFragmentCreate;

    private static final float DEFAULT_READ_MODE_RULE = 2f;
    //阅读模式（长图的默认适宽显示）
    protected boolean isReadMode = true;
    protected boolean supportSuperBigImage = true;
    //判定是否是长图的变量
    protected float readModeRule = DEFAULT_READ_MODE_RULE;
    private boolean disEnableTouchClose;
    private boolean disEnableClickClose;
    private float touchCloseScale;
    private boolean isFixSharedAnimMemoryLeaks = true;
    private boolean isFixAndroid12OnBackPressed = true;

    private int preloadCount = 1;
    private boolean lazyPreload = true;

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

    /**
     * 设置大图加载类
     * @param bigImageHelper 大图加载类
     */
    public void setBigImageHelper(BigImageHelper bigImageHelper) {
        this.bigImageHelper = bigImageHelper;
    }

    public ImageFragmentCreate getImageFragmentCreate() {
        return imageFragmentCreate;
    }

    /**
     * 这里是设置用于创建公共图片展示页面的类，设置后你可不必每次调用 {@link OpenImage#setImageFragmentCreate}
     *
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
     *
     * @param videoFragmentCreate 用于自定义视频展示页面
     */
    public void setVideoFragmentCreate(VideoFragmentCreate videoFragmentCreate) {
        this.videoFragmentCreate = videoFragmentCreate;
    }

    public boolean isReadMode() {
        return isReadMode;
    }

    /**
     * 是否开启阅读模式，开启后如果是长图，图片将展示第一屏。判定长图比例可通过{@link OpenImageConfig#setReadModeRule}来设置
     * @param readMode 是否阅读模式
     */
    public void setReadMode(boolean readMode) {
        isReadMode = readMode;
    }

    public float getReadModeRule() {
        return readModeRule;
    }

    /**
     * 设置判断是否是长图的比例
     * @param readModeRule 判定是否是长图的比例
     */
    public void setReadModeRule(float readModeRule) {
        this.readModeRule = readModeRule;
    }

    public boolean isDisEnableTouchClose() {
        return disEnableTouchClose;
    }

    /**
     * 开启或关闭 拖动关闭功能
     * @param disEnableTouchClose 是否关闭拖动关闭功能
     */
    public void setDisEnableTouchClose(boolean disEnableTouchClose) {
        this.disEnableTouchClose = disEnableTouchClose;
    }

    public boolean isDisEnableClickClose() {
        return disEnableClickClose;
    }

    /**
     * 开启或关闭 拖动关闭功能
     * @param disEnableClickClose 是否关闭点击关闭功能
     */
    public void setDisEnableClickClose(boolean disEnableClickClose) {
        this.disEnableClickClose = disEnableClickClose;
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
     * 修复共享元素内存泄漏的bug，默认修复，你可设置为不修复
     * @param fixSharedAnimMemoryLeaks 是否修复共享元素内存泄漏的bug
     */
    public void setFixSharedAnimMemoryLeaks(boolean fixSharedAnimMemoryLeaks) {
        isFixSharedAnimMemoryLeaks = fixSharedAnimMemoryLeaks;
    }

    public boolean isFixAndroid12OnBackPressed() {
        return isFixAndroid12OnBackPressed;
    }

    public void setFixAndroid12OnBackPressed(boolean fixAndroid12OnBackPressed) {
        isFixAndroid12OnBackPressed = fixAndroid12OnBackPressed;
    }

    public DownloadMediaHelper getDownloadMediaHelper() {
        return downloadMediaHelper;
    }

    /**
     * 设置下载图片或视频的类，设置之后才可拥有下载图片或视频的能力
     * @param downloadMediaHelper
     */
    public void setDownloadMediaHelper(DownloadMediaHelper downloadMediaHelper) {
        this.downloadMediaHelper = downloadMediaHelper;
    }

    public boolean isSupportSuperBigImage() {
        return supportSuperBigImage;
    }

    /**
     * 在此可以对超大图设置是否打开
     * @param supportSuperBigImage 是否支持超大图
     */
    public void setSupportSuperBigImage(boolean supportSuperBigImage) {
        this.supportSuperBigImage = supportSuperBigImage;
    }

    public int getPreloadCount() {
        return preloadCount;
    }

    public boolean isLazyPreload() {
        return lazyPreload;
    }

    /**
     * 全局设置预加载个数
     * <p>不设置时，默认是
     * <ul>
     *  <li>如果你用的是 「OpenImageLib」 或 「OpenImageGlideLib」默认是 lazyPreload = true , preloadCount = 1
     *  <li>如果你用的是 「OpenImageFullLib」默认是 lazyPreload = false , preloadCount = 4
     * </ul>
     * @param lazyPreload 是否懒加载 true 的话打开页面时不会预加载，滑动一个时才开始预加载；false的话打开页面时就开始预加载
     * @param preloadCount 预加载个数，对应于{@link androidx.viewpager2.widget.ViewPager2#setOffscreenPageLimit(int)}
     */
    public void setPreloadCount(boolean lazyPreload,@IntRange(from = 1) int preloadCount) {
        this.lazyPreload = lazyPreload;
        this.preloadCount = preloadCount;
    }

    /**
     * 全局设置关闭预加载<br>
     * 关闭预加载，关闭后不会预加载，页面回收会更频繁
     */
    public void closePreload() {
        this.lazyPreload = true;
        this.preloadCount = -1;
    }
}
