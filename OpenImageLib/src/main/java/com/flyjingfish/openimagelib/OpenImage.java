package com.flyjingfish.openimagelib;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.flyjingfish.openimagelib.beans.ClickViewParam;
import com.flyjingfish.openimagelib.beans.CloseParams;
import com.flyjingfish.openimagelib.beans.DownloadParams;
import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.beans.RectangleConnerRadius;
import com.flyjingfish.openimagelib.enums.ImageDiskMode;
import com.flyjingfish.openimagelib.enums.ImageShapeType;
import com.flyjingfish.openimagelib.enums.MediaType;
import com.flyjingfish.openimagelib.enums.MoreViewShowType;
import com.flyjingfish.openimagelib.listener.ImageFragmentCreate;
import com.flyjingfish.openimagelib.listener.ItemLoadHelper;
import com.flyjingfish.openimagelib.listener.OnExitListener;
import com.flyjingfish.openimagelib.listener.OnItemClickListener;
import com.flyjingfish.openimagelib.listener.OnItemLongClickListener;
import com.flyjingfish.openimagelib.listener.OnLoadViewFinishListener;
import com.flyjingfish.openimagelib.listener.OnSelectMediaListener;
import com.flyjingfish.openimagelib.listener.OnUpdateViewListener;
import com.flyjingfish.openimagelib.listener.SourceImageViewGet;
import com.flyjingfish.openimagelib.listener.SourceImageViewIdGet;
import com.flyjingfish.openimagelib.listener.UpperLayerFragmentCreate;
import com.flyjingfish.openimagelib.listener.VideoFragmentCreate;
import com.flyjingfish.openimagelib.utils.ActivityCompatHelper;
import com.flyjingfish.shapeimageviewlib.ShapeImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * 打开大图类
 */
public final class OpenImage extends OpenImage4ParseData {
    public static OpenImage with(Context context) {
        return new OpenImage(context);
    }

    private OpenImage() {
    }

    private OpenImage(Context context) {
        if (context instanceof Activity) {
            this.context = context;
            this.contextKey = context.toString();
        } else {
            throw new IllegalArgumentException("context must be activity");
        }
    }

    /**
     * 设置数据
     *
     * @param openImageUrls 图片数据组
     * @return {@link OpenImage}
     */
    public OpenImage setImageUrlList(List<? extends OpenImageUrl> openImageUrls) {
        this.openImageUrls.addAll(openImageUrls);
        return this;
    }

    /**
     * 设置数据
     *
     * @param openImageUrl 单个图片数据可设置这个
     * @return {@link OpenImage}
     */
    public OpenImage setImageUrl(OpenImageUrl openImageUrl) {
        return setImageUrlList(new ArrayList<>(Collections.singletonList(openImageUrl)));
    }

    /**
     * 设置数据
     *
     * @param openImageUrls 图片String数据组
     * @param mediaType     图片还是视频
     * @return {@link OpenImage}
     */
    public OpenImage setImageUrlList(List<String> openImageUrls, MediaType mediaType) {
        List<SingleImageUrl> list = new ArrayList<>();
        for (String openImageUrl : openImageUrls) {
            list.add(new SingleImageUrl(openImageUrl, mediaType));
        }
        setImageUrlList(list);
        return this;
    }

    /**
     * 设置数据
     *
     * @param openImageUrl 单个String图片数据可设置这个
     * @param mediaType    图片还是视频
     * @return {@link OpenImage}
     */
    public OpenImage setImageUrl(String openImageUrl, MediaType mediaType) {
        return setImageUrlList(new ArrayList<>(Collections.singletonList(new SingleImageUrl(openImageUrl, mediaType))));
    }

    /**
     * 设置点击的ImageView所在容器
     *
     * @param recyclerView         展示数据的RecyclerView
     * @param sourceImageViewIdGet 展示数据的RecyclerView 的图片Id
     * @return {@link OpenImage}
     */
    public OpenImage setClickRecyclerView(RecyclerView recyclerView, SourceImageViewIdGet<OpenImageUrl> sourceImageViewIdGet) {
        this.recyclerView = recyclerView;
        this.sourceImageViewIdGet = sourceImageViewIdGet;
        return this;
    }

    /**
     * 设置点击的ImageView所在容器
     *
     * @param viewPager2           展示数据的ViewPager2
     * @param sourceImageViewIdGet 展示数据的ViewPager2 的图片Id
     * @return {@link OpenImage}
     */
    public OpenImage setClickViewPager2(ViewPager2 viewPager2, SourceImageViewIdGet<OpenImageUrl> sourceImageViewIdGet) {
        this.viewPager2 = viewPager2;
        this.sourceImageViewIdGet = sourceImageViewIdGet;
        return this;
    }

    /**
     * 设置点击的ImageView所在容器
     *
     * @param viewPager          展示数据的ViewPager
     * @param sourceImageViewGet 展示数据的ViewPager 的图片ImageView
     * @return {@link OpenImage}
     */
    public OpenImage setClickViewPager(ViewPager viewPager, SourceImageViewGet<OpenImageUrl> sourceImageViewGet) {
        this.viewPager = viewPager;
        this.sourceImageViewGet = sourceImageViewGet;
        return this;
    }

    /**
     * 设置点击的ImageView所在容器
     *
     * @param gridView             展示数据的GridView
     * @param sourceImageViewIdGet 展示数据的GridView 的图片Id
     * @return {@link OpenImage}
     */
    public OpenImage setClickGridView(GridView gridView, SourceImageViewIdGet<OpenImageUrl> sourceImageViewIdGet) {
        this.absListView = gridView;
        this.sourceImageViewIdGet = sourceImageViewIdGet;
        return this;
    }

    /**
     * 设置点击的ImageView所在容器
     *
     * @param listView             展示数据的ListView
     * @param sourceImageViewIdGet 展示数据的ListView 的图片Id
     * @return {@link OpenImage}
     */
    public OpenImage setClickListView(ListView listView, SourceImageViewIdGet<OpenImageUrl> sourceImageViewIdGet) {
        this.absListView = listView;
        this.sourceImageViewIdGet = sourceImageViewIdGet;
        return this;
    }

    /**
     * 设置点击的ImageView数组
     *
     * @param imageViews 自己传展示数据的ImageView组
     * @return {@link OpenImage}
     */
    public OpenImage setClickImageViews(ImageView[] imageViews) {
        return setClickImageViews(new ArrayList<>(Arrays.asList(imageViews)));
    }

    /**
     * 设置点击的ImageView
     *
     * @param imageView 自己传展示数据的单个ImageView
     * @return {@link OpenImage}
     */
    public OpenImage setClickImageView(ImageView imageView) {
        return setClickImageViews(new ArrayList<>(Collections.singletonList(imageView)));
    }

    /**
     * 设置点击的ImageView集合
     *
     * @param imageViews 自己传展示数据的ImageView组
     * @return {@link OpenImage}
     */
    public OpenImage setClickImageViews(List<ImageView> imageViews) {
        this.imageViews = imageViews;
        return this;
    }

    /**
     * 如果没有可以传的点击 View 可调用这个方法
     *
     * @return {@link OpenImage}
     */
    public OpenImage setNoneClickView() {
        isNoneClickView = true;
        return this;
    }

    /**
     * 支持在网页内点击图片，你需要和网页前端人员对接后才可获得对应的参数
     *
     * @param webView 网页浏览器
     * @param clickViewParam 点击图片或视频在网页内的参数
     * @return {@link OpenImage}
     */
    public OpenImage setClickWebView(View webView, ClickViewParam clickViewParam) {
        if (clickViewParam != null){
            return setClickWebView(webView, new ArrayList<>(Collections.singletonList(clickViewParam)));
        }else {
            return setClickWebView(webView, (List<ClickViewParam>) null);
        }
    }

    /**
     * 支持在网页内点击图片，你需要和网页前端人员对接后才可获得对应的参数
     *
     * @param webView 网页浏览器
     * @param clickViewParams  点击图片或视频在网页内的参数
     * @return {@link OpenImage}
     */
    public OpenImage setClickWebView(View webView, List<ClickViewParam> clickViewParams) {
        this.parentParamsView = webView;
        this.clickViewParams = clickViewParams;
        return this;
    }

    /**
     * 如果数据下标 和 RecyclerView、ViewPager2、ListView、GridView 的所在位置一致 可调用这个
     *
     * @param clickPosition 点击的图片和View所在的位置
     * @return {@link OpenImage}
     */
    public OpenImage setClickPosition(int clickPosition) {
        return setClickPosition(clickPosition, clickPosition);
    }

    /**
     * 如果数据下标 和 RecyclerView、ViewPager2、ListView、GridView 的所在位置不一致 调用这个
     *
     * @param clickDataPosition 点击的图片所在数据的位置
     * @param clickViewPosition 点击的图片View在RecyclerView或ListView或GridView的位置
     * @return {@link OpenImage}
     */
    public OpenImage setClickPosition(int clickDataPosition, int clickViewPosition) {
        this.clickDataPosition = clickDataPosition;
        this.clickViewPosition = clickViewPosition;
        return this;
    }

    /**
     * 使用普通 ImageView 时调用这个，请一定仔细核对是否设置正确，如果不正确将使显示效果不正常
     *
     * @param srcImageViewScaleType 点击的ImageView显示模式
     * @param autoSetScaleType      如果点击的ImageView与您所设置scaleType不相同，则自动设置
     * @return {@link OpenImage}
     */
    public OpenImage setSrcImageViewScaleType(ImageView.ScaleType srcImageViewScaleType, boolean autoSetScaleType) {
        this.srcImageViewScaleType = srcImageViewScaleType;
        this.autoSetScaleType = autoSetScaleType;
        return this;
    }

    /**
     * 使用ShapeImageView时调用这个
     *
     * @param srcImageViewShapeScaleType 点击的{@link ShapeImageView}显示模式
     * @param autoSetScaleType           如果点击的{@link ShapeImageView}与您所设置scaleType不相同，则自动设置
     * @return {@link OpenImage}
     */
    public OpenImage setSrcImageViewScaleType(ShapeImageView.ShapeScaleType srcImageViewShapeScaleType, boolean autoSetScaleType) {
        this.srcImageViewShapeScaleType = srcImageViewShapeScaleType;
        this.autoSetScaleType = autoSetScaleType;
        return this;
    }

    /**
     * 这项已被废弃请不要调用
     *
     * @param imageDiskMode 这项已被废弃请不要调用
     * @return {@link OpenImage}
     */
    @Deprecated
    public OpenImage setImageDiskMode(ImageDiskMode imageDiskMode) {
        return this;
    }

    /**
     * <p>加载大图失败后:
     * <ul>
     *  <li>如果设置此选项则展示这个 errorResId 图片
     *  <li>如果不设置
     *      <ul>
     *          <li> 有缓存则 "可能" 展示缓存图片
     *          <li> 没有缓存图片 "可能" 展示上一页面的加载失败图片，"也可能" 没有任何显示
     *      </ul>
     *  </li>
     * </ul>
     * <p> 所以建议设置此项
     * <p>
     *
     * @param errorResId 大图加载失败后显示的图片
     * @return {@link OpenImage}
     */
    public OpenImage setErrorResId(@DrawableRes int errorResId) {
        this.errorResId = errorResId;
        return this;
    }

    /**
     * 这项已被废弃请不要调用
     *
     * @param itemLoadHelper 这项已被废弃请不要调用
     * @return {@link OpenImage}
     */
    @Deprecated
    public OpenImage setItemLoadHelper(ItemLoadHelper itemLoadHelper) {
        return this;
    }

    /**
     * @param openImageStyle 查看图片显示设置StyleId
     * @return {@link OpenImage}
     */
    public OpenImage setOpenImageStyle(@StyleRes int openImageStyle) {
        this.openImageStyle = openImageStyle;
        return this;
    }

    /**
     * @param openPageAnimTimeMs 打开页面动画的时间
     * @return {@link OpenImage}
     */
    public OpenImage setOpenPageAnimTimeMs(long openPageAnimTimeMs) {
        this.openPageAnimTimeMs = openPageAnimTimeMs;
        return this;
    }

    /**
     * 设置 item 切换监听器
     *
     * @param onSelectMediaListener 回调查看图片所在数据的位置
     * @return {@link OpenImage}
     */
    public OpenImage setOnSelectMediaListener(OnSelectMediaListener onSelectMediaListener) {
        onSelectKey = UUID.randomUUID().toString();
        ImageLoadUtils.getInstance().setOnSelectMediaListener(onSelectKey, onSelectMediaListener);
        return this;
    }

    /**
     * 只对传入RecyclerView，ViewPager，ViewPager2， ListView, GridView 有效
     *
     * @param autoScrollScanPosition 自动滑向最后看的图片的位置
     * @return {@link OpenImage}
     */
    public OpenImage setAutoScrollScanPosition(boolean autoScrollScanPosition) {
        isAutoScrollScanPosition = autoScrollScanPosition;
        return this;
    }

    /**
     * @param pageTransformer ViewPager的页面切换效果
     * @return {@link OpenImage}
     */
    public OpenImage addPageTransformer(ViewPager2.PageTransformer... pageTransformer) {
        pageTransformersKey = UUID.randomUUID().toString();
        ImageLoadUtils.getInstance().setPageTransformers(pageTransformersKey, new ArrayList<>(Arrays.asList(pageTransformer)));
        return this;
    }

    /**
     * @param leftRightShowWidthDp 可设置画廊效果，左右漏出的宽度，单位dp
     * @return {@link OpenImage}
     */
    public OpenImage setGalleryEffect(int leftRightShowWidthDp) {
        this.leftRightShowWidthDp = leftRightShowWidthDp;
        return this;
    }

    /**
     * 设置微信补位效果，设置后当退出大图页面时，如果前一页面没有当前图片，则自动回到点击进来的那张图的位置
     * 开启后自动自动滚动效果关闭
     * (只对父容器是RecyclerView, ViewPager2，ListView, GridView 时有效)
     *
     * @param wechatExitFillInEffect 是否设置微信补位效果
     * @return {@link OpenImage}
     */
    public OpenImage setWechatExitFillInEffect(boolean wechatExitFillInEffect) {
        this.wechatExitFillInEffect = wechatExitFillInEffect;
        return this;
    }

    /**
     * 设置点击图片监听
     *
     * @param onItemClickListener 点击 item 监听类
     * @return {@link OpenImage}
     */
    public OpenImage setOnItemClickListener(OnItemClickListener onItemClickListener) {
        onItemClickListenerKey = UUID.randomUUID().toString();
        ImageLoadUtils.getInstance().setOnItemClickListener(onItemClickListenerKey, onItemClickListener);
        return this;
    }

    /**
     * 设置长按图片监听
     *
     * @param onItemLongClickListener 长按 item 监听类
     * @return {@link OpenImage}
     */
    public OpenImage setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        onItemLongClickListenerKey = UUID.randomUUID().toString();
        ImageLoadUtils.getInstance().setOnItemLongClickListener(onItemLongClickListenerKey, onItemLongClickListener);
        return this;
    }

    /**
     * 禁用点击图片关闭页面功能，设置此项后{@link OpenImageConfig#setDisEnableClickClose(boolean)} 就不起作用了
     *
     * @return {@link OpenImage}
     */
    public OpenImage disableClickClose() {
        disableClickClose = true;
        return this;
    }

    /**
     * 开始点击图片关闭页面功能，设置此项后{@link OpenImageConfig#setDisEnableClickClose(boolean)} 就不起作用了
     *
     * @return {@link OpenImage}
     */
    public OpenImage enableClickClose() {
        disableClickClose = false;
        return this;
    }

    /**
     * 添加View 到大图页面，此方法可多次调用，添加多个View，可以联合 {@link OpenImage#setOnSelectMediaListener} 使用，
     * 获取正在显示的图片或视频（即您传入的数据 {@link OpenImageUrl}）
     *
     * @param layoutRes                添加的布局xml id
     * @param layoutParams             要添加到页面布局的参数
     * @param moreViewShowType         展示类型
     * @param onLoadViewFinishListener 加载完毕View后回调
     * @return {@link OpenImage}
     */
    public OpenImage addMoreView(@LayoutRes int layoutRes, @NonNull FrameLayout.LayoutParams layoutParams, MoreViewShowType moreViewShowType, OnLoadViewFinishListener onLoadViewFinishListener) {
        return addMoreView(layoutRes, layoutParams, moreViewShowType, false, onLoadViewFinishListener);
    }

    /**
     * 添加View 到大图页面，此方法可多次调用，添加多个View，可以联合 {@link OpenImage#setOnSelectMediaListener} 使用，
     * 获取正在显示的图片或视频（即您传入的数据 {@link OpenImageUrl}）
     *
     * @param layoutView       添加的View
     * @param layoutParams     要添加到页面布局的参数
     * @param moreViewShowType 展示类型
     * @return {@link OpenImage}
     */
    public OpenImage addMoreView(View layoutView, @NonNull FrameLayout.LayoutParams layoutParams, MoreViewShowType moreViewShowType) {
        return addMoreView(layoutView, layoutParams, moreViewShowType, false);
    }

    /**
     * 添加View 到大图页面，此方法可多次调用，添加多个View，可以联合 {@link OpenImage#setOnSelectMediaListener} 使用，
     * 获取正在显示的图片或视频（即您传入的数据 {@link OpenImageUrl}）
     *
     * @param layoutRes                添加的布局xml id
     * @param layoutParams             要添加到页面布局的参数
     * @param moreViewShowType         展示类型
     * @param followTouch              是否跟随图片拖动
     * @param onLoadViewFinishListener 加载完毕View后回调
     * @return {@link OpenImage}
     */
    public OpenImage addMoreView(@LayoutRes int layoutRes, @NonNull FrameLayout.LayoutParams layoutParams, MoreViewShowType moreViewShowType, boolean followTouch, OnLoadViewFinishListener onLoadViewFinishListener) {
        MoreViewOption moreViewOption = new MoreViewOption(layoutRes, layoutParams, moreViewShowType, followTouch, onLoadViewFinishListener);
        moreViewOptions.add(moreViewOption);
        return this;
    }

    /**
     * 添加View 到大图页面，此方法可多次调用，添加多个View，可以联合 {@link OpenImage#setOnSelectMediaListener} 使用，
     * 获取正在显示的图片或视频（即您传入的数据 {@link OpenImageUrl}）
     *
     * @param layoutView       添加的View
     * @param layoutParams     要添加到页面布局的参数
     * @param moreViewShowType 展示类型
     * @param followTouch      是否跟随图片拖动
     * @return {@link OpenImage}
     */
    public OpenImage addMoreView(View layoutView, @NonNull FrameLayout.LayoutParams layoutParams, MoreViewShowType moreViewShowType, boolean followTouch) {
        MoreViewOption moreViewOption = new MoreViewOption(layoutView, layoutParams, moreViewShowType, followTouch);
        moreViewOptions.add(moreViewOption);
        return this;
    }

    /**
     * @param showSrcImageView 退出时，前一页面的ImageView是否可见
     * @return {@link OpenImage}
     */
    public OpenImage setShowSrcImageView(boolean showSrcImageView) {
        this.showSrcImageView = showSrcImageView;
        return this;
    }

    /**
     * 调用这个方法将覆盖 {@link OpenImageConfig#setImageFragmentCreate}  的配置，用于解决在app内多种不同需求的场景
     *
     * @param imageFragmentCreate 用于自定义图片展示页面
     * @return {@link OpenImage}
     */
    public OpenImage setImageFragmentCreate(ImageFragmentCreate imageFragmentCreate) {
        imageFragmentCreateKey = UUID.randomUUID().toString();
        ImageLoadUtils.getInstance().setImageFragmentCreate(imageFragmentCreateKey, imageFragmentCreate);
        return this;
    }

    /**
     * 调用这个方法将覆盖 {@link OpenImageConfig#setVideoFragmentCreate} 的配置，用于解决在app内多种不同需求的场景
     *
     * @param videoFragmentCreate 用于自定义视频展示页面
     * @return {@link OpenImage}
     */
    public OpenImage setVideoFragmentCreate(VideoFragmentCreate videoFragmentCreate) {
        videoFragmentCreateKey = UUID.randomUUID().toString();
        ImageLoadUtils.getInstance().setVideoFragmentCreate(videoFragmentCreateKey, videoFragmentCreate);
        return this;
    }

    /**
     * 这是可以显示在页面上方的Fragment
     *
     * @param upperLayerFragmentCreate 用于创建覆盖在页面上方的Fragment
     * @param bundle                   传入数据
     * @return {@link OpenImage}
     */
    public OpenImage setUpperLayerFragmentCreate(UpperLayerFragmentCreate upperLayerFragmentCreate, Bundle bundle) {
        return setUpperLayerFragmentCreate(upperLayerFragmentCreate, bundle, false);
    }

    /**
     * 这是可以显示在页面上方的Fragment
     *
     * @param upperLayerFragmentCreate 用于创建覆盖在页面上方的Fragment
     * @param bundle                   传入数据
     * @param followTouch              是否跟随拖动
     * @return {@link OpenImage}
     */
    public OpenImage setUpperLayerFragmentCreate(UpperLayerFragmentCreate upperLayerFragmentCreate, Bundle bundle, boolean followTouch) {
        return setUpperLayerFragmentCreate(upperLayerFragmentCreate, bundle, followTouch, true);
    }

    /**
     * 这是可以显示在页面上方的Fragment
     *
     * @param upperLayerFragmentCreate 用于创建覆盖在页面上方的Fragment
     * @param bundle                   传入数据
     * @param followTouch              是否跟随拖动
     * @param touchingHide             拖动图片时是否隐藏Fragment页面
     * @return {@link OpenImage}
     */
    public OpenImage setUpperLayerFragmentCreate(UpperLayerFragmentCreate upperLayerFragmentCreate, Bundle bundle, boolean followTouch, boolean touchingHide) {
        upperLayerFragmentCreateKey = UUID.randomUUID().toString();
        upperLayerBundle = bundle;
        ImageLoadUtils.getInstance().setUpperLayerFragmentCreate(upperLayerFragmentCreateKey, new UpperLayerOption(upperLayerFragmentCreate, followTouch, touchingHide));
        return this;
    }

    /**
     * 如果以上定义页面样式的方法还不够用，可继承 OpenImageActivity 页面自己去写页面
     *
     * @param openImageActivityCls 自己定义的大图页面，必须继承 {@link OpenImageActivity}
     * @return {@link OpenImage}
     */
    public OpenImage setOpenImageActivityCls(@NonNull Class<? extends OpenImageActivity> openImageActivityCls) {
        return setOpenImageActivityCls(openImageActivityCls, null, null, null);
    }

    /**
     * 如果以上定义页面样式的方法还不够用，可继承 OpenImageActivity 页面自己去写页面
     *
     * @param openImageActivityCls 自己定义的大图页面，必须继承 {@link OpenImageActivity}
     * @param bundleKey            传给页面的数据 key [ bundle = getIntent().getBundleExtra(bundleKey) ]
     * @param bundle               传给页面的数据
     * @return {@link OpenImage}
     */
    public OpenImage setOpenImageActivityCls(@NonNull Class<? extends OpenImageActivity> openImageActivityCls, String bundleKey, Bundle bundle) {
        return setOpenImageActivityCls(openImageActivityCls, bundleKey, bundle, null);
    }

    /**
     * 如果以上定义页面样式的方法还不够用，可继承 OpenImageActivity 页面自己去写页面
     *
     * @param openImageActivityCls 自己定义的大图页面，必须继承 {@link OpenImageActivity}
     * @param onUpdateViewListener 如果您想在大图页面加载更多数据并且更新前一页面的列表，那么你可传入此接口
     * @return {@link OpenImage}
     */
    public OpenImage setOpenImageActivityCls(@NonNull Class<? extends OpenImageActivity> openImageActivityCls, OnUpdateViewListener onUpdateViewListener) {
        return setOpenImageActivityCls(openImageActivityCls, null, null, onUpdateViewListener);
    }

    /**
     * 如果以上定义页面样式的方法还不够用，可继承 OpenImageActivity 页面自己去写页面
     *
     * @param openImageActivityCls 自己定义的大图页面，必须继承 {@link OpenImageActivity}
     * @param bundleKey            传给页面的数据 key [ bundle = getIntent().getBundleExtra(bundleKey) ]
     * @param bundle               传给页面的数据
     * @param onUpdateViewListener 如果您想在大图页面加载更多数据并且更新前一页面的列表，那么你可传入此接口
     * @return {@link OpenImage}
     */
    public OpenImage setOpenImageActivityCls(@NonNull Class<? extends OpenImageActivity> openImageActivityCls, String bundleKey, Bundle bundle, @Nullable OnUpdateViewListener onUpdateViewListener) {
        this.openImageActivityCls = openImageActivityCls;
        this.openImageActivityClsBundleKey = bundleKey;
        this.onUpdateViewListener = onUpdateViewListener;
        if (bundle != null && TextUtils.isEmpty(bundleKey)) {
            throw new IllegalArgumentException("bundleKey 不能为 null");
        }
        this.openImageActivityClsBundle = bundle;
        return this;
    }

    /**
     * 如果你想让圆图和矩形圆角图在打开关闭时追求更细腻的体验可以设置这个
     *
     * @param shapeType             图片类型
     * @param rectangleConnerRadius 如果是矩形圆角图，设置这个为圆角角度
     * @return
     */
    public OpenImage setImageShapeParams(ImageShapeType shapeType, RectangleConnerRadius rectangleConnerRadius) {
        if (shapeType == null) {
            throw new IllegalArgumentException("shapeType 不能为 null");
        }
        imageShapeParams = new ImageShapeParams(shapeType, rectangleConnerRadius);
        return this;
    }

    /**
     * 如果你需要监听大图退出复位的时刻不妨调用这个
     *
     * @param onExitListener 退出复位监听
     */
    public OpenImage setOnExitListener(OnExitListener onExitListener) {
        this.onExitListener = onExitListener;
        return this;
    }

    /**
     * 设置显示下载按钮
     */
    public OpenImage setShowDownload() {
        return setShowDownload(null);
    }

    /**
     * 设置显示下载按钮
     * @param downloadParams 下载按钮相关参数{@link DownloadParams}
     */
    public OpenImage setShowDownload(DownloadParams downloadParams) {
        this.showDownload = true;
        this.downloadParams = downloadParams;
        return this;
    }

    /**
     * 设置显示关闭按钮
     */
    public OpenImage setShowClose() {
        return setShowClose(new CloseParams());
    }

    /**
     * 设置显示关闭按钮
     * @param closeParams 关闭按钮相关参数{@link CloseParams}
     */
    public OpenImage setShowClose(CloseParams closeParams) {
        this.showClose = true;
        this.closeParams = closeParams;
        return this;
    }

    /**
     * 禁用下拉触摸关闭页面功能，设置此项后{@link OpenImageConfig#setDisEnableTouchClose(boolean)} (boolean)} 就不起作用了
     *
     * @return {@link OpenImage}
     */
    public OpenImage disableTouchClose() {
        disableTouchClose = true;
        return this;
    }

    /**
     * 启用下拉触摸关闭页面功能
     *
     * @return {@link OpenImage}，设置此项后{@link OpenImageConfig#setDisEnableTouchClose(boolean)} 就不起作用了
     */
    public OpenImage enableTouchClose() {
        disableTouchClose = false;
        return this;
    }

    /**
     * 打开大图页面，只可以调用一次哦～切勿多次调用
     */
    public void show() {
        if (!isCallShow) {
            isCallShow = true;
        } else if (ActivityCompatHelper.isApkInDebug(context)) {
            throw new UnsupportedOperationException("不可以多次调用 show 方法");
        }
        goShow();
    }


}
