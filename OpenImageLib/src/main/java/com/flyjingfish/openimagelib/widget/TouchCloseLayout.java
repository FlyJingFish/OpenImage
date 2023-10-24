package com.flyjingfish.openimagelib.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.LayoutDirection;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import androidx.annotation.FloatRange;
import androidx.core.text.TextUtilsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.flyjingfish.openimagelib.enums.OpenImageOrientation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TouchCloseLayout extends FrameLayout {

    private AnimatorSet touchAnim;
    private ObjectAnimator touchYAnim;
    private ObjectAnimator touchXAnim;
    private ObjectAnimator touchScaleXAnim;
    private ObjectAnimator touchScaleYAnim;
    private OnTouchCloseListener setOnTouchCloseListener;
    private final OnTouchCloseListener onTouchCloseListener = new OnTouchCloseListener() {
        @Override
        public void onStartTouch() {
            for (OnTouchCloseListener onTouchCloseListener : onTouchCloseListeners) {
                onTouchCloseListener.onStartTouch();
            }
        }

        @Override
        public void onEndTouch() {
            for (OnTouchCloseListener onTouchCloseListener : onTouchCloseListeners) {
                onTouchCloseListener.onEndTouch();
            }
        }

        @Override
        public void onTouchScale(float scale) {
            for (OnTouchCloseListener onTouchCloseListener : onTouchCloseListeners) {
                onTouchCloseListener.onTouchScale(scale);
            }
        }

        @Override
        public void onTouchClose(float scale) {
            for (OnTouchCloseListener onTouchCloseListener : onTouchCloseListeners) {
                onTouchCloseListener.onTouchClose(scale);
            }
        }
    };
    private final List<OnTouchCloseListener> onTouchCloseListeners = new ArrayList<>();
    private ObjectAnimator bgViewAnim;
    private float scale = 1f;
    private final boolean isRtl;
    private float startDragX;
    private float startDragY;

    private static final float SCALE_CLOSE = .76f;
    private float touchCloseScale = SCALE_CLOSE;
    private View touchView;
    private View bgView;
    private boolean disEnableTouchClose;
    private OpenImageOrientation orientation;
    private int startX = 0;
    private int startY = 0;
    private ViewPager2 viewPager2;
    private final VelocityTracker velocityTracker = VelocityTracker.obtain();

    private final ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());

    private final int minVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
    private final int maxVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
    private final float DRAG_SPEED = (maxVelocity - minVelocity)/1000f * 0.1f;

    private final int minTouchSlop = viewConfiguration.getScaledTouchSlop();

    public TouchCloseLayout(Context context) {
        this(context, null);
    }

    public TouchCloseLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        isRtl = TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == LayoutDirection.RTL;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (disEnableTouchClose || ev.getPointerCount() > 1) {
            return super.onInterceptTouchEvent(ev);
        }
        if (ev.getAction() == MotionEvent.ACTION_DOWN){
            velocityTracker.clear();
        }
        velocityTracker.addMovement(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) ev.getX();
                startY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int endX = (int) ev.getX();
                int endY = (int) ev.getY();
                int disX = Math.abs(endX - startX);
                int disY = Math.abs(endY - startY);
                if ((orientation == OpenImageOrientation.HORIZONTAL && disX > disY && (isRtl?endX < startX:endX > startX) && isCanScroll() && disX > minTouchSlop)
                        || (orientation == OpenImageOrientation.VERTICAL && disY > disX && endY > startY && isCanScroll() && disY > minTouchSlop)) {
                    onTouchCloseListener.onStartTouch();
                    getParent().requestDisallowInterceptTouchEvent(true);
                    return true;
                } else {
                    return super.onInterceptTouchEvent(ev);
                }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (disEnableTouchClose || e.getPointerCount() > 1) {
            return super.onTouchEvent(e);
        }
        if (e.getAction() == MotionEvent.ACTION_DOWN){
            velocityTracker.clear();
        }
        velocityTracker.addMovement(e);
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) e.getX();
                startY = (int) e.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (startDragY == 0) {
                    startDragY = e.getRawY();
                }
                if (startDragX == 0) {
                    startDragX = e.getRawX();
                }
                float moveY = e.getRawY() - startDragY;
                float moveX = e.getRawX() - startDragX;
                touchView.setTranslationY(moveY);
                touchView.setTranslationX(moveX);
                if (orientation == OpenImageOrientation.HORIZONTAL) {
                    scale = (getWidth() - (isRtl?-1:1)*moveX) / getWidth();
                } else {
                    scale = (getHeight() - moveY) / getHeight();
                }

                if (scale > 1) {
                    scale = 1;
                }
                touchView.setScaleX(scale);
                touchView.setScaleY(scale);
                if (bgView != null){
                    bgView.setAlpha(scale);
                }
                onTouchCloseListener.onTouchScale(scale);

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                velocityTracker.computeCurrentVelocity(1,maxVelocity);
                float velocity = orientation == OpenImageOrientation.HORIZONTAL ?velocityTracker.getXVelocity():velocityTracker.getYVelocity();
                if (velocity > DRAG_SPEED || scale < touchCloseScale) {
                    onTouchCloseListener.onTouchClose(scale);
                } else {
                    if (touchView.getTranslationY() != 0 || touchView.getTranslationX() != 0) {
                        touchYAnim.setFloatValues(touchView.getTranslationY(), 0);
                        touchXAnim.setFloatValues(touchView.getTranslationX(), 0);
                        touchScaleXAnim.setFloatValues(touchView.getScaleX(), 1);
                        touchScaleYAnim.setFloatValues(touchView.getScaleY(), 1);
                        if (bgViewAnim != null && bgView != null){
                            bgViewAnim.setFloatValues(bgView.getAlpha(), 1);
                        }
                        touchAnim.start();
                    }
                }

                startDragY = 0;
                startDragX = 0;
                break;
        }
        return true;
    }

    private boolean isCanScroll(){
        if (viewPager2 != null){
            if (orientation == OpenImageOrientation.VERTICAL && viewPager2.getOrientation() == ViewPager2.ORIENTATION_VERTICAL){
                return viewPager2.getCurrentItem() == 0;
            }else if (orientation == OpenImageOrientation.HORIZONTAL && viewPager2.getOrientation() == ViewPager2.ORIENTATION_HORIZONTAL){
                return viewPager2.getCurrentItem() == 0;
            }else {
                return orientation == OpenImageOrientation.HORIZONTAL && viewPager2.getOrientation() == ViewPager2.ORIENTATION_VERTICAL
                        || orientation == OpenImageOrientation.VERTICAL && viewPager2.getOrientation() == ViewPager2.ORIENTATION_HORIZONTAL;
            }
        }else {
            return true;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (touchAnim != null) {
            touchAnim.removeAllListeners();
            touchAnim.cancel();
        }

        if (touchScaleXAnim != null) {
            touchScaleXAnim.removeAllListeners();
            touchScaleXAnim.cancel();
        }

    }

    public interface OnTouchCloseListener {
        void onStartTouch();
        void onEndTouch();
        void onTouchScale(float scale);

        void onTouchClose(float scale);
    }

    /**
     * @param touchView 下拉触摸缩放的View
     * @param bgView 需要改变透明度的背景View
     */
    public void setTouchView(View touchView, View bgView) {
        this.touchView = touchView;
        this.bgView = bgView;
        touchYAnim = ObjectAnimator.ofFloat(touchView, "translationY", 0, 0);
        touchXAnim = ObjectAnimator.ofFloat(touchView, "translationX", 0, 0);
        touchScaleXAnim = ObjectAnimator.ofFloat(touchView, "scaleX", 1, 1);
        touchScaleYAnim = ObjectAnimator.ofFloat(touchView, "scaleY", 1, 1);
        if (bgView != null){
            bgViewAnim = ObjectAnimator.ofFloat(bgView, "alpha", 1, 1);
        }
        touchAnim = new AnimatorSet();
        touchScaleXAnim.addUpdateListener(animation -> {
            float scaleX = touchView.getScaleX();
            onTouchCloseListener.onTouchScale(scaleX);
        });
        if (bgViewAnim != null){
            touchAnim.playTogether(touchXAnim, touchYAnim, touchScaleXAnim, touchScaleYAnim, bgViewAnim);
        }else {
            touchAnim.playTogether(touchXAnim, touchYAnim, touchScaleXAnim, touchScaleYAnim);
        }
        touchAnim.setDuration(200);
        touchAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                onTouchCloseListener.onTouchScale(1f);
                onTouchCloseListener.onEndTouch();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                onTouchCloseListener.onEndTouch();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    /**
     * 如果不需要背景透明度变化可以使用此方法
     * @param touchView 下拉触摸缩放的View
     */
    public void setTouchView(View touchView) {
        setTouchView(touchView,null);
    }

    /**
     * 设置相册{@link ViewPager2}
     * @param viewPager2 相册{@link ViewPager2}
     */
    public void setViewPager2(ViewPager2 viewPager2) {
        this.viewPager2 = viewPager2;
    }

    /**
     * 设置触摸监听
     * @param onTouchCloseListener
     */
    public void setOnTouchCloseListener(OnTouchCloseListener onTouchCloseListener) {
        if (onTouchCloseListener != null){
            setOnTouchCloseListener = onTouchCloseListener;
            addOnTouchCloseListener(onTouchCloseListener);
        }else if (setOnTouchCloseListener != null){
            removeOnTouchCloseListener(setOnTouchCloseListener);
        }
    }

    /**
     * 添加触摸监听
     * @param onTouchCloseListener
     */
    public void addOnTouchCloseListener(OnTouchCloseListener onTouchCloseListener) {
        onTouchCloseListeners.add(onTouchCloseListener);
    }

    /**
     * 移除触摸监听
     * @param onTouchCloseListener
     */
    public void removeOnTouchCloseListener(OnTouchCloseListener onTouchCloseListener) {
        onTouchCloseListeners.remove(onTouchCloseListener);
    }

    /**
     *
     * @return 是否关闭触摸关闭功能
     */
    public boolean isDisEnableTouchClose() {
        return disEnableTouchClose;
    }

    /**
     *
     * @param disEnableTouchClose 是否关闭触摸关闭功能
     */
    public void setDisEnableTouchClose(boolean disEnableTouchClose) {
        this.disEnableTouchClose = disEnableTouchClose;
    }

    /**
     *
     * @return 触摸下拉的缩放比例小于多少时关闭页面
     */
    public float getTouchCloseScale() {
        return touchCloseScale;
    }

    /**
     *
     * @param touchCloseScale 触摸下拉的缩放比例小于多少时关闭页面
     */
    public void setTouchCloseScale(@FloatRange(from = 0.0000001f,to = 1f) float touchCloseScale) {
        if (touchCloseScale > 0 && touchCloseScale <= 1) {
            this.touchCloseScale = touchCloseScale;
        }
    }

    /**
     *
     * @param orientation 是上下触摸还是左右触摸，这取决于您的{@link androidx.viewpager2.widget.ViewPager2}的方向设定，
     *                    如果你的{@link androidx.viewpager2.widget.ViewPager2} 是横向，那么你应该传入竖向，否则传入横向
     */
    public void setOrientation(OpenImageOrientation orientation) {
        this.orientation = orientation;
    }
}
