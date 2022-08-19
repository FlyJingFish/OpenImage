package com.flyjingfish.openimagelib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.beans.OpenImageDetail;
import com.flyjingfish.openimagelib.enums.BackViewType;
import com.flyjingfish.openimagelib.enums.ImageDiskMode;
import com.flyjingfish.openimagelib.enums.MediaType;
import com.flyjingfish.openimagelib.listener.ItemLoadHelper;
import com.flyjingfish.openimagelib.listener.OnLoadBigImageListener;
import com.flyjingfish.openimagelib.listener.OnSelectMediaListener;
import com.flyjingfish.openimagelib.listener.SourceImageViewIdGet;
import com.flyjingfish.openimagelib.utils.ActivityCompatHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class OpenImage {
    private Context context;
    private final List<OpenImageUrl> openImageUrls = new ArrayList<>();
    private List<ImageView> imageViews;
    private RecyclerView recyclerView;
    private AbsListView absListView;
    private long openPageAnimTimeMs;
    private int clickViewPosition;
    private int clickDataPosition;
    private int errorResId;
    private int openImageStyle;
    private ImageView.ScaleType srcImageViewScaleType;
    private boolean autoSetScaleType;
    private ImageDiskMode imageDiskMode = ImageDiskMode.CONTAIN_ORIGINAL;
    private ItemLoadHelper itemLoadHelper;
    private final HashSet<Integer> srcWidthCache = new HashSet<>();
    private final HashSet<Integer> srcHeightCache = new HashSet<>();
    private boolean isStartActivity;
    private boolean isAutoScrollScanPosition = false;
    private boolean wechatExitFillInEffect = false;
    private OnSelectMediaListener onSelectMediaListener;
    private SourceImageViewIdGet<OpenImageUrl> sourceImageViewIdGet;
    private final List<ViewPager2.PageTransformer> pageTransformers = new ArrayList<>();
    private int leftRightShowWidthDp;
    public static boolean isCanOpen = true;

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
     * 如果数据下标 和 RecyclerView或ListView或GridView 的所在位置一致 可调用这个
     *
     * @param clickPosition 点击的图片和View所在的位置
     * @return
     */
    public OpenImage setClickPosition(int clickPosition) {
        this.setClickPosition(clickPosition, clickPosition);
        return this;
    }

    /**
     * 如果数据下标 和 RecyclerView或ListView或GridView 的所在位置不一致 调用这个
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
        this.itemLoadHelper = itemLoadHelper;
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
        this.onSelectMediaListener = onSelectMediaListener;
        return this;
    }

    /**
     * 只对传入RecyclerView, ListView, GridView 有效
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
        pageTransformers.addAll(Arrays.asList(pageTransformer));
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
     *
     * 设置微信补位效果，设置后当退出大图页面时，如果前一页面没有当前图片，则自动回到点击进来的那张图的位置
     * 开启后自动自动滚动效果关闭
     * (只对父容器是RecyclerView, ListView, GridView 时有效)
     * @param wechatExitFillInEffect 是否设置微信补位效果
     * @return
     */
    public OpenImage setWechatExitFillInEffect(boolean wechatExitFillInEffect) {
        this.wechatExitFillInEffect = wechatExitFillInEffect;
        return this;
    }

    private void show4ParseData() {
        if (openImageUrls.size() == 0) {
            throw new IllegalArgumentException("请设置数据");
        }
        if (itemLoadHelper == null) {
            throw new IllegalArgumentException("请设置ItemLoadHelper");
        }
        if (clickDataPosition >= openImageUrls.size()) {
            throw new IllegalArgumentException("clickDataPosition不能 >= OpenImageUrl 的个数");
        }
        if (wechatExitFillInEffect){
            isAutoScrollScanPosition = false;
        }
        Intent intent = new Intent(context, ViewPagerActivity.class);

        intent.putExtra(OpenParams.CLICK_POSITION, clickDataPosition);
        if (onSelectMediaListener != null) {
            String selectKey = UUID.randomUUID().toString();
            ImageLoadUtils.getInstance().setOnSelectMediaListener(selectKey, onSelectMediaListener);
            intent.putExtra(OpenParams.ON_SELECT_KEY, selectKey);
        }
        if (pageTransformers.size() > 0) {
            String pageTransformersKey = UUID.randomUUID().toString();
            ImageLoadUtils.getInstance().setPageTransformers(pageTransformersKey, pageTransformers);
            intent.putExtra(OpenParams.PAGE_TRANSFORMERS, pageTransformersKey);
        }
        intent.putExtra(OpenParams.AUTO_SCROLL_SELECT, isAutoScrollScanPosition);
        intent.putExtra(OpenParams.DISABLE_TOUCH_CLOSE, OpenImageConfig.getInstance().isDisEnableTouchClose());
        intent.putExtra(OpenParams.SRC_SCALE_TYPE, srcImageViewScaleType);
        intent.putExtra(OpenParams.IMAGE_DISK_MODE, imageDiskMode);
        intent.putExtra(OpenParams.ERROR_RES_ID, errorResId);
        String key = UUID.randomUUID().toString();
        ImageLoadUtils.getInstance().setItemLoadHelper(key, itemLoadHelper);
        intent.putExtra(OpenParams.ITEM_LOAD_KEY, key);
        intent.putExtra(OpenParams.TOUCH_CLOSE_SCALE, OpenImageConfig.getInstance().getTouchCloseScale());
        intent.putExtra(OpenParams.OPEN_IMAGE_STYLE, openImageStyle);
        intent.putExtra(OpenParams.OPEN_ANIM_TIME_MS, openPageAnimTimeMs);
        intent.putExtra(OpenParams.GALLERY_EFFECT_WIDTH, leftRightShowWidthDp);

        if (recyclerView != null) {
            if (sourceImageViewIdGet == null) {
                throw new IllegalArgumentException("sourceImageViewIdGet 不能为null");
            }

            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (!(layoutManager instanceof LinearLayoutManager || layoutManager instanceof StaggeredGridLayoutManager)) {
                throw new IllegalArgumentException("只支持使用继承自LinearLayoutManager和StaggeredGridLayoutManager的RecyclerView");
            }

            View shareViewClick = null;
            String shareNameClick = null;
            int firstPos = 0;
            int lastPos = 0;
            ArrayList<OpenImageDetail> openImageDetails = new ArrayList<>();
            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                firstPos = linearLayoutManager.findFirstVisibleItemPosition();
                lastPos = linearLayoutManager.findLastVisibleItemPosition();
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                int[] firstVisibleItems = null;
                firstVisibleItems = staggeredGridLayoutManager.findFirstVisibleItemPositions(firstVisibleItems);
                int[] lastVisibleItems = null;
                lastVisibleItems = staggeredGridLayoutManager.findLastVisibleItemPositions(lastVisibleItems);
                if (firstVisibleItems == null || lastVisibleItems == null || firstVisibleItems.length == 0 || lastVisibleItems.length == 0) {
                    return;
                }
                firstPos = Integer.MAX_VALUE;
                lastPos = 0;
                for (int firstVisibleItem : firstVisibleItems) {
                    if (firstVisibleItem < firstPos && firstVisibleItem >= 0) {
                        firstPos = firstVisibleItem;
                    }
                }
                for (int lastVisibleItem : lastVisibleItems) {
                    if (lastVisibleItem > lastPos) {
                        lastPos = lastVisibleItem;
                    }
                }
                if (lastPos < firstPos) {
                    return;
                }

            }
            int viewIndex = clickViewPosition - clickDataPosition;
            for (int i = 0; i < openImageUrls.size(); i++) {
                OpenImageUrl imageBean = openImageUrls.get(i);
                if (imageBean.getType() == MediaType.IMAGE || imageBean.getType() == MediaType.VIDEO) {
                    OpenImageDetail openImageDetail = new OpenImageDetail();
                    openImageDetail.openImageUrl = imageBean;
                    openImageDetail.dataPosition = i;
                    if (viewIndex >= firstPos && viewIndex <= lastPos) {
                        View view = layoutManager.findViewByPosition(viewIndex);
                        if (view == null) {
                            continue;
                        }
                        ImageView shareView = view.findViewById(sourceImageViewIdGet.getImageViewId(imageBean, i));
                        if (autoSetScaleType && shareView.getScaleType() != srcImageViewScaleType) {
                            shareView.setScaleType(srcImageViewScaleType);
                        }
                        String shareName = OpenParams.SHARE_VIEW + openImageDetails.size();
                        if (clickViewPosition == viewIndex) {
                            shareViewClick = shareView;
                            shareNameClick = shareName;
                        }
                        int shareViewWidth = shareView.getWidth();
                        int shareViewHeight = shareView.getHeight();
                        openImageDetail.srcWidth = shareViewWidth;
                        openImageDetail.srcHeight = shareViewHeight;
                        srcWidthCache.add(shareViewWidth);
                        srcHeightCache.add(shareViewHeight);
                    }
                    openImageDetail.viewPosition = viewIndex;
                    openImageDetails.add(openImageDetail);
                }
                viewIndex++;
            }
            if (shareViewClick == null) {
                throw new IllegalArgumentException("请确保是否调用了setClickPosition并且参数设置正确");
            }
            ImageLoadUtils.getInstance().setOnBackView(new ExitOnBackView(shareViewClick) {

                @Override
                public BackViewType onBack(int showPosition) {
                    BackViewType backViewType = BackViewType.NO_SHARE;
                    Activity activity = ActivityCompatHelper.getActivity(context);
                    if (activity == null) {
                        return BackViewType.NO_SHARE;
                    }
                    int firstPos = 0;
                    int lastPos = 0;
                    if (layoutManager instanceof LinearLayoutManager) {
                        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                        firstPos = linearLayoutManager.findFirstVisibleItemPosition();
                        lastPos = linearLayoutManager.findLastVisibleItemPosition();
                    } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                        StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                        int[] firstVisibleItems = null;
                        firstVisibleItems = staggeredGridLayoutManager.findFirstVisibleItemPositions(firstVisibleItems);
                        int[] lastVisibleItems = null;
                        lastVisibleItems = staggeredGridLayoutManager.findLastVisibleItemPositions(lastVisibleItems);
                        if (firstVisibleItems == null || lastVisibleItems == null || firstVisibleItems.length == 0 || lastVisibleItems.length == 0) {
                            return backViewType;
                        }
                        firstPos = Integer.MAX_VALUE;
                        lastPos = 0;
                        for (int firstVisibleItem : firstVisibleItems) {
                            if (firstVisibleItem < firstPos && firstVisibleItem >= 0) {
                                firstPos = firstVisibleItem;
                            }
                        }
                        for (int lastVisibleItem : lastVisibleItems) {
                            if (lastVisibleItem > lastPos) {
                                lastPos = lastVisibleItem;
                            }
                        }
                        if (lastPos < firstPos) {
                            return backViewType;
                        }

                    }
                    if (lastPos < 0 || firstPos < 0) {
                        return backViewType;
                    }
                    ImageView shareExitView = null;
                    OpenImageDetail openImageDetail = openImageDetails.get(showPosition);
                    OpenImageUrl openImageUrl = openImageDetail.openImageUrl;
                    int viewPosition = openImageDetail.viewPosition;
                    int dataPosition = openImageDetail.dataPosition;

                    View view = layoutManager.findViewByPosition(viewPosition);

                    if (view != null) {
                        ImageView shareView = view.findViewById(sourceImageViewIdGet.getImageViewId(openImageUrl, dataPosition));
                        if (shareView != null) {
                            if (autoSetScaleType && shareView.getScaleType() != srcImageViewScaleType) {
                                shareView.setScaleType(srcImageViewScaleType);
                            }
                            boolean isAttachedToWindow = shareView.isAttachedToWindow();
                            if (isAttachedToWindow) {
                                shareExitView = shareView;
                                backViewType = BackViewType.SHARE_NORMAL;
                            }
                        }
                    }

                    if (shareExitView == null && wechatExitFillInEffect){
                        openImageUrl = openImageUrls.get(clickDataPosition);
                        viewPosition = clickViewPosition;
                        dataPosition = clickDataPosition;
                        View wechatView = layoutManager.findViewByPosition(viewPosition);
                        if (wechatView != null) {
                            ImageView shareView = wechatView.findViewById(sourceImageViewIdGet.getImageViewId(openImageUrl, dataPosition));
                            if (shareView != null) {
                                if (autoSetScaleType && shareView.getScaleType() != srcImageViewScaleType) {
                                    shareView.setScaleType(srcImageViewScaleType);
                                }
                                boolean isAttachedToWindow = shareView.isAttachedToWindow();
                                if (isAttachedToWindow) {
                                    shareExitView = shareView;
                                    backViewType = BackViewType.SHARE_WECHAT;
                                }
                            }
                        }
                    }

                    activity.setExitSharedElementCallback(new ExitSharedElementCallback(context, srcImageViewScaleType, shareExitView));
                    return backViewType;
                }

                @Override
                public void onScrollPos(int pos) {
                    if (!isAutoScrollScanPosition) {
                        return;
                    }
                    final RecyclerView.LayoutManager layoutManager =
                            recyclerView.getLayoutManager();
                    View viewAtPosition =
                            layoutManager.findViewByPosition(pos);
                    if (viewAtPosition == null
                            || layoutManager.isViewPartiallyVisible(viewAtPosition, true, true)) {
                        recyclerView.post(() -> layoutManager.scrollToPosition(pos));
                    }
                }
            });

            intent.putExtra(OpenParams.IMAGES, openImageDetails);
            replenishImageUrl(openImageDetails);
            Bundle newOptions = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, shareViewClick, shareNameClick).toBundle();
            open(intent, newOptions);
        } else if (absListView != null) {
            if (sourceImageViewIdGet == null) {
                throw new IllegalArgumentException("sourceImageViewIdGet 不能为null");
            }
            View shareViewClick = null;
            String shareNameClick = null;
            int firstListItemPosition = absListView.getFirstVisiblePosition();
            int lastListItemPosition = absListView.getLastVisiblePosition();
            ArrayList<OpenImageDetail> openImageDetails = new ArrayList<>();

            int viewIndex = clickViewPosition - clickDataPosition;
            for (int i = 0; i < openImageUrls.size(); i++) {
                OpenImageUrl imageBean = openImageUrls.get(i);
                if (imageBean.getType() == MediaType.IMAGE || imageBean.getType() == MediaType.VIDEO) {
                    OpenImageDetail openImageDetail = new OpenImageDetail();
                    openImageDetail.openImageUrl = imageBean;
                    openImageDetail.dataPosition = i;

                    if (viewIndex >= firstListItemPosition && viewIndex <= lastListItemPosition) {
                        View view = absListView.getChildAt(viewIndex - firstListItemPosition);
                        if (view == null) {
                            continue;
                        }
                        ImageView shareView = view.findViewById(sourceImageViewIdGet.getImageViewId(imageBean, i));
                        if (autoSetScaleType && shareView.getScaleType() != srcImageViewScaleType) {
                            shareView.setScaleType(srcImageViewScaleType);
                        }
                        String shareName = OpenParams.SHARE_VIEW + openImageDetails.size();
                        int shareViewWidth = shareView.getWidth();
                        int shareViewHeight = shareView.getHeight();
                        openImageDetail.srcWidth = shareViewWidth;
                        openImageDetail.srcHeight = shareViewHeight;
                        srcWidthCache.add(shareViewWidth);
                        srcHeightCache.add(shareViewHeight);

                        if (clickViewPosition == viewIndex) {
                            shareViewClick = shareView;
                            shareNameClick = shareName;
                        }

                    }
                    openImageDetail.viewPosition = viewIndex;
                    openImageDetails.add(openImageDetail);
                }
                viewIndex++;
            }
            if (shareViewClick == null) {
                throw new IllegalArgumentException("请确保是否调用了setClickPosition并且参数设置正确");
            }
            ImageLoadUtils.getInstance().setOnBackView(new ExitOnBackView(shareViewClick) {

                @Override
                public BackViewType onBack(int showPosition) {
                    BackViewType backViewType = BackViewType.NO_SHARE;
                    Activity activity = ActivityCompatHelper.getActivity(context);
                    if (activity == null) {
                        return backViewType;
                    }
                    int firstPos = absListView.getFirstVisiblePosition();
                    int lastPos = absListView.getLastVisiblePosition();

                    if (lastPos < 0 || firstPos < 0) {
                        return backViewType;
                    }

                    ImageView shareExitView = null;

                    OpenImageDetail openImageDetail = openImageDetails.get(showPosition);
                    OpenImageUrl openImageUrl = openImageDetail.openImageUrl;
                    int viewPosition = openImageDetail.viewPosition;
                    int dataPosition = openImageDetail.dataPosition;

                    View view = absListView.getChildAt(viewPosition - firstPos);
                    if (view != null) {
                        ImageView shareView = view.findViewById(sourceImageViewIdGet.getImageViewId(openImageUrl, dataPosition));
                        if (shareView != null) {
                            if (autoSetScaleType && shareView.getScaleType() != srcImageViewScaleType) {
                                shareView.setScaleType(srcImageViewScaleType);
                            }
                            boolean isAttachedToWindow = shareView.isAttachedToWindow();
                            if (isAttachedToWindow) {
                                shareExitView = shareView;
                                backViewType = BackViewType.SHARE_NORMAL;
                            }
                        }
                    }

                    if (shareExitView == null && wechatExitFillInEffect){
                        openImageUrl = openImageUrls.get(clickDataPosition);
                        viewPosition = clickViewPosition;
                        dataPosition = clickDataPosition;

                        View wechatView = absListView.getChildAt(viewPosition - firstPos);
                        if (wechatView != null) {
                            ImageView shareView = wechatView.findViewById(sourceImageViewIdGet.getImageViewId(openImageUrl, dataPosition));
                            if (shareView != null) {
                                if (autoSetScaleType && shareView.getScaleType() != srcImageViewScaleType) {
                                    shareView.setScaleType(srcImageViewScaleType);
                                }
                                boolean isAttachedToWindow = shareView.isAttachedToWindow();
                                if (isAttachedToWindow) {
                                    shareExitView = shareView;
                                    backViewType = BackViewType.SHARE_WECHAT;
                                }
                            }
                        }
                    }

                    activity.setExitSharedElementCallback(new ExitSharedElementCallback(context, srcImageViewScaleType, shareExitView));
                    return backViewType;
                }

                @Override
                public void onScrollPos(int pos) {
                    if (!isAutoScrollScanPosition) {
                        return;
                    }
                    absListView.post(() -> {
                        absListView.smoothScrollToPosition(pos);
                    });
                }

            });
            intent.putExtra(OpenParams.IMAGES, openImageDetails);
            replenishImageUrl(openImageDetails);
            Bundle newOptions = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, shareViewClick, shareNameClick).toBundle();
            open(intent, newOptions);
        } else if (imageViews != null && imageViews.size() > 0) {
            if (imageViews.size() != openImageUrls.size()) {
                throw new IllegalArgumentException("所传ImageView个数需与数据个数一致");
            }
            if (clickDataPosition != clickViewPosition) {
                throw new IllegalArgumentException("clickDataPosition 和 clickViewPosition不能不相等");
            }
            if (clickViewPosition >= imageViews.size()) {
                throw new IllegalArgumentException("clickViewPosition不能 >= ImageView 的个数");
            }
            View shareViewClick = null;
            String shareNameClick = null;
            ArrayList<OpenImageDetail> openImageDetails = new ArrayList<>();

            for (int i = 0; i < openImageUrls.size(); i++) {
                OpenImageUrl imageBean = openImageUrls.get(i);
                OpenImageDetail openImageDetail = new OpenImageDetail();
                openImageDetail.openImageUrl = imageBean;
                openImageDetail.dataPosition = i;
                openImageDetail.viewPosition = i;
                ImageView shareView = imageViews.get(i);
                if (autoSetScaleType && shareView.getScaleType() != srcImageViewScaleType) {
                    shareView.setScaleType(srcImageViewScaleType);
                }
                String shareName = OpenParams.SHARE_VIEW + i;
                int shareViewWidth = shareView.getWidth();
                int shareViewHeight = shareView.getHeight();
                openImageDetail.srcWidth = shareViewWidth;
                openImageDetail.srcHeight = shareViewHeight;
                openImageDetails.add(openImageDetail);
                if (clickViewPosition == i) {
                    shareViewClick = shareView;
                    shareNameClick = shareName;
                }
            }
            ImageLoadUtils.getInstance().setOnBackView(new ExitOnBackView(shareViewClick) {

                @Override
                public BackViewType onBack(int showPosition) {
                    BackViewType backViewType = BackViewType.NO_SHARE;
                    Activity activity = ActivityCompatHelper.getActivity(context);
                    if (activity == null) {
                        return backViewType;
                    }
                    ImageView shareExitView = null;
                    ImageView shareView = imageViews.get(showPosition);
                    if (shareView != null && shareView.isAttachedToWindow()) {
                        shareExitView = shareView;
                        backViewType = BackViewType.SHARE_NORMAL;
                    }

                    if (shareExitView == null && wechatExitFillInEffect){
                        ImageView wechatView = imageViews.get(clickViewPosition);
                        if (wechatView != null && wechatView.isAttachedToWindow()) {
                            shareExitView = wechatView;
                            backViewType = BackViewType.SHARE_WECHAT;
                        }
                    }

                    activity.setExitSharedElementCallback(new ExitSharedElementCallback(context, srcImageViewScaleType, shareExitView));
                    return backViewType;
                }

            });
            replenishImageUrl(openImageDetails);
            intent.putExtra(OpenParams.IMAGES, openImageDetails);
            Bundle newOptions = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, shareViewClick, shareNameClick).toBundle();
            open(intent, newOptions);
        } else {
            throw new IllegalArgumentException("请设置至少一个点击的ImageView");
        }

    }

    private void open(Intent intent, Bundle newOptions) {
        isCanOpen = false;
        OpenImageUrl openImageUrl = openImageUrls.get(clickDataPosition);
        Handler handler = new Handler(Looper.getMainLooper());
        OpenImageConfig.getInstance().getBigImageHelper().loadImage(context, ImageLoadUtils.getInstance().getImageLoadSuccess(openImageUrl.getImageUrl()) ? openImageUrl.getImageUrl() : openImageUrl.getCoverImageUrl(), new OnLoadBigImageListener() {
            @Override
            public void onLoadImageSuccess(Drawable drawable) {
                handler.removeCallbacksAndMessages(null);
                if (!ActivityCompatHelper.assertValidRequest(context))
                    return;
                String key = UUID.randomUUID().toString();
                intent.putExtra(OpenParams.OPEN_COVER_DRAWABLE, key);
                ImageLoadUtils.getInstance().setCoverDrawable(key, drawable);
                startActivity(intent, newOptions, key);
            }

            @Override
            public void onLoadImageFailed() {
                handler.removeCallbacksAndMessages(null);
                if (!ActivityCompatHelper.assertValidRequest(context))
                    return;
                startActivity(intent, newOptions, null);
            }
        });
        handler.postDelayed(() -> startActivity(intent, newOptions, null), 100);
    }

    private void startActivity(Intent intent, Bundle newOptions, String drawableKey) {
        if (!isStartActivity) {
            if (ActivityCompatHelper.assertValidRequest(context)) {
                context.startActivity(intent, newOptions);
                release();
            } else {
                isCanOpen = true;
            }
        } else if (!TextUtils.isEmpty(drawableKey)) {
            ImageLoadUtils.getInstance().clearCoverDrawable(drawableKey);
        }
        isStartActivity = true;
    }

    private void replenishImageUrl(ArrayList<OpenImageDetail> openImageDetails) {
        if (srcWidthCache.size() == 1 || srcHeightCache.size() == 1) {
            int srcWidth = srcWidthCache.iterator().next();
            int srcHeight = srcHeightCache.iterator().next();
            for (OpenImageDetail openImageDetail : openImageDetails) {
                if (openImageDetail.srcWidth == 0 || openImageDetail.srcHeight == 0) {
                    openImageDetail.srcWidth = srcWidth;
                    openImageDetail.srcHeight = srcHeight;
                }
            }
        }
    }

    public void show() {
        if (isCanOpen) {
            Activity activity = ActivityCompatHelper.getActivity(context);
            activity.setExitSharedElementCallback(null);
            show4ParseData();
        }
    }

    private void release() {
        FragmentActivity fragmentActivity = ActivityCompatHelper.getFragmentActivity(context);
        if (fragmentActivity != null) {
            fragmentActivity.getLifecycle().addObserver(new LifecycleEventObserver() {
                @Override
                public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        context = null;
                        recyclerView = null;
                        absListView = null;
                        if (imageViews != null) {
                            imageViews.clear();
                            imageViews = null;
                        }
                        source.getLifecycle().removeObserver(this);
                    }
                }
            });
        }

    }
}
