package com.flyjingfish.openimagelib;

import android.app.Application;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.HashMap;

public class PhotosViewModel extends AndroidViewModel {
    MutableLiveData<Integer> closeViewLiveData = new MutableLiveData<>();
    MutableLiveData<Boolean> transitionEndLiveData = new MutableLiveData<>();
    MutableLiveData<Float> onTouchScaleLiveData = new MutableLiveData<>();
    MutableLiveData<Float> onTouchCloseLiveData = new MutableLiveData<>();

    public PhotosViewModel(@NonNull Application application) {
        super(application);
    }

}
