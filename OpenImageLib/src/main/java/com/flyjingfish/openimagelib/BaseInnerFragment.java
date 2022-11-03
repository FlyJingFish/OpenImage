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
    private List<String> onItemClickListenerKeys = new ArrayList<>();
    private List<String> onItemLongClickListenerKeys = new ArrayList<>();

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
    }

    protected void addOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener){
        if (onItemLongClickListener != null){
            ImageLoadUtils.getInstance().setOnItemLongClickListener(onItemLongClickListener.toString(),onItemLongClickListener);
            basePhotosViewModel.onAddItemLongListenerLiveData.setValue(onItemLongClickListener.toString());
            onItemLongClickListenerKeys.add(onItemLongClickListener.toString());
        }
    }

    protected void addOnItemClickListener(OnItemClickListener onItemClickListener){
        if (onItemClickListener != null){
            ImageLoadUtils.getInstance().setOnItemClickListener(onItemClickListener.toString(),onItemClickListener);
            basePhotosViewModel.onAddItemListenerLiveData.setValue(onItemClickListener.toString());
            onItemClickListenerKeys.add(onItemClickListener.toString());
        }
    }

    protected void removeOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener){
        if (onItemLongClickListener != null){
            basePhotosViewModel.onRemoveItemLongListenerLiveData.setValue(onItemLongClickListener.toString());
        }
    }

    protected void removeOnItemClickListener(OnItemClickListener onItemClickListener){
        if (onItemClickListener != null){
            basePhotosViewModel.onRemoveItemListenerLiveData.setValue(onItemClickListener.toString());
        }
    }


    protected void addOnSelectMediaListener(OnSelectMediaListener onSelectMediaListener){
        if (onSelectMediaListener != null){
            ImageLoadUtils.getInstance().setOnSelectMediaListener(onSelectMediaListener.toString(),onSelectMediaListener);
            basePhotosViewModel.onAddOnSelectMediaListenerLiveData.setValue(onSelectMediaListener.toString());
        }
    }

    protected void removeOnSelectMediaListener(OnSelectMediaListener onSelectMediaListener){
        if (onSelectMediaListener != null){
            basePhotosViewModel.onRemoveOnSelectMediaListenerLiveData.setValue(onSelectMediaListener.toString());
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
}
