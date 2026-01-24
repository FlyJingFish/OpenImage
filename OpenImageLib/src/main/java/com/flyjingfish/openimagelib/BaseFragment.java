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
    OpenImageDetail imageDetail;
    @Nullable
    protected OpenImageUrl openImageUrl;
    int showPosition,clickPosition;
    PhotosViewModel photosViewModel;
    protected Handler mHandler = new Handler(Looper.getMainLooper());
    boolean isLoadSuccess;
    boolean disableClickClose;
    int errorResId;
    Drawable coverDrawable;
    String coverFilePath;
    Drawable smallCoverDrawable;
    boolean isNoneClickView;
    private String dataKey;

    public abstract View getExitImageView();
    protected void onTransitionEnd(){}
    protected boolean isTransitionEnd;
    boolean isInitImage;
    boolean isLoading;
    boolean isStartCoverAnim = true;
    ShapeImageView.ShapeScaleType srcScaleType;
    AnimatorSet coverAnim;
//    protected ItemLoadHelper itemLoadHelper;
    float autoAspectRadio;
    protected long beanId;
    int preloadCount;
    protected boolean lazyPreload;
    boolean bothLoadCover;
    protected String openLive;
    protected String closeLive;
    protected String live;
    protected String replay;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OpenFragmentDataViewModel openFragmentDataViewModel = new ViewModelProvider(this).get(OpenFragmentDataViewModel.class);
        OpenFragmentData openFragmentData = openFragmentDataViewModel.openDataMutableLiveData.getValue();
        if (openFragmentData == null){
            openFragmentData = new OpenFragmentData();
            openFragmentData.setFragment(this);
            if (openFragmentData.parseIntent()){
                return;
            }
            openFragmentData.setFragment(null);
            openFragmentDataViewModel.openDataMutableLiveData.setValue(openFragmentData);
        }
        dataKey = openFragmentData.dataKey;
        imageDetail = openFragmentData.imageDetail;
        openImageUrl = openFragmentData.openImageUrl;
        showPosition = openFragmentData.showPosition;
        clickPosition = openFragmentData.clickPosition;
        srcScaleType = openFragmentData.srcScaleType;
        errorResId = openFragmentData.errorResId;
        disableClickClose = openFragmentData.disableClickClose;
        onItemClickListeners.addAll(openFragmentData.onItemClickListeners);
        onItemLongClickListeners.addAll(openFragmentData.onItemLongClickListeners);
        coverDrawable = openFragmentData.coverDrawable;
        coverFilePath = openFragmentData.coverFilePath;
        smallCoverDrawable = openFragmentData.smallCoverDrawable;

        autoAspectRadio = openFragmentData.autoAspectRadio;
        isNoneClickView = openFragmentData.isNoneClickView;
        preloadCount = openFragmentData.preloadCount;
        lazyPreload = openFragmentData.lazyPreload;
        bothLoadCover = openFragmentData.bothLoadCover;
        beanId = openFragmentData.beanId;
        openLive = openFragmentData.openLive;
        closeLive = openFragmentData.closeLive;
        live = openFragmentData.live;
        replay = openFragmentData.replay;
    }

    protected int getShowPosition() {
        return showPosition;
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

    boolean isOpenPosition(){
        return showPosition == clickPosition;
    }

    boolean isInOpening(){
        return isOpenPosition() && !isTransitionEnd;
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
    }
}
