package com.flyjingfish.openimagelib;

import android.app.SharedElementCallback;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
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
import androidx.viewpager2.widget.ViewPager2;

import com.flyjingfish.openimagelib.beans.OpenImageDetail;
import com.flyjingfish.openimagelib.databinding.ActivityViewpagerBinding;
import com.flyjingfish.openimagelib.databinding.IndicatorTextBinding;
import com.flyjingfish.openimagelib.enums.ImageDiskMode;
import com.flyjingfish.openimagelib.enums.MediaType;
import com.flyjingfish.openimagelib.enums.OpenImageOrientation;
import com.flyjingfish.openimagelib.listener.ItemLoadHelper;
import com.flyjingfish.openimagelib.listener.OnSelectMediaListener;
import com.flyjingfish.openimagelib.photoview.PhotoView;
import com.flyjingfish.openimagelib.utils.AttrsUtils;
import com.flyjingfish.openimagelib.utils.ScreenUtils;
import com.flyjingfish.openimagelib.utils.StatusBarUtils;
import com.flyjingfish.openimagelib.widget.TouchCloseLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewPagerActivity extends AppCompatActivity {

    private ActivityViewpagerBinding binding;
    private List<OpenImageDetail> openImageBeans;
    private int clickPosition;
    public static int showPosition;
    private final HashMap<Integer, BaseFragment> fragmentHashMap = new HashMap<>();
    protected Handler mHandler = new Handler(Looper.getMainLooper());
    private PhotosViewModel photosViewModel;
    private String itemLoadKey;
    private IndicatorTextBinding indicatorTextBinding;
    private int indicatorType;
    private String textFormat = "%1$d/%2$d";
    private static final int INDICATOR_TEXT = 0;
    private static final int INDICATOR_IMAGE = 1;
    private ImageIndicatorAdapter imageIndicatorAdapter;
    private OpenImageOrientation orientation;
    private ImageDiskMode imageDiskMode;
    private ImageView.ScaleType srcScaleType;
    private ImageView coverImageView;
    private int selectPos;
    private ItemLoadHelper itemLoadHelper;
    private OnSelectMediaListener onSelectMediaListener;
    private String onSelectKey;
    private boolean isAutoScrollSelect;
    private String openCoverKey;
    //    private ArrayList<ContentViewOriginModel> contentViewOriginModels;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
//        getWindow().setAllowEnterTransitionOverlap(false);
        getWindow().setExitTransition(TransitionInflater.from(this)
                .inflateTransition(R.transition.grid_exit_transition));
        super.onCreate(savedInstanceState);
        binding = ActivityViewpagerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        photosViewModel = new ViewModelProvider(this).get(PhotosViewModel.class);
        photosViewModel.closeViewLiveData.observe(this, integer -> close());
        srcScaleType = (ImageView.ScaleType) getIntent().getSerializableExtra(OpenParams.SRC_SCALE_TYPE);
        openImageBeans = (List<OpenImageDetail>) getIntent().getSerializableExtra(OpenParams.IMAGES);
        clickPosition = getIntent().getIntExtra(OpenParams.CLICK_POSITION, 0);
        boolean disEnableTouchClose = getIntent().getBooleanExtra(OpenParams.DISABLE_TOUCH_CLOSE, false);
        imageDiskMode = (ImageDiskMode) getIntent().getSerializableExtra(OpenParams.IMAGE_DISK_MODE);
        int errorResId = getIntent().getIntExtra(OpenParams.ERROR_RES_ID, 0);
        itemLoadKey = getIntent().getStringExtra(OpenParams.ITEM_LOAD_KEY);
        float touchScaleClose = getIntent().getFloatExtra(OpenParams.TOUCH_CLOSE_SCALE, .76f);
        initStyleConfig();
        selectPos = 0;
        for (int i = 0; i < openImageBeans.size(); i++) {
            OpenImageDetail openImageBean = openImageBeans.get(i);
            if (openImageBean.dataPosition == clickPosition) {
                selectPos = i;
                break;
            }
        }
        isAutoScrollSelect = getIntent().getBooleanExtra(OpenParams.AUTO_SCROLL_SELECT,true);
        itemLoadHelper = ImageLoadUtils.getInstance().getItemLoadHelper(itemLoadKey);
        onSelectKey = getIntent().getStringExtra(OpenParams.ON_SELECT_KEY);
        onSelectMediaListener = ImageLoadUtils.getInstance().getOnSelectMediaListener(onSelectKey);
//        initSrcViews();
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
                if (position != selectPos && binding.viewPager.getOffscreenPageLimit() != 1) {
                    binding.viewPager.setOffscreenPageLimit(1);
                }
                if (isFirstBacked){
                    ImageLoadUtils.getInstance().getOnBackView().onScrollPos(openImageBeans.get(showPosition).dataPosition);
                }
                if (onSelectMediaListener != null){
                    onSelectMediaListener.onSelect(openImageBeans.get(showPosition).dataPosition);
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
                initSrcViews();
            }

            @Override
            public void onTouchScale(float scale) {
                photosViewModel.onTouchScaleLiveData.setValue(scale);
            }

            @Override
            public void onTouchClose(float scale) {
                photosViewModel.onTouchCloseLiveData.setValue(scale);
                close();
            }
        });
        setViewTransition();
        addTransitionListener();
    }

    private void initSrcViews() {
        ImageLoadUtils.getInstance().getOnBackView().onGetContentViewOriginModel();
    }

    private void setViewTransition() {
        openCoverKey = getIntent().getStringExtra(OpenParams.OPEN_COVER_DRAWABLE);
        Drawable drawable = ImageLoadUtils.getInstance().getCoverDrawable(openCoverKey);
        OpenImageDetail openImageDetail = openImageBeans.get(selectPos);
        if (drawable != null) {
            if (openImageDetail.getType() == MediaType.IMAGE) {
                coverImageView = new PhotoView(this);
            } else {
                coverImageView = new ImageView(this);
            }
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            binding.getRoot().addView(coverImageView, layoutParams);
            coverImageView.setImageDrawable(drawable);
            binding.viewPager.setAlpha(0f);
            ViewCompat.setTransitionName(coverImageView, OpenParams.SHARE_VIEW + selectPos);
        } else {
            ViewCompat.setTransitionName(binding.viewPager, OpenParams.SHARE_VIEW + selectPos);
        }

    }


    private void setIndicatorPosition() {
        mHandler.post(() -> {
            if (indicatorType == INDICATOR_IMAGE) {//图片样式
                imageIndicatorAdapter.setSelectPosition(showPosition);
            } else {
                indicatorTextBinding.tvShowPos.setText(String.format(textFormat, showPosition + 1, openImageBeans.size()));
            }
        });
    }

    private void initStyleConfig() {
        int themeRes = getIntent().getIntExtra(OpenParams.OPEN_IMAGE_STYLE, 0);
        StatusBarUtils.setTransparent(this);
        if (themeRes != 0) {
            setTheme(themeRes);
            int fontStyle = AttrsUtils.getTypeValueInt(this, R.attr.openImage_statusBar_fontStyle);
            if (fontStyle == 1) {
                StatusBarUtils.setDarkMode(this);
            } else {
                StatusBarUtils.setLightMode(this);
            }
            int bgColor = AttrsUtils.getTypeValueResourceId(this, R.attr.openImage_background);
            if (bgColor != 0) {
                binding.vBg.setBackgroundResource(bgColor);
            }
            indicatorType = AttrsUtils.getTypeValueInt(this, R.attr.openImage_indicator_type);
            orientation = OpenImageOrientation.getOrientation(AttrsUtils.getTypeValueInt(this, R.attr.openImage_viewPager_orientation));
            if (indicatorType == INDICATOR_IMAGE) {//图片样式
                float interval = AttrsUtils.getTypeValueDimension(this, R.attr.openImage_indicator_image_interval);
                int imageRes = AttrsUtils.getTypeValueResourceId(this, R.attr.openImage_indicator_imageRes);
                RecyclerView recyclerView = new RecyclerView(this);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                setIndicatorLayoutParams(layoutParams);
                binding.getRoot().addView(recyclerView, layoutParams);

                OpenImageOrientation realOrientation = OpenImageOrientation.getOrientation(AttrsUtils.getTypeValueInt(this, R.attr.openImage_indicator_image_orientation));
                recyclerView.setLayoutManager(new LinearLayoutManager(this, realOrientation == OpenImageOrientation.HORIZONTAL ? LinearLayoutManager.HORIZONTAL : LinearLayoutManager.VERTICAL, false));
                imageIndicatorAdapter = new ImageIndicatorAdapter(openImageBeans.size(), interval, imageRes, realOrientation);
                recyclerView.setAdapter(imageIndicatorAdapter);
            } else {
                int textColor = AttrsUtils.getTypeValueColor(this, R.attr.openImage_indicator_textColor, Color.WHITE);
                float textSize = AttrsUtils.getTypeValueDimension(this, R.attr.openImage_indicator_textSize);
                indicatorTextBinding = IndicatorTextBinding.inflate(getLayoutInflater(), binding.getRoot(), true);
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
        } else {
            StatusBarUtils.setLightMode(this);
            orientation = OpenImageOrientation.HORIZONTAL;
            indicatorTextBinding = IndicatorTextBinding.inflate(getLayoutInflater(), binding.getRoot(), true);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) indicatorTextBinding.tvShowPos.getLayoutParams();
            layoutParams.bottomMargin = (int) ScreenUtils.dp2px(this, 10);
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
            indicatorTextBinding.tvShowPos.setLayoutParams(layoutParams);
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
                    mHandler.post(() -> {
                        binding.viewPager.setAlpha(1f);
                        if (coverImageView != null) {
                            coverImageView.setVisibility(View.GONE);
                        }
                        binding.getRoot().removeView(coverImageView);
                    });
                    transition.removeListener(this);
                }

                @Override
                public void onTransitionCancel(Transition transition) {
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
        showPosition = 0;
        fragmentHashMap.clear();
        mHandler.removeCallbacksAndMessages(null);
        ImageLoadUtils.getInstance().clearItemLoadHelper(itemLoadKey);
        ImageLoadUtils.getInstance().clearOnSelectMediaListener(onSelectKey);
        ImageLoadUtils.getInstance().clearCoverDrawable(openCoverKey);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            close();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setExitView() {
        View shareView = getCoverView();

        if (shareView != null) {
            if (coverImageView != null) {
                ViewCompat.setTransitionName(coverImageView, "");
            }
            ViewCompat.setTransitionName(binding.viewPager, "");
            ViewCompat.setTransitionName(shareView, OpenParams.SHARE_VIEW + showPosition);

            setEnterSharedElementCallback(new SharedElementCallback() {
                @Override
                public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                    super.onMapSharedElements(names, sharedElements);
                    if (names.size()==0){
                        return;
                    }
                    if (isAutoScrollSelect){
                        sharedElements.put(OpenParams.SHARE_VIEW + showPosition, shareView);
                    }else if (TextUtils.equals(names.get(0),OpenParams.SHARE_VIEW + showPosition)){
                        sharedElements.put(OpenParams.SHARE_VIEW + showPosition, shareView);
                    }
                }
            });
        } else {
            if (showPosition != selectPos) {
                if (coverImageView != null) {
                    ViewCompat.setTransitionName(coverImageView, "");
                }
                ViewCompat.setTransitionName(binding.viewPager, OpenParams.SHARE_VIEW + showPosition);
                setEnterSharedElementCallback(new SharedElementCallback() {
                    @Override
                    public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                        super.onMapSharedElements(names, sharedElements);
                        if (names.size()==0){
                            return;
                        }
                        if (isAutoScrollSelect){
                            sharedElements.put(OpenParams.SHARE_VIEW + showPosition, binding.viewPager);
                        }else if (TextUtils.equals(names.get(0),OpenParams.SHARE_VIEW + showPosition)){
                            sharedElements.put(OpenParams.SHARE_VIEW + showPosition, binding.viewPager);
                        }
                    }
                });
            } else {
                if (coverImageView != null) {
                    binding.viewPager.setVisibility(View.GONE);
                    coverImageView.setVisibility(View.VISIBLE);
                    binding.getRoot().removeView(coverImageView);
                    binding.getRoot().addView(coverImageView);
                    ViewCompat.setTransitionName(coverImageView, OpenParams.SHARE_VIEW + showPosition);
                    setEnterSharedElementCallback(new SharedElementCallback() {
                        @Override
                        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                            super.onMapSharedElements(names, sharedElements);
                            if (names.size()==0){
                                return;
                            }
                            if (isAutoScrollSelect){
                                sharedElements.put(OpenParams.SHARE_VIEW + showPosition, coverImageView);
                            }else if (TextUtils.equals(names.get(0),OpenParams.SHARE_VIEW + showPosition)){
                                sharedElements.put(OpenParams.SHARE_VIEW + showPosition, coverImageView);
                            }
                        }
                    });
                } else {
                    ViewCompat.setTransitionName(binding.viewPager, OpenParams.SHARE_VIEW + showPosition);
                    setEnterSharedElementCallback(new SharedElementCallback() {
                        @Override
                        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                            super.onMapSharedElements(names, sharedElements);
                            if (names.size()==0){
                                return;
                            }
                            if (isAutoScrollSelect){
                                sharedElements.put(OpenParams.SHARE_VIEW + showPosition, binding.viewPager);
                            }else if (TextUtils.equals(names.get(0),OpenParams.SHARE_VIEW + showPosition)){
                                sharedElements.put(OpenParams.SHARE_VIEW + showPosition, binding.viewPager);
                            }
                        }
                    });
                }

            }
        }
    }

    private void close() {
        ImageLoadUtils.getInstance().getOnBackView().onBack();
        setExitView();
        finishAfterTransition();
    }

    private View getCoverView() {
        BaseFragment baseFragment = fragmentHashMap.get(showPosition);
        if (baseFragment != null) {
            return baseFragment.getExitImageView();
        }
        return null;
    }
}
