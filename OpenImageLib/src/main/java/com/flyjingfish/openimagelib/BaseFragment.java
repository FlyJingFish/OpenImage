package com.flyjingfish.openimagelib;

import android.animation.AnimatorSet;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.listener.OnDownloadMediaListener;
import com.flyjingfish.openimagelib.listener.OnItemClickListener;
import com.flyjingfish.openimagelib.listener.OnItemLongClickListener;
import com.flyjingfish.shapeimageviewlib.ShapeImageView;

abstract class BaseFragment extends BaseInnerFragment {

    protected OpenImageDetail imageDetail;
    protected OpenImageUrl openImageUrl;
    protected int showPosition,clickPosition;
    protected PhotosViewModel photosViewModel;
    protected Handler mHandler = new Handler(Looper.getMainLooper());
    protected boolean isLoadSuccess;
    protected boolean disableClickClose;
    protected int errorResId;
    protected Drawable coverDrawable;
    protected String coverFilePath;
    protected Drawable smallCoverDrawable;
    protected boolean isNoneClickView;
    private String dataKey;

    public abstract View getExitImageView();
    protected void onTransitionEnd(){}
    protected boolean isTransitionEnd;
    protected boolean isInitImage;
    protected boolean isLoading;
    protected boolean isStartCoverAnim = true;
    protected ShapeImageView.ShapeScaleType srcScaleType;
    protected AnimatorSet coverAnim;
//    protected ItemLoadHelper itemLoadHelper;
    protected float autoAspectRadio;
    protected long beanId;
    protected int preloadCount;
    protected boolean lazyPreload;
    protected boolean bothLoadCover;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null){
            dataKey = bundle.getString(OpenParams.IMAGE);
            imageDetail = ImageLoadUtils.getInstance().getOpenImageDetail(dataKey);
            openImageUrl = imageDetail.openImageUrl;
            showPosition = bundle.getInt(OpenParams.SHOW_POSITION);
            clickPosition = bundle.getInt(OpenParams.CLICK_POSITION);
            int srcScaleTypeInt = bundle.getInt(OpenParams.SRC_SCALE_TYPE,-1);
            srcScaleType = srcScaleTypeInt == -1 ? null : ShapeImageView.ShapeScaleType.values()[srcScaleTypeInt];;
            errorResId = bundle.getInt(OpenParams.ERROR_RES_ID,0);
            disableClickClose = getArguments().getBoolean(OpenParams.DISABLE_CLICK_CLOSE,false);
            String onItemCLickKey = getArguments().getString(OpenParams.ON_ITEM_CLICK_KEY);
            String onItemLongCLickKey = getArguments().getString(OpenParams.ON_ITEM_LONG_CLICK_KEY);
            OnItemClickListener onItemClickListener = ImageLoadUtils.getInstance().getOnItemClickListener(onItemCLickKey);
            OnItemLongClickListener onItemLongClickListener = ImageLoadUtils.getInstance().getOnItemLongClickListener(onItemLongCLickKey);
            if (onItemClickListener != null){
                onItemClickListeners.add(onItemClickListener);
            }
            if (onItemLongClickListener != null){
                onItemLongClickListeners.add(onItemLongClickListener);
            }
            String drawableKey = openImageUrl.toString();
            coverDrawable = ImageLoadUtils.getInstance().getCoverDrawable(drawableKey);
            coverFilePath = ImageLoadUtils.getInstance().getCoverFilePath(bundle.getString(OpenParams.OPEN_COVER_DRAWABLE));
            smallCoverDrawable = ImageLoadUtils.getInstance().getSmallCoverDrawable(drawableKey);
            ImageLoadUtils.getInstance().clearSmallCoverDrawable(drawableKey);

            autoAspectRadio = bundle.getFloat(OpenParams.AUTO_ASPECT_RATIO,0);
            isNoneClickView = bundle.getBoolean(OpenParams.NONE_CLICK_VIEW,false);
            preloadCount = bundle.getInt(OpenParams.PRELOAD_COUNT,1);
            lazyPreload = bundle.getBoolean(OpenParams.LAZY_PRELOAD, false);
            bothLoadCover = bundle.getBoolean(OpenParams.BOTH_LOAD_COVER, false);
            beanId = imageDetail.getId();
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        photosViewModel = new ViewModelProvider(requireActivity()).get(PhotosViewModel.class);
        photosViewModel.transitionEndLiveData.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean){
                isTransitionEnd = true;
                onTransitionEnd();
            }
        });
    }

    protected void setTransitionEndListener(@NonNull Observer<Boolean> observer){
        photosViewModel.transitionEndLiveData.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                photosViewModel.transitionEndLiveData.removeObserver(this);
                observer.onChanged(aBoolean);
            }
        });
    }

    /**
     * 检测权限并下载当前页面的图片或视频
     * @param onDownloadMediaListener 下载监听
     */
    protected void downloadCurrentMedia(OnDownloadMediaListener onDownloadMediaListener) {
        downloadMedia(openImageUrl,onDownloadMediaListener);
    }

    /**
     * 检测权限并下载当前页面的图片或视频
     * @param onDownloadMediaListener 下载监听
     * @param requestWriteExternalStoragePermissionsFail 请求存储权限失败后 Toast 的文案，如果为null 或 “” 则不显示
     */
    protected void checkPermissionAndDownloadCurrent(OnDownloadMediaListener onDownloadMediaListener, @Nullable String requestWriteExternalStoragePermissionsFail) {
        checkPermissionAndDownload(openImageUrl,onDownloadMediaListener,requestWriteExternalStoragePermissionsFail);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (coverAnim != null){
            coverAnim.removeAllListeners();
            coverAnim.cancel();
        }
        coverDrawable = null;
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ImageLoadUtils.getInstance().clearOpenImageDetail(dataKey);
    }
}
