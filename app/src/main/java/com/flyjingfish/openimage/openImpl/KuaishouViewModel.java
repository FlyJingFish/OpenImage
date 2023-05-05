package com.flyjingfish.openimage.openImpl;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class KuaishouViewModel extends AndroidViewModel {
    MutableLiveData<Boolean> clickLikeLiveData = new MutableLiveData<>();
    MutableLiveData<Float> btnsTranslationYLiveData = new MutableLiveData<>();
    MutableLiveData<Float> btnsAlphaLiveData = new MutableLiveData<>();
    public KuaishouViewModel(@NonNull Application application) {
        super(application);
    }


}
