package com.flyjingfish.openimagelib;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.flyjingfish.openimagelib.live_data.UnPeekLiveData;

public class PhotosViewModel extends AndroidViewModel {
    MutableLiveData<Boolean> closeViewLiveData = new MutableLiveData<>();
    public MutableLiveData<Boolean> transitionEndLiveData = new MutableLiveData<>();
    MutableLiveData<Float> onTouchScaleLiveData = new MutableLiveData<>();
    MutableLiveData<Float> onTouchCloseLiveData = new MutableLiveData<>();
    MutableLiveData<String> onAddItemListenerLiveData = new MutableLiveData<>();
    MutableLiveData<String> onAddItemLongListenerLiveData = new MutableLiveData<>();
    MutableLiveData<String> onRemoveItemListenerLiveData = new MutableLiveData<>();
    MutableLiveData<String> onRemoveItemLongListenerLiveData = new MutableLiveData<>();
    MutableLiveData<String> onAddOnSelectMediaListenerLiveData = new MutableLiveData<>();
    MutableLiveData<String> onRemoveOnSelectMediaListenerLiveData = new MutableLiveData<>();
    public MutableLiveData<Boolean> onCanLayoutLiveData = new MutableLiveData<>();

    public PhotosViewModel(@NonNull Application application) {
        super(application);
    }

}
