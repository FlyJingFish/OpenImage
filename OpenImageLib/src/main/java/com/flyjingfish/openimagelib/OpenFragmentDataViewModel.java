package com.flyjingfish.openimagelib;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class OpenFragmentDataViewModel extends AndroidViewModel {
    public MutableLiveData<OpenFragmentData> openDataMutableLiveData = new MutableLiveData<>();
    public OpenFragmentDataViewModel(@NonNull Application application) {
        super(application);
    }

}
