package com.flyjingfish.openimagelib;

import android.app.SharedElementCallback;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
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

class ExitSharedElementCallback2 extends SharedElementCallback {
    private final Context context;
    protected Float startAlpha;
    protected Integer startVisibility;
    private final ImageView.ScaleType srcImageViewScaleType;
    private final ImageView shareExitMapView;
    private float startSrcAlpha;
    private final boolean showSrcImageView;
    private final Float showCurrentViewStartAlpha;

    public ExitSharedElementCallback2(Context context, ImageView.ScaleType srcImageViewScaleType, ImageView shareExitMapView, boolean showSrcImageView, Float showCurrentViewStartAlpha) {
        this.context = context;
        this.srcImageViewScaleType = srcImageViewScaleType;
        this.shareExitMapView = shareExitMapView;
        this.showSrcImageView = showSrcImageView;
        this.showCurrentViewStartAlpha = showCurrentViewStartAlpha;
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
    }

    @Override
    public Parcelable onCaptureSharedElementSnapshot(View sharedElement, Matrix viewToGlobalMatrix, RectF screenBounds) {
        Parcelable parcelable = super.onCaptureSharedElementSnapshot(sharedElement, viewToGlobalMatrix, screenBounds);
        if (showSrcImageView && sharedElement != null) {
            Rect showRect = new Rect();
            sharedElement.getLocalVisibleRect(showRect);
            sharedElement.setClipBounds(showRect);
            if (startAlpha != null){
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
