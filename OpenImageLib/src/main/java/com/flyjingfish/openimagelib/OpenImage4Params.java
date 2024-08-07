package com.flyjingfish.openimagelib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;

import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.flyjingfish.openimagelib.beans.ClickViewParam;
import com.flyjingfish.openimagelib.beans.CloseParams;
import com.flyjingfish.openimagelib.beans.DownloadParams;
import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.listener.OnExitListener;
import com.flyjingfish.openimagelib.listener.OnPermissionsInterceptListener;
import com.flyjingfish.openimagelib.listener.OnUpdateViewListener;
import com.flyjingfish.openimagelib.listener.LayoutManagerFindVisiblePosition;
import com.flyjingfish.openimagelib.listener.SourceImageViewGet;
import com.flyjingfish.openimagelib.listener.SourceImageViewIdGet;
import com.flyjingfish.openimagelib.utils.ActivityCompatHelper;
import com.flyjingfish.shapeimageviewlib.ShapeImageView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

class OpenImage4Params {
    protected Context context;
    protected LifecycleOwner lifecycleOwner;
    protected String contextKey;
    protected final List<OpenImageUrl> openImageUrls = new ArrayList<>();
    protected List<ImageView> imageViews;
    protected RecyclerView recyclerView;
    protected LayoutManagerFindVisiblePosition layoutManagerFindVisiblePosition;
    protected AbsListView absListView;
    protected ViewPager2 viewPager2;
    protected ViewPager viewPager;
    protected View parentParamsView;
    protected List<ClickViewParam> clickViewParams;
    protected long openPageAnimTimeMs;
    protected int clickViewPosition;
    protected int clickDataPosition;
    protected int errorResId;
    protected int openImageStyle;
    protected ImageView.ScaleType srcImageViewScaleType;
    protected ShapeImageView.ShapeScaleType srcImageViewShapeScaleType;
    protected boolean autoSetScaleType;
    protected final HashSet<Integer> srcImageWidthCache = new HashSet<>();
    protected final HashSet<Integer> srcImageHeightCache = new HashSet<>();
    protected final HashSet<Integer> srcVideoWidthCache = new HashSet<>();
    protected final HashSet<Integer> srcVideoHeightCache = new HashSet<>();
    protected boolean isStartActivity;
    protected boolean isAutoScrollScanPosition = false;
    protected boolean wechatExitFillInEffect = false;
    protected SourceImageViewIdGet<OpenImageUrl> sourceImageViewIdGet;
    protected SourceImageViewGet<OpenImageUrl> sourceImageViewGet;
    protected int leftRightShowWidthDp;
    protected String onSelectKey;
    protected String pageTransformersKey;
    protected String onItemClickListenerKey;
    protected String onItemLongClickListenerKey;
    protected Boolean disableClickClose;
    protected boolean showSrcImageView = true;
    protected final List<MoreViewOption> moreViewOptions = new ArrayList<>();
    protected SrcViewType srcViewType;
    protected String backViewKey;
    protected String moreViewOptionKey;
    protected String videoFragmentCreateKey;
    protected String imageFragmentCreateKey;
    protected String upperLayerFragmentCreateKey;
    protected Bundle upperLayerBundle;
    protected volatile boolean isMapShareView = true;
    protected String drawableKey;
    protected Class<?> openImageActivityCls = StandardOpenImageActivity.class;
    protected Bundle openImageActivityClsBundle;
    protected String openImageActivityClsBundleKey;
    protected boolean isNoneClickView = false;
    protected boolean isCallShow = false;
    protected ImageShapeParams imageShapeParams;
    protected OnUpdateViewListener onUpdateViewListener;
    protected OnExitListener onExitListener;
    protected boolean showDownload;
    protected boolean showClose;
    protected Boolean disableTouchClose;
    protected DownloadParams downloadParams;
    protected CloseParams closeParams;
    protected OnPermissionsInterceptListener onPermissionsInterceptListener;

    protected int preloadCount = OpenImageConfig.getInstance().getPreloadCount();
    protected boolean lazyPreload = OpenImageConfig.getInstance().isLazyPreload();
    boolean isReleaseAllData = false;
    protected boolean bothLoadCover = OpenImageConfig.getInstance().isBothLoadCover();

    protected enum SrcViewType {
        RV, AB_LIST, VP, VP2, IV, WEB_VIEW
    }

    protected Intent inputIntentData() {
        if (openImageUrls.size() == 0) {
            throw new IllegalArgumentException("请设置数据");
        }
        if (clickDataPosition >= openImageUrls.size() && ActivityCompatHelper.isApkInDebug(context)) {
            throw new IllegalArgumentException("clickDataPosition不能 >= OpenImageUrl 的个数");
        }
        if (wechatExitFillInEffect) {
            isAutoScrollScanPosition = false;
        }
        Intent intent = new Intent(context, openImageActivityCls);

        intent.putExtra(OpenParams.CLICK_POSITION, clickDataPosition);
        intent.putExtra(OpenParams.WECHAT_EXIT_FILL_IN_EFFECT, wechatExitFillInEffect);
        if (onSelectKey != null) {
            intent.putExtra(OpenParams.ON_SELECT_KEY, onSelectKey);
        }
        if (pageTransformersKey != null) {
            intent.putExtra(OpenParams.PAGE_TRANSFORMERS, pageTransformersKey);
        }
        if (onItemClickListenerKey != null) {
            intent.putExtra(OpenParams.ON_ITEM_CLICK_KEY, onItemClickListenerKey);
        }
        if (onItemLongClickListenerKey != null) {
            intent.putExtra(OpenParams.ON_ITEM_LONG_CLICK_KEY, onItemLongClickListenerKey);
        }
        if (imageFragmentCreateKey != null) {
            intent.putExtra(OpenParams.IMAGE_FRAGMENT_KEY, imageFragmentCreateKey);
        }
        if (videoFragmentCreateKey != null) {
            intent.putExtra(OpenParams.VIDEO_FRAGMENT_KEY, videoFragmentCreateKey);
        }
        if (upperLayerFragmentCreateKey != null) {
            intent.putExtra(OpenParams.UPPER_LAYER_FRAGMENT_KEY, upperLayerFragmentCreateKey);
        }
        if (upperLayerBundle != null) {
            intent.putExtra(OpenParams.UPPER_LAYER_BUNDLE, upperLayerBundle);
        }
        if (moreViewOptions.size() > 0) {
            moreViewOptionKey = UUID.randomUUID().toString();
            intent.putExtra(OpenParams.MORE_VIEW_KEY, moreViewOptionKey);
            ImageLoadUtils.getInstance().setMoreViewOption(moreViewOptionKey, moreViewOptions);
        }
        if (openImageActivityClsBundle != null && !TextUtils.isEmpty(openImageActivityClsBundleKey)) {
            intent.putExtra(openImageActivityClsBundleKey, openImageActivityClsBundle);
        }
        if (imageShapeParams != null){
            intent.putExtra(OpenParams.IMAGE_SHAPE_PARAMS, imageShapeParams);
        }
        if (disableClickClose == null){
            intent.putExtra(OpenParams.DISABLE_CLICK_CLOSE, OpenImageConfig.getInstance().isDisEnableClickClose());
        }else {
            intent.putExtra(OpenParams.DISABLE_CLICK_CLOSE, disableClickClose);
        }
        intent.putExtra(OpenParams.AUTO_SCROLL_SELECT, isAutoScrollScanPosition);
        ShapeImageView.ShapeScaleType shapeScaleType = getShapeScaleType();
        if (shapeScaleType != null){
            intent.putExtra(OpenParams.SRC_SCALE_TYPE, shapeScaleType.ordinal());
        }
        intent.putExtra(OpenParams.ERROR_RES_ID, errorResId);
        intent.putExtra(OpenParams.TOUCH_CLOSE_SCALE, OpenImageConfig.getInstance().getTouchCloseScale());
        intent.putExtra(OpenParams.OPEN_IMAGE_STYLE, openImageStyle);
        intent.putExtra(OpenParams.OPEN_ANIM_TIME_MS, openPageAnimTimeMs);
        intent.putExtra(OpenParams.GALLERY_EFFECT_WIDTH, leftRightShowWidthDp);
        intent.putExtra(OpenParams.CONTEXT_KEY, contextKey);
        intent.putExtra(OpenParams.NONE_CLICK_VIEW, isNoneClickView);
        if (onUpdateViewListener != null) {
            String key = this.toString();
            intent.putExtra(OpenParams.ON_UPDATE_VIEW, key);
            ImageLoadUtils.getInstance().setOnUpdateViewListener(key, onUpdateViewListener);
        }
        intent.putExtra(OpenParams.DOWNLOAD_SHOW, showDownload);
        intent.putExtra(OpenParams.CLOSE_SHOW, showClose);
        if (showDownload && downloadParams != null){
            String key = this.toString();
            intent.putExtra(OpenParams.DOWNLOAD_PARAMS, key);
            ImageLoadUtils.getInstance().setDownloadParams(key, downloadParams);
        }
        if (showClose && closeParams != null){
            String key = this.toString();
            intent.putExtra(OpenParams.CLOSE_PARAMS, key);
            ImageLoadUtils.getInstance().setCloseParams(key, closeParams);
        }
        if (disableTouchClose == null){
            intent.putExtra(OpenParams.DISABLE_TOUCH_CLOSE, OpenImageConfig.getInstance().isDisEnableTouchClose());
        }else {
            intent.putExtra(OpenParams.DISABLE_TOUCH_CLOSE, disableTouchClose);
        }
        if (onPermissionsInterceptListener != null) {
            String key = this.toString();
            intent.putExtra(OpenParams.PERMISSION_LISTENER, key);
            ImageLoadUtils.getInstance().setPermissionsInterceptListener(key,onPermissionsInterceptListener);
        }
        intent.putExtra(OpenParams.PRELOAD_COUNT, preloadCount);
        intent.putExtra(OpenParams.LAZY_PRELOAD, lazyPreload);
        intent.putExtra(OpenParams.BOTH_LOAD_COVER, bothLoadCover);
        backViewKey = this.toString();
        return intent;
    }

    ShapeImageView.ShapeScaleType getShapeScaleType(){
        return srcImageViewShapeScaleType != null ? srcImageViewShapeScaleType : ShapeImageView.ShapeScaleType.getType(srcImageViewScaleType);
    }

    protected class ExitOnBackView4ListView extends ExitOnBackView {
        private final ArrayList<OpenImageDetail> openImageDetails;
        protected View showCurrentView;
        protected Float showCurrentViewStartAlpha;

        public ExitOnBackView4ListView(View transitionView, ArrayList<OpenImageDetail> openImageDetails) {
            super(transitionView);
            this.openImageDetails = openImageDetails;
        }

        @Override
        public ShareExitViewBean onBack(int showPosition) {
            ShareExitViewBean shareExitViewBean = new ShareExitViewBean(BackViewType.NO_SHARE, null);
            Activity activity = ActivityCompatHelper.getActivity(context);
            if (activity == null) {
                return shareExitViewBean;
            }
            ImageView shareExitView = null;
            boolean isClipSrcImageView = true;
            shareExitViewBean = getShareExitViewBean(showPosition);
            if (shareExitViewBean != null) {
                shareExitView = shareExitViewBean.shareExitView;
                isClipSrcImageView = shareExitViewBean.isClipSrcImageView;
            }
            activity.setExitSharedElementCallback(new ExitSharedElementCallback2(context, shareExitView, showSrcImageView, shareExitView == showCurrentView ? showCurrentViewStartAlpha : null, isClipSrcImageView,OpenImage4Params.this));
            showCurrentView = null;
            transitionView = null;
            return shareExitViewBean;
        }

        @Override
        public void onStartTouchScale(int showPosition) {
            super.onStartTouchScale(showPosition);
            if (!showSrcImageView) {
                ShareExitViewBean shareExitViewBean = getShareExitViewBean(showPosition);
                if (shareExitViewBean != null) {
                    showCurrentView = shareExitViewBean.shareExitView;
                    showCurrentViewStartAlpha = showCurrentView.getAlpha();
                    showCurrentView.setAlpha(0f);
                }
            }
        }

        @Override
        public void onEndTouchScale(int showPosition) {
            super.onEndTouchScale(showPosition);
            if (!showSrcImageView) {
                if (showCurrentView != null && showCurrentViewStartAlpha != null) {
                    showCurrentView.setAlpha(showCurrentViewStartAlpha);
                }
                showCurrentView = null;
                showCurrentViewStartAlpha = null;
            }
        }

        private boolean checkViewAvailable() {
            if (srcViewType == SrcViewType.RV) {
                return recyclerView != null && recyclerView.isAttachedToWindow();
            } else if (srcViewType == SrcViewType.AB_LIST) {
                return absListView != null && absListView.isAttachedToWindow();
            } else if (srcViewType == SrcViewType.VP2) {
                return viewPager2 != null && viewPager2.isAttachedToWindow();
            } else if (srcViewType == SrcViewType.VP) {
                return viewPager != null && viewPager.isAttachedToWindow();
            } else if (srcViewType == SrcViewType.IV) {
                return imageViews != null && imageViews.size() > 0;
            }
            return false;
        }

        private ShareExitViewBean getShareExitViewBean(int showPosition) {
            Activity activity = ActivityCompatHelper.getActivity(context);
            if (activity == null || !checkViewAvailable()) {
                return null;
            }
            ShareExitViewBean shareExitViewBean = null;
            if (srcViewType == SrcViewType.RV || srcViewType == SrcViewType.AB_LIST || srcViewType == SrcViewType.VP2) {
                if (showPosition >= openImageDetails.size()) {
                    return null;
                }
                int[] position = getVisiblePosition();
                int firstPos = position[0];
                int lastPos = position[1];
                if (lastPos < 0 || firstPos < 0) {
                    return null;
                }
                ImageView shareExitView = null;

                OpenImageDetail openImageDetail = openImageDetails.get(showPosition);
                OpenImageUrl openImageUrl = openImageDetail.openImageUrl;
                int viewPosition = openImageDetail.viewPosition;
                int dataPosition = openImageDetail.dataPosition;

                View view = getItemView(viewPosition);

                if (view != null) {
                    ImageView shareView = view.findViewById(sourceImageViewIdGet.getImageViewId(openImageUrl, dataPosition));
                    if (shareView != null) {
                        boolean isAttachedToWindow = shareView.isAttachedToWindow();
                        if (isAttachedToWindow) {
                            autoSetScaleType(shareView);
                            shareExitView = shareView;
                            shareExitViewBean = new ShareExitViewBean(BackViewType.SHARE_NORMAL, shareExitView);
                        }
                    }
                }

                if (shareExitView == null && wechatExitFillInEffect) {
                    openImageUrl = openImageUrls.get(clickDataPosition);
                    viewPosition = clickViewPosition;
                    dataPosition = clickDataPosition;
                    View wechatView = getItemView(viewPosition);
                    if (wechatView != null) {
                        ImageView shareView = wechatView.findViewById(sourceImageViewIdGet.getImageViewId(openImageUrl, dataPosition));
                        if (shareView != null && shareView.isAttachedToWindow()) {
                            autoSetScaleType(shareView);
                            shareExitView = shareView;
                            shareExitViewBean = new ShareExitViewBean(BackViewType.SHARE_WECHAT, shareExitView);
                        }
                    }
                }
                if (srcViewType == SrcViewType.VP2 && shareExitViewBean != null && viewPager2.getCurrentItem() != viewPosition) {
                    shareExitViewBean.isClipSrcImageView = false;
                }
            } else if (srcViewType == SrcViewType.VP) {
                if (showPosition >= openImageDetails.size()) {
                    return null;
                }
                ImageView shareExitView = null;
                OpenImageDetail openImageDetail = openImageDetails.get(showPosition);
                OpenImageUrl openImageUrl = openImageDetail.openImageUrl;
                int viewPosition = openImageDetail.viewPosition;
                int dataPosition = openImageDetail.dataPosition;

                ImageView shareView = sourceImageViewGet.getImageView(openImageUrl, dataPosition);
                if (shareView != null && shareView.isAttachedToWindow()) {
                    autoSetScaleType(shareView);
                    shareExitView = shareView;
                    shareExitViewBean = new ShareExitViewBean(BackViewType.SHARE_NORMAL, shareView);
                }
                if (shareExitView == null && wechatExitFillInEffect) {
                    openImageUrl = openImageUrls.get(clickDataPosition);
                    viewPosition = clickViewPosition;
                    dataPosition = clickDataPosition;
                    ImageView wechatView = sourceImageViewGet.getImageView(openImageUrl, dataPosition);
                    if (wechatView != null && wechatView.isAttachedToWindow()) {
                        autoSetScaleType(wechatView);
                        shareExitView = wechatView;
                        shareExitViewBean = new ShareExitViewBean(BackViewType.SHARE_WECHAT, shareExitView);
                    }
                }
                if (shareExitViewBean != null && viewPager.getCurrentItem() != viewPosition) {
                    shareExitViewBean.isClipSrcImageView = false;
                }
            } else {
                ImageView shareExitView = null;
                ImageView shareView = null;
                if (showPosition < imageViews.size()){
                    shareView = imageViews.get(showPosition);
                }
                if (shareView != null && shareView.isAttachedToWindow()) {
                    autoSetScaleType(shareView);
                    shareExitView = shareView;
                    shareExitViewBean = new ShareExitViewBean(BackViewType.SHARE_NORMAL, shareExitView);
                }

                if (shareExitView == null && wechatExitFillInEffect) {
                    ImageView wechatView = imageViews.get(clickViewPosition);
                    if (wechatView != null && wechatView.isAttachedToWindow()) {
                        autoSetScaleType(wechatView);
                        shareExitView = wechatView;
                        shareExitViewBean = new ShareExitViewBean(BackViewType.SHARE_WECHAT, shareExitView);
                    }
                }
            }
            return shareExitViewBean;
        }

    }
    protected View getItemView(int position) {
        if (position < 0){
            return null;
        }
        View view = null;
        if (srcViewType == SrcViewType.RV) {
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager != null) {
                view = layoutManager.findViewByPosition(position);
            }
        } else if (srcViewType == SrcViewType.AB_LIST) {
            if (absListView != null) {
                int firstPos = absListView.getFirstVisiblePosition();
                view = absListView.getChildAt(position - firstPos);
            }
        } else if (srcViewType == SrcViewType.VP2) {
            int childCount = viewPager2.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childView = viewPager2.getChildAt(i);
                if (childView instanceof RecyclerView) {
                    RecyclerView recyclerView = (RecyclerView) childView;
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    if (layoutManager != null) {
                        view = layoutManager.findViewByPosition(position);
                    }
                    break;
                }
            }
        } else {
            if (imageViews != null) {
                view = imageViews.get(position);
            }
        }
        return view;
    }

    protected void autoSetScaleType(ImageView shareView) {
        if (!autoSetScaleType) {
            return;
        }
        if (srcImageViewShapeScaleType != null && shareView instanceof ShapeImageView) {
            ShapeImageView shapeImageView = (ShapeImageView) shareView;
            if (shapeImageView.getShapeScaleType() != srcImageViewShapeScaleType) {
                shapeImageView.setShapeScaleType(srcImageViewShapeScaleType);
            }
        }
        if (srcImageViewScaleType != null) {
            if (shareView != null && shareView.getScaleType() != srcImageViewScaleType) {
                shareView.setScaleType(srcImageViewScaleType);
            }
        }

    }

    protected int[] getVisiblePosition() {
        int[] position = new int[]{-1, -1};
        if (srcViewType == SrcViewType.RV) {
            int firstPos = 0;
            int lastPos = 0;
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManagerFindVisiblePosition != null){
                firstPos = layoutManagerFindVisiblePosition.findFirstVisibleItemPosition();
                lastPos = layoutManagerFindVisiblePosition.findLastVisibleItemPosition();
            }else if (layoutManager instanceof LinearLayoutManager) {
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
                    return position;
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
                    return position;
                }
            }

            position[0] = firstPos;
            position[1] = lastPos;
        } else if (srcViewType == SrcViewType.VP2) {
            int childCount = viewPager2.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childView = viewPager2.getChildAt(i);
                if (childView instanceof RecyclerView) {
                    RecyclerView recyclerView = (RecyclerView) childView;
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    if (layoutManager instanceof LinearLayoutManager) {
                        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                        position[0] = linearLayoutManager.findFirstVisibleItemPosition();
                        position[1] = linearLayoutManager.findLastVisibleItemPosition();
                    }
                    break;
                }
            }
        } else if (srcViewType == SrcViewType.AB_LIST) {
            int firstPos = absListView.getFirstVisiblePosition();
            int lastPos = absListView.getLastVisiblePosition();
            position[0] = firstPos;
            position[1] = lastPos;
        }
        return position;
    }

    void onExit(){}
}
