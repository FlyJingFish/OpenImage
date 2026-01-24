package com.flyjingfish.openimagelib;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.FragmentManager;
import android.app.Instrumentation;
import android.app.SharedElementCallback;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseLongArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.flyjingfish.openimagelib.beans.ClickViewParam;
import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.enums.MediaType;
import com.flyjingfish.openimagelib.listener.OnLoadBigImageListener;
import com.flyjingfish.openimagelib.utils.ActivityCompatHelper;
import com.flyjingfish.openimagelib.utils.OpenImageLogUtils;
import com.flyjingfish.shapeimageviewlib.ShapeImageView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

class OpenImage4ParseData extends OpenImage4Params {

    protected void goShow() {
        if (ImageLoadUtils.getInstance().isCanOpenOpenImageActivity(contextKey)) {
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
            ImageLoadUtils.getInstance().setOnBackView(backViewKey, new ExitOnBackView(null){
                @Override
                public ShareExitViewBean onBack(int showPosition) {
                    Activity activity = ActivityCompatHelper.getActivity(context);
                    if (activity != null) {
                        activity.setExitSharedElementCallback(new BaseSharedElementCallback(context,OpenImage4ParseData.this));
                    }
                    return super.onBack(showPosition);
                }
            });
        }else if (recyclerView != null) {
            if (sourceImageViewIdGet == null) {
                throw new IllegalArgumentException("sourceImageViewIdGet 不能为null");
            }

            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (!(layoutManager instanceof LinearLayoutManager || layoutManager instanceof StaggeredGridLayoutManager || layoutManagerFindVisiblePosition != null)) {
                throw new IllegalArgumentException("只支持使用继承自 LinearLayoutManager 和 StaggeredGridLayoutManager 的 RecyclerView，或在调用setClickRecyclerView时设置了 LayoutManagerFindVisiblePosition 接口");
            }
            srcViewType = SrcViewType.RV;

            openImageDetails = new ArrayList<>();
            viewPair = initShareView(openImageDetails);
            if (checkIllegalException4ShareView(viewPair,"请确保是否调用了 setClickPosition 并且参数设置正确，或 SourceImageViewIdGet 返回的 ImageView 的Id正确")){
                return;
            }
            View shareViewClick = viewPair.first;
            if (shareViewClick instanceof ShapeImageView) {
                intent.putExtra(OpenParams.AUTO_ASPECT_RATIO, ((ShapeImageView) shareViewClick).getAutoCropHeightWidthRatio());
            }
            ImageLoadUtils.getInstance().setOnBackView(backViewKey, new ExitOnBackView4ListView(shareViewClick, openImageDetails) {
                @Override
                public void onScrollPos(int pos) {
                    if (isAutoScrollScanPosition && recyclerView != null) {
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
                    if (isAutoScrollScanPosition && absListView != null) {
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
                    if (isAutoScrollScanPosition && viewPager2 != null) {
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
                    if (isAutoScrollScanPosition && viewPager != null) {
                        viewPager.post(() -> {
                            viewPager.setCurrentItem(pos, false);
                        });
                    }
                }
            });
        } else if (imageViews != null && imageViews.size() > 0) {
            SrcViewType oldSrcViewType = srcViewType;
            srcViewType = SrcViewType.IV;
            openImageDetails = new ArrayList<>();

            viewPair = initShareView(openImageDetails);
            if (checkIllegalException4ShareView(viewPair,"请确保是否调用了setClickPosition并且参数设置正确，或所传"+(oldSrcViewType == SrcViewType.WEB_VIEW?"ClickViewParam":"ImageView")+"个数是否正确")){
                return;
            }

            View shareViewClick = viewPair.first;
            if (shareViewClick instanceof ShapeImageView) {
                intent.putExtra(OpenParams.AUTO_ASPECT_RATIO, ((ShapeImageView) shareViewClick).getAutoCropHeightWidthRatio());
            }

            ImageLoadUtils.getInstance().setOnBackView(backViewKey, new ExitOnBackView4ListView(shareViewClick, openImageDetails));
        } else if (parentParamsView != null && clickViewParams != null) {
            srcViewType = SrcViewType.WEB_VIEW;

            ViewGroup decorView = (ViewGroup) ((Activity) context).getWindow().getDecorView();
            int[] webViewLocation = new int[2];
            parentParamsView.getLocationOnScreen(webViewLocation);
            FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(parentParamsView.getWidth(), parentParamsView.getHeight());
            layoutParams2.leftMargin = webViewLocation[0];
            layoutParams2.topMargin = webViewLocation[1];
            FrameLayout frameLayout = new FrameLayout(context);
            decorView.addView(frameLayout,layoutParams2);

            imageViews = new ArrayList<>();
            ImageView clickImageView = null;
            int index = 0;
            for (ClickViewParam clickViewParam : clickViewParams) {
                if (clickViewParam == null && ActivityCompatHelper.isApkInDebug(context)){
                    throw new IllegalArgumentException("ClickViewParam 不可为 null");
                }else if (clickViewParam != null){
                    ImageView imageView = new ImageView(context);
                    int webViewWidth = parentParamsView.getWidth();
                    float scale = clickViewParam.imgWidth *1f/clickViewParam.browserWidth;
                    float scale2 = clickViewParam.browserWidth *1f/webViewWidth;
                    int imageViewWidth = (int) (webViewWidth*scale);
                    int imageViewHeight = (int) (imageViewWidth*(clickViewParam.imgHeight *1f/clickViewParam.imgWidth));
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(imageViewWidth,imageViewHeight);
                    layoutParams.topMargin = (int) (clickViewParam.marginTop/scale2);
                    layoutParams.leftMargin = (int) (clickViewParam.marginLeft/scale2);
                    frameLayout.addView(imageView,layoutParams);
                    autoSetScaleType(imageView);
                    imageViews.add(imageView);
                    if (index == clickViewPosition){
                        clickImageView = imageView;
                    }
                }
                index++;
            }
            final View posView = clickImageView;
            if (posView != null){
                posView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        posView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        show4ParseData();
                    }
                });
            }else {
                show4ParseData();
            }
            return;
        } else {
            if (ActivityCompatHelper.isApkInDebug(context)){
                throw new IllegalArgumentException("请设置至少一个点击的ImageView");
            }else {
                Log.e("OpenImage","请设置至少一个点击的ImageView");
                return;
            }
        }
        String dataKey = this.toString();
        intent.putExtra(OpenParams.ON_BACK_VIEW, backViewKey);
        intent.putExtra(OpenParams.IMAGES, dataKey);
        ImageLoadUtils.getInstance().setOpenImageDetailData(dataKey,openImageDetails);
        postOpen(intent, viewPair);
    }

    private boolean checkIllegalException4ShareView(Pair<View, String> viewPair,String str){
        if (viewPair == null){
            if (ActivityCompatHelper.isApkInDebug(context)){
                throw new IllegalArgumentException(str+",详情看问题13 https://github.com/FlyJingFish/OpenImage/wiki/%E5%B8%B8%E8%A7%81%E9%97%AE%E9%A2%98#13%E5%81%87%E5%A6%82%E7%A2%B0%E5%88%B0%E4%BB%A5%E4%B8%8B%E5%87%A0%E7%A7%8D%E9%94%99%E8%AF%AF%E6%8F%90%E7%A4%BA");
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
                if (imageBean.getType() != MediaType.NONE) {
                    OpenImageDetail openImageDetail = OpenImageDetail.getNewOpenImageDetail();
                    openImageDetail.openImageUrl = imageBean;
                    openImageDetail.dataPosition = i;
                    openImageDetail.viewPosition = i;
                    openImageDetails.add(openImageDetail);
                }
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
                if (imageBean.getType() != MediaType.NONE) {
                    OpenImageDetail openImageDetail = OpenImageDetail.getNewOpenImageDetail();
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
                            srcImageWidthCache.add(shareViewWidth);
                            srcImageHeightCache.add(shareViewHeight);
//                            if (imageBean.getType() == MediaType.IMAGE) {
//                                srcImageWidthCache.add(shareViewWidth);
//                                srcImageHeightCache.add(shareViewHeight);
//                            }
//                            if (imageBean.getType() == MediaType.VIDEO) {
//                                srcVideoWidthCache.add(shareViewWidth);
//                                srcVideoHeightCache.add(shareViewHeight);
//                            }
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
                if (imageBean.getType() != MediaType.NONE) {
                    OpenImageDetail openImageDetail = OpenImageDetail.getNewOpenImageDetail();
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
                            srcImageWidthCache.add(shareViewWidth);
                            srcImageHeightCache.add(shareViewHeight);
//                            if (imageBean.getType() == MediaType.IMAGE) {
//                                srcImageWidthCache.add(shareViewWidth);
//                                srcImageHeightCache.add(shareViewHeight);
//                            }
//                            if (imageBean.getType() == MediaType.VIDEO) {
//                                srcVideoWidthCache.add(shareViewWidth);
//                                srcVideoHeightCache.add(shareViewHeight);
//                            }
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
                OpenImageDetail openImageDetail = OpenImageDetail.getNewOpenImageDetail();
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
                OpenImageDetail openImageDetail = OpenImageDetail.getNewOpenImageDetail();
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
                if (imageBean.getType() != MediaType.NONE) {
                    OpenImageDetail openImageDetail = OpenImageDetail.getNewOpenImageDetail();
                    openImageDetail.openImageUrl = imageBean;
                    openImageDetail.dataPosition = i;
                    openImageDetail.viewPosition = i;
                    openImageDetails.add(openImageDetail);
                }
            }
        }

        return pair;
    }


    private void postOpen(Intent intent, Pair<View, String> viewPair) {
        ImageLoadUtils.getInstance().setCanOpenOpenImageActivity(contextKey,false);
        OpenImageUrl openImageUrl = openImageUrls.get(clickDataPosition);
        final String key = openImageUrl.toString();
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = () -> {
            Drawable drawable;
            if (viewPair != null && viewPair.first instanceof ImageView && (drawable = ((ImageView) viewPair.first).getDrawable()) != null){
                intent.putExtra(OpenParams.OPEN_COVER_DRAWABLE, key);
                ImageLoadUtils.getInstance().setSmallCoverDrawable(key, drawable);
                startActivity(intent, viewPair, key);
            }else {
                startActivity(intent, viewPair, null);
            }
        };
        ShapeImageView.ShapeScaleType shapeScaleType = getShapeScaleType();
        long startRecord = SystemClock.uptimeMillis();
        OpenImageConfig.getInstance().getBigImageHelper().loadImage(context, (ImageLoadUtils.getInstance().getImageLoadSuccess(openImageUrl.getImageUrl()) && shapeScaleType != ShapeImageView.ShapeScaleType.CENTER_INSIDE && shapeScaleType != ShapeImageView.ShapeScaleType.CENTER) ? openImageUrl.getImageUrl() : openImageUrl.getCoverImageUrl(), new OnLoadBigImageListener() {
            @Override
            public void onLoadImageSuccess(Drawable drawable, String filePath) {
                handler.removeCallbacksAndMessages(null);
                intent.putExtra(OpenParams.OPEN_COVER_DRAWABLE, key);
                ImageLoadUtils.getInstance().setCoverDrawable(key, drawable);
                ImageLoadUtils.getInstance().setCoverFilePath(key, filePath);
                startActivity(intent, viewPair, key);
                recordTime(key,SystemClock.uptimeMillis() - startRecord);
            }

            @Override
            public void onLoadImageFailed() {
                handler.removeCallbacksAndMessages(null);
                runnable.run();
            }
        });
        handler.postDelayed(runnable, getDelayOpenTimeMs());
    }

    private static final int MAX_DELAY_MS = 100;
    private static final Map<String,Long> useTime = new HashMap<>();

    private static long getDelayOpenTimeMs(){
        int size = useTime.size();
        long use;
        if (size > 4){
            Set<Map.Entry<String,Long>> set = useTime.entrySet();
            long total = 0;
            long max = 0;
            long min = Long.MAX_VALUE;
            for (Map.Entry<String, Long> stringLongEntry : set) {
                total += stringLongEntry.getValue();
                if (stringLongEntry.getValue() > max){
                    max = stringLongEntry.getValue();
                }
                if (stringLongEntry.getValue() < min){
                    min = stringLongEntry.getValue();
                }
            }
            use = (total - min - max)/(size - 2);
        }else {
            use = MAX_DELAY_MS;
        }
        use = Math.min(use,MAX_DELAY_MS);
        return use;
    }

    private static void recordTime(String key,long time){
        if (useTime.containsKey(key)){
            Long oldTime = useTime.get(key);
            if (oldTime == null || oldTime < time){
                useTime.put(key,time);
            }
        }else {
            useTime.put(key,time);
        }
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
                context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation((Activity) context).toBundle());
                release();
            } else {
                releaseImageLoadUtilMap();
            }
        } else if (!TextUtils.isEmpty(drawableKey)) {
            ImageLoadUtils.getInstance().clearCoverDrawable(drawableKey);
            ImageLoadUtils.getInstance().clearCoverFilePath(drawableKey);
            ImageLoadUtils.getInstance().clearSmallCoverDrawable(drawableKey);
        }
        isStartActivity = true;
    }

    private void releaseImageLoadUtilMap() {
        ImageLoadUtils.getInstance().clearCanOpenOpenImageActivity(contextKey);
        ImageLoadUtils.getInstance().clearOnSelectMediaListener(onSelectKey);
        ImageLoadUtils.getInstance().clearCoverDrawable(drawableKey);
        ImageLoadUtils.getInstance().clearCoverFilePath(drawableKey);
        ImageLoadUtils.getInstance().clearSmallCoverDrawable(drawableKey);
        ImageLoadUtils.getInstance().clearPageTransformers(pageTransformersKey);
        ImageLoadUtils.getInstance().clearOnItemClickListener(onItemClickListenerKey);
        ImageLoadUtils.getInstance().clearOnItemLongClickListener(onItemLongClickListenerKey);
        ImageLoadUtils.getInstance().clearMoreViewOption(moreViewOptionKey);
        ImageLoadUtils.getInstance().clearOnBackView(backViewKey);
        ImageLoadUtils.getInstance().clearImageFragmentCreate(imageFragmentCreateKey);
        ImageLoadUtils.getInstance().clearVideoFragmentCreate(videoFragmentCreateKey);
        ImageLoadUtils.getInstance().clearLivePhotoFragmentCreate(livePhotoFragmentCreateKey);
        ImageLoadUtils.getInstance().clearUpperLayerFragmentCreate(upperLayerFragmentCreateKey);
        ImageLoadUtils.getInstance().setOnRemoveListener4FixBug(null);
        ImageLoadUtils.getInstance().clearOnUpdateViewListener(this.toString());
        ImageLoadUtils.getInstance().clearPermissionsInterceptListener(toString());
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
                if (openImageDetail.getType() != MediaType.NONE && (openImageDetail.srcWidth == 0 || openImageDetail.srcHeight == 0)) {
                    openImageDetail.srcWidth = srcWidth;
                    openImageDetail.srcHeight = srcHeight;
                }
            }
        }
//        if (srcVideoWidthCache.size() == 1 || srcVideoHeightCache.size() == 1) {
//            int srcWidth = srcVideoWidthCache.iterator().next();
//            int srcHeight = srcVideoHeightCache.iterator().next();
//            for (OpenImageDetail openImageDetail : openImageDetails) {
//                if (openImageDetail.getType() == MediaType.VIDEO && (openImageDetail.srcWidth == 0 || openImageDetail.srcHeight == 0)) {
//                    openImageDetail.srcWidth = srcWidth;
//                    openImageDetail.srcHeight = srcHeight;
//                }
//            }
//        }
    }

    private void release() {
        if (lifecycleOwner != null) {
            lifecycleOwner.getLifecycle().addObserver(new LifecycleEventObserver() {
                @Override
                public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        source.getLifecycle().removeObserver(this);
                        releaseAllData();
                    }
                }
            });
        }

    }
    void releaseActivity(final Context context) {
        ((Activity) context).getApplication().registerActivityLifecycleCallbacks(new OpenImageActivityLifecycleCallbacks(){
            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                if (context == activity){
                    releaseAllData();
                    activity.getApplication().unregisterActivityLifecycleCallbacks(this);
                }
            }
        });
    }
    void releaseAppFragment(final android.app.Fragment fragment) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ((Activity) fragment.getContext()).getFragmentManager().registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
                @Override
                public void onFragmentDestroyed(FragmentManager fm, android.app.Fragment f) {
                    super.onFragmentDestroyed(fm, f);
                    if (f == fragment){
                        releaseAllData();
                        fm.unregisterFragmentLifecycleCallbacks(this);
                    }
                }
            },true);
        }else {
            isReleaseAllData = true;
        }
    }

    @Override
    void onExit() {
        super.onExit();
        if (isReleaseAllData){
            releaseAllData();
        }
    }

    protected void releaseAllData() {
        context = null;
        lifecycleOwner = null;
        recyclerView = null;
        layoutManagerFindVisiblePosition = null;
        absListView = null;
        viewPager = null;
        viewPager2 = null;
        parentParamsView = null;
        if (imageViews != null) {
            imageViews.clear();
            imageViews = null;
        }
        onPermissionsInterceptListener = null;
        onExitListener = null;
        onUpdateViewListener = null;
        sourceImageViewIdGet = null;
        sourceImageViewGet = null;
        releaseImageLoadUtilMap();

    }
}
