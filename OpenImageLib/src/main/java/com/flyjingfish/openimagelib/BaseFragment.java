package com.flyjingfish.openimagelib;

import android.animation.AnimatorSet;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.flyjingfish.openimagelib.beans.OpenImageDetail;
import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.enums.ImageDiskMode;
import com.flyjingfish.openimagelib.listener.ItemLoadHelper;

public abstract class BaseFragment extends Fragment {

    protected OpenImageDetail openImageBean;
    protected OpenImageUrl openImageUrl;
    protected int showPosition,clickPosition;
    protected PhotosViewModel photosViewModel;
    protected Handler mHandler = new Handler(Looper.getMainLooper());
    protected boolean isLoadSuccess;
    protected ImageDiskMode imageDiskMode;
    protected int errorResId;

    public abstract View getExitImageView();
    protected void onTransitionEnd(){}
    protected boolean isTransitionEnd;
    protected boolean isInitImage;
    protected boolean isLoading;
    protected boolean isStartCoverAnim = true;
    protected ImageView.ScaleType srcScaleType;
    protected AnimatorSet coverAnim;
    protected ItemLoadHelper itemLoadHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        openImageBean = (OpenImageDetail) bundle.getSerializable(OpenParams.IMAGE);
        openImageUrl = openImageBean.openImageUrl;
        imageDiskMode = (ImageDiskMode) bundle.getSerializable(OpenParams.IMAGE_DISK_MODE);
        if (imageDiskMode == null){
            imageDiskMode = ImageDiskMode.NONE;
        }
        showPosition = bundle.getInt(OpenParams.SHOW_POSITION);
        clickPosition = bundle.getInt(OpenParams.CLICK_POSITION);
        srcScaleType = (ImageView.ScaleType) bundle.getSerializable(OpenParams.SRC_SCALE_TYPE);
        errorResId = bundle.getInt(OpenParams.ERROR_RES_ID,0);
        String itemLoadKey = bundle.getString(OpenParams.ITEM_LOAD_KEY);
        itemLoadHelper = ImageLoadUtils.getInstance().getItemLoadHelper(itemLoadKey);
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
        photosViewModel.onTouchCloseLiveData.observe(getViewLifecycleOwner(), aFloat -> onTouchClose(aFloat));
        photosViewModel.onTouchScaleLiveData.observe(getViewLifecycleOwner(), aFloat -> onTouchScale(aFloat));
    }

    protected void onTouchClose(float scale){

    }
    protected void onTouchScale(float scale){

    }

    protected void close() {
        photosViewModel.closeViewLiveData.setValue(1);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (coverAnim != null){
            coverAnim.removeAllListeners();
            coverAnim.cancel();
        }
        itemLoadHelper = null;
        mHandler.removeCallbacksAndMessages(null);
    }
}
