package com.flyjingfish.openimagelib.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.flyjingfish.openimagelib.enums.OpenImageOrientation;


public class TouchCloseLayout extends FrameLayout {

    private AnimatorSet touchAnim;
    private ObjectAnimator touchYAnim;
    private ObjectAnimator touchXAnim;
    private ObjectAnimator touchScaleXAnim;
    private ObjectAnimator touchScaleYAnim;
    private OnTouchCloseListener onTouchCloseListener;
    private ObjectAnimator bgViewAnim;
    private float scale;

    public TouchCloseLayout(Context context) {
        this(context, null);
    }

    public TouchCloseLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private float startDragX;
    private float startDragY;
    private static final float DRAG_SPEED = 1f;
    private static final float SCALE_CLOSE = .76f;
    private float touchCloseScale = SCALE_CLOSE;
    private View touchView;
    private View bgView;
    private boolean disEnableTouchClose;
    private OpenImageOrientation orientation;

    public void setTouchView(View touchView, View bgView) {
        this.touchView = touchView;
        this.bgView = bgView;
        touchYAnim = ObjectAnimator.ofFloat(touchView, "translationY", 0, 0);
        touchXAnim = ObjectAnimator.ofFloat(touchView, "translationX", 0, 0);
        touchScaleXAnim = ObjectAnimator.ofFloat(touchView, "scaleX", 1, 1);
        touchScaleYAnim = ObjectAnimator.ofFloat(touchView, "scaleY", 1, 1);
        bgViewAnim = ObjectAnimator.ofFloat(bgView, "alpha", 1, 1);
        touchAnim = new AnimatorSet();
        touchAnim.playTogether(touchXAnim, touchYAnim, touchScaleXAnim, touchScaleYAnim, bgViewAnim);
        touchAnim.setDuration(200);
        touchAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (onTouchCloseListener != null) {
                    onTouchCloseListener.onTouchScale(1f);
                }
                if (onTouchCloseListener != null){
                    onTouchCloseListener.onEndTouch();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                if (onTouchCloseListener != null){
                    onTouchCloseListener.onEndTouch();
                }
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private int startX = 0;
    private int startY = 0;
    private long touchDownTime;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (disEnableTouchClose || ev.getPointerCount() > 1) {
            return super.onInterceptTouchEvent(ev);
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) ev.getX();
                startY = (int) ev.getY();
                touchDownTime = SystemClock.uptimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                int endX = (int) ev.getX();
                int endY = (int) ev.getY();
                int disX = Math.abs(endX - startX);
                int disY = Math.abs(endY - startY);
                if ((orientation == OpenImageOrientation.HORIZONTAL && disX > disY && endX > startX) || (orientation == OpenImageOrientation.VERTICAL && disY > disX && endY > startY)) {
                    if (onTouchCloseListener != null){
                        onTouchCloseListener.onStartTouch();
                    }
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
        switch (e.getAction()) {
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
                    scale = (getWidth() - Math.abs(moveX)) / getWidth();
                } else {
                    scale = (getHeight() - moveY) / getHeight();
                }

                if (scale > 1) {
                    scale = 1;
                }
                touchView.setScaleX(scale);
                touchView.setScaleY(scale);
                bgView.setAlpha(scale);
                if (onTouchCloseListener != null) {
                    onTouchCloseListener.onTouchScale(scale);
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                long timeSub = (SystemClock.uptimeMillis() - touchDownTime);
                float distance = orientation == OpenImageOrientation.HORIZONTAL ? touchView.getTranslationX() : touchView.getTranslationY();
                float speed = distance / timeSub;
                if (speed > DRAG_SPEED || scale < touchCloseScale) {
                    if (onTouchCloseListener != null) {
                        onTouchCloseListener.onTouchClose(scale);
                    }
                } else {
                    if (touchView.getTranslationY() != 0 || touchView.getTranslationX() != 0) {
                        touchYAnim.setFloatValues(touchView.getTranslationY(), 0);
                        touchXAnim.setFloatValues(touchView.getTranslationX(), 0);
                        touchScaleXAnim.setFloatValues(touchView.getScaleX(), 1);
                        touchScaleYAnim.setFloatValues(touchView.getScaleY(), 1);
                        bgViewAnim.setFloatValues(bgView.getAlpha(), 1);
                        touchAnim.start();
                    }
                }

                startDragY = 0;
                startDragX = 0;
                break;
        }
        return true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (touchAnim != null) {
            touchAnim.removeAllListeners();
            touchAnim.cancel();
        }

    }

    public void setOnTouchCloseListener(OnTouchCloseListener onTouchCloseListener) {
        this.onTouchCloseListener = onTouchCloseListener;
    }

    public interface OnTouchCloseListener {
        void onStartTouch();
        void onEndTouch();
        void onTouchScale(float scale);

        void onTouchClose(float scale);
    }

    public boolean isDisEnableTouchClose() {
        return disEnableTouchClose;
    }

    public void setDisEnableTouchClose(boolean disEnableTouchClose) {
        this.disEnableTouchClose = disEnableTouchClose;
    }

    public float getTouchCloseScale() {
        return touchCloseScale;
    }

    public void setTouchCloseScale(float touchCloseScale) {
        if (touchCloseScale > 0 && touchCloseScale <= 1) {
            this.touchCloseScale = touchCloseScale;
        }
    }

    public void setOrientation(OpenImageOrientation orientation) {
        this.orientation = orientation;
    }
}
