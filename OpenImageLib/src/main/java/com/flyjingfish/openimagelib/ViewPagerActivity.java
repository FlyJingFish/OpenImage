package com.flyjingfish.openimagelib;

import android.animation.ObjectAnimator;
import android.app.SharedElementCallback;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
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
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
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
import com.flyjingfish.openimagelib.enums.MediaType;
import com.flyjingfish.openimagelib.enums.MoreViewShowType;
import com.flyjingfish.openimagelib.enums.OpenImageOrientation;
import com.flyjingfish.openimagelib.listener.ImageFragmentCreate;
import com.flyjingfish.openimagelib.listener.OnLoadViewFinishListener;
import com.flyjingfish.openimagelib.listener.OnSelectMediaListener;
import com.flyjingfish.openimagelib.listener.UpperLayerFragmentCreate;
import com.flyjingfish.openimagelib.listener.VideoFragmentCreate;
import com.flyjingfish.openimagelib.photoview.PhotoView;
import com.flyjingfish.openimagelib.utils.AttrsUtils;
import com.flyjingfish.openimagelib.utils.ScreenUtils;
import com.flyjingfish.openimagelib.utils.StatusBarHelper;
import com.flyjingfish.openimagelib.widget.TouchCloseLayout;
import com.flyjingfish.shapeimageviewlib.ShapeImageView;

import java.util.List;
import java.util.Map;

public class ViewPagerActivity extends BaseActivity {

    protected View vBg;
    protected FrameLayout flTouchView;
    protected ViewPager2 viewPager;
    protected TouchCloseLayout rootView;
    protected View contentView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        getWindow().setAllowEnterTransitionOverlap(true);
        getWindow().setExitTransition(TransitionInflater.from(this)
                .inflateTransition(R.transition.open_image_exit_transition));
        initPhotosViewModel();
        initRootView();
        parseIntent();
        initStyleConfig();

        super.onCreate(savedInstanceState);
        setContentView(contentView);

        initMoreView();
        initViewPager2();
        initTouchCloseLayout();
        if (isNoneClickView()){
            onShareTransitionEnd();
        }else {
            setViewTransition();
            addTransitionListener();
        }
    }

    protected void initRootView(){
        OpenImageActivityViewpagerBinding binding = OpenImageActivityViewpagerBinding.inflate(getLayoutInflater());
        vBg = binding.vBg;
        flTouchView = binding.flTouchView;
        viewPager = binding.viewPager;
        rootView = binding.getRoot();
        contentView = rootView;
    }

    private void initPhotosViewModel(){
        photosViewModel = new ViewModelProvider(this).get(PhotosViewModel.class);
        photosViewModel.closeViewLiveData.observe(this, integer -> close(false));
        photosViewModel.onAddOnSelectMediaListenerLiveData.observe(this, s -> {
            OnSelectMediaListener onSelectMediaListener = ImageLoadUtils.getInstance().getOnSelectMediaListener(s);
            if (onSelectMediaListener != null){
                onSelectMediaListeners.add(onSelectMediaListener);
            }
            onSelectMediaListenerKeys.add(s);
        });

        photosViewModel.onRemoveOnSelectMediaListenerLiveData.observe(this, s -> {
            OnSelectMediaListener onSelectMediaListener = ImageLoadUtils.getInstance().getOnSelectMediaListener(s);
            if (onSelectMediaListener != null){
                onSelectMediaListeners.remove(onSelectMediaListener);
            }
            ImageLoadUtils.getInstance().clearOnItemClickListener(s);
        });
    }

    protected void initTouchCloseLayout(){
        boolean disEnableTouchClose = getIntent().getBooleanExtra(OpenParams.DISABLE_TOUCH_CLOSE, false);
        float touchScaleClose = getIntent().getFloatExtra(OpenParams.TOUCH_CLOSE_SCALE, .76f);
        rootView.setTouchCloseScale(touchScaleClose);
        rootView.setTouchView(flTouchView, vBg);
        rootView.setDisEnableTouchClose(disEnableTouchClose);
        rootView.setOrientation(orientation == OpenImageOrientation.VERTICAL ? OpenImageOrientation.HORIZONTAL : OpenImageOrientation.VERTICAL);
        rootView.setOnTouchCloseListener(new TouchCloseLayout.OnTouchCloseListener() {
            @Override
            public void onStartTouch() {
                if (onBackView != null) {
                    onBackView.onStartTouchScale(showPosition);
                }
                if (fontStyle == FontStyle.FULL_SCREEN) {
                    StatusBarHelper.cancelFullScreen(ViewPagerActivity.this);
                }
                touchHideMoreView();
            }

            @Override
            public void onEndTouch() {
                if (onBackView != null) {
                    onBackView.onEndTouchScale(showPosition);
                }
                if (fontStyle == FontStyle.FULL_SCREEN) {
                    StatusBarHelper.setFullScreen(ViewPagerActivity.this);
                }
                showMoreView();
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
    }

    protected void initViewPager2(){
        int errorResId = getIntent().getIntExtra(OpenParams.ERROR_RES_ID, 0);
        float autoAspectRadio = getIntent().getFloatExtra(OpenParams.AUTO_ASPECT_RATIO,0);
        boolean disableClickClose = getIntent().getBooleanExtra(OpenParams.DISABLE_CLICK_CLOSE, false);
        VideoFragmentCreate videoCreate = ImageLoadUtils.getInstance().getVideoFragmentCreate(videoFragmentCreateKey);
        ImageFragmentCreate imageCreate = ImageLoadUtils.getInstance().getImageFragmentCreate(imageFragmentCreateKey);
        if (videoCreate == null){
            videoCreate = OpenImageConfig.getInstance().getVideoFragmentCreate();
        }
        if (imageCreate == null){
            imageCreate = OpenImageConfig.getInstance().getImageFragmentCreate();
        }
        VideoFragmentCreate videoFragmentCreate = videoCreate;
        ImageFragmentCreate imageFragmentCreate = imageCreate;
        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                OpenImageDetail openImageBean = openImageBeans.get(position);
                MediaType mediaType = openImageBean.getType();
                BaseFragment fragment = null;
                if (mediaType == MediaType.VIDEO) {
                    if (videoFragmentCreate != null) {
                        fragment = videoFragmentCreate.createVideoFragment();
                        if (fragment == null) {
                            throw new IllegalArgumentException(videoFragmentCreate.getClass().getName() + "?????????createVideoFragment");
                        }
                    } else {
                        if (fragment == null) {
                            throw new IllegalArgumentException("????????????????????????fragment --> OpenImageConfig");
                        }
                    }

                } else {
                    if (imageFragmentCreate != null) {
                        fragment = imageFragmentCreate.createImageFragment();
                    } else {
                        fragment = new ImageFragment();
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
                bundle.putFloat(OpenParams.AUTO_ASPECT_RATIO, autoAspectRadio);
                bundle.putBoolean(OpenParams.NONE_CLICK_VIEW, isNoneClickView());
                fragment.setArguments(bundle);
                return fragment;
            }

            @Override
            public int getItemCount() {
                return openImageBeans.size();
            }
        });
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            boolean isFirstBacked = false;

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                showPosition = position;
                setIndicatorPosition();
                showMoreView();
                if (position != selectPos && viewPager.getOffscreenPageLimit() != 1) {
                    viewPager.setOffscreenPageLimit(1);
                }
                if (isFirstBacked && onBackView != null) {
                    onBackView.onScrollPos(openImageBeans.get(showPosition).viewPosition);
                }
                if (onSelectMediaListener != null) {
                    onSelectMediaListener.onSelect(openImageBeans.get(showPosition).openImageUrl, openImageBeans.get(showPosition).dataPosition);
                }
                for (OnSelectMediaListener selectMediaListener : onSelectMediaListeners) {
                    selectMediaListener.onSelect(openImageBeans.get(showPosition).openImageUrl, openImageBeans.get(showPosition).dataPosition);
                }
                isFirstBacked = true;
            }
        });
        viewPager.setCurrentItem(selectPos, false);
    }

    protected void setViewTransition() {
        ViewCompat.setTransitionName(viewPager, OpenParams.SHARE_VIEW + selectPos);
    }

    protected void initMoreView() {
        if (moreViewKey != null) {
            List<MoreViewOption> viewOptions = ImageLoadUtils.getInstance().getMoreViewOption(moreViewKey);
            for (MoreViewOption moreViewOption : viewOptions) {
                View view = null;
                if (moreViewOption != null && moreViewOption.getViewType() == MoreViewOption.LAYOUT_RES) {
                    view = LayoutInflater.from(this).inflate(moreViewOption.getLayoutRes(), null, false);
                }if (moreViewOption != null && moreViewOption.getViewType() == MoreViewOption.LAYOUT_VIEW) {
                    view = moreViewOption.getView();
                }

                if (view != null){
                    if (moreViewOption.isFollowTouch()){
                        flTouchView.addView(view, moreViewOption.getLayoutParams());
                    }else {
                        rootView.addView(view, moreViewOption.getLayoutParams());
                    }
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

    protected void showMoreView() {
        if (moreViewOptions.size() > 0) {
            OpenImageDetail openImageDetail = openImageBeans.get(showPosition);
            MediaType mediaType = openImageDetail.getType();
            for (MoreViewOption moreViewOption : moreViewOptions) {
                MoreViewShowType showType = moreViewOption.getMoreViewShowType();
                if (mediaType == MediaType.IMAGE && (showType == MoreViewShowType.IMAGE || showType == MoreViewShowType.BOTH)) {
                    moreViewOption.getView().setVisibility(View.VISIBLE);
                } else if (mediaType == MediaType.VIDEO && (showType == MoreViewShowType.VIDEO || showType == MoreViewShowType.BOTH)) {
                    moreViewOption.getView().setVisibility(View.VISIBLE);
                } else {
                    moreViewOption.getView().setVisibility(View.GONE);
                }
            }
        }

        View upperView;
        if (upLayerFragment != null && (upperView = upLayerFragment.getView()) != null && upperLayerOption !=null && upperLayerOption.isTouchingHide()){
            upperView.setVisibility(View.VISIBLE);
        }
    }

    protected void touchHideMoreView() {
        if (moreViewOptions.size() > 0) {
            OpenImageDetail openImageDetail = openImageBeans.get(showPosition);
            MediaType mediaType = openImageDetail.getType();
            for (MoreViewOption moreViewOption : moreViewOptions) {
                MoreViewShowType showType = moreViewOption.getMoreViewShowType();
                if (mediaType == MediaType.IMAGE && (showType == MoreViewShowType.IMAGE || showType == MoreViewShowType.BOTH) && !moreViewOption.isFollowTouch()) {
                    moreViewOption.getView().setVisibility(View.GONE);
                } else if (mediaType == MediaType.VIDEO && (showType == MoreViewShowType.VIDEO || showType == MoreViewShowType.BOTH) && !moreViewOption.isFollowTouch()) {
                    moreViewOption.getView().setVisibility(View.GONE);
                } else {
                    moreViewOption.getView().setVisibility(View.VISIBLE);
                }
            }
        }
        View upperView;
        if (upLayerFragment != null && (upperView = upLayerFragment.getView()) != null && upperLayerOption !=null && upperLayerOption.isTouchingHide()){
            upperView.setVisibility(View.GONE);
        }
    }

    protected void setIndicatorPosition() {
        mHandler.post(() -> {
            if (indicatorType == INDICATOR_IMAGE) {//????????????
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
            fontStyle = FontStyle.getStyle(AttrsUtils.getTypeValueInt(this, themeRes, R.attr.openImage_statusBar_fontStyle));
            StatusBarHelper.translucent(this);
            if (fontStyle == FontStyle.LIGHT) {
                StatusBarHelper.setStatusBarLightMode(this);
            } else if (fontStyle == FontStyle.FULL_SCREEN) {
                StatusBarHelper.setFullScreen(this);
            } else {
                StatusBarHelper.setStatusBarDarkMode(this);
            }
            AttrsUtils.setBackgroundResourceOrColor(this, themeRes, R.attr.openImage_background, vBg);
            indicatorType = AttrsUtils.getTypeValueInt(this, themeRes, R.attr.openImage_indicator_type);
            orientation = OpenImageOrientation.getOrientation(AttrsUtils.getTypeValueInt(this, themeRes, R.attr.openImage_viewPager_orientation));
            if (indicatorType < 2 && openImageBeans.size() > 1) {
                if (indicatorType == INDICATOR_IMAGE) {//????????????
                    float interval = AttrsUtils.getTypeValueDimension(this, themeRes, R.attr.openImage_indicator_image_interval, -1);
                    int imageRes = AttrsUtils.getTypeValueResourceId(this, themeRes, R.attr.openImage_indicator_imageRes);
                    if (interval == -1) {
                        interval = ScreenUtils.dp2px(this, 4);
                    }
                    if (imageRes == 0) {
                        imageRes = R.drawable.open_image_indicator_image;
                    }
                    RecyclerView recyclerView = new RecyclerView(this);
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    setIndicatorLayoutParams(layoutParams, themeRes);
                    rootView.addView(recyclerView, layoutParams);

                    OpenImageOrientation realOrientation = OpenImageOrientation.getOrientation(AttrsUtils.getTypeValueInt(this, themeRes, R.attr.openImage_indicator_image_orientation));
                    imageIndicatorLayoutManager = new LinearLayoutManager(this, realOrientation == OpenImageOrientation.HORIZONTAL ? LinearLayoutManager.HORIZONTAL : LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(imageIndicatorLayoutManager);
                    imageIndicatorAdapter = new ImageIndicatorAdapter(openImageBeans.size(), interval, imageRes, realOrientation);
                    recyclerView.setAdapter(imageIndicatorAdapter);

                } else {
                    int textColor = AttrsUtils.getTypeValueColor(this, themeRes, R.attr.openImage_indicator_textColor, Color.WHITE);
                    float textSize = AttrsUtils.getTypeValueDimension(this, themeRes, R.attr.openImage_indicator_textSize);
                    indicatorTextBinding = OpenImageIndicatorTextBinding.inflate(getLayoutInflater(), rootView, true);
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) indicatorTextBinding.tvShowPos.getLayoutParams();
                    setIndicatorLayoutParams(layoutParams, themeRes);
                    indicatorTextBinding.tvShowPos.setLayoutParams(layoutParams);
                    indicatorTextBinding.tvShowPos.setTextColor(textColor);
                    if (textSize != 0) {
                        indicatorTextBinding.tvShowPos.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                    }
                    CharSequence strFormat = AttrsUtils.getTypeValueText(this, themeRes, R.attr.openImage_indicator_textFormat);
                    if (!TextUtils.isEmpty(strFormat)) {
                        textFormat = strFormat + "";
                    }
                }
            }
            int pageMargin = (int) AttrsUtils.getTypeValueDimension(this, themeRes, R.attr.openImage_viewPager_pageMargin, -1);
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
                indicatorTextBinding = OpenImageIndicatorTextBinding.inflate(getLayoutInflater(), rootView, true);
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
        viewPager.setPageTransformer(compositePageTransformer);

        int leftRightPadding = getIntent().getIntExtra(OpenParams.GALLERY_EFFECT_WIDTH, 0);
        if (leftRightPadding > 0) {
            View recyclerView = viewPager.getChildAt(0);
            if (recyclerView instanceof RecyclerView) {
                recyclerView.setPadding((int) ScreenUtils.dp2px(this, leftRightPadding), 0, (int) ScreenUtils.dp2px(this, leftRightPadding), 0);
                ((RecyclerView) recyclerView).setClipToPadding(false);
            }
        }

        if (orientation == OpenImageOrientation.VERTICAL) {
            viewPager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        } else {
            viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        }

    }

    private void setIndicatorLayoutParams(FrameLayout.LayoutParams layoutParams, int themeRes) {
        int gravity = AttrsUtils.getTypeValueInt(this, themeRes, R.attr.openImage_indicator_gravity);
        int topMargin = (int) AttrsUtils.getTypeValueDimension(this, themeRes, R.attr.openImage_indicator_marginTop);
        int bottomMargin = (int) AttrsUtils.getTypeValueDimension(this, themeRes, R.attr.openImage_indicator_marginBottom);
        int leftMargin = (int) AttrsUtils.getTypeValueDimension(this, themeRes, R.attr.openImage_indicator_marginLeft);
        int rightMargin = (int) AttrsUtils.getTypeValueDimension(this, themeRes, R.attr.openImage_indicator_marginRight);
        int startMargin = (int) AttrsUtils.getTypeValueDimension(this, themeRes, R.attr.openImage_indicator_marginStart);
        int endMargin = (int) AttrsUtils.getTypeValueDimension(this, themeRes, R.attr.openImage_indicator_marginEnd);
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

    protected void addTransitionListener() {
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
                    transition.removeListener(this);

                    onShareTransitionEnd();
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

    private void onShareTransitionEnd(){
        photosViewModel.transitionEndLiveData.setValue(true);
        mHandler.post(() -> {

            if (upperLayerOption != null && ViewPagerActivity.this.getLifecycle().getCurrentState() != Lifecycle.State.DESTROYED){
                UpperLayerFragmentCreate upperLayerCreate = upperLayerOption.getUpperLayerFragmentCreate();
                if (upperLayerCreate != null){
                    upLayerFragment = upperLayerCreate.createLayerFragment();
                }
                if (upLayerFragment != null){
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    FrameLayout frameLayout = new FrameLayout(ViewPagerActivity.this);
                    frameLayout.setId(R.id.upper_layer_container);
                    Bundle upperLayerBundle = getIntent().getBundleExtra(OpenParams.UPPER_LAYER_BUNDLE);
                    if (upperLayerBundle != null){
                        upLayerFragment.setArguments(upperLayerBundle);
                    }
                    if (upperLayerOption.isFollowTouch()){
                        flTouchView.addView(frameLayout,new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    }else {
                        rootView.addView(frameLayout,new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    }
                    transaction.replace(R.id.upper_layer_container,upLayerFragment).commit();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        showPosition = 0;
        mHandler.removeCallbacksAndMessages(null);
        ImageLoadUtils.getInstance().clearItemLoadHelper(itemLoadKey);
        ImageLoadUtils.getInstance().clearOnSelectMediaListener(onSelectKey);
        ImageLoadUtils.getInstance().clearCoverDrawable(openCoverKey);
        ImageLoadUtils.getInstance().clearPageTransformers(pageTransformersKey);
        ImageLoadUtils.getInstance().clearOnItemClickListener(onItemCLickKey);
        ImageLoadUtils.getInstance().clearOnItemLongClickListener(onItemLongCLickKey);
        ImageLoadUtils.getInstance().clearMoreViewOption(moreViewKey);
        ImageLoadUtils.getInstance().clearOnBackView(onBackViewKey);
        ImageLoadUtils.getInstance().clearImageFragmentCreate(imageFragmentCreateKey);
        ImageLoadUtils.getInstance().clearVideoFragmentCreate(videoFragmentCreateKey);
        ImageLoadUtils.getInstance().clearUpperLayerFragmentCreate(upperLayerFragmentCreateKey);
        if (wechatEffectAnim != null) {
            wechatEffectAnim.cancel();
        }
        for (String onSelectMediaListenerKey : onSelectMediaListenerKeys) {
            ImageLoadUtils.getInstance().clearOnSelectMediaListener(onSelectMediaListenerKey);
        }
        onSelectMediaListenerKeys.clear();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (canFragmentBack()){
                close(false);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void setExitView() {
        BackViewType backViewType = BackViewType.NO_SHARE;
        ImageView backView = null;
        ExitOnBackView.ShareExitViewBean shareExitViewBean;
        if (onBackView != null && (shareExitViewBean = onBackView.onBack(showPosition)) != null) {
            backViewType = shareExitViewBean.backViewType;
            backView = shareExitViewBean.shareExitView;
        }
        if (backViewType == BackViewType.NO_SHARE) {
            ViewCompat.setTransitionName(viewPager, "");
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
        final ImageView exitView = backView;
        if (shareView != null) {
            ViewCompat.setTransitionName(viewPager, "");
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
                    if (exitView instanceof ShapeImageView){
                        ShapeImageView.ShapeScaleType ShapeScaleType = ((ShapeImageView) exitView).getShapeScaleType();
                        if (shareView instanceof PhotoView){
                            ((PhotoView) shareView).setSrcScaleType(ShapeScaleType);
                            if (ShapeScaleType == ShapeImageView.ShapeScaleType.AUTO_START_CENTER_CROP || srcScaleType == ShapeImageView.ShapeScaleType.AUTO_END_CENTER_CROP){
                                ((PhotoView) shareView).setAutoCropHeightWidthRatio( ((ShapeImageView) exitView).getAutoCropHeightWidthRatio());
                            }
                        }
                    }
                    if (shareView instanceof PhotoView){
                        ((PhotoView) shareView).setStartWidth(exitView.getWidth());
                        ((PhotoView) shareView).setStartHeight(exitView.getHeight());
                    }
                    sharedElements.put(OpenParams.SHARE_VIEW + showPosition, shareView);
                }
            });
        } else {
            if (backViewType == BackViewType.SHARE_WECHAT) {
                addWechatEffect(viewPager);
            }
            ViewCompat.setTransitionName(viewPager, OpenParams.SHARE_VIEW + showPosition);
            setEnterSharedElementCallback(new SharedElementCallback() {
                @Override
                public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                    super.onMapSharedElements(names, sharedElements);
                    if (names.size() == 0) {
                        return;
                    }
                    sharedElements.put(OpenParams.SHARE_VIEW + showPosition, viewPager);
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

    protected void close(boolean isTouchClose) {
        if (isNoneClickView()){
            finish();
            return;
        }
        if (isCallClosed) {
            return;
        }
        if (onBackView != null) {
            onBackView.onTouchClose(isTouchClose);
        }
        touchHideMoreView();
        setExitView();
        if (!isTouchClose && fontStyle == FontStyle.FULL_SCREEN) {
            StatusBarHelper.cancelFullScreen(ViewPagerActivity.this);
            mHandler.postDelayed(this::finishAfterTransition, 100);
        } else {
            finishAfterTransition();
        }
        isCallClosed = true;
    }

    private View getCoverView() {
        Fragment fragment = getCurrentFragment();
        if (fragment instanceof BaseFragment) {
            BaseFragment baseFragment = (BaseFragment) fragment;
            return baseFragment.getExitImageView();
        }
        return null;
    }

    private Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentByTag("f"+showPosition);
    }

    public boolean canFragmentBack(){
        boolean canLayerBack = true;
        boolean canImageBack = true;
        //?????????????????????
        if (upLayerFragment != null){
            canLayerBack = upLayerFragment.onKeyBackDown();
        }
        //??????????????????????????????
        Fragment fragment = getCurrentFragment();
        if (fragment instanceof BaseInnerFragment) {
            BaseInnerFragment baseFragment = (BaseInnerFragment) fragment;
            canImageBack = baseFragment.onKeyBackDown();
        }
        return canLayerBack && canImageBack;
    }

    public View getBgView() {
        return vBg;
    }

    public FrameLayout getTouchView() {
        return flTouchView;
    }

    public ViewPager2 getViewPager() {
        return viewPager;
    }

    public TouchCloseLayout getRootView() {
        return rootView;
    }
}
