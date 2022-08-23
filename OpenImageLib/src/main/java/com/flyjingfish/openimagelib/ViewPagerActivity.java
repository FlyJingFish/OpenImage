package com.flyjingfish.openimagelib;

import android.animation.ObjectAnimator;
import android.app.Instrumentation;
import android.app.SharedElementCallback;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.flyjingfish.openimagelib.beans.OpenImageDetail;
import com.flyjingfish.openimagelib.databinding.OpenImageActivityViewpagerBinding;
import com.flyjingfish.openimagelib.databinding.OpenImageIndicatorTextBinding;
import com.flyjingfish.openimagelib.enums.BackViewType;
import com.flyjingfish.openimagelib.enums.ImageDiskMode;
import com.flyjingfish.openimagelib.enums.MediaType;
import com.flyjingfish.openimagelib.enums.MoreViewShowType;
import com.flyjingfish.openimagelib.enums.OpenImageOrientation;
import com.flyjingfish.openimagelib.listener.OnLoadViewFinishListener;
import com.flyjingfish.openimagelib.listener.OnSelectMediaListener;
import com.flyjingfish.openimagelib.utils.AttrsUtils;
import com.flyjingfish.openimagelib.utils.StatusBarHelper;
import com.flyjingfish.openimagelib.utils.ScreenUtils;
import com.flyjingfish.openimagelib.widget.TouchCloseLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewPagerActivity extends AppCompatActivity {

    private OpenImageActivityViewpagerBinding binding;
    private List<OpenImageDetail> openImageBeans;
    private int clickPosition;
    private int showPosition;
    private final HashMap<Integer, BaseFragment> fragmentHashMap = new HashMap<>();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private PhotosViewModel photosViewModel;
    private String itemLoadKey;
    private OpenImageIndicatorTextBinding indicatorTextBinding;
    private int indicatorType;
    private String textFormat = "%1$d/%2$d";
    private static final int INDICATOR_TEXT = 0;
    private static final int INDICATOR_IMAGE = 1;
    private static final long WECHAT_DURATION = 250;
    private static final long WECHAT_DURATION_END_ALPHA = 50;
    private ImageIndicatorAdapter imageIndicatorAdapter;
    private OpenImageOrientation orientation;
    private ImageDiskMode imageDiskMode;
    private ImageView.ScaleType srcScaleType;
    private int selectPos;
    private OnSelectMediaListener onSelectMediaListener;
    private String onSelectKey;
    private String openCoverKey;
    private String pageTransformersKey;
    private ObjectAnimator wechatEffectAnim;
    private String onItemCLickKey;
    private String onItemLongCLickKey;
    private String moreViewKey;
    private List<MoreViewOption> moreViewOptions = new ArrayList<>();
    private LinearLayoutManager imageIndicatorLayoutManager;
    private String onBackViewKey;
    private ImageLoadUtils.OnBackView onBackView;
    private FontStyle fontStyle;
    private boolean isCallClosed;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        getWindow().setAllowEnterTransitionOverlap(true);
        getWindow().setExitTransition(TransitionInflater.from(this)
                .inflateTransition(R.transition.open_image_exit_transition));
        binding = OpenImageActivityViewpagerBinding.inflate(getLayoutInflater());

        photosViewModel = new ViewModelProvider(this).get(PhotosViewModel.class);
        photosViewModel.closeViewLiveData.observe(this, integer -> close(false));
        srcScaleType = (ImageView.ScaleType) getIntent().getSerializableExtra(OpenParams.SRC_SCALE_TYPE);
        openImageBeans = (List<OpenImageDetail>) getIntent().getSerializableExtra(OpenParams.IMAGES);
        clickPosition = getIntent().getIntExtra(OpenParams.CLICK_POSITION, 0);
        boolean disEnableTouchClose = getIntent().getBooleanExtra(OpenParams.DISABLE_TOUCH_CLOSE, false);
        imageDiskMode = (ImageDiskMode) getIntent().getSerializableExtra(OpenParams.IMAGE_DISK_MODE);
        int errorResId = getIntent().getIntExtra(OpenParams.ERROR_RES_ID, 0);
        itemLoadKey = getIntent().getStringExtra(OpenParams.ITEM_LOAD_KEY);
        float touchScaleClose = getIntent().getFloatExtra(OpenParams.TOUCH_CLOSE_SCALE, .76f);
        selectPos = 0;
        for (int i = 0; i < openImageBeans.size(); i++) {
            OpenImageDetail openImageBean = openImageBeans.get(i);
            if (openImageBean.dataPosition == clickPosition) {
                selectPos = i;
                break;
            }
        }
        onSelectKey = getIntent().getStringExtra(OpenParams.ON_SELECT_KEY);
        openCoverKey = getIntent().getStringExtra(OpenParams.OPEN_COVER_DRAWABLE);
        onSelectMediaListener = ImageLoadUtils.getInstance().getOnSelectMediaListener(onSelectKey);
        boolean disableClickClose = getIntent().getBooleanExtra(OpenParams.DISABLE_CLICK_CLOSE, false);
        onItemCLickKey = getIntent().getStringExtra(OpenParams.ON_ITEM_CLICK_KEY);
        onItemLongCLickKey = getIntent().getStringExtra(OpenParams.ON_ITEM_LONG_CLICK_KEY);
        moreViewKey = getIntent().getStringExtra(OpenParams.MORE_VIEW_KEY);
        onBackViewKey = getIntent().getStringExtra(OpenParams.ON_BACK_VIEW);
        onBackView = ImageLoadUtils.getInstance().getOnBackView(onBackViewKey);
        initStyleConfig();

        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        initMoreView();
        binding.viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                BaseFragment baseFragment = fragmentHashMap.get(position);
                if (baseFragment == null) {
                    OpenImageDetail openImageBean = openImageBeans.get(position);
                    MediaType mediaType = openImageBean.getType();
                    BaseFragment fragment = null;
                    if (mediaType == MediaType.VIDEO) {
                        if (OpenImageConfig.getInstance().getVideoFragmentCreate() != null) {
                            fragment = OpenImageConfig.getInstance().getVideoFragmentCreate().createVideoFragment();
                            if (fragment == null) {
                                throw new IllegalArgumentException(OpenImageConfig.getInstance().getVideoFragmentCreate().getClass().getName() + "请重写createVideoFragment");
                            }
                        } else {
                            if (fragment == null) {
                                throw new IllegalArgumentException("请设置视频播放器fragment --> OpenImageConfig");
                            }
                        }

                    } else {
                        if (OpenImageConfig.getInstance().getImageFragmentCreate() != null) {
                            fragment = OpenImageConfig.getInstance().getImageFragmentCreate().createImageFragment();
                        } else {
                            fragment = new ImageFragment();
                        }

                        if (fragment == null) {
                            throw new IllegalArgumentException(OpenImageConfig.getInstance().getImageFragmentCreate().getClass().getName() + "请重写createImageFragment");
                        }
                    }
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(OpenParams.IMAGE, openImageBean);
                    bundle.putInt(OpenParams.SHOW_POSITION, position);
                    bundle.putInt(OpenParams.ERROR_RES_ID, errorResId);
                    bundle.putInt(OpenParams.CLICK_POSITION, selectPos);
                    bundle.putSerializable(OpenParams.IMAGE_DISK_MODE, imageDiskMode);
                    bundle.putSerializable(OpenParams.SRC_SCALE_TYPE, srcScaleType);
                    bundle.putString(OpenParams.ITEM_LOAD_KEY, itemLoadKey);
                    bundle.putBoolean(OpenParams.DISABLE_CLICK_CLOSE, disableClickClose);
                    bundle.putString(OpenParams.ON_ITEM_CLICK_KEY, onItemCLickKey);
                    bundle.putString(OpenParams.ON_ITEM_LONG_CLICK_KEY, onItemLongCLickKey);
                    bundle.putString(OpenParams.OPEN_COVER_DRAWABLE, openCoverKey);
                    fragment.setArguments(bundle);
                    fragmentHashMap.put(position, fragment);
                    baseFragment = fragment;
                }

                return baseFragment;
            }

            @Override
            public int getItemCount() {
                return openImageBeans.size();
            }
        });
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            boolean isFirstBacked = false;

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                showPosition = position;
                setIndicatorPosition();
                showMoreView();
                if (position != selectPos && binding.viewPager.getOffscreenPageLimit() != 1) {
                    binding.viewPager.setOffscreenPageLimit(1);
                }
                if (isFirstBacked && onBackView != null) {
                    onBackView.onScrollPos(openImageBeans.get(showPosition).viewPosition);
                }
                if (onSelectMediaListener != null) {
                    onSelectMediaListener.onSelect(openImageBeans.get(showPosition).openImageUrl, openImageBeans.get(showPosition).dataPosition);
                }
                isFirstBacked = true;
            }
        });
        binding.viewPager.setCurrentItem(selectPos, false);
        binding.getRoot().setTouchCloseScale(touchScaleClose);
        binding.getRoot().setTouchView(binding.viewPager, binding.vBg);
        binding.getRoot().setDisEnableTouchClose(disEnableTouchClose);
        binding.getRoot().setOrientation(orientation == OpenImageOrientation.VERTICAL ? OpenImageOrientation.HORIZONTAL : OpenImageOrientation.VERTICAL);
        binding.getRoot().setOnTouchCloseListener(new TouchCloseLayout.OnTouchCloseListener() {
            @Override
            public void onStartTouch() {
                if (onBackView != null){
                    onBackView.onStartTouchScale(showPosition);
                }
                if (fontStyle == FontStyle.FULL_SCREEN){
                    StatusBarHelper.cancelFullScreen(ViewPagerActivity.this);
                }
            }

            @Override
            public void onEndTouch() {
                if (onBackView != null){
                    onBackView.onEndTouchScale(showPosition);
                }
                if (fontStyle == FontStyle.FULL_SCREEN){
                    StatusBarHelper.setFullScreen(ViewPagerActivity.this);
                }
            }

            @Override
            public void onTouchScale(float scale) {
                photosViewModel.onTouchScaleLiveData.setValue(scale);
            }

            @Override
            public void onTouchClose(float scale) {
                photosViewModel.onTouchCloseLiveData.setValue(scale);
                close(true);
            }
        });
        setViewTransition();
        addTransitionListener();
    }

    private void setViewTransition() {
        ViewCompat.setTransitionName(binding.viewPager, OpenParams.SHARE_VIEW + selectPos);
    }

    private void initMoreView() {
        if (moreViewKey != null) {
            List<MoreViewOption> viewOptions = ImageLoadUtils.getInstance().getMoreViewOption(moreViewKey);
            for (MoreViewOption moreViewOption : viewOptions) {
                if (moreViewOption != null) {
                    View view = LayoutInflater.from(this).inflate(moreViewOption.getLayoutRes(), null, false);
                    binding.getRoot().addView(view, moreViewOption.getLayoutParams());
                    OnLoadViewFinishListener onLoadViewFinishListener = moreViewOption.getOnLoadViewFinishListener();
                    if (onLoadViewFinishListener != null) {
                        onLoadViewFinishListener.onLoadViewFinish(view);
                    }
                    moreViewOption.setView(view);
                    view.setVisibility(View.GONE);
                }
            }
            moreViewOptions.addAll(viewOptions);
        }
    }

    private void showMoreView() {
        if (moreViewOptions.size() > 0) {
            OpenImageDetail openImageDetail = openImageBeans.get(showPosition);
            MediaType mediaType = openImageDetail.getType();
            for (MoreViewOption moreViewOption : moreViewOptions) {
                MoreViewShowType showType = moreViewOption.getMoreViewShowType();
                if (mediaType == MediaType.IMAGE && (showType == MoreViewShowType.IMAGE || showType == MoreViewShowType.BOTH)){
                    moreViewOption.getView().setVisibility(View.VISIBLE);
                }else if (mediaType == MediaType.VIDEO && (showType == MoreViewShowType.VIDEO || showType == MoreViewShowType.BOTH)){
                    moreViewOption.getView().setVisibility(View.VISIBLE);
                }else {
                    moreViewOption.getView().setVisibility(View.GONE);
                }
            }
        }
    }


    private void setIndicatorPosition() {
        mHandler.post(() -> {
            if (indicatorType == INDICATOR_IMAGE) {//图片样式
                if (imageIndicatorAdapter != null) {
                    imageIndicatorAdapter.setSelectPosition(showPosition);
                    imageIndicatorLayoutManager.scrollToPosition(showPosition);
                }
            } else if (indicatorType == INDICATOR_TEXT) {
                if (indicatorTextBinding != null) {
                    indicatorTextBinding.tvShowPos.setText(String.format(textFormat, showPosition + 1, openImageBeans.size()));
                }
            }
        });
    }

    private void initStyleConfig() {
        int themeRes = getIntent().getIntExtra(OpenParams.OPEN_IMAGE_STYLE, 0);
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        if (themeRes != 0) {
            setTheme(themeRes);
            fontStyle = FontStyle.getStyle(AttrsUtils.getTypeValueInt(this, R.attr.openImage_statusBar_fontStyle));
            StatusBarHelper.translucent(this);
            if (fontStyle == FontStyle.LIGHT) {
                StatusBarHelper.setStatusBarLightMode(this);
            } else if (fontStyle == FontStyle.FULL_SCREEN) {
                StatusBarHelper.setFullScreen(this);
            } else {
                StatusBarHelper.setStatusBarDarkMode(this);
            }
            int bgColor = AttrsUtils.getTypeValueResourceId(this, R.attr.openImage_background);
            if (bgColor != 0) {
                binding.vBg.setBackgroundResource(bgColor);
            }
            indicatorType = AttrsUtils.getTypeValueInt(this, R.attr.openImage_indicator_type);
            orientation = OpenImageOrientation.getOrientation(AttrsUtils.getTypeValueInt(this, R.attr.openImage_viewPager_orientation));
            if (indicatorType < 2 && openImageBeans.size() > 1) {
                if (indicatorType == INDICATOR_IMAGE) {//图片样式
                    float interval = AttrsUtils.getTypeValueDimension(this, R.attr.openImage_indicator_image_interval, -1);
                    int imageRes = AttrsUtils.getTypeValueResourceId(this, R.attr.openImage_indicator_imageRes);
                    if (interval == -1) {
                        interval = ScreenUtils.dp2px(this, 4);
                    }
                    if (imageRes == 0) {
                        imageRes = R.drawable.open_image_indicator_image;
                    }
                    RecyclerView recyclerView = new RecyclerView(this);
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    setIndicatorLayoutParams(layoutParams);
                    binding.getRoot().addView(recyclerView, layoutParams);

                    OpenImageOrientation realOrientation = OpenImageOrientation.getOrientation(AttrsUtils.getTypeValueInt(this, R.attr.openImage_indicator_image_orientation));
                    imageIndicatorLayoutManager = new LinearLayoutManager(this, realOrientation == OpenImageOrientation.HORIZONTAL ? LinearLayoutManager.HORIZONTAL : LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(imageIndicatorLayoutManager);
                    imageIndicatorAdapter = new ImageIndicatorAdapter(openImageBeans.size(), interval, imageRes, realOrientation);
                    recyclerView.setAdapter(imageIndicatorAdapter);

                } else {
                    int textColor = AttrsUtils.getTypeValueColor(this, R.attr.openImage_indicator_textColor, Color.WHITE);
                    float textSize = AttrsUtils.getTypeValueDimension(this, R.attr.openImage_indicator_textSize);
                    indicatorTextBinding = OpenImageIndicatorTextBinding.inflate(getLayoutInflater(), binding.getRoot(), true);
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) indicatorTextBinding.tvShowPos.getLayoutParams();
                    setIndicatorLayoutParams(layoutParams);
                    indicatorTextBinding.tvShowPos.setLayoutParams(layoutParams);
                    indicatorTextBinding.tvShowPos.setTextColor(textColor);
                    if (textSize != 0) {
                        indicatorTextBinding.tvShowPos.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                    }
                    CharSequence strFormat = AttrsUtils.getTypeValueText(this, R.attr.openImage_indicator_textFormat);
                    if (!TextUtils.isEmpty(strFormat)) {
                        textFormat = strFormat + "";
                    }
                }
            }
            int pageMargin = (int) AttrsUtils.getTypeValueDimension(this, R.attr.openImage_viewPager_pageMargin, -1);
            if (pageMargin >= 0) {
                compositePageTransformer.addTransformer(new MarginPageTransformer(pageMargin));
            } else {
                compositePageTransformer.addTransformer(new MarginPageTransformer((int) ScreenUtils.dp2px(this, 10)));
            }
        } else {
            fontStyle = FontStyle.DARK;
            StatusBarHelper.translucent(this);
            StatusBarHelper.setStatusBarDarkMode(this);
            orientation = OpenImageOrientation.HORIZONTAL;
            if (openImageBeans.size() > 1) {
                indicatorTextBinding = OpenImageIndicatorTextBinding.inflate(getLayoutInflater(), binding.getRoot(), true);
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) indicatorTextBinding.tvShowPos.getLayoutParams();
                layoutParams.bottomMargin = (int) ScreenUtils.dp2px(this, 10);
                layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
                indicatorTextBinding.tvShowPos.setLayoutParams(layoutParams);
            }
            compositePageTransformer.addTransformer(new MarginPageTransformer((int) ScreenUtils.dp2px(this, 10)));
        }

        pageTransformersKey = getIntent().getStringExtra(OpenParams.PAGE_TRANSFORMERS);
        List<ViewPager2.PageTransformer> pageTransformers = ImageLoadUtils.getInstance().getPageTransformers(pageTransformersKey);
        if (pageTransformers != null && pageTransformers.size() > 0) {
            for (ViewPager2.PageTransformer pageTransformer : pageTransformers) {
                compositePageTransformer.addTransformer(pageTransformer);
            }
        }
        binding.viewPager.setPageTransformer(compositePageTransformer);

        int leftRightPadding = getIntent().getIntExtra(OpenParams.GALLERY_EFFECT_WIDTH, 0);
        if (leftRightPadding > 0) {
            View recyclerView = binding.viewPager.getChildAt(0);
            if (recyclerView instanceof RecyclerView) {
                recyclerView.setPadding((int) ScreenUtils.dp2px(this, leftRightPadding), 0, (int) ScreenUtils.dp2px(this, leftRightPadding), 0);
                ((RecyclerView) recyclerView).setClipToPadding(false);
            }
        }

        if (orientation == OpenImageOrientation.VERTICAL) {
            binding.viewPager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        } else {
            binding.viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        }

    }

    private void setIndicatorLayoutParams(FrameLayout.LayoutParams layoutParams) {
        int gravity = AttrsUtils.getTypeValueInt(this, R.attr.openImage_indicator_gravity);
        int topMargin = (int) AttrsUtils.getTypeValueDimension(this, R.attr.openImage_indicator_marginTop);
        int bottomMargin = (int) AttrsUtils.getTypeValueDimension(this, R.attr.openImage_indicator_marginBottom);
        int leftMargin = (int) AttrsUtils.getTypeValueDimension(this, R.attr.openImage_indicator_marginLeft);
        int rightMargin = (int) AttrsUtils.getTypeValueDimension(this, R.attr.openImage_indicator_marginRight);
        int startMargin = (int) AttrsUtils.getTypeValueDimension(this, R.attr.openImage_indicator_marginStart);
        int endMargin = (int) AttrsUtils.getTypeValueDimension(this, R.attr.openImage_indicator_marginEnd);
        layoutParams.bottomMargin = bottomMargin;
        layoutParams.topMargin = topMargin + ScreenUtils.getStatusBarHeight(this);
        layoutParams.leftMargin = leftMargin;
        layoutParams.rightMargin = rightMargin;
        layoutParams.setMarginStart(startMargin);
        layoutParams.setMarginEnd(endMargin);
        if (gravity == 0) {
            if (orientation == OpenImageOrientation.VERTICAL) {
                layoutParams.gravity = Gravity.CENTER_VERTICAL | Gravity.END;
                layoutParams.setMarginEnd(endMargin == 0 ? (int) ScreenUtils.dp2px(this, 14) : endMargin);
            } else {
                layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
                layoutParams.bottomMargin = bottomMargin == 0 ? (int) ScreenUtils.dp2px(this, 14) : bottomMargin;
            }
        } else {
            layoutParams.gravity = gravity;
        }
    }

    private void addTransitionListener() {
        Transition transition = getWindow().getSharedElementEnterTransition();
        if (transition != null) {
            long timeMs = getIntent().getLongExtra(OpenParams.OPEN_ANIM_TIME_MS, 0);
            if (timeMs != 0) {
                transition.setDuration(timeMs);
            }
            transition.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {
                }

                @Override
                public void onTransitionEnd(Transition transition) {
                    photosViewModel.transitionEndLiveData.setValue(true);
                    transition.removeListener(this);
                }

                @Override
                public void onTransitionCancel(Transition transition) {
                    transition.removeListener(this);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OpenImage.isCanOpen = true;
        showPosition = 0;
        fragmentHashMap.clear();
        mHandler.removeCallbacksAndMessages(null);
        ImageLoadUtils.getInstance().clearItemLoadHelper(itemLoadKey);
        ImageLoadUtils.getInstance().clearOnSelectMediaListener(onSelectKey);
        ImageLoadUtils.getInstance().clearCoverDrawable(openCoverKey);
        ImageLoadUtils.getInstance().clearPageTransformers(pageTransformersKey);
        ImageLoadUtils.getInstance().clearOnItemClickListener(onItemCLickKey);
        ImageLoadUtils.getInstance().clearOnItemLongClickListener(onItemLongCLickKey);
        ImageLoadUtils.getInstance().clearMoreViewOption(moreViewKey);
        ImageLoadUtils.getInstance().clearOnBackView(onBackViewKey);
        if (wechatEffectAnim != null) {
            wechatEffectAnim.cancel();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            close(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setExitView() {
        BackViewType backViewType = BackViewType.NO_SHARE;
        if (onBackView != null){
            backViewType= onBackView.onBack(showPosition);
        }
        if (backViewType == BackViewType.NO_SHARE) {
            ViewCompat.setTransitionName(binding.viewPager, "");
            setEnterSharedElementCallback(new SharedElementCallback() {
                @Override
                public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                    super.onMapSharedElements(names, sharedElements);
                    if (names.size() == 0) {
                        return;
                    }
                    names.clear();
                    sharedElements.clear();
                }
            });
            return;
        }

        View shareView = getCoverView();

        if (shareView != null) {
            ViewCompat.setTransitionName(binding.viewPager, "");
            ViewCompat.setTransitionName(shareView, OpenParams.SHARE_VIEW + showPosition);
            if (backViewType == BackViewType.SHARE_WECHAT) {
                addWechatEffect(shareView);
            }
            setEnterSharedElementCallback(new SharedElementCallback() {
                @Override
                public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                    super.onMapSharedElements(names, sharedElements);
                    if (names.size() == 0) {
                        return;
                    }
                    sharedElements.put(OpenParams.SHARE_VIEW + showPosition, shareView);
                }
            });
        } else {
            if (backViewType == BackViewType.SHARE_WECHAT) {
                addWechatEffect(binding.viewPager);
            }
            ViewCompat.setTransitionName(binding.viewPager, OpenParams.SHARE_VIEW + showPosition);
            setEnterSharedElementCallback(new SharedElementCallback() {
                @Override
                public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                    super.onMapSharedElements(names, sharedElements);
                    if (names.size() == 0) {
                        return;
                    }
                    sharedElements.put(OpenParams.SHARE_VIEW + showPosition, binding.viewPager);
                }
            });
        }


    }

    private void addWechatEffect(View shareView) {
        Transition transition = getWindow().getSharedElementEnterTransition();
        if (transition != null) {
            wechatEffectAnim = ObjectAnimator.ofFloat(shareView, "alpha", 1f, 0f);
            wechatEffectAnim.setStartDelay(WECHAT_DURATION - WECHAT_DURATION_END_ALPHA);
            wechatEffectAnim.setDuration(WECHAT_DURATION_END_ALPHA);
            transition.setDuration(WECHAT_DURATION);
            transition.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {
                    wechatEffectAnim.start();
                }

                @Override
                public void onTransitionEnd(Transition transition) {
                    transition.removeListener(this);
                    wechatEffectAnim.cancel();
                }

                @Override
                public void onTransitionCancel(Transition transition) {
                    transition.removeListener(this);
                    wechatEffectAnim.cancel();
                }

                @Override
                public void onTransitionPause(Transition transition) {
                    wechatEffectAnim.pause();
                }

                @Override
                public void onTransitionResume(Transition transition) {
                    wechatEffectAnim.resume();
                }
            });
        }
    }

    private void close(boolean isTouchClose) {
        if (isCallClosed){
            return;
        }
        if (onBackView != null){
            onBackView.onTouchClose(isTouchClose);
        }
        setExitView();
        if (!isTouchClose && fontStyle == FontStyle.FULL_SCREEN){
            StatusBarHelper.cancelFullScreen(ViewPagerActivity.this);
            mHandler.postDelayed(this::finishAfterTransition,100);
        }else {
            finishAfterTransition();
        }
        isCallClosed = true;
    }

    private View getCoverView() {
        BaseFragment baseFragment = fragmentHashMap.get(showPosition);
        if (baseFragment != null) {
            return baseFragment.getExitImageView();
        }
        return null;
    }

    @Override
    protected void onStop() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !isFinishing()){
            new Instrumentation().callActivityOnSaveInstanceState(this,new Bundle());
        }
        super.onStop();
    }

    private enum FontStyle{
        LIGHT(1),
        DARK(2),
        FULL_SCREEN(3);
        int value;

        FontStyle(int value) {
            this.value = value;
        }

        public static FontStyle getStyle(int style){
            if (style == 1){
                return LIGHT;
            }else  if (style == 2){
                return DARK;
            }else  if (style == 3){
                return FULL_SCREEN;
            }else {
                return DARK;
            }
        }
    }
}
