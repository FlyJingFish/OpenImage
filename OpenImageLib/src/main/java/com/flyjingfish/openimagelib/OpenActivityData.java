package com.flyjingfish.openimagelib;

import android.content.Intent;

import androidx.fragment.app.FragmentActivity;

import com.flyjingfish.openimagelib.listener.OnSelectMediaListener;
import com.flyjingfish.shapeimageviewlib.ShapeImageView;

import java.util.ArrayList;
import java.util.List;

class OpenActivityData {
    final List<OpenImageDetail> openImageBeans = new ArrayList<>();
    String dataKey;
    int showPosition;
    int selectPos;
    String onSelectKey;
    String onPermissionKey;
    String openCoverKey;
    String onItemCLickKey;
    String onItemLongCLickKey;
    String moreViewKey;
    String onBackViewKey;
    String videoFragmentCreateKey;
    String imageFragmentCreateKey;
    String upperLayerFragmentCreateKey;
    OnSelectMediaListener onSelectMediaListener;
    ImageLoadUtils.OnBackView onBackView;
    ImageShapeParams imageShapeParams;
    boolean wechatExitFillInEffect;

    ShapeImageView.ShapeScaleType srcScaleType;
    UpperLayerOption upperLayerOption;

    int preloadCount;
    boolean lazyPreload;
    boolean bothLoadCover;
    String clickContextKey;
    boolean isNoneClickView;

    protected boolean parseIntent() {

        int srcScaleTypeInt = getIntent().getIntExtra(OpenParams.SRC_SCALE_TYPE, -1);
        srcScaleType = srcScaleTypeInt == -1 ? null : ShapeImageView.ShapeScaleType.values()[srcScaleTypeInt];
        dataKey = getIntent().getStringExtra(OpenParams.IMAGES);
        List<OpenImageDetail> openImageList = ImageLoadUtils.getInstance().getOpenImageDetailData(dataKey);
        if (openImageList == null) {
            finishAfterTransition();
            return true;
        }
        openImageBeans.addAll(openImageList);
        if (openImageBeans.size() == 0) {
            finishAfterTransition();
            return true;
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
        wechatExitFillInEffect = getIntent().getBooleanExtra(OpenParams.WECHAT_EXIT_FILL_IN_EFFECT, false);
        onPermissionKey = getIntent().getStringExtra(OpenParams.PERMISSION_LISTENER);
        preloadCount = getIntent().getIntExtra(OpenParams.PRELOAD_COUNT, 1);
        lazyPreload = getIntent().getBooleanExtra(OpenParams.LAZY_PRELOAD, false);
        bothLoadCover = getIntent().getBooleanExtra(OpenParams.BOTH_LOAD_COVER, false);

        return false;
    }

    private Intent getIntent() {
        return activity.getIntent();
    }

    private FragmentActivity activity;

    public void setActivity(FragmentActivity activity) {
        this.activity = activity;
    }

    public void finishAfterTransition() {
        activity.finishAfterTransition();
    }
}
