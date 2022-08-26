package com.flyjingfish.openimagelib;

import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.app.SharedElementCallback;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BaseActivity extends AppCompatActivity {
    private static final int CHECK_FINISH = 1009;
    private static final long CHECK_DELAY_MS = 200;
    private Handler closeHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            finishAfterTransition();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fixAndroid5_7Bug();
    }

    private void fixAndroid5_7Bug() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {
            setEnterSharedElementCallback(new SharedElementCallback() {
                @Override
                public void onSharedElementStart(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                    super.onSharedElementStart(sharedElementNames, sharedElements, sharedElementSnapshots);
                    closeHandler.removeMessages(CHECK_FINISH);
                }

                @Override
                public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                    super.onMapSharedElements(names, sharedElements);
                    closeHandler.sendEmptyMessageDelayed(CHECK_FINISH,CHECK_DELAY_MS);
                }
            });
        }
    }

    @Override
    public void finish() {
        OpenImage.isCanOpen = true;
        super.finish();
    }

    @Override
    public void finishAfterTransition() {
        super.finishAfterTransition();
        OpenImage.isCanOpen = true;
    }

    @Override
    protected void onStop() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !isFinishing()) {
            try {
                new Instrumentation().callActivityOnSaveInstanceState(this, new Bundle());
            } catch (Throwable ignored) {
            }
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setEnterSharedElementCallback((SharedElementCallback) null);
        fixSharedAnimMemoryLeaks();
    }

    @SuppressLint("DiscouragedPrivateApi")
    private void fixSharedAnimMemoryLeaks() {
        if (OpenImageConfig.getInstance().isFixSharedAnimMemoryLeaks()){
            try {
                Method method = TransitionManager.class.getDeclaredMethod("getRunningTransitions");
                method.setAccessible(true);
                ArrayMap<ViewGroup, ArrayList<Transition>> map = (ArrayMap<ViewGroup, ArrayList<Transition>>) method.invoke(null);
                View decorView = getWindow().getDecorView();

                if (map != null){
                    ArrayList<Transition> transitions = map.get(decorView);
                    if (transitions != null && transitions.size()>0){
                        for (Transition transition : transitions) {
                            if (transition != null){
                                transition.addListener(new Transition.TransitionListener() {
                                    @Override
                                    public void onTransitionEnd(Transition transition) {
                                        try {
                                            if (transitions.isEmpty()) {
                                                map.remove(decorView);
                                            }
                                            transition.removeListener(this);
                                        } catch (Throwable ignored) {
                                        }
                                    }
                                    @Override
                                    public void onTransitionStart(Transition transition) {}
                                    @Override
                                    public void onTransitionCancel(Transition transition) {
                                        try {
                                            if (transitions.isEmpty()) {
                                                map.remove(decorView);
                                            }
                                            transition.removeListener(this);
                                        } catch (Throwable ignored) {
                                        }
                                    }
                                    @Override
                                    public void onTransitionPause(Transition transition) {}
                                    @Override
                                    public void onTransitionResume(Transition transition) {}
                                });
                            }
                        }
                    }else {
                        map.remove(decorView);
                    }

                }
            } catch (Throwable ignored) {

            }
        }
    }

    protected enum FontStyle {
        LIGHT(1),
        DARK(2),
        FULL_SCREEN(3);
        int value;

        FontStyle(int value) {
            this.value = value;
        }

        public static FontStyle getStyle(int style) {
            if (style == 1) {
                return LIGHT;
            } else if (style == 2) {
                return DARK;
            } else if (style == 3) {
                return FULL_SCREEN;
            } else {
                return DARK;
            }
        }
    }
}
