package com.flyjingfish.openimagelib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.flyjingfish.openimagelib.beans.OpenImageDetail;
import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.enums.BackViewType;
import com.flyjingfish.openimagelib.enums.ImageDiskMode;
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
    protected String contextKey;
    protected final List<OpenImageUrl> openImageUrls = new ArrayList<>();
    protected List<ImageView> imageViews;
    protected RecyclerView recyclerView;
    protected AbsListView absListView;
    protected ViewPager2 viewPager2;
    protected ViewPager viewPager;
    protected long openPageAnimTimeMs;
    protected int clickViewPosition;
    protected int clickDataPosition;
    protected int errorResId;
    protected int openImageStyle;
    protected ImageView.ScaleType srcImageViewScaleType;
    protected ShapeImageView.ShapeScaleType srcImageViewShapeScaleType;
    protected boolean autoSetScaleType;
    protected ImageDiskMode imageDiskMode = ImageDiskMode.CONTAIN_ORIGINAL;
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
    protected String itemLoadHelperKey;
    protected boolean disableClickClose;
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
    protected Class<?> openImageActivityCls = ViewPagerActivity.class;
    protected boolean isNoneClickView = false;

    protected enum SrcViewType {
        RV, AB_LIST, VP, VP2, IV
    }

    protected Intent inputIntentData() {
        if (openImageUrls.size() == 0) {
            throw new IllegalArgumentException("请设置数据");
        }
        if (imageDiskMode != ImageDiskMode.CONTAIN_ORIGINAL && itemLoadHelperKey == null && BuildConfig.DEBUG) {
            throw new IllegalArgumentException("请设置ItemLoadHelper");
        }
        if (clickDataPosition >= openImageUrls.size() && BuildConfig.DEBUG) {
            throw new IllegalArgumentException("clickDataPosition不能 >= OpenImageUrl 的个数");
        }
        if (wechatExitFillInEffect) {
            isAutoScrollScanPosition = false;
        }
        Intent intent = new Intent(context, openImageActivityCls);

        intent.putExtra(OpenParams.CLICK_POSITION, clickDataPosition);
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
        intent.putExtra(OpenParams.DISABLE_CLICK_CLOSE, disableClickClose);
        intent.putExtra(OpenParams.AUTO_SCROLL_SELECT, isAutoScrollScanPosition);
        intent.putExtra(OpenParams.DISABLE_TOUCH_CLOSE, OpenImageConfig.getInstance().isDisEnableTouchClose());
        intent.putExtra(OpenParams.SRC_SCALE_TYPE, srcImageViewShapeScaleType != null ? srcImageViewShapeScaleType : ShapeImageView.ShapeScaleType.getType(srcImageViewScaleType));
        intent.putExtra(OpenParams.IMAGE_DISK_MODE, imageDiskMode);
        intent.putExtra(OpenParams.ERROR_RES_ID, errorResId);
        intent.putExtra(OpenParams.ITEM_LOAD_KEY, itemLoadHelperKey);
        intent.putExtra(OpenParams.TOUCH_CLOSE_SCALE, OpenImageConfig.getInstance().getTouchCloseScale());
        intent.putExtra(OpenParams.OPEN_IMAGE_STYLE, openImageStyle);
        intent.putExtra(OpenParams.OPEN_ANIM_TIME_MS, openPageAnimTimeMs);
        intent.putExtra(OpenParams.GALLERY_EFFECT_WIDTH, leftRightShowWidthDp);
        intent.putExtra(OpenParams.CONTEXT_KEY, contextKey);
        intent.putExtra(OpenParams.NONE_CLICK_VIEW, isNoneClickView);
        backViewKey = this.toString();
        return intent;
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
            activity.setExitSharedElementCallback(new ExitSharedElementCallback2(context, shareExitView, showSrcImageView, shareExitView == showCurrentView ? showCurrentViewStartAlpha : null, isClipSrcImageView));
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
                return true;
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
                        if (shareView != null) {
                            boolean isAttachedToWindow = shareView.isAttachedToWindow();
                            if (isAttachedToWindow) {
                                autoSetScaleType(shareView);
                                shareExitView = shareView;
                                shareExitViewBean = new ShareExitViewBean(BackViewType.SHARE_WECHAT, shareExitView);
                            }
                        }
                    }
                }
                if (srcViewType == SrcViewType.VP2 && shareExitViewBean != null && viewPager2.getCurrentItem() != viewPosition) {
                    shareExitViewBean.isClipSrcImageView = false;
                }
            } else if (srcViewType == SrcViewType.VP) {
                OpenImageDetail openImageDetail = openImageDetails.get(showPosition);
                OpenImageUrl openImageUrl = openImageDetail.openImageUrl;
                int viewPosition = openImageDetail.viewPosition;
                int dataPosition = openImageDetail.dataPosition;

                ImageView shareView = sourceImageViewGet.getImageView(openImageUrl, dataPosition);
                if (shareView != null) {
                    boolean isAttachedToWindow = shareView.isAttachedToWindow();
                    if (isAttachedToWindow) {
                        autoSetScaleType(shareView);
                        shareExitViewBean = new ShareExitViewBean(BackViewType.SHARE_NORMAL, shareView);
                    }
                }
                if (shareExitViewBean != null && viewPager.getCurrentItem() != viewPosition) {
                    shareExitViewBean.isClipSrcImageView = false;
                }
            } else {
                ImageView shareExitView = null;
                ImageView shareView = imageViews.get(showPosition);
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
            ShapeImageView ShapeImageView = (ShapeImageView) shareView;
            if (ShapeImageView.getShapeScaleType() != srcImageViewShapeScaleType) {
                ShapeImageView.setShapeScaleType(srcImageViewShapeScaleType);
            }
        }
        if (srcImageViewScaleType != null) {
            if (shareView.getScaleType() != srcImageViewScaleType) {
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
}
