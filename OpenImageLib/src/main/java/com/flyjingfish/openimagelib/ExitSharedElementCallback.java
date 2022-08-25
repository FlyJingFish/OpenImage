package com.flyjingfish.openimagelib;

import android.app.SharedElementCallback;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.util.LayoutDirection;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.text.TextUtilsCompat;

import com.flyjingfish.openimagelib.utils.ActivityCompatHelper;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Deprecated
class ExitSharedElementCallback extends SharedElementCallback {
    private final Context context;
    private View backView;
    private ImageView exitView;
    protected Float startAlpha;
    protected Integer startVisibility;
    protected Drawable startDrawable;
    protected Drawable startBackgroundDrawable;
    private final ImageView.ScaleType srcImageViewScaleType;
    private final ImageView shareExitMapView;
    private Rect paddingRect;
    private final boolean isRtl;
    private float startSrcAlpha;
    private final boolean showSrcImageView;
    private final Float showCurrentViewStartAlpha;

    public ExitSharedElementCallback(Context context, ImageView.ScaleType srcImageViewScaleType, ImageView shareExitMapView, boolean showSrcImageView,Float showCurrentViewStartAlpha) {
        this.context = context;
        this.srcImageViewScaleType = srcImageViewScaleType;
        this.shareExitMapView = shareExitMapView;
        this.showSrcImageView = showSrcImageView;
        this.showCurrentViewStartAlpha = showCurrentViewStartAlpha;
        isRtl = TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == LayoutDirection.RTL;
    }

    @Override
    public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
        super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots);
        removeBackView();
        if (!showSrcImageView && shareExitMapView != null){
            shareExitMapView.setAlpha(Math.max(startAlpha,startSrcAlpha));
        }
    }

    @Override
    public Parcelable onCaptureSharedElementSnapshot(View sharedElement, Matrix viewToGlobalMatrix, RectF screenBounds) {
        Parcelable parcelable = super.onCaptureSharedElementSnapshot(sharedElement, viewToGlobalMatrix, screenBounds);
        if (showSrcImageView){
            if (screenBounds != null && sharedElement != null) {
                Rect showRect = new Rect();
                sharedElement.getLocalVisibleRect(showRect);
                initSrcViews(screenBounds, showRect);
            }
            if (exitView != null && startDrawable != null) {
                exitView.setPadding(paddingRect.left,paddingRect.top,paddingRect.right,paddingRect.bottom);
                exitView.setImageDrawable(startDrawable);
                if (startAlpha != null){
                    exitView.setAlpha(startAlpha);
                }
                if (startBackgroundDrawable != null){
                    exitView.setBackground(startBackgroundDrawable);
                }
            } else if (sharedElement != null && startAlpha != null){
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
            startDrawable = shareExitMapView.getDrawable();
            startBackgroundDrawable = shareExitMapView.getBackground();
            paddingRect = new Rect();
            paddingRect.left = isRtl ?Math.max(shareExitMapView.getPaddingLeft(),shareExitMapView.getPaddingEnd()):Math.max(shareExitMapView.getPaddingLeft(),shareExitMapView.getPaddingStart());
            paddingRect.right = isRtl ?Math.max(shareExitMapView.getPaddingRight(),shareExitMapView.getPaddingStart()):Math.max(shareExitMapView.getPaddingRight(),shareExitMapView.getPaddingEnd());
            paddingRect.top = shareExitMapView.getPaddingTop();
            paddingRect.bottom = shareExitMapView.getPaddingBottom();
            sharedEls.put(name, shareExitMapView);
            if (!showSrcImageView){
                startSrcAlpha = showCurrentViewStartAlpha != null?showCurrentViewStartAlpha:shareExitMapView.getAlpha();
                shareExitMapView.setAlpha(0f);
            }
        } else {
            sharedEls.clear();
            names.clear();
        }
    }


    private void initSrcViews(@NonNull RectF screenBounds, Rect showRect) {
        if (context == null) {
            return;
        }
        ViewGroup rootView = (ViewGroup) ActivityCompatHelper.getWindow(context).getDecorView();
        if (backView != null) {
            rootView.removeView(backView);
        }
        FrameLayout rootInView = new FrameLayout(context);
        rootInView.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        backView = rootInView;
        FrameLayout.LayoutParams rootInLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rootView.addView(rootInView, rootInLayoutParams);
        int[] location = new int[2];
        rootInView.getLocationOnScreen(location);

        exitView = new ImageView(context);
        exitView.setScaleType(srcImageViewScaleType);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int) screenBounds.width(), (int) screenBounds.height());
        params.topMargin = (int) screenBounds.top - location[1];
        params.leftMargin = (int) screenBounds.left  - location[0];
        rootInView.addView(exitView, params);
        exitView.setClipBounds(showRect);
    }

    private void removeBackView() {
        if (backView != null) {
            ViewGroup rootView = (ViewGroup) ActivityCompatHelper.getWindow(context).getDecorView();
            rootView.removeView(backView);
        }
    }

}
