package com.flyjingfish.openimagelib;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class OpenActivityDataViewModel extends AndroidViewModel {
    public MutableLiveData<OpenActivityData> openDataMutableLiveData = new MutableLiveData<>();
    public OpenActivityDataViewModel(@NonNull Application application) {
        super(application);
    }

}
