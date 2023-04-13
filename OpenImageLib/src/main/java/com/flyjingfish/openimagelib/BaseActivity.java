package com.flyjingfish.openimagelib;

import android.animation.ObjectAnimator;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.flyjingfish.openimagelib.beans.OpenImageDetail;
import com.flyjingfish.openimagelib.databinding.OpenImageIndicatorTextBinding;
import com.flyjingfish.openimagelib.enums.OpenImageOrientation;
import com.flyjingfish.openimagelib.listener.OnSelectMediaListener;
import com.flyjingfish.shapeimageviewlib.ShapeImageView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class BaseActivity extends AppCompatActivity {
    private static final int CHECK_FINISH = 1009;
    private static final long CHECK_DELAY_MS = 2000;
    protected static final int INDICATOR_TEXT = 0;
    protected static final int INDICATOR_IMAGE = 1;
    protected static final long WECHAT_DURATION = 250;
    protected static final long WECHAT_DURATION_END_ALPHA = 50;
    protected int indicatorType;
    protected int showPosition;
    protected int selectPos;
    protected boolean isCallClosed;
    protected String textFormat = "%1$d/%2$d";
    protected String onSelectKey;
    protected String openCoverKey;
    protected String pageTransformersKey;
    protected String onItemCLickKey;
    protected String onItemLongCLickKey;
    protected String moreViewKey;
    protected String onBackViewKey;
    protected String videoFragmentCreateKey;
    protected String imageFragmentCreateKey;
    protected String upperLayerFragmentCreateKey;
    protected final List<OnSelectMediaListener> onSelectMediaListeners = new ArrayList<>();
    protected final List<String> onSelectMediaListenerKeys = new ArrayList<>();
    protected final List<MoreViewOption> moreViewOptions = new ArrayList<>();
    protected final Handler mHandler = new Handler(Looper.getMainLooper());
    protected List<OpenImageDetail> openImageBeans;
    protected PhotosViewModel photosViewModel;
    protected OpenImageIndicatorTextBinding indicatorTextBinding;
    protected ImageIndicatorAdapter imageIndicatorAdapter;
    protected OpenImageOrientation orientation;
//    protected ImageDiskMode imageDiskMode;
    protected ShapeImageView.ShapeScaleType srcScaleType;
    protected OnSelectMediaListener onSelectMediaListener;
    protected ObjectAnimator wechatEffectAnim;
    protected LinearLayoutManager imageIndicatorLayoutManager;
    protected ImageLoadUtils.OnBackView onBackView;
    protected FontStyle fontStyle;
    protected BaseInnerFragment upLayerFragment;
    protected UpperLayerOption upperLayerOption;
    protected final Handler closeHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == CHECK_FINISH) {
                finishAfterTransition();
                fixAndroid5_7BugForRemoveListener();
            }

        }
    };
    private String clickContextKey;
    private boolean isNoneClickView;
    private String dataKey;
    protected String contextKey;
    protected float touchCloseScale = 1f;
    protected ImageShapeParams imageShapeParams;

    public boolean isNoneClickView() {
        return isNoneClickView;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contextKey = this.toString();
        fixAndroid5_7Bug();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fixAndroid5_7BugForRemoveListener();
    }

    private void fixAndroid5_7BugForRemoveListener() {
        if (!isNoneClickView && Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {
            ImageLoadUtils.getInstance().notifyOnRemoveListener4FixBug();
        }
    }
    private void fixAndroid5_7Bug() {
        if (!isNoneClickView && Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {
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
    public void finish() {
        super.finish();
        ImageLoadUtils.getInstance().setCanOpenOpenImageActivity(clickContextKey,true);
    }

    @Override
    public void finishAfterTransition() {
        super.finishAfterTransition();
        ImageLoadUtils.getInstance().setCanOpenOpenImageActivity(clickContextKey,true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setEnterSharedElementCallback((SharedElementCallback) null);
        fixSharedAnimMemoryLeaks();
        ImageLoadUtils.getInstance().clearOpenImageDetailData(dataKey);
        ImageLoadUtils.getInstance().clearOpenImageDetail(contextKey);
    }

    @SuppressLint("DiscouragedPrivateApi")
    private void fixSharedAnimMemoryLeaks() {
        if (!isNoneClickView && OpenImageConfig.getInstance().isFixSharedAnimMemoryLeaks()){
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

    protected void parseIntent(){

        int srcScaleTypeInt = getIntent().getIntExtra(OpenParams.SRC_SCALE_TYPE,-1);
        srcScaleType = srcScaleTypeInt == -1 ? null : ShapeImageView.ShapeScaleType.values()[srcScaleTypeInt];
        dataKey = getIntent().getStringExtra(OpenParams.IMAGES);
        openImageBeans = ImageLoadUtils.getInstance().getOpenImageDetailData(dataKey);
        if (openImageBeans == null){
            finishAfterTransition();
            return;
        }
        int clickPosition = getIntent().getIntExtra(OpenParams.CLICK_POSITION, 0);

        selectPos = 0;
        for (int i = 0; i < openImageBeans.size(); i++) {
            OpenImageDetail openImageBean = openImageBeans.get(i);
            if (openImageBean.dataPosition == clickPosition) {
                selectPos = i;
                break;
            }
        }
        showPosition = selectPos;
        onSelectKey = getIntent().getStringExtra(OpenParams.ON_SELECT_KEY);
        openCoverKey = getIntent().getStringExtra(OpenParams.OPEN_COVER_DRAWABLE);
        onSelectMediaListener = ImageLoadUtils.getInstance().getOnSelectMediaListener(onSelectKey);
        imageFragmentCreateKey = getIntent().getStringExtra(OpenParams.IMAGE_FRAGMENT_KEY);
        videoFragmentCreateKey = getIntent().getStringExtra(OpenParams.VIDEO_FRAGMENT_KEY);
        upperLayerFragmentCreateKey = getIntent().getStringExtra(OpenParams.UPPER_LAYER_FRAGMENT_KEY);
        upperLayerOption = ImageLoadUtils.getInstance().getUpperLayerFragmentCreate(upperLayerFragmentCreateKey);

        onItemCLickKey = getIntent().getStringExtra(OpenParams.ON_ITEM_CLICK_KEY);
        onItemLongCLickKey = getIntent().getStringExtra(OpenParams.ON_ITEM_LONG_CLICK_KEY);
        moreViewKey = getIntent().getStringExtra(OpenParams.MORE_VIEW_KEY);
        onBackViewKey = getIntent().getStringExtra(OpenParams.ON_BACK_VIEW);
        onBackView = ImageLoadUtils.getInstance().getOnBackView(onBackViewKey);
        clickContextKey = getIntent().getStringExtra(OpenParams.CONTEXT_KEY);
        isNoneClickView = getIntent().getBooleanExtra(OpenParams.NONE_CLICK_VIEW,false);
        imageShapeParams = (ImageShapeParams) getIntent().getParcelableExtra(OpenParams.IMAGE_SHAPE_PARAMS);
    }
}
