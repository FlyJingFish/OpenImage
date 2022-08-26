package com.flyjingfish.openimagelib;

import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.app.SharedElementCallback;
import android.os.Build;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class BaseActivity extends AppCompatActivity {
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
