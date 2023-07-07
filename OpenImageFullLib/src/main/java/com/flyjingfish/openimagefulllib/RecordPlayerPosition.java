package com.flyjingfish.openimagefulllib;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import java.util.HashMap;
import java.util.HashSet;

public enum RecordPlayerPosition {
    INSTANCE;
    private final HashMap<String, HashMap<Long,Long>> recordPlayPositionMap = new HashMap<>();
    private final HashSet<String> recordPlayPositionSet = new HashSet<>();

    public void setPlayPosition(Activity activity, long key, long position) {
        String activityKey = activity.toString();
        HashMap<Long,Long> hashMap = recordPlayPositionMap.get(activityKey);
        if (hashMap == null){
            hashMap = new HashMap<>();
            recordPlayPositionMap.put(activityKey,hashMap);
        }
        hashMap.put(key, position);
    }

    public long getPlayPosition(Activity activity, long key) {
        String activityKey = activity.toString();
        HashMap<Long,Long> hashMap = recordPlayPositionMap.get(activityKey);
        Long position = null;
        if (hashMap != null){
            position = hashMap.get(key);
        }
        return position != null ?position:0;
    }

    public void clearRecord(FragmentActivity fragmentActivity){
        String activityKey = fragmentActivity.toString();
        if (recordPlayPositionSet.add(activityKey)){
            fragmentActivity.getLifecycle().addObserver(new LifecycleEventObserver() {
                @Override
                public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                    if (event == Lifecycle.Event.ON_DESTROY){
                        recordPlayPositionSet.remove(activityKey);
                        recordPlayPositionMap.remove(activityKey);
                        source.getLifecycle().removeObserver(this);
                    }
                }
            });
        }
    }
}
