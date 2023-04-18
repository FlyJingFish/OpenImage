package com.flyjingfish.openimagelib;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.SharedElementCallback;
import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageView;


import com.flyjingfish.openimagelib.utils.ActivityCompatHelper;

import java.util.List;
import java.util.Map;

class ExitSharedElementCallback2 extends SharedElementCallback {
    protected Context context;
    protected Float startAlpha;
    protected Integer startVisibility;
    private final ImageView shareExitMapView;
    private float startSrcAlpha;
    private final boolean showSrcImageView;
    private final boolean isClipSrcImageView;
    private final Float showCurrentViewStartAlpha;

    public ExitSharedElementCallback2(Context context, ImageView shareExitMapView, boolean showSrcImageView, Float showCurrentViewStartAlpha,boolean isClipSrcImageView) {
        this.context = context;
        this.shareExitMapView = shareExitMapView;
        this.showSrcImageView = showSrcImageView;
        this.showCurrentViewStartAlpha = showCurrentViewStartAlpha;
        this.isClipSrcImageView = isClipSrcImageView;
    }

    @Override
    public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
        super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots);
        if (shareExitMapView != null) {
            new Handler(Looper.getMainLooper()).post(() -> shareExitMapView.setClipBounds(null));
        }
        if (!showSrcImageView && shareExitMapView != null) {
            shareExitMapView.setAlpha(Math.max(startAlpha, startSrcAlpha));
        }
        Activity activity = ActivityCompatHelper.getActivity(context);
        if (activity != null){
            activity.setExitSharedElementCallback(null);
            fixAndroid12Bug();
        }
    }

    private void fixAndroid12Bug(){
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R && OpenImageConfig.getInstance().isFixAndroid12OnBackPressed()) {
            context.startActivity(new Intent(context, FixAndroid12BugActivity.class), ActivityOptions.makeSceneTransitionAnimation((Activity) context).toBundle());
        }
    }

    @Override
    public Parcelable onCaptureSharedElementSnapshot(View sharedElement, Matrix viewToGlobalMatrix, RectF screenBounds) {
        Parcelable parcelable = super.onCaptureSharedElementSnapshot(sharedElement, viewToGlobalMatrix, screenBounds);
        if (showSrcImageView && sharedElement != null) {
            if (isClipSrcImageView){
                Rect showRect = new Rect();
                sharedElement.getLocalVisibleRect(showRect);
                sharedElement.setClipBounds(showRect);
            }
            if (startAlpha != null) {
                sharedElement.setAlpha(startAlpha);
                sharedElement.setVisibility(startVisibility);
            }
        }
        return parcelable;
    }

    @Override
    public void onMapSharedElements(List<String> names, Map<String, View> sharedEls) {
        super.onMapSharedElements(names, sharedEls);
        if (names.size() == 0) {
            return;
        }
        String name = names.get(0);

        if (shareExitMapView != null) {
            startAlpha = shareExitMapView.getAlpha();
            startVisibility = shareExitMapView.getVisibility();
            sharedEls.put(name, shareExitMapView);
            if (!showSrcImageView) {
                startSrcAlpha = showCurrentViewStartAlpha != null ? showCurrentViewStartAlpha : shareExitMapView.getAlpha();
                shareExitMapView.setAlpha(0f);
            }
        } else {
            sharedEls.clear();
            names.clear();
        }
    }


}
