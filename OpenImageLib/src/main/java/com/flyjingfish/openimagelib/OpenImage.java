package com.flyjingfish.openimagelib;

import android.app.Activity;
import android.app.SharedElementCallback;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.flyjingfish.openimagelib.beans.ContentViewOriginModel;
import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.beans.OpenImageDetail;
import com.flyjingfish.openimagelib.enums.ImageDiskMode;
import com.flyjingfish.openimagelib.enums.MediaType;
import com.flyjingfish.openimagelib.listener.ItemLoadHelper;
import com.flyjingfish.openimagelib.listener.OnLoadBigImageListener;
import com.flyjingfish.openimagelib.listener.OnLoadCoverImageListener;
import com.flyjingfish.openimagelib.listener.OnSelectMediaListener;
import com.flyjingfish.openimagelib.listener.SourceImageViewIdGet;
import com.flyjingfish.openimagelib.utils.ActivityCompatHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class OpenImage {
    private Context context;
    private final List<OpenImageUrl> openImageUrls = new ArrayList<>();
    private List<ImageView> imageViews;
    private RecyclerView recyclerView;
    private AbsListView absListView;
    private long openPageAnimTimeMs;
    private int clickPosition;
    private int errorResId;
    private int openImageStyle;
    private ImageView.ScaleType srcImageViewScaleType;
    private boolean autoSetScaleType;
    private ImageDiskMode imageDiskMode = ImageDiskMode.CONTAIN_ORIGINAL;
    private ItemLoadHelper itemLoadHelper;
    private final HashSet<Integer> srcWidthCache = new HashSet<>();
    private final HashSet<Integer> srcHeightCache = new HashSet<>();
    private boolean isStartActivity;
    private boolean isAutoScrollScanPosition = true;
    private OnSelectMediaListener onSelectMediaListener;
    private SourceImageViewIdGet<OpenImageUrl> sourceImageViewIdGet;

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
     * @param clickPosition 点击的图片所在数据的位置
     * @return
     */
    public OpenImage setClickPosition(int clickPosition) {
        this.clickPosition = clickPosition;
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
     *
     * @param onSelectMediaListener 回调查看图片所在数据的位置
     * @return
     */
    public OpenImage setOnSelectMediaListener(OnSelectMediaListener onSelectMediaListener) {
        this.onSelectMediaListener = onSelectMediaListener;
        return this;
    }

    /**
     *
     * @param autoScrollScanPosition 自动滑向最后看的图片的位置
     * @return
     */
    public OpenImage setAutoScrollScanPosition(boolean autoScrollScanPosition) {
        isAutoScrollScanPosition = autoScrollScanPosition;
        return this;
    }

    private View backView;

    private void initSrcViews(Rect rvRect,List<OpenImageDetail> openImageDetails,List<ContentViewOriginModel> contentViewOriginModels) {
        if (context == null){
            return;
        }
        ViewGroup rootView = (ViewGroup) getWindow(context).getDecorView();
        if (backView != null){
            rootView.removeView(backView);
        }
        FrameLayout flBelowView = new FrameLayout(context);
        backView = flBelowView;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        if (rvRect != null){
            layoutParams.topMargin = rvRect.top;
            layoutParams.leftMargin = rvRect.left;
            layoutParams.width = rvRect.width();
            layoutParams.height = rvRect.height();
        }
        rootView.addView(flBelowView,layoutParams);
        for (OpenImageDetail oDetail : openImageDetails) {
            oDetail.isAdded = false;
            oDetail.tagViewLoadSuc = false;
        }
        for (ContentViewOriginModel contentViewOriginModel : contentViewOriginModels) {

            if (contentViewOriginModel.dataPosition == clickPosition) {
                for (OpenImageDetail openImageBean : openImageDetails) {
                    if (openImageBean.dataPosition == clickPosition) {
                        ImageView imageView = new ImageView(context);
                        imageView.setScaleType(srcImageViewScaleType);
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(contentViewOriginModel.width, contentViewOriginModel.height);
                        params.leftMargin = contentViewOriginModel.left;
                        params.topMargin = contentViewOriginModel.top;
                        flBelowView.addView(imageView, params);
                        loadSrcImage(openImageBean, imageView,contentViewOriginModel.width, contentViewOriginModel.height);
                        openImageBean.isAdded = true;
                    }
                }
            }
            OpenImageDetail openImageDetail = openImageDetails.get(ViewPagerActivity.showPosition);
            if (openImageDetail.dataPosition == contentViewOriginModel.dataPosition && !openImageDetail.isAdded){
                ImageView imageView = new ImageView(context);
                imageView.setScaleType(srcImageViewScaleType);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(contentViewOriginModel.width, contentViewOriginModel.height);
                params.leftMargin = contentViewOriginModel.left;
                params.topMargin = contentViewOriginModel.top;
                flBelowView.addView(imageView, params);
                loadSrcImage(openImageDetail, imageView,contentViewOriginModel.width, contentViewOriginModel.height);
                openImageDetail.isAdded = true;
            }

            if (contentViewOriginModel.transitioned){
                for (OpenImageDetail openImageBean : openImageDetails) {
                    if (openImageBean.dataPosition == contentViewOriginModel.dataPosition&& !openImageBean.isAdded) {
                        ImageView imageView = new ImageView(context);
                        imageView.setScaleType(srcImageViewScaleType);
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(contentViewOriginModel.width, contentViewOriginModel.height);
                        params.leftMargin = contentViewOriginModel.left;
                        params.topMargin = contentViewOriginModel.top;
                        flBelowView.addView(imageView, params);
                        loadSrcImage(openImageBean, imageView,contentViewOriginModel.width, contentViewOriginModel.height);
                        openImageBean.isAdded = true;
                    }
                }
            }
        }
    }

    private void removeBackView(){
        if (backView != null){
            ViewGroup rootView = (ViewGroup) getWindow(context).getDecorView();
            rootView.removeView(backView);
        }
    }

    private void loadSrcImage(OpenImageDetail openImageBean, ImageView srcImageView,int width,int height) {
        if (srcImageView != null && !openImageBean.tagViewLoadSuc) {
            itemLoadHelper.loadImage(context, openImageBean.openImageUrl, openImageBean.getCoverImageUrl(), srcImageView, width, height, new OnLoadCoverImageListener() {
                @Override
                public void onLoadImageSuccess() {
                    openImageBean.tagViewLoadSuc = true;
                }

                @Override
                public void onLoadImageFailed() {

                }
            });
        }
    }

    private void scrollRecyclerView(int pos){
        if (!isAutoScrollScanPosition){
            return;
        }
        final RecyclerView.LayoutManager layoutManager =
                recyclerView.getLayoutManager();
        View viewAtPosition =
                layoutManager.findViewByPosition(pos);
        if (viewAtPosition == null
                || layoutManager.isViewPartiallyVisible(viewAtPosition, false, true)){
            recyclerView.post(()-> layoutManager.scrollToPosition(pos));
        }
    }

    private void show4ParseData() {
        if (openImageUrls.size() == 0) {
            throw new IllegalArgumentException("请设置数据");
        }
        if (itemLoadHelper == null){
            throw new IllegalArgumentException("请设置ItemLoadHelper");
        }
        Intent intent = new Intent(context, ViewPagerActivity.class);
        if (openImageUrls.size() == 1) {
            clickPosition = 0;
        }

        intent.putExtra(OpenParams.CLICK_POSITION, clickPosition);
        if (onSelectMediaListener!= null){
            String selectKey = UUID.randomUUID().toString();
            ImageLoadUtils.getInstance().setOnSelectMediaListener(selectKey,onSelectMediaListener);
            intent.putExtra(OpenParams.ON_SELECT_KEY, selectKey);
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

        if (recyclerView != null) {
            if (sourceImageViewIdGet == null) {
                throw new IllegalArgumentException("sourceImageViewIdGet 不能为null");
            }

            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (!(layoutManager instanceof LinearLayoutManager || layoutManager instanceof StaggeredGridLayoutManager)){
                throw new IllegalArgumentException("只支持使用继承自LinearLayoutManager和StaggeredGridLayoutManager的RecyclerView");
            }

            int rvLocation[] = new int[2];
            recyclerView.getLocationInWindow(rvLocation);

            int rvWidth = recyclerView.getWidth();
            int rvHeight = recyclerView.getHeight();
            Rect rect = new Rect();
            rect.left = rvLocation[0];
            rect.top = rvLocation[1];
            rect.right = rect.left+rvWidth;
            rect.bottom = rect.top+rvHeight;
            intent.putExtra(OpenParams.SRC_PARENT_RECT, rect);

            View shareViewClick = null;
            String shareNameClick = null;
            int firstPos = 0;
            int lastPos = 0;
            List<Pair<View, String>> sharedElements = new ArrayList<>();
            ArrayList<OpenImageDetail> openImageDetails = new ArrayList<>();
            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                firstPos = linearLayoutManager.findFirstVisibleItemPosition();
                lastPos = linearLayoutManager.findLastVisibleItemPosition();
            }else if (layoutManager instanceof StaggeredGridLayoutManager){
                StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                int[] firstVisibleItems = null;
                firstVisibleItems = staggeredGridLayoutManager.findFirstVisibleItemPositions(firstVisibleItems);
                int[] lastVisibleItems = null;
                lastVisibleItems = staggeredGridLayoutManager.findLastVisibleItemPositions(lastVisibleItems);
                if(firstVisibleItems == null || lastVisibleItems == null || firstVisibleItems.length == 0|| lastVisibleItems.length == 0){
                    return;
                }
                firstPos = Integer.MAX_VALUE;
                lastPos = 0;
                for (int firstVisibleItem : firstVisibleItems) {
                    if (firstVisibleItem<firstPos && firstVisibleItem>=0){
                        firstPos = firstVisibleItem;
                    }
                }
                for (int lastVisibleItem : lastVisibleItems) {
                    if (lastVisibleItem>lastPos){
                        lastPos = lastVisibleItem;
                    }
                }
                if (lastPos < firstPos){
                    return;
                }

            }

            for (int i = 0; i < openImageUrls.size(); i++) {
                OpenImageUrl imageBean = openImageUrls.get(i);
                if (imageBean.getType() == MediaType.IMAGE || imageBean.getType() == MediaType.VIDEO) {
                    OpenImageDetail openImageDetail = new OpenImageDetail();
                    openImageDetail.openImageUrl = imageBean;
                    openImageDetail.dataPosition = i;
                    if (i >= firstPos && i <= lastPos) {
                        View view = layoutManager.findViewByPosition(i);
                        if (view == null){
                            continue;
                        }
                        ImageView shareView = view.findViewById(sourceImageViewIdGet.getImageViewId(imageBean, i));
                        if (autoSetScaleType && shareView.getScaleType() != srcImageViewScaleType) {
                            shareView.setScaleType(srcImageViewScaleType);
                        }
                        String shareName = OpenParams.SHARE_VIEW + openImageDetails.size();
                        if (clickPosition == i){
                            shareViewClick = shareView;
                            shareNameClick = shareName;
                        }
                        sharedElements.add(Pair.create(shareView, shareName));
                        int shareViewWidth = shareView.getWidth();
                        int shareViewHeight = shareView.getHeight();
                        openImageDetail.srcWidth = shareViewWidth;
                        openImageDetail.srcHeight = shareViewHeight;
                        srcWidthCache.add(shareViewWidth);
                        srcHeightCache.add(shareViewHeight);
                    }
                    openImageDetails.add(openImageDetail);
                }
            }
            final View transitionView = shareViewClick;
            ImageLoadUtils.getInstance().setOnBackView(new ImageLoadUtils.OnBackView() {
                @Override
                public void onBack() {

                    Activity activity = getActivity(context);
                    if (activity == null){
                        return;
                    }
                    activity.setExitSharedElementCallback(new SharedElementCallback() {
                        @Override
                        public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                            super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots);
                            removeBackView();
                        }
                        @Override
                        public void onMapSharedElements(List<String> names, Map<String, View> sharedEls) {
                            super.onMapSharedElements(names, sharedEls);
                            if (names.size() == 0){
                                return;
                            }
                            String name = names.get(0);
                            boolean isMapEls = false;
                            for (Pair<View, String> element : sharedElements) {
                                if (TextUtils.equals(element.second,name)){
                                    sharedEls.put(name,element.first);
                                    isMapEls = true;
                                    break;
                                }
                            }
                            int index = Integer.parseInt(name.replace(OpenParams.SHARE_VIEW,""));
                            if (!isMapEls){
                                if (!isAutoScrollScanPosition){
                                    sharedEls.clear();
                                    names.clear();
                                    return;
                                }
                                int firstPos = 0;
                                int lastPos = 0;
                                if (layoutManager instanceof LinearLayoutManager) {
                                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                                    firstPos = linearLayoutManager.findFirstVisibleItemPosition();
                                    lastPos = linearLayoutManager.findLastVisibleItemPosition();
                                }else if (layoutManager instanceof StaggeredGridLayoutManager){
                                    StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                                    int[] firstVisibleItems = null;
                                    firstVisibleItems = staggeredGridLayoutManager.findFirstVisibleItemPositions(firstVisibleItems);
                                    int[] lastVisibleItems = null;
                                    lastVisibleItems = staggeredGridLayoutManager.findLastVisibleItemPositions(lastVisibleItems);
                                    if(firstVisibleItems == null || lastVisibleItems == null || firstVisibleItems.length == 0|| lastVisibleItems.length == 0){
                                        return;
                                    }
                                    firstPos = Integer.MAX_VALUE;
                                    lastPos = 0;
                                    for (int firstVisibleItem : firstVisibleItems) {
                                        if (firstVisibleItem<firstPos && firstVisibleItem>=0){
                                            firstPos = firstVisibleItem;
                                        }
                                    }
                                    for (int lastVisibleItem : lastVisibleItems) {
                                        if (lastVisibleItem>lastPos){
                                            lastPos = lastVisibleItem;
                                        }
                                    }
                                    if (lastPos < firstPos){
                                        return;
                                    }

                                }


                                OpenImageDetail openImageDetail = openImageDetails.get(index);
                                for (int i = firstPos; i < lastPos + 1; i++) {
                                    OpenImageUrl openImageUrl = openImageUrls.get(i);
                                    if (openImageDetail.dataPosition == i && (openImageUrl.getType() == MediaType.IMAGE || openImageUrl.getType() == MediaType.VIDEO)){
                                        View view = layoutManager.findViewByPosition(i);
                                        if (view != null){
                                            sharedEls
                                                    .put(name,view.findViewById(sourceImageViewIdGet.getImageViewId(openImageUrl, openImageDetail.dataPosition))
                                                    );
                                        }
                                    }
                                }

                            }
                        }
                    });
                }

                @Override
                public void onScrollPos(int pos) {
                    scrollRecyclerView(pos);
                }

                @Override
                public List<ContentViewOriginModel> onGetContentViewOriginModel() {
                    List<ContentViewOriginModel> list = new ArrayList<>();
                    int firstPos = 0;
                    int lastPos = 0;
                    if (layoutManager instanceof LinearLayoutManager) {
                        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                        firstPos = linearLayoutManager.findFirstVisibleItemPosition();
                        lastPos = linearLayoutManager.findLastVisibleItemPosition();
                    }else if (layoutManager instanceof StaggeredGridLayoutManager){
                        StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                        int[] firstVisibleItems = null;
                        firstVisibleItems = staggeredGridLayoutManager.findFirstVisibleItemPositions(firstVisibleItems);
                        int[] lastVisibleItems = null;
                        lastVisibleItems = staggeredGridLayoutManager.findLastVisibleItemPositions(lastVisibleItems);
                        if(firstVisibleItems == null || lastVisibleItems == null || firstVisibleItems.length == 0|| lastVisibleItems.length == 0){
                            return list;
                        }
                        firstPos = Integer.MAX_VALUE;
                        lastPos = 0;
                        for (int firstVisibleItem : firstVisibleItems) {
                            if (firstVisibleItem<firstPos && firstVisibleItem>=0){
                                firstPos = firstVisibleItem;
                            }
                        }
                        for (int lastVisibleItem : lastVisibleItems) {
                            if (lastVisibleItem>lastPos){
                                lastPos = lastVisibleItem;
                            }
                        }
                        if (lastPos < firstPos){
                            return list;
                        }

                    }
                    if (lastPos < 0||firstPos<0){
                        return list;
                    }

                    for (int i = firstPos; i < lastPos + 1 && i<openImageUrls.size(); i++) {
                        OpenImageUrl openImageUrl = openImageUrls.get(i);
                        if (openImageUrl.getType() == MediaType.IMAGE || openImageUrl.getType() == MediaType.VIDEO){

                            View view = layoutManager.findViewByPosition(i);
                            if (view == null){
                                continue;
                            }
                            ImageView shareView = view.findViewById(sourceImageViewIdGet.getImageViewId(openImageUrl, i));
                            int shareViewWidth = shareView.getWidth();
                            int shareViewHeight = shareView.getHeight();

                            ContentViewOriginModel contentViewOriginModel = new ContentViewOriginModel();
                            int location[] = new int[2];
                            shareView.getLocationInWindow(location);
                            contentViewOriginModel.left = location[0]-rvLocation[0];
                            contentViewOriginModel.top = location[1]-rvLocation[1];
                            contentViewOriginModel.width = shareViewWidth;
                            contentViewOriginModel.height = shareViewHeight;
                            contentViewOriginModel.dataPosition = i;
                            list.add(contentViewOriginModel);
                            if (transitionView == shareView){
                                contentViewOriginModel.transitioned = true;
                            }
                        }
                    }

                    initSrcViews(rect,openImageDetails,list);
                    return list;
                }

            });

            intent.putExtra(OpenParams.IMAGES, openImageDetails);
            replenishImageUrl(openImageDetails);
            Bundle newOptions = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, shareViewClick,shareNameClick).toBundle();
            open(intent, newOptions);
        } else if (absListView != null) {
            if (sourceImageViewIdGet == null) {
                throw new IllegalArgumentException("sourceImageViewIdGet 不能为null");
            }
            View shareViewClick = null;
            String shareNameClick = null;
            int rvLocation[] = new int[2];
            absListView.getLocationInWindow(rvLocation);

            int rvWidth = absListView.getWidth();
            int rvHeight = absListView.getHeight();
            Rect rect = new Rect();
            rect.left = rvLocation[0];
            rect.top = rvLocation[1];
            rect.right = rect.left+rvWidth;
            rect.bottom = rect.top+rvHeight;
            intent.putExtra(OpenParams.SRC_PARENT_RECT, rect);
            int firstListItemPosition = absListView.getFirstVisiblePosition();
            int lastListItemPosition = absListView.getLastVisiblePosition();
            List<Pair<View, String>> sharedElements = new ArrayList<>();
            ArrayList<OpenImageDetail> openImageDetails = new ArrayList<>();
            for (int i = 0; i < openImageUrls.size(); i++) {
                OpenImageUrl imageBean = openImageUrls.get(i);
                if (imageBean.getType() == MediaType.IMAGE || imageBean.getType() == MediaType.VIDEO) {
                    OpenImageDetail openImageDetail = new OpenImageDetail();
                    openImageDetail.openImageUrl = imageBean;
                    openImageDetail.dataPosition = i;
                    if (i >= firstListItemPosition && i <= lastListItemPosition) {
                        View view = absListView.getChildAt(i - firstListItemPosition);
                        if (view == null){
                            continue;
                        }
                        ImageView shareView = view.findViewById(sourceImageViewIdGet.getImageViewId(imageBean, i));
                        if (autoSetScaleType && shareView.getScaleType() != srcImageViewScaleType) {
                            shareView.setScaleType(srcImageViewScaleType);
                        }
                        String shareName = OpenParams.SHARE_VIEW + openImageDetails.size();
                        sharedElements.add(Pair.create(shareView, shareName));
//                        shareView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                        int shareViewWidth = shareView.getWidth();
                        int shareViewHeight = shareView.getHeight();
                        openImageDetail.srcWidth = shareViewWidth;
                        openImageDetail.srcHeight = shareViewHeight;
                        srcWidthCache.add(shareViewWidth);
                        srcHeightCache.add(shareViewHeight);

                        if (clickPosition == i){
                            shareViewClick = shareView;
                            shareNameClick = shareName;
                        }

                    }
                    openImageDetails.add(openImageDetail);
                }
            }
            final View transitionView = shareViewClick;
            ImageLoadUtils.getInstance().setOnBackView(new ImageLoadUtils.OnBackView() {
                @Override
                public void onBack() {

                    Activity activity = getActivity(context);
                    if (activity == null){
                        return;
                    }
                    activity.setExitSharedElementCallback(new SharedElementCallback() {
                        @Override
                        public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                            super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots);
                            removeBackView();
                        }
                        @Override
                        public void onMapSharedElements(List<String> names, Map<String, View> sharedEls) {
                            super.onMapSharedElements(names, sharedEls);
                            if (names.size() == 0){
                                return;
                            }
                            String name = names.get(0);
                            boolean isMapEls = false;
                            for (Pair<View, String> element : sharedElements) {
                                if (TextUtils.equals(element.second,name)){
                                    sharedEls.put(name,element.first);
                                    isMapEls = true;
                                    break;
                                }
                            }
                            int index = Integer.parseInt(name.replace(OpenParams.SHARE_VIEW,""));
                            if (!isMapEls){
                                if (!isAutoScrollScanPosition){
                                    sharedEls.clear();
                                    names.clear();
                                    return;
                                }
//                                absListView.smoothScrollToPosition(index);
                                int firstPos = absListView.getFirstVisiblePosition();
                                int lastPos = absListView.getLastVisiblePosition();
                                OpenImageDetail openImageDetail = openImageDetails.get(index);
                                for (int i = firstPos; i < lastPos + 1; i++) {
                                    OpenImageUrl openImageUrl = openImageUrls.get(i);
                                    if (openImageDetail.dataPosition == i && (openImageUrl.getType() == MediaType.IMAGE || openImageUrl.getType() == MediaType.VIDEO)){
                                        View view = absListView.getChildAt(i - firstPos);
                                        if (view == null){
                                            return;
                                        }
                                        sharedEls
                                                .put(name,view.findViewById(sourceImageViewIdGet.getImageViewId(openImageUrl, openImageDetail.dataPosition))
                                                );
                                    }
                                }



                            }

                        }
                    });

                }

                @Override
                public void onScrollPos(int pos) {
                    if (!isAutoScrollScanPosition){
                        return;
                    }
                    absListView.post(() -> {
                        absListView.smoothScrollToPosition(pos);;
                    });
                }

                @Override
                public List<ContentViewOriginModel> onGetContentViewOriginModel() {
                    List<ContentViewOriginModel> list = new ArrayList<>();
                    int firstPos = absListView.getFirstVisiblePosition();
                    int lastPos = absListView.getLastVisiblePosition();
                    if (lastPos < 0||firstPos<0){
                        return list;
                    }
                    for (int i = firstPos; i < lastPos + 1 && i<openImageUrls.size(); i++) {
                        OpenImageUrl openImageUrl = openImageUrls.get(i);
                        if (openImageUrl.getType() == MediaType.IMAGE || openImageUrl.getType() == MediaType.VIDEO){
                            View view = absListView.getChildAt(i - firstPos);
                            if (view == null){
                                return list;
                            }
                            ImageView shareView = view.findViewById(sourceImageViewIdGet.getImageViewId(openImageUrl, i));
                            int shareViewWidth = shareView.getWidth();
                            int shareViewHeight = shareView.getHeight();


                            ContentViewOriginModel contentViewOriginModel = new ContentViewOriginModel();
                            int location[] = new int[2];
                            shareView.getLocationInWindow(location);
                            contentViewOriginModel.left = location[0]-rvLocation[0];
                            contentViewOriginModel.top = location[1]-rvLocation[1];
                            contentViewOriginModel.width = shareViewWidth;
                            contentViewOriginModel.height = shareViewHeight;
                            contentViewOriginModel.dataPosition = i;
                            list.add(contentViewOriginModel);

                            if (transitionView == shareView){
                                contentViewOriginModel.transitioned = true;
                            }
                        }
                    }


                    initSrcViews(rect,openImageDetails,list);
                    return list;
                }

            });
            intent.putExtra(OpenParams.IMAGES, openImageDetails);
            replenishImageUrl(openImageDetails);
//            Bundle newOptions = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, sharedEls).toBundle();
            Bundle newOptions = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, shareViewClick,shareNameClick).toBundle();
            open(intent, newOptions);
        } else if (imageViews != null && imageViews.size() > 0) {
            if (imageViews.size() != openImageUrls.size()) {
                throw new IllegalArgumentException("所传ImageView个数需与数据个数一致");
            }
            View shareViewClick = null;
            String shareNameClick = null;
            Pair<View, String>[] sharedElements = new Pair[imageViews.size()];
            ArrayList<OpenImageDetail> openImageDetails = new ArrayList<>();
            ArrayList<ContentViewOriginModel> contentViewOriginModels = new ArrayList<>();
            for (int i = 0; i < openImageUrls.size(); i++) {
                OpenImageUrl imageBean = openImageUrls.get(i);
                OpenImageDetail openImageDetail = new OpenImageDetail();
                openImageDetail.openImageUrl = imageBean;
                openImageDetail.dataPosition = i;
                ImageView shareView = imageViews.get(i);
                if (autoSetScaleType && shareView.getScaleType() != srcImageViewScaleType) {
                    shareView.setScaleType(srcImageViewScaleType);
                }
                String shareName = OpenParams.SHARE_VIEW + i;
                sharedElements[i] = Pair.create(shareView, OpenParams.SHARE_VIEW + i);
                int shareViewWidth = shareView.getWidth();
                int shareViewHeight = shareView.getHeight();
                openImageDetail.srcWidth = shareViewWidth;
                openImageDetail.srcHeight = shareViewHeight;
                openImageDetails.add(openImageDetail);
                if (clickPosition == i){
                    shareViewClick = shareView;
                    shareNameClick = shareName;
                }
            }
            ImageLoadUtils.getInstance().setOnBackView(new ImageLoadUtils.OnBackView() {
                @Override
                public void onBack() {

                    Activity activity = getActivity(context);
                    if (activity == null){
                        return;
                    }
                    activity.setExitSharedElementCallback(new SharedElementCallback() {
                        @Override
                        public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                            super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots);
                            removeBackView();
                        }
                        @Override
                        public void onMapSharedElements(List<String> names, Map<String, View> sharedEls) {
                            super.onMapSharedElements(names, sharedEls);
                            if (names.size() == 0){
                                return;
                            }
                            String name = names.get(0);
                            boolean isMapEls = false;
                            for (Pair<View, String> element : sharedElements) {
                                if (TextUtils.equals(element.second,name)){
                                    sharedEls.put(name,element.first);
                                    isMapEls = true;
                                    break;
                                }
                            }
                            int index = Integer.parseInt(name.replace(OpenParams.SHARE_VIEW,""));
                            if (!isMapEls){
                                if (!isAutoScrollScanPosition){
                                    sharedEls.clear();
                                    names.clear();
                                    return;
                                }
                                int firstListItemPosition = absListView.getFirstVisiblePosition();
                                View view = absListView.getChildAt(index - firstListItemPosition);
                                if (view == null){
                                    return;
                                }
                                for (int i = 0; i < openImageDetails.size(); i++) {
                                    OpenImageDetail openImageDetail = openImageDetails.get(i);
                                    if (i == index){
                                        sharedEls
                                                .put(name,view.findViewById(sourceImageViewIdGet.getImageViewId(openImageDetail, openImageDetail.dataPosition))
                                                );
                                    }
                                }
                                sharedEls.put(name,imageViews.get(index));

                            }
                        }
                    });

                }

                @Override
                public void onScrollPos(int pos) {
                    if (!isAutoScrollScanPosition){
                        return;
                    }
                }

                @Override
                public List<ContentViewOriginModel> onGetContentViewOriginModel() {
                    ArrayList<ContentViewOriginModel> contentViewOriginModels = new ArrayList<>();
                    for (int i = 0; i < openImageUrls.size(); i++) {
                        ImageView shareView = imageViews.get(i);
                        int shareViewWidth = shareView.getWidth();
                        int shareViewHeight = shareView.getHeight();
                        int location[] = new int[2];
                        shareView.getLocationInWindow(location);
                        ContentViewOriginModel contentViewOriginModel = new ContentViewOriginModel();
                        contentViewOriginModel.left = location[0];
                        contentViewOriginModel.top = location[1];
                        contentViewOriginModel.width = shareViewWidth;
                        contentViewOriginModel.height = shareViewHeight;
                        contentViewOriginModel.dataPosition = i;
                        contentViewOriginModels.add(contentViewOriginModel);

                    }
                    initSrcViews(null,openImageDetails,contentViewOriginModels);
                    return contentViewOriginModels;
                }

            });
            replenishImageUrl(openImageDetails);
            intent.putExtra(OpenParams.IMAGES, openImageDetails);
//            Bundle newOptions = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, sharedElements).toBundle();
            Bundle newOptions = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, shareViewClick,shareNameClick).toBundle();
            open(intent, newOptions);
        } else {
            throw new IllegalArgumentException("请设置至少一个点击的ImageView");
        }

    }

    Window getWindow(Context context) {
        return getActivity(context).getWindow();
    }

    Activity getActivity(Context context) {
        return (Activity) context;
    }

    FragmentActivity getFragmentActivity(Context context) {
        if (context instanceof FragmentActivity){
            return (FragmentActivity) context;
        }
        return null;
    }

    private void open(Intent intent, Bundle newOptions) {
        isCanOpen = false;
        OpenImageUrl openImageUrl = openImageUrls.get(clickPosition);
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
                startActivity(intent, newOptions,key);
            }

            @Override
            public void onLoadImageFailed() {
                handler.removeCallbacksAndMessages(null);
                if (!ActivityCompatHelper.assertValidRequest(context))
                    return;
                startActivity(intent, newOptions,null);
            }
        });
        handler.postDelayed(() -> startActivity(intent, newOptions,null), 100);
    }

    private void startActivity(Intent intent, Bundle newOptions,String drawableKey) {
        if (!isStartActivity) {
            if (ActivityCompatHelper.assertValidRequest(context)){
                context.startActivity(intent, newOptions);
                release();
            }else {
                isCanOpen = true;
            }
        }else if (!TextUtils.isEmpty(drawableKey)){
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
        if (isCanOpen){
            Activity activity = getActivity(context);
            activity.setExitSharedElementCallback(null);
            show4ParseData();
        }
    }


    private void release() {
        FragmentActivity fragmentActivity = getFragmentActivity(context);
        if (fragmentActivity != null){
            fragmentActivity.getLifecycle().addObserver(new LifecycleEventObserver() {
                @Override
                public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                    if (event == Lifecycle.Event.ON_DESTROY){
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
