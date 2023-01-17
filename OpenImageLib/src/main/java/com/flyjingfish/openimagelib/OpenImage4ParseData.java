package com.flyjingfish.openimagelib;

import android.app.Activity;
import android.app.Instrumentation;
import android.app.SharedElementCallback;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.flyjingfish.openimagelib.beans.OpenImageDetail;
import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.enums.MediaType;
import com.flyjingfish.openimagelib.listener.OnLoadBigImageListener;
import com.flyjingfish.openimagelib.utils.ActivityCompatHelper;
import com.flyjingfish.shapeimageviewlib.ShapeImageView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

class OpenImage4ParseData extends OpenImage4Params {

    protected void goShow() {
        if (ImageLoadUtils.getInstance().isCanOpenViewPagerActivity(contextKey)) {
            Activity activity = ActivityCompatHelper.getActivity(context);
            activity.setExitSharedElementCallback(null);
            show4ParseData();
        }
    }

    private void show4ParseData() {
        Intent intent = inputIntentData();
        ArrayList<OpenImageDetail> openImageDetails;
        Pair<View, String> viewPair;
        if (isNoneClickView){
            openImageDetails = new ArrayList<>();
            viewPair = initShareView(openImageDetails);
        }else if (recyclerView != null) {
            if (sourceImageViewIdGet == null) {
                throw new IllegalArgumentException("sourceImageViewIdGet 不能为null");
            }

            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (!(layoutManager instanceof LinearLayoutManager || layoutManager instanceof StaggeredGridLayoutManager)) {
                throw new IllegalArgumentException("只支持使用继承自LinearLayoutManager和StaggeredGridLayoutManager的RecyclerView");
            }
            srcViewType = SrcViewType.RV;

            openImageDetails = new ArrayList<>();
            viewPair = initShareView(openImageDetails);
            if (checkIllegalException4ShareView(viewPair,"请确保是否调用了setClickPosition并且参数设置正确，或 SourceImageViewIdGet 返回的 ImageView 的Id正确")){
                return;
            }
            View shareViewClick = viewPair.first;
            if (shareViewClick instanceof ShapeImageView) {
                intent.putExtra(OpenParams.AUTO_ASPECT_RATIO, ((ShapeImageView) shareViewClick).getAutoCropHeightWidthRatio());
            }
            ImageLoadUtils.getInstance().setOnBackView(backViewKey, new ExitOnBackView4ListView(shareViewClick, openImageDetails) {
                @Override
                public void onScrollPos(int pos) {
                    if (isAutoScrollScanPosition) {
                        final RecyclerView.LayoutManager layoutManager =
                                recyclerView.getLayoutManager();
                        View viewAtPosition =
                                layoutManager.findViewByPosition(pos);
                        if (viewAtPosition == null
                                || layoutManager.isViewPartiallyVisible(viewAtPosition, true, true)) {
                            recyclerView.post(() -> layoutManager.scrollToPosition(pos));
                        }
                    }
                }
            });
            replenishImageUrl(openImageDetails);
        } else if (absListView != null) {
            if (sourceImageViewIdGet == null) {
                throw new IllegalArgumentException("sourceImageViewIdGet 不能为null");
            }
            srcViewType = SrcViewType.AB_LIST;
            openImageDetails = new ArrayList<>();

            viewPair = initShareView(openImageDetails);
            if (checkIllegalException4ShareView(viewPair,"请确保是否调用了setClickPosition并且参数设置正确，或 SourceImageViewIdGet 返回的 ImageView 的Id正确")){
                return;
            }
            View shareViewClick = viewPair.first;
            if (shareViewClick instanceof ShapeImageView) {
                intent.putExtra(OpenParams.AUTO_ASPECT_RATIO, ((ShapeImageView) shareViewClick).getAutoCropHeightWidthRatio());
            }
            ImageLoadUtils.getInstance().setOnBackView(backViewKey, new ExitOnBackView4ListView(shareViewClick, openImageDetails) {

                @Override
                public void onScrollPos(int pos) {
                    if (isAutoScrollScanPosition) {
                        absListView.post(() -> {
                            absListView.smoothScrollToPosition(pos);
                        });
                    }
                }

            });
            replenishImageUrl(openImageDetails);
        } else if (viewPager2 != null) {
            if (sourceImageViewIdGet == null) {
                throw new IllegalArgumentException("sourceImageViewIdGet 不能为null");
            }

            srcViewType = SrcViewType.VP2;
            openImageDetails = new ArrayList<>();

            viewPair = initShareView(openImageDetails);
            if (checkIllegalException4ShareView(viewPair,"请确保是否调用了setClickPosition并且参数设置正确，或 SourceImageViewIdGet 返回的 ImageView 的Id正确")){
                return;
            }

            View shareViewClick = viewPair.first;
            if (shareViewClick instanceof ShapeImageView) {
                intent.putExtra(OpenParams.AUTO_ASPECT_RATIO, ((ShapeImageView) shareViewClick).getAutoCropHeightWidthRatio());
            }
            ImageLoadUtils.getInstance().setOnBackView(backViewKey, new ExitOnBackView4ListView(shareViewClick, openImageDetails) {
                @Override
                public void onScrollPos(int pos) {
                    if (isAutoScrollScanPosition) {
                        viewPager2.post(() -> {
                            viewPager2.setCurrentItem(pos, false);
                        });
                    }
                }
            });
            replenishImageUrl(openImageDetails);
        } else if (viewPager != null) {
            if (sourceImageViewGet == null) {
                throw new IllegalArgumentException("sourceImageViewGet 不能为null");
            }

            srcViewType = SrcViewType.VP;
            openImageDetails = new ArrayList<>();

            viewPair = initShareView(openImageDetails);
            if (checkIllegalException4ShareView(viewPair,"请确保是否调用了setClickPosition并且参数设置正确，SourceImageViewGet 返回的 ImageView 不能为null")){
                return;
            }
            View shareViewClick = viewPair.first;
            if (shareViewClick instanceof ShapeImageView) {
                intent.putExtra(OpenParams.AUTO_ASPECT_RATIO, ((ShapeImageView) shareViewClick).getAutoCropHeightWidthRatio());
            }
            ImageLoadUtils.getInstance().setOnBackView(backViewKey, new ExitOnBackView4ListView(shareViewClick, openImageDetails) {
                @Override
                public void onScrollPos(int pos) {
                    if (isAutoScrollScanPosition) {
                        viewPager.post(() -> {
                            viewPager.setCurrentItem(pos, false);
                        });
                    }
                }
            });
        } else if (imageViews != null && imageViews.size() > 0) {
            srcViewType = SrcViewType.IV;
            openImageDetails = new ArrayList<>();

            viewPair = initShareView(openImageDetails);
            if (checkIllegalException4ShareView(viewPair,"请确保是否调用了setClickPosition并且参数设置正确，或所传ImageView个数是否正确")){
                return;
            }

            View shareViewClick = viewPair.first;
            if (shareViewClick instanceof ShapeImageView) {
                intent.putExtra(OpenParams.AUTO_ASPECT_RATIO, ((ShapeImageView) shareViewClick).getAutoCropHeightWidthRatio());
            }

            ImageLoadUtils.getInstance().setOnBackView(backViewKey, new ExitOnBackView4ListView(shareViewClick, openImageDetails));
        } else {
            throw new IllegalArgumentException("请设置至少一个点击的ImageView");
        }
        intent.putExtra(OpenParams.ON_BACK_VIEW, backViewKey);
        intent.putExtra(OpenParams.IMAGES, openImageDetails);
        postOpen(intent, viewPair);
    }

    private boolean checkIllegalException4ShareView(Pair<View, String> viewPair,String str){
        if (viewPair == null){
            if (BuildConfig.DEBUG){
                throw new IllegalArgumentException(str);
            }else {
                isNoneClickView = true;
                show4ParseData();
                return true;
            }
        }else {
            return false;
        }

    }

    private Pair<View, String> initShareView(ArrayList<OpenImageDetail> openImageDetails) {
        Pair<View, String> pair = null;
        if (isNoneClickView){
            for (int i = 0; i < openImageUrls.size(); i++) {
                OpenImageUrl imageBean = openImageUrls.get(i);
                OpenImageDetail openImageDetail = new OpenImageDetail();
                openImageDetail.openImageUrl = imageBean;
                openImageDetail.dataPosition = i;
                openImageDetail.viewPosition = i;
                openImageDetails.add(openImageDetail);
            }
        }else if (srcViewType == SrcViewType.RV || srcViewType == SrcViewType.AB_LIST) {
            int[] position = getVisiblePosition();
            int firstPos = position[0];
            int lastPos = position[1];
            if (lastPos < 0 || firstPos < 0) {
                return null;
            }
            int viewIndex = clickViewPosition - clickDataPosition;
            for (int i = 0; i < openImageUrls.size(); i++) {
                OpenImageUrl imageBean = openImageUrls.get(i);
                if (imageBean.getType() == MediaType.IMAGE || imageBean.getType() == MediaType.VIDEO) {
                    OpenImageDetail openImageDetail = new OpenImageDetail();
                    openImageDetail.openImageUrl = imageBean;
                    openImageDetail.dataPosition = i;
                    if (viewIndex >= firstPos && viewIndex <= lastPos) {
                        View view = getItemView(viewIndex);
                        if (view != null) {
                            ImageView shareView = view.findViewById(sourceImageViewIdGet.getImageViewId(imageBean, i));
                            if (shareView == null) {
                                return null;
                            }
                            autoSetScaleType(shareView);
                            String shareName = OpenParams.SHARE_VIEW + openImageDetails.size();
                            if (clickViewPosition == viewIndex) {
                                pair = Pair.create(shareView, shareName);
                            }
                            int shareViewWidth = shareView.getWidth();
                            int shareViewHeight = shareView.getHeight();
                            openImageDetail.srcWidth = shareViewWidth;
                            openImageDetail.srcHeight = shareViewHeight;
                            if (imageBean.getType() == MediaType.IMAGE) {
                                srcImageWidthCache.add(shareViewWidth);
                                srcImageHeightCache.add(shareViewHeight);
                            }
                            if (imageBean.getType() == MediaType.VIDEO) {
                                srcVideoWidthCache.add(shareViewWidth);
                                srcVideoHeightCache.add(shareViewHeight);
                            }
                        }
                    }
                    openImageDetail.viewPosition = viewIndex;
                    openImageDetails.add(openImageDetail);
                }
                viewIndex++;
            }
        } else if (srcViewType == SrcViewType.VP2) {
            int viewIndex = clickViewPosition - clickDataPosition;
            for (int i = 0; i < openImageUrls.size(); i++) {
                OpenImageUrl imageBean = openImageUrls.get(i);
                if (imageBean.getType() == MediaType.IMAGE || imageBean.getType() == MediaType.VIDEO) {
                    OpenImageDetail openImageDetail = new OpenImageDetail();
                    openImageDetail.openImageUrl = imageBean;
                    openImageDetail.dataPosition = i;
                    if (viewIndex >= 0) {
                        View view = getItemView(viewIndex);
                        if (view != null) {
                            ImageView shareView = view.findViewById(sourceImageViewIdGet.getImageViewId(imageBean, i));
                            if (shareView == null) {
                                return null;
                            }
                            autoSetScaleType(shareView);
                            String shareName = OpenParams.SHARE_VIEW + openImageDetails.size();
                            if (clickViewPosition == viewIndex) {
                                pair = Pair.create(shareView, shareName);
                            }
                            int shareViewWidth = shareView.getWidth();
                            int shareViewHeight = shareView.getHeight();
                            openImageDetail.srcWidth = shareViewWidth;
                            openImageDetail.srcHeight = shareViewHeight;
                            if (imageBean.getType() == MediaType.IMAGE) {
                                srcImageWidthCache.add(shareViewWidth);
                                srcImageHeightCache.add(shareViewHeight);
                            }
                            if (imageBean.getType() == MediaType.VIDEO) {
                                srcVideoWidthCache.add(shareViewWidth);
                                srcVideoHeightCache.add(shareViewHeight);
                            }
                        }
                    }
                    openImageDetail.viewPosition = viewIndex;
                    openImageDetails.add(openImageDetail);
                }
                viewIndex++;
            }
        } else if (srcViewType == SrcViewType.VP) {
            ImageView shareView = sourceImageViewGet.getImageView(openImageUrls.get(clickDataPosition), clickDataPosition);
            if (shareView == null) {
                return null;
            }
            autoSetScaleType(shareView);
            String shareName = OpenParams.SHARE_VIEW + clickDataPosition;
            pair = Pair.create(shareView, shareName);
            int shareViewWidth = shareView.getWidth();
            int shareViewHeight = shareView.getHeight();
            for (int i = 0; i < openImageUrls.size(); i++) {
                OpenImageUrl imageBean = openImageUrls.get(i);
                OpenImageDetail openImageDetail = new OpenImageDetail();
                openImageDetail.openImageUrl = imageBean;
                openImageDetail.dataPosition = i;
                openImageDetail.viewPosition = i;
                openImageDetail.srcWidth = shareViewWidth;
                openImageDetail.srcHeight = shareViewHeight;
                openImageDetails.add(openImageDetail);
            }
        } else if (srcViewType == SrcViewType.IV){
            for (int i = 0; i < openImageUrls.size(); i++) {
                OpenImageUrl imageBean = openImageUrls.get(i);
                OpenImageDetail openImageDetail = new OpenImageDetail();
                openImageDetail.openImageUrl = imageBean;
                openImageDetail.dataPosition = i;
                openImageDetail.viewPosition = i;
                if (i < imageViews.size()){
                    ImageView shareView = imageViews.get(i);
                    autoSetScaleType(shareView);
                    String shareName = OpenParams.SHARE_VIEW + i;
                    int shareViewWidth = shareView.getWidth();
                    int shareViewHeight = shareView.getHeight();
                    openImageDetail.srcWidth = shareViewWidth;
                    openImageDetail.srcHeight = shareViewHeight;
                    if (clickViewPosition == i) {
                        pair = Pair.create(shareView, shareName);
                    }
                }
                openImageDetails.add(openImageDetail);
            }
        }else {
            for (int i = 0; i < openImageUrls.size(); i++) {
                OpenImageUrl imageBean = openImageUrls.get(i);
                OpenImageDetail openImageDetail = new OpenImageDetail();
                openImageDetail.openImageUrl = imageBean;
                openImageDetail.dataPosition = i;
                openImageDetail.viewPosition = i;
                openImageDetails.add(openImageDetail);
            }
        }

        return pair;
    }


    private void postOpen(Intent intent, Pair<View, String> viewPair) {
        ImageLoadUtils.getInstance().setCanOpenViewPagerActivity(contextKey,false);
        OpenImageUrl openImageUrl = openImageUrls.get(clickDataPosition);
        Handler handler = new Handler(Looper.getMainLooper());
        OpenImageConfig.getInstance().getBigImageHelper().loadImage(context, ImageLoadUtils.getInstance().getImageLoadSuccess(openImageUrl.getImageUrl()) ? openImageUrl.getImageUrl() : openImageUrl.getCoverImageUrl(), new OnLoadBigImageListener() {
            @Override
            public void onLoadImageSuccess(Drawable drawable) {
                handler.removeCallbacksAndMessages(null);
                String key = UUID.randomUUID().toString();
                intent.putExtra(OpenParams.OPEN_COVER_DRAWABLE, key);
                ImageLoadUtils.getInstance().setCoverDrawable(key, drawable);
                startActivity(intent, viewPair, key);
            }

            @Override
            public void onLoadImageFailed() {
                handler.removeCallbacksAndMessages(null);
                startActivity(intent, viewPair, null);
            }
        });
        handler.postDelayed(() -> startActivity(intent, viewPair, null), 100);
    }

    private void startActivity(Intent intent, Pair<View, String> viewPair, String drawableKey) {
        if (!isStartActivity) {
            if (ActivityCompatHelper.assertValidRequest(context) && viewPair != null && !isNoneClickView) {
                this.drawableKey = drawableKey;
                View shareElementView = viewPair.first;
                String shareElementName = viewPair.second;
                try {
                    if (shareElementView != null && shareElementView.isAttachedToWindow()) {
                        fixAndroid5_7Bug(shareElementView, true);
                        Bundle options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, shareElementView, shareElementName).toBundle();
                        context.startActivity(intent, options);
                    } else {
                        releaseImageLoadUtilMap();
                    }
                } catch (Exception ignored) {
                    releaseImageLoadUtilMap();
                }
                release();
            }else if (ActivityCompatHelper.assertValidRequest(context) && isNoneClickView) {
                context.startActivity(intent);
                release();
            } else {
                releaseImageLoadUtilMap();
            }
        } else if (!TextUtils.isEmpty(drawableKey)) {
            ImageLoadUtils.getInstance().clearCoverDrawable(drawableKey);
        }
        isStartActivity = true;
    }

    private void releaseImageLoadUtilMap() {
        ImageLoadUtils.getInstance().clearCanOpenViewPagerActivity(contextKey);
        ImageLoadUtils.getInstance().clearItemLoadHelper(itemLoadHelperKey);
        ImageLoadUtils.getInstance().clearOnSelectMediaListener(onSelectKey);
        ImageLoadUtils.getInstance().clearCoverDrawable(drawableKey);
        ImageLoadUtils.getInstance().clearPageTransformers(pageTransformersKey);
        ImageLoadUtils.getInstance().clearOnItemClickListener(onItemClickListenerKey);
        ImageLoadUtils.getInstance().clearOnItemLongClickListener(onItemLongClickListenerKey);
        ImageLoadUtils.getInstance().clearMoreViewOption(moreViewOptionKey);
        ImageLoadUtils.getInstance().clearOnBackView(backViewKey);
        ImageLoadUtils.getInstance().clearImageFragmentCreate(imageFragmentCreateKey);
        ImageLoadUtils.getInstance().clearVideoFragmentCreate(videoFragmentCreateKey);
        ImageLoadUtils.getInstance().clearUpperLayerFragmentCreate(upperLayerFragmentCreateKey);
        ImageLoadUtils.getInstance().setOnRemoveListener4FixBug(null);
        moreViewOptions.clear();
    }

    private void fixAndroid5_7Bug(final View shareElementView, final boolean isSetExitSharedElementCallback) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {
            if (isSetExitSharedElementCallback && context != null) {
                Activity activity = ActivityCompatHelper.getActivity(context);
                activity.setExitSharedElementCallback(new SharedElementCallback() {
                    @Override
                    public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                        if (!isMapShareView) {
                            names.clear();
                            sharedElements.clear();
                        }
                        super.onMapSharedElements(names, sharedElements);
                        activity.setExitSharedElementCallback(null);
                    }
                });
            }
            ViewGroup parent = (ViewGroup) shareElementView.getParent();
            ViewTreeObserver parentViewTreeObserver = parent.getViewTreeObserver();
            final View.OnAttachStateChangeListener onAttachStateChangeListener = new View.OnAttachStateChangeListener() {


                @Override
                public void onViewAttachedToWindow(View v) {

                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    if (ActivityCompatHelper.getActivity(context) == null) {
                        shareElementView.removeOnAttachStateChangeListener(this);
                        return;
                    }
                    if (v != shareElementView) {
                        return;
                    }
                    shareElementView.removeOnAttachStateChangeListener(this);
                    boolean isCallStop = false;
                    try {
                        Field fieldPassword = ViewTreeObserver.class.getDeclaredField("mOnPreDrawListeners");
                        fieldPassword.setAccessible(true);
                        Object mOnPreDrawListeners = fieldPassword.get(parentViewTreeObserver);

                        Field mDataField = mOnPreDrawListeners.getClass().getDeclaredField("mData");
                        mDataField.setAccessible(true);
                        ArrayList<ViewTreeObserver.OnPreDrawListener> mData = (ArrayList<ViewTreeObserver.OnPreDrawListener>) mDataField.get(mOnPreDrawListeners);
                        for (ViewTreeObserver.OnPreDrawListener mDatum : mData) {
                            if (mDatum.getClass().getName().contains("GhostViewListeners")) {
                                isCallStop = true;
                                parentViewTreeObserver.removeOnPreDrawListener(mDatum);
                            }
                        }
                    } catch (Throwable ignored) {
                    } finally {
                        isMapShareView = false;
                        if (isCallStop) {
                            new Instrumentation().callActivityOnStop(ActivityCompatHelper.getActivity(context));
                        }
                    }

                }

            };
            shareElementView.addOnAttachStateChangeListener(onAttachStateChangeListener);
            ImageLoadUtils.getInstance().setOnRemoveListener4FixBug(() -> shareElementView.removeOnAttachStateChangeListener(onAttachStateChangeListener));
        }
    }


    private void replenishImageUrl(ArrayList<OpenImageDetail> openImageDetails) {
        if (srcImageWidthCache.size() == 1 || srcImageHeightCache.size() == 1) {
            int srcWidth = srcImageWidthCache.iterator().next();
            int srcHeight = srcImageHeightCache.iterator().next();
            for (OpenImageDetail openImageDetail : openImageDetails) {
                if (openImageDetail.getType() == MediaType.IMAGE && (openImageDetail.srcWidth == 0 || openImageDetail.srcHeight == 0)) {
                    openImageDetail.srcWidth = srcWidth;
                    openImageDetail.srcHeight = srcHeight;
                }
            }
        }
        if (srcVideoWidthCache.size() == 1 || srcVideoHeightCache.size() == 1) {
            int srcWidth = srcVideoWidthCache.iterator().next();
            int srcHeight = srcVideoHeightCache.iterator().next();
            for (OpenImageDetail openImageDetail : openImageDetails) {
                if (openImageDetail.getType() == MediaType.VIDEO && (openImageDetail.srcWidth == 0 || openImageDetail.srcHeight == 0)) {
                    openImageDetail.srcWidth = srcWidth;
                    openImageDetail.srcHeight = srcHeight;
                }
            }
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
                        viewPager = null;
                        viewPager2 = null;
                        if (imageViews != null) {
                            imageViews.clear();
                            imageViews = null;
                        }
                        source.getLifecycle().removeObserver(this);
                        releaseImageLoadUtilMap();
                    }
                }
            });
        }

    }


}
