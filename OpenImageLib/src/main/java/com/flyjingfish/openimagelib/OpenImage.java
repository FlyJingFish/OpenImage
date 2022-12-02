package com.flyjingfish.openimagelib;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;


import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.enums.ImageDiskMode;
import com.flyjingfish.openimagelib.enums.MediaType;
import com.flyjingfish.openimagelib.enums.MoreViewShowType;
import com.flyjingfish.openimagelib.listener.ImageFragmentCreate;
import com.flyjingfish.openimagelib.listener.ItemLoadHelper;
import com.flyjingfish.openimagelib.listener.OnItemClickListener;
import com.flyjingfish.openimagelib.listener.OnItemLongClickListener;
import com.flyjingfish.openimagelib.listener.OnLoadViewFinishListener;
import com.flyjingfish.openimagelib.listener.OnSelectMediaListener;
import com.flyjingfish.openimagelib.listener.SourceImageViewGet;
import com.flyjingfish.openimagelib.listener.SourceImageViewIdGet;
import com.flyjingfish.openimagelib.listener.UpperLayerFragmentCreate;
import com.flyjingfish.openimagelib.listener.VideoFragmentCreate;
import com.flyjingfish.shapeimageviewlib.ShapeImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class OpenImage extends OpenImage4ParseData {
    public static OpenImage with(Context context) {
        return new OpenImage(context);
    }

    private OpenImage(Context context) {
        if (context instanceof Activity) {
            this.context = context;
        } else {
            throw new IllegalArgumentException("context must be activity");
        }
    }

    /**
     * @param openImageUrls 图片数据组
     * @return
     */
    public OpenImage setImageUrlList(List<? extends OpenImageUrl> openImageUrls) {
        this.openImageUrls.addAll(openImageUrls);
        return this;
    }

    /**
     * @param openImageUrl 单个图片数据可设置这个
     * @return
     */
    public OpenImage setImageUrl(OpenImageUrl openImageUrl) {
        return setImageUrlList(new ArrayList<>(Arrays.asList(openImageUrl)));
    }

    /**
     * @param openImageUrls 图片String数据组
     * @param mediaType     图片还是视频
     * @return
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
     * @param openImageUrl 单个String图片数据可设置这个
     * @param mediaType    图片还是视频
     * @return
     */
    public OpenImage setImageUrl(String openImageUrl, MediaType mediaType) {
        return setImageUrlList(new ArrayList<>(Arrays.asList(new SingleImageUrl(openImageUrl, mediaType))));
    }

    /**
     * @param recyclerView         展示数据的RecyclerView
     * @param sourceImageViewIdGet 展示数据的RecyclerView 的图片Id
     * @return
     */
    public OpenImage setClickRecyclerView(RecyclerView recyclerView, SourceImageViewIdGet<OpenImageUrl> sourceImageViewIdGet) {
        this.recyclerView = recyclerView;
        this.sourceImageViewIdGet = sourceImageViewIdGet;
        return this;
    }

    /**
     * @param viewPager2           展示数据的ViewPager2
     * @param sourceImageViewIdGet 展示数据的ViewPager2 的图片Id
     * @return
     */
    public OpenImage setClickViewPager2(ViewPager2 viewPager2, SourceImageViewIdGet<OpenImageUrl> sourceImageViewIdGet) {
        this.viewPager2 = viewPager2;
        this.sourceImageViewIdGet = sourceImageViewIdGet;
        return this;
    }

    /**
     * @param viewPager          展示数据的ViewPager
     * @param sourceImageViewGet 展示数据的ViewPager 的图片ImageView
     * @return
     */
    public OpenImage setClickViewPager(ViewPager viewPager, SourceImageViewGet<OpenImageUrl> sourceImageViewGet) {
        this.viewPager = viewPager;
        this.sourceImageViewGet = sourceImageViewGet;
        return this;
    }

    /**
     * @param gridView             展示数据的GridView
     * @param sourceImageViewIdGet 展示数据的GridView 的图片Id
     * @return
     */
    public OpenImage setClickGridView(GridView gridView, SourceImageViewIdGet<OpenImageUrl> sourceImageViewIdGet) {
        this.absListView = gridView;
        this.sourceImageViewIdGet = sourceImageViewIdGet;
        return this;
    }

    /**
     * @param listView             展示数据的ListView
     * @param sourceImageViewIdGet 展示数据的ListView 的图片Id
     * @return
     */
    public OpenImage setClickListView(ListView listView, SourceImageViewIdGet<OpenImageUrl> sourceImageViewIdGet) {
        this.absListView = listView;
        this.sourceImageViewIdGet = sourceImageViewIdGet;
        return this;
    }

    /**
     * @param imageViews 自己传展示数据的ImageView组
     * @return
     */
    public OpenImage setClickImageViews(ImageView[] imageViews) {
        return setClickImageViews(new ArrayList<>(Arrays.asList(imageViews)));
    }

    /**
     * @param imageView 自己传展示数据的单个ImageView
     * @return
     */
    public OpenImage setClickImageView(ImageView imageView) {
        return setClickImageViews(new ArrayList<>(Arrays.asList(imageView)));
    }

    /**
     * @param imageViews 自己传展示数据的ImageView组
     * @return
     */
    public OpenImage setClickImageViews(List<ImageView> imageViews) {
        this.imageViews = imageViews;
        return this;
    }

    /**
     * 如果数据下标 和 RecyclerView、ViewPager2、ListView、GridView 的所在位置一致 可调用这个
     *
     * @param clickPosition 点击的图片和View所在的位置
     * @return
     */
    public OpenImage setClickPosition(int clickPosition) {
        return setClickPosition(clickPosition, clickPosition);
    }

    /**
     * 如果数据下标 和 RecyclerView、ViewPager2、ListView、GridView 的所在位置不一致 调用这个
     *
     * @param clickDataPosition 点击的图片所在数据的位置
     * @param clickViewPosition 点击的图片View在RecyclerView或ListView或GridView的位置
     * @return
     */
    public OpenImage setClickPosition(int clickDataPosition, int clickViewPosition) {
        this.clickDataPosition = clickDataPosition;
        this.clickViewPosition = clickViewPosition;
        return this;
    }

    /**
     * @param srcImageViewScaleType 点击的ImageView显示模式
     * @param autoSetScaleType      如果点击的ImageView与您所设置scaleType不相同，则自动设置
     * @return
     */
    public OpenImage setSrcImageViewScaleType(ImageView.ScaleType srcImageViewScaleType, boolean autoSetScaleType) {
        this.srcImageViewScaleType = srcImageViewScaleType;
        this.autoSetScaleType = autoSetScaleType;
        return this;
    }

    /**
     * 使用ShapeImageView时调用这个
     *
     * @param srcImageViewShapeScaleType 点击的ShapeImageView显示模式
     * @param autoSetScaleType           如果点击的ShapeImageView与您所设置scaleType不相同，则自动设置
     * @return
     */
    public OpenImage setSrcImageViewScaleType(ShapeImageView.ShapeScaleType srcImageViewShapeScaleType, boolean autoSetScaleType) {
        this.srcImageViewShapeScaleType = srcImageViewShapeScaleType;
        this.autoSetScaleType = autoSetScaleType;
        return this;
    }

    /**
     * @param imageDiskMode 点击的ImageView图片所缓存的模式（建议缓存原图）
     * @return
     */
    public OpenImage setImageDiskMode(ImageDiskMode imageDiskMode) {
        this.imageDiskMode = imageDiskMode;
        return this;
    }

    /**
     * 加载大图失败后
     * <p>
     * 一,如果设置此选项则展示这个errorResId图片
     * 二,如果不设置
     * 1> 有缓存则展示缓存图片
     * 2> 没有缓存图片就展示上个页面小图的加载失败图片（可在ItemLoadHelper.loadImage中设置加载失败图片）
     *
     * @param errorResId 大图加载失败后显示的图片
     * @return
     */
    public OpenImage setErrorResId(@DrawableRes int errorResId) {
        this.errorResId = errorResId;
        return this;
    }

    /**
     * @param itemLoadHelper 图片加载器，当图片缓存模式不包含原图时，请设置和前一页面加载图片一样的配置
     * @return
     */
    public OpenImage setItemLoadHelper(ItemLoadHelper itemLoadHelper) {
        itemLoadHelperKey = UUID.randomUUID().toString();
        ImageLoadUtils.getInstance().setItemLoadHelper(itemLoadHelperKey, itemLoadHelper);
        return this;
    }

    /**
     * @param openImageStyle 查看图片显示设置StyleId
     * @return
     */
    public OpenImage setOpenImageStyle(@StyleRes int openImageStyle) {
        this.openImageStyle = openImageStyle;
        return this;
    }

    /**
     * @param openPageAnimTimeMs 打开页面动画的时间
     * @return
     */
    public OpenImage setOpenPageAnimTimeMs(long openPageAnimTimeMs) {
        this.openPageAnimTimeMs = openPageAnimTimeMs;
        return this;
    }

    /**
     * @param onSelectMediaListener 回调查看图片所在数据的位置
     * @return
     */
    public OpenImage setOnSelectMediaListener(OnSelectMediaListener onSelectMediaListener) {
        onselectKey = UUID.randomUUID().toString();
        ImageLoadUtils.getInstance().setOnSelectMediaListener(onselectKey, onSelectMediaListener);
        return this;
    }

    /**
     * 只对传入RecyclerView，ViewPager，ViewPager2， ListView, GridView 有效
     *
     * @param autoScrollScanPosition 自动滑向最后看的图片的位置
     * @return
     */
    public OpenImage setAutoScrollScanPosition(boolean autoScrollScanPosition) {
        isAutoScrollScanPosition = autoScrollScanPosition;
        return this;
    }

    /**
     * @param pageTransformer ViewPager的页面切换效果
     * @return
     */
    public OpenImage addPageTransformer(ViewPager2.PageTransformer... pageTransformer) {
        pageTransformersKey = UUID.randomUUID().toString();
        ImageLoadUtils.getInstance().setPageTransformers(pageTransformersKey, new ArrayList<>(Arrays.asList(pageTransformer)));
        return this;
    }

    /**
     * @param leftRightShowWidthDp 可设置画廊效果，左右漏出的宽度，单位dp
     * @return
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
     * @return
     */
    public OpenImage setWechatExitFillInEffect(boolean wechatExitFillInEffect) {
        this.wechatExitFillInEffect = wechatExitFillInEffect;
        return this;
    }

    /**
     * 设置点击图片监听
     *
     * @param onItemClickListener
     * @return
     */
    public OpenImage setOnItemClickListener(OnItemClickListener onItemClickListener) {
        onItemClickListenerKey = UUID.randomUUID().toString();
        ImageLoadUtils.getInstance().setOnItemClickListener(onItemClickListenerKey, onItemClickListener);
        return this;
    }

    /**
     * 设置长按图片监听
     *
     * @param onItemLongClickListener
     * @return
     */
    public OpenImage setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        onItemLongClickListenerKey = UUID.randomUUID().toString();
        ImageLoadUtils.getInstance().setOnItemLongClickListener(onItemLongClickListenerKey, onItemLongClickListener);
        return this;
    }

    /**
     * 禁用点击图片关闭页面功能
     *
     * @return
     */
    public OpenImage disableClickClose() {
        disableClickClose = true;
        return this;
    }

    /**
     * 添加View 到大图页面，此方法可多次调用，添加多个View
     *
     * @param layoutRes                添加的布局xml id
     * @param layoutParams             要添加到页面布局的参数
     * @param moreViewShowType         展示类型
     * @param onLoadViewFinishListener 加载完毕View后回调
     * @return
     */
    public OpenImage addMoreView(@LayoutRes int layoutRes, @NonNull FrameLayout.LayoutParams layoutParams, MoreViewShowType moreViewShowType, OnLoadViewFinishListener onLoadViewFinishListener) {
        return addMoreView(layoutRes, layoutParams, moreViewShowType, false, onLoadViewFinishListener);
    }

    /**
     * 添加View 到大图页面，此方法可多次调用，添加多个View
     *
     * @param layoutView       添加的View
     * @param layoutParams     要添加到页面布局的参数
     * @param moreViewShowType 展示类型
     * @return
     */
    public OpenImage addMoreView(View layoutView, @NonNull FrameLayout.LayoutParams layoutParams, MoreViewShowType moreViewShowType) {
        return addMoreView(layoutView, layoutParams, moreViewShowType, false);
    }

    /**
     * @param layoutRes                添加的布局xml id
     * @param layoutParams             要添加到页面布局的参数
     * @param moreViewShowType         展示类型
     * @param followTouch              是否跟随图片拖动
     * @param onLoadViewFinishListener 加载完毕View后回调
     * @return
     */
    public OpenImage addMoreView(@LayoutRes int layoutRes, @NonNull FrameLayout.LayoutParams layoutParams, MoreViewShowType moreViewShowType, boolean followTouch, OnLoadViewFinishListener onLoadViewFinishListener) {
        MoreViewOption moreViewOption = new MoreViewOption(layoutRes, layoutParams, moreViewShowType, followTouch, onLoadViewFinishListener);
        moreViewOptions.add(moreViewOption);
        return this;
    }

    /**
     * @param layoutView       添加的View
     * @param layoutParams     要添加到页面布局的参数
     * @param moreViewShowType 展示类型
     * @param followTouch      是否跟随图片拖动
     * @return
     */
    public OpenImage addMoreView(View layoutView, @NonNull FrameLayout.LayoutParams layoutParams, MoreViewShowType moreViewShowType, boolean followTouch) {
        MoreViewOption moreViewOption = new MoreViewOption(layoutView, layoutParams, moreViewShowType, followTouch);
        moreViewOptions.add(moreViewOption);
        return this;
    }

    /**
     * @param showSrcImageView 退出时，前一页面的ImageView是否可见
     * @return
     */
    public OpenImage setShowSrcImageView(boolean showSrcImageView) {
        this.showSrcImageView = showSrcImageView;
        return this;
    }

    /**
     * 调用这个方法将使 OpenImageConfig 的配置失效
     *
     * @param imageFragmentCreate 用于自定义图片展示页面
     * @return
     */
    public OpenImage setImageFragmentCreate(ImageFragmentCreate imageFragmentCreate) {
        imageFragmentCreateKey = UUID.randomUUID().toString();
        ImageLoadUtils.getInstance().setImageFragmentCreate(imageFragmentCreateKey, imageFragmentCreate);
        return this;
    }

    /**
     * 调用这个方法将使 OpenImageConfig 的配置失效
     *
     * @param videoFragmentCreate 用于自定义视频展示页面
     * @return
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
     * @return
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
     * @return
     */
    public OpenImage setUpperLayerFragmentCreate(UpperLayerFragmentCreate upperLayerFragmentCreate, Bundle bundle, boolean followTouch) {
        return setUpperLayerFragmentCreate(upperLayerFragmentCreate, bundle, followTouch,true);
    }

    /**
     * 这是可以显示在页面上方的Fragment
     *
     * @param upperLayerFragmentCreate 用于创建覆盖在页面上方的Fragment
     * @param bundle                   传入数据
     * @param followTouch              是否跟随拖动
     * @return
     */
    public OpenImage setUpperLayerFragmentCreate(UpperLayerFragmentCreate upperLayerFragmentCreate, Bundle bundle, boolean followTouch,boolean touchingHide) {
        upperLayerFragmentCreateKey = UUID.randomUUID().toString();
        upperLayerBundle = bundle;
        ImageLoadUtils.getInstance().setUpperLayerFragmentCreate(upperLayerFragmentCreateKey, new UpperLayerOption(upperLayerFragmentCreate, followTouch,touchingHide));
        return this;
    }

    /**
     * 打开大图页面
     */
    public void show() {
        goShow();
    }



}
