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
import android.text.TextUtils;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.flyjingfish.openimagelib.databinding.OpenImageIndicatorTextBinding;
import com.flyjingfish.openimagelib.enums.MoreViewShowType;
import com.flyjingfish.openimagelib.enums.OpenImageOrientation;
import com.flyjingfish.openimagelib.listener.OnItemClickListener;
import com.flyjingfish.openimagelib.listener.OnItemLongClickListener;
import com.flyjingfish.openimagelib.listener.OnPermissionsInterceptListener;
import com.flyjingfish.openimagelib.listener.OnSelectMediaListener;
import com.flyjingfish.shapeimageviewlib.ShapeImageView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class BaseActivity extends AppCompatActivity {
    private static final int CHECK_FINISH = 1009;
    private static final long CHECK_DELAY_MS = 2000;
    private List<OpenImageDetail> openImageBeans;
    private String clickContextKey;
    private boolean isNoneClickView;
    private String dataKey;
    private final List<String> onItemClickListenerKeys = new ArrayList<>();
    private final List<String> onItemLongClickListenerKeys = new ArrayList<>();
    static final int INDICATOR_TEXT = 0;
    static final int INDICATOR_IMAGE = 1;
    static final long WECHAT_DURATION = 250;
    static final long WECHAT_DURATION_END_ALPHA = 50;
    int indicatorType;
    int showPosition;
    int selectPos;
    String onSelectKey;
    String onPermissionKey;
    String openCoverKey;
    String pageTransformersKey;
    String onItemCLickKey;
    String onItemLongCLickKey;
    String moreViewKey;
    String onBackViewKey;
    String videoFragmentCreateKey;
    String imageFragmentCreateKey;
    String upperLayerFragmentCreateKey;
    final List<OnSelectMediaListener> onSelectMediaListeners = new ArrayList<>();
    final List<String> onSelectMediaListenerKeys = new ArrayList<>();
    final List<MoreViewOption> moreViewOptions = new ArrayList<>();
    final Handler mHandler = new Handler(Looper.getMainLooper());
    PhotosViewModel photosViewModel;
    OnSelectMediaListener onSelectMediaListener;
    ObjectAnimator wechatEffectAnim;
    ImageLoadUtils.OnBackView onBackView;
    final Handler closeHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == CHECK_FINISH) {
                finishAfterTransition();
                fixAndroid5_7BugForRemoveListener();
            }

        }
    };
    String contextKey;
    float touchCloseScale = 1f;
    ImageShapeParams imageShapeParams;
    boolean wechatExitFillInEffect;

    protected String textFormat = "%1$d/%2$d";
    protected OpenImageIndicatorTextBinding indicatorTextBinding;
    protected ImageIndicatorAdapter imageIndicatorAdapter;
    protected OpenImageOrientation orientation;
    protected OpenImageOrientation touchCloseOrientation;
    //    protected ImageDiskMode imageDiskMode;
    protected ShapeImageView.ShapeScaleType srcScaleType;
    protected LinearLayoutManager imageIndicatorLayoutManager;
    protected FontStyle fontStyle;
    protected BaseInnerFragment upLayerFragment;
    protected UpperLayerOption upperLayerOption;
    protected OpenImageFragmentStateAdapter openImageAdapter;
    boolean indicatorTouchingHide = true;
    View indicatorView;
    protected boolean downloadToast = true;
    protected String startToast;
    protected String successToast;
    protected String errorToast;
    protected String requestWriteExternalStoragePermissionsFail;

    String downloadParamsKey;
    String closeParamsKey;

    boolean downloadTouchingHide = true;
    boolean closeTouchingHide = true;
    MoreViewShowType downloadShowType = MoreViewShowType.BOTH;
    MoreViewShowType closeShowType = MoreViewShowType.IMAGE;

    int themeRes;

    int preloadCount;
    boolean lazyPreload;

    List<OpenImageDetail> getOpenImageBeans() {
        return openImageBeans;
    }

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
                    closeHandler.sendEmptyMessageDelayed(CHECK_FINISH, CHECK_DELAY_MS);
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
        ImageLoadUtils.getInstance().setCanOpenOpenImageActivity(clickContextKey, true);
    }

    @Override
    public void finishAfterTransition() {
        super.finishAfterTransition();
        ImageLoadUtils.getInstance().setCanOpenOpenImageActivity(clickContextKey, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setEnterSharedElementCallback((SharedElementCallback) null);
        fixSharedAnimMemoryLeaks();
        ImageLoadUtils.getInstance().clearOpenImageDetailData(dataKey);
        ImageLoadUtils.getInstance().clearOpenImageDetail(contextKey);
        for (String onItemLongClickListenerKey : onItemLongClickListenerKeys) {
            ImageLoadUtils.getInstance().clearOnItemLongClickListener(onItemLongClickListenerKey);
        }

        for (String onItemClickListenerKey : onItemClickListenerKeys) {
            ImageLoadUtils.getInstance().clearOnItemClickListener(onItemClickListenerKey);
        }
        onItemClickListenerKeys.clear();
        onItemLongClickListenerKeys.clear();
        if (!TextUtils.isEmpty(downloadParamsKey)){
            ImageLoadUtils.getInstance().clearDownloadParams(downloadParamsKey);
        }
        if (!TextUtils.isEmpty(closeParamsKey)){
            ImageLoadUtils.getInstance().clearCloseParams(closeParamsKey);
        }
    }

    @SuppressLint("DiscouragedPrivateApi")
    private void fixSharedAnimMemoryLeaks() {
        if (!isNoneClickView && OpenImageConfig.getInstance().isFixSharedAnimMemoryLeaks()) {
            try {
                Method method = TransitionManager.class.getDeclaredMethod("getRunningTransitions");
                method.setAccessible(true);
                ArrayMap<ViewGroup, ArrayList<Transition>> map = (ArrayMap<ViewGroup, ArrayList<Transition>>) method.invoke(null);
                View decorView = getWindow().getDecorView();

                if (map != null) {
                    ArrayList<Transition> transitions = map.get(decorView);
                    if (transitions != null && transitions.size() > 0) {
                        for (Transition transition : transitions) {
                            if (transition != null) {
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
                                    public void onTransitionStart(Transition transition) {
                                    }

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
                                    public void onTransitionPause(Transition transition) {
                                    }

                                    @Override
                                    public void onTransitionResume(Transition transition) {
                                    }
                                });
                            }
                        }
                    } else {
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

    protected void parseIntent() {

        int srcScaleTypeInt = getIntent().getIntExtra(OpenParams.SRC_SCALE_TYPE, -1);
        srcScaleType = srcScaleTypeInt == -1 ? null : ShapeImageView.ShapeScaleType.values()[srcScaleTypeInt];
        dataKey = getIntent().getStringExtra(OpenParams.IMAGES);
        openImageBeans = ImageLoadUtils.getInstance().getOpenImageDetailData(dataKey);
        if (openImageBeans == null) {
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
        isNoneClickView = getIntent().getBooleanExtra(OpenParams.NONE_CLICK_VIEW, false);
        imageShapeParams = getIntent().getParcelableExtra(OpenParams.IMAGE_SHAPE_PARAMS);
        wechatExitFillInEffect = getIntent().getBooleanExtra(OpenParams.WECHAT_EXIT_FILL_IN_EFFECT,false);
        onPermissionKey = getIntent().getStringExtra(OpenParams.PERMISSION_LISTENER);
        preloadCount = getIntent().getIntExtra(OpenParams.PRELOAD_COUNT,1);
        lazyPreload = getIntent().getBooleanExtra(OpenParams.LAZY_PRELOAD, false);
    }

    protected void addOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        if (onItemLongClickListener != null) {
            ImageLoadUtils.getInstance().setOnItemLongClickListener(onItemLongClickListener.toString(), onItemLongClickListener);
            photosViewModel.onAddItemLongListenerLiveData.setValue(onItemLongClickListener.toString());
            onItemLongClickListenerKeys.add(onItemLongClickListener.toString());
        }
    }

    /**
     * 设置后 {@link OpenImage#enableClickClose()} 则不起作用
     * @param onItemClickListener
     */
    protected void addOnItemClickListener(OnItemClickListener onItemClickListener) {
        if (onItemClickListener != null) {
            ImageLoadUtils.getInstance().setOnItemClickListener(onItemClickListener.toString(), onItemClickListener);
            photosViewModel.onAddItemListenerLiveData.setValue(onItemClickListener.toString());
            onItemClickListenerKeys.add(onItemClickListener.toString());
        }
    }

    protected void removeOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        if (onItemLongClickListener != null) {
            photosViewModel.onRemoveItemLongListenerLiveData.setValue(onItemLongClickListener.toString());
        }
    }

    protected void removeOnItemClickListener(OnItemClickListener onItemClickListener) {
        if (onItemClickListener != null) {
            photosViewModel.onRemoveItemListenerLiveData.setValue(onItemClickListener.toString());
        }
    }

    protected void addOnSelectMediaListener(OnSelectMediaListener onSelectMediaListener) {
        if (onSelectMediaListener != null) {
            ImageLoadUtils.getInstance().setOnSelectMediaListener(onSelectMediaListener.toString(), onSelectMediaListener);
            photosViewModel.onAddOnSelectMediaListenerLiveData.setValue(onSelectMediaListener.toString());
        }
    }

    protected void removeOnSelectMediaListener(OnSelectMediaListener onSelectMediaListener) {
        if (onSelectMediaListener != null) {
            photosViewModel.onRemoveOnSelectMediaListenerLiveData.setValue(onSelectMediaListener.toString());
        }
    }

    /**
     * 设置打开页面动画结束监听器
     * @param observer
     */
    protected void setTransitionEndListener(@NonNull Observer<Boolean> observer){
        if (photosViewModel.transitionEndLiveData.getValue() != null && photosViewModel.transitionEndLiveData.getValue()){
            observer.onChanged(true);
        }else {
            photosViewModel.transitionEndLiveData.observe(this, new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    photosViewModel.transitionEndLiveData.removeObserver(this);
                    observer.onChanged(aBoolean);
                }
            });
        }
    }

    /**
     *
     * @return 页面是否已经打开
     */
    protected boolean isTransitionEnd(){
        Boolean end = photosViewModel.transitionEndLiveData.getValue();
        return end != null?end:false;
    }

    protected boolean isWechatExitFillInEffect() {
        return wechatExitFillInEffect;
    }
}
