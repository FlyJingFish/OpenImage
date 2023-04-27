package com.flyjingfish.openimagelib;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.flyjingfish.openimagelib.listener.OnItemClickListener;
import com.flyjingfish.openimagelib.listener.OnItemLongClickListener;
import com.flyjingfish.openimagelib.listener.OnSelectMediaListener;

import java.util.ArrayList;
import java.util.List;

public class BaseInnerFragment extends Fragment {

    private PhotosViewModel basePhotosViewModel;
    protected List<OnItemClickListener> onItemClickListeners = new ArrayList<>();
    protected List<OnItemLongClickListener> onItemLongClickListeners = new ArrayList<>();
    private final List<String> onItemClickListenerKeys = new ArrayList<>();
    private final List<String> onItemLongClickListenerKeys = new ArrayList<>();
    protected float currentScale = 1f;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        basePhotosViewModel = new ViewModelProvider(requireActivity()).get(PhotosViewModel.class);
        basePhotosViewModel.onAddItemListenerLiveData.observe(getViewLifecycleOwner(), s -> {
            OnItemClickListener onItemClickListener = ImageLoadUtils.getInstance().getOnItemClickListener(s);
            if (onItemClickListener != null){
                onItemClickListeners.add(onItemClickListener);
            }
        });

        basePhotosViewModel.onAddItemLongListenerLiveData.observe(getViewLifecycleOwner(), s -> {
            OnItemLongClickListener onItemLongClickListener = ImageLoadUtils.getInstance().getOnItemLongClickListener(s);
            if (onItemLongClickListener != null){
                onItemLongClickListeners.add(onItemLongClickListener);
            }
        });

        basePhotosViewModel.onRemoveItemListenerLiveData.observe(getViewLifecycleOwner(), s -> {
            OnItemClickListener onItemClickListener = ImageLoadUtils.getInstance().getOnItemClickListener(s);
            if (onItemClickListener != null){
                onItemClickListeners.remove(onItemClickListener);
            }
            ImageLoadUtils.getInstance().clearOnItemClickListener(s);
        });

        basePhotosViewModel.onRemoveItemLongListenerLiveData.observe(getViewLifecycleOwner(), s -> {
            OnItemLongClickListener onItemLongClickListener = ImageLoadUtils.getInstance().getOnItemLongClickListener(s);
            if (onItemLongClickListener != null){
                onItemLongClickListeners.remove(onItemLongClickListener);
            }
            ImageLoadUtils.getInstance().clearOnItemLongClickListener(s);
        });

        basePhotosViewModel.onTouchCloseLiveData.observe(getViewLifecycleOwner(), aFloat -> onTouchClose(aFloat));
        basePhotosViewModel.onTouchScaleLiveData.observe(getViewLifecycleOwner(), aFloat -> onTouchScale(aFloat));
    }

    /**
     * 触摸拖动关闭时回调此方法
     * @param scale 图片缩放比例
     */
    protected void onTouchClose(float scale){
        currentScale = scale;
    }
    /**
     * 触摸拖动图片时回调此方法
     * @param scale 图片缩放比例
     */
    protected void onTouchScale(float scale){
        currentScale = scale;
    }

    protected void addOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener){
        try {
            if (onItemLongClickListener != null){
                ImageLoadUtils.getInstance().setOnItemLongClickListener(onItemLongClickListener.toString(),onItemLongClickListener);
                basePhotosViewModel.onAddItemLongListenerLiveData.setValue(onItemLongClickListener.toString());
                onItemLongClickListenerKeys.add(onItemLongClickListener.toString());
            }
        } catch (Exception e) {
            hintRuntimeException();
        }
    }

    protected void addOnItemClickListener(OnItemClickListener onItemClickListener){
        try {
            if (onItemClickListener != null){
                ImageLoadUtils.getInstance().setOnItemClickListener(onItemClickListener.toString(),onItemClickListener);
                basePhotosViewModel.onAddItemListenerLiveData.setValue(onItemClickListener.toString());
                onItemClickListenerKeys.add(onItemClickListener.toString());
            }
        } catch (Exception e) {
            hintRuntimeException();
        }
    }

    protected void removeOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener){
        try {
            if (onItemLongClickListener != null){
                basePhotosViewModel.onRemoveItemLongListenerLiveData.setValue(onItemLongClickListener.toString());
            }
        } catch (Exception e) {
            hintRuntimeException();
        }
    }

    protected void removeOnItemClickListener(OnItemClickListener onItemClickListener){
        try {
            if (onItemClickListener != null){
                basePhotosViewModel.onRemoveItemListenerLiveData.setValue(onItemClickListener.toString());
            }
        } catch (Exception e) {
            hintRuntimeException();
        }
    }


    protected void addOnSelectMediaListener(OnSelectMediaListener onSelectMediaListener){
        try {
            if (onSelectMediaListener != null){
                ImageLoadUtils.getInstance().setOnSelectMediaListener(onSelectMediaListener.toString(),onSelectMediaListener);
                basePhotosViewModel.onAddOnSelectMediaListenerLiveData.setValue(onSelectMediaListener.toString());
            }
        } catch (Exception e) {
            hintRuntimeException();
        }
    }

    protected void removeOnSelectMediaListener(OnSelectMediaListener onSelectMediaListener){
        try {
            if (onSelectMediaListener != null){
                basePhotosViewModel.onRemoveOnSelectMediaListenerLiveData.setValue(onSelectMediaListener.toString());
            }
        } catch (Exception e) {
            hintRuntimeException();
        }
    }

    /**
     * 关闭页面
     */
    public void close() {
        close(false);
    }

    private void close(boolean isTouchClose) {
        try {
            basePhotosViewModel.closeViewLiveData.setValue(isTouchClose);
        } catch (Exception e) {
            hintRuntimeException();
        }
    }

    private void hintRuntimeException(){
        if (ImageLoadUtils.getInstance().isApkInDebug()){
            throw new RuntimeException("请确保你是在 onViewCreated 及其之后的生命周期中调用的此方法");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        onItemClickListeners.clear();
        onItemLongClickListeners.clear();
        for (String onItemLongClickListenerKey : onItemLongClickListenerKeys) {
            ImageLoadUtils.getInstance().clearOnItemLongClickListener(onItemLongClickListenerKey);
        }

        for (String onItemClickListenerKey : onItemClickListenerKeys) {
            ImageLoadUtils.getInstance().clearOnItemClickListener(onItemClickListenerKey);
        }
        onItemClickListenerKeys.clear();
        onItemLongClickListenerKeys.clear();
    }

    public boolean onKeyBackDown(){
        return true;
    }
}
