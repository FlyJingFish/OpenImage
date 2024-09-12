package com.flyjingfish.openimagelib;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.flyjingfish.openimagelib.live_data.UnPeekLiveData;


public class PhotosViewModel extends AndroidViewModel {
    UnPeekLiveData<Boolean> closeViewLiveData = new UnPeekLiveData<>();
    public UnPeekLiveData<Boolean> transitionEndLiveData = new UnPeekLiveData<>();
    UnPeekLiveData<Float> onTouchScaleLiveData = new UnPeekLiveData<>();
    UnPeekLiveData<Float> onTouchCloseLiveData = new UnPeekLiveData<>();
    UnPeekLiveData<String> onAddItemListenerLiveData = new UnPeekLiveData<>();
    UnPeekLiveData<String> onAddItemLongListenerLiveData = new UnPeekLiveData<>();
    UnPeekLiveData<String> onRemoveItemListenerLiveData = new UnPeekLiveData<>();
    UnPeekLiveData<String> onRemoveItemLongListenerLiveData = new UnPeekLiveData<>();
    UnPeekLiveData<String> onAddOnSelectMediaListenerLiveData = new UnPeekLiveData<>();
    UnPeekLiveData<String> onRemoveOnSelectMediaListenerLiveData = new UnPeekLiveData<>();
    public UnPeekLiveData<Boolean> onCanLayoutLiveData = new UnPeekLiveData<>();

    public PhotosViewModel(@NonNull Application application) {
        super(application);
    }

}
