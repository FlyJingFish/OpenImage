package com.flyjingfish.openimagelib;

import android.app.SharedElementCallback;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.flyjingfish.openimagelib.utils.ActivityCompatHelper;

import java.util.List;
import java.util.Map;

class ExitSharedElementCallback extends SharedElementCallback {
    private Context context;
    private View backView;
    private ImageView exitView;
    protected Float startAlpha;
    protected Integer startVisibility;
    protected Drawable startDrawable;
    private ImageView.ScaleType srcImageViewScaleType;
    private ImageView shareExitMapView;

    public ExitSharedElementCallback(Context context, ImageView.ScaleType srcImageViewScaleType, ImageView shareExitMapView) {
        this.context = context;
        this.srcImageViewScaleType = srcImageViewScaleType;
        this.shareExitMapView = shareExitMapView;
    }

    @Override
    public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
        super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots);
        removeBackView();
    }

    @Override
    public Parcelable onCaptureSharedElementSnapshot(View sharedElement, Matrix viewToGlobalMatrix, RectF screenBounds) {
        Parcelable parcelable = super.onCaptureSharedElementSnapshot(sharedElement, viewToGlobalMatrix, screenBounds);
        if (screenBounds != null && sharedElement != null) {
            Rect showRect = new Rect();
            sharedElement.getLocalVisibleRect(showRect);
            initSrcViews(screenBounds, showRect);
        }
        if (exitView != null && startDrawable != null) {
            exitView.setImageDrawable(startDrawable);
        } else if (sharedElement != null && startAlpha != null){
            sharedElement.setAlpha(startAlpha);
            sharedElement.setVisibility(startVisibility);
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
            startDrawable = shareExitMapView.getDrawable();
            sharedEls.put(name, shareExitMapView);
        } else {
            sharedEls.clear();
            names.clear();
        }
    }

    private void initSrcViews(RectF screenBounds, Rect showRect) {
        if (context == null) {
            return;
        }
        ViewGroup rootView = (ViewGroup) ActivityCompatHelper.getWindow(context).getDecorView();
        if (backView != null) {
            rootView.removeView(backView);
        }
        FrameLayout flBelowView = new FrameLayout(context);
        backView = flBelowView;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (screenBounds != null) {
            layoutParams.topMargin = (int) screenBounds.top + showRect.top;
            layoutParams.leftMargin = (int) screenBounds.left + showRect.left;
            layoutParams.width = showRect.width();
            layoutParams.height = showRect.height();
        }
        rootView.addView(flBelowView, layoutParams);
        exitView = new ImageView(context);
        exitView.setScaleType(srcImageViewScaleType);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int) screenBounds.width(), (int) screenBounds.height());
        params.leftMargin = -showRect.left;
        params.topMargin = -showRect.top;
        flBelowView.addView(exitView, params);
    }

    private void removeBackView() {
        if (backView != null) {
            ViewGroup rootView = (ViewGroup) ActivityCompatHelper.getWindow(context).getDecorView();
            rootView.removeView(backView);
        }
    }

}