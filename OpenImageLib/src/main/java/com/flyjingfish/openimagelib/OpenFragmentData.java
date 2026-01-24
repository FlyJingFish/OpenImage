package com.flyjingfish.openimagelib;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.listener.OnItemClickListener;
import com.flyjingfish.openimagelib.listener.OnItemLongClickListener;
import com.flyjingfish.shapeimageviewlib.ShapeImageView;

import java.util.ArrayList;
import java.util.List;

class OpenFragmentData {
    OpenImageDetail imageDetail;
    @Nullable
    OpenImageUrl openImageUrl;
    int showPosition, clickPosition;
    boolean disableClickClose;
    int errorResId;
    Drawable coverDrawable;
    String coverFilePath;
    Drawable smallCoverDrawable;
    boolean isNoneClickView;
    String dataKey;

    ShapeImageView.ShapeScaleType srcScaleType;
    float autoAspectRadio;
    long beanId;
    int preloadCount;
    boolean lazyPreload;
    boolean bothLoadCover;
    List<OnItemClickListener> onItemClickListeners = new ArrayList<>();
    List<OnItemLongClickListener> onItemLongClickListeners = new ArrayList<>();
    String openLive;
    String closeLive;
    String live;
    String replay;
    boolean parseIntent() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            dataKey = bundle.getString(OpenParams.IMAGE);
            imageDetail = ImageLoadUtils.getInstance().getOpenImageDetail(dataKey);
            if (imageDetail == null) {
                return true;
            }
            openImageUrl = imageDetail.openImageUrl;
            showPosition = bundle.getInt(OpenParams.SHOW_POSITION);
            clickPosition = bundle.getInt(OpenParams.CLICK_POSITION);
            int srcScaleTypeInt = bundle.getInt(OpenParams.SRC_SCALE_TYPE, -1);
            srcScaleType = srcScaleTypeInt == -1 ? null : ShapeImageView.ShapeScaleType.values()[srcScaleTypeInt];

            errorResId = bundle.getInt(OpenParams.ERROR_RES_ID, 0);
            disableClickClose = bundle.getBoolean(OpenParams.DISABLE_CLICK_CLOSE, false);
            String onItemCLickKey = bundle.getString(OpenParams.ON_ITEM_CLICK_KEY);
            String onItemLongCLickKey = bundle.getString(OpenParams.ON_ITEM_LONG_CLICK_KEY);
            OnItemClickListener onItemClickListener = ImageLoadUtils.getInstance().getOnItemClickListener(onItemCLickKey);
            OnItemLongClickListener onItemLongClickListener = ImageLoadUtils.getInstance().getOnItemLongClickListener(onItemLongCLickKey);
            if (onItemClickListener != null) {
                onItemClickListeners.add(onItemClickListener);
            }
            if (onItemLongClickListener != null) {
                onItemLongClickListeners.add(onItemLongClickListener);
            }
            String drawableKey = openImageUrl.toString();
            coverDrawable = ImageLoadUtils.getInstance().getCoverDrawable(drawableKey);
            coverFilePath = ImageLoadUtils.getInstance().getCoverFilePath(bundle.getString(OpenParams.OPEN_COVER_DRAWABLE));
            smallCoverDrawable = ImageLoadUtils.getInstance().getSmallCoverDrawable(drawableKey);
            ImageLoadUtils.getInstance().clearSmallCoverDrawable(drawableKey);

            autoAspectRadio = bundle.getFloat(OpenParams.AUTO_ASPECT_RATIO, 0);
            isNoneClickView = bundle.getBoolean(OpenParams.NONE_CLICK_VIEW, false);
            preloadCount = bundle.getInt(OpenParams.PRELOAD_COUNT, 1);
            lazyPreload = bundle.getBoolean(OpenParams.LAZY_PRELOAD, false);
            bothLoadCover = bundle.getBoolean(OpenParams.BOTH_LOAD_COVER, false);
            openLive = bundle.getString(OpenParams.OPEN_LIVE, "");
            closeLive = bundle.getString(OpenParams.CLOSE_LIVE, "");
            live = bundle.getString(OpenParams.LIVE, "");
            replay = bundle.getString(OpenParams.REPLAY, "");
            beanId = imageDetail.getId();
        }

        return false;
    }

    private Bundle getArguments() {
        return fragment.getArguments();
    }

    private Fragment fragment;

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

}
