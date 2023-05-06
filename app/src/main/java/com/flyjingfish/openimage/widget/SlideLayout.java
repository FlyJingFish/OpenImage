package com.flyjingfish.openimage.widget;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.LayoutDirection;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.core.text.TextUtilsCompat;

import java.util.Locale;

public class SlideLayout extends RelativeLayout {

    private int slideMaxWidth;
    private int slidingDistance;
    private View slideView;
    private boolean disEnableTouch;
    private int startX = 0;
    private int startY = 0;
    private ValueAnimator showCommentAnim;
    private int distance;
    private OnSlideListener onSlideListener;

    public SlideLayout(Context context) {
        this(context, null);
    }

    public SlideLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAnim();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (disEnableTouch || ev.getPointerCount() > 1) {
            return super.onInterceptTouchEvent(ev);
        }
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
                if ((disX > disY && disX > ViewConfiguration.get(getContext()).getScaledTouchSlop()) || (disX > disY && disX > ViewConfiguration.get(getContext()).getScaledTouchSlop() && Math.abs(slidingDistance)>0)) {
                    showCommentAnim.cancel();
                    if (onSlideListener != null){
                        onSlideListener.onStartSlide();
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
        if (disEnableTouch || e.getPointerCount() > 1) {
            return super.onTouchEvent(e);
        }
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                distance = (int) (slidingDistance - (e.getX() - startX));
                if (distance < 0){
                    distance = 0;
                }
                if (Math.abs(distance) > slideMaxWidth){
                    distance = slideMaxWidth;
                }
                if (onSlideListener != null){
                    onSlideListener.onSliding(Math.abs(distance));
                }
                setViewWidth(slideView, Math.abs(distance));
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                slidingDistance = Math.abs(distance);
                if (slidingDistance != 0 && slidingDistance != slideMaxWidth){
                    if (slidingDistance > slideMaxWidth/2){//打开
                        showCommentAnim.setIntValues(slidingDistance,slideMaxWidth);
                    }else {//关闭
                        showCommentAnim.setIntValues(slidingDistance,0);
                    }
                    showCommentAnim.start();
                }else {
                    if (onSlideListener != null){
                        onSlideListener.onEndSlide(slidingDistance);
                    }
                }
                break;
        }
        return true;
    }

    public void setViewWidth(View view ,int width){
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = width;
        view.setLayoutParams(layoutParams);
    }

    public void slideBack(){
        if (slidingDistance > 0){
            showCommentAnim.setIntValues(slidingDistance,0);
            showCommentAnim.start();
        }
    }

    private void initAnim(){
        if (showCommentAnim == null){
            ShowCommentTypeEvaluator showCommentTypeEvaluator = new ShowCommentTypeEvaluator();
            showCommentAnim = ValueAnimator.ofObject(showCommentTypeEvaluator,0f,0f);
            showCommentAnim.addUpdateListener(animation -> {
                int value = (int) animation.getAnimatedValue();
                setViewWidth(slideView, value);
                slidingDistance = value;
                if (animation.getAnimatedFraction() == 1){
                    if (onSlideListener != null){
                        onSlideListener.onEndSlide(slidingDistance);
                    }
                }
            });
        }

    }
    private static class ShowCommentTypeEvaluator implements TypeEvaluator<Integer> {

        @Override
        public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
            return startValue + (int)((endValue - startValue) * fraction);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

    }


    /**
     * @param slideView 侧拉的View
     */
    public void setSlideView(View slideView) {
        this.slideView = slideView;
    }

    /**
     *
     * @return 是否关闭触摸抽屉功能
     */
    public boolean isDisEnableTouch() {
        return disEnableTouch;
    }

    /**
     *
     * @param disEnableTouch 是否关闭触摸关闭功能
     */
    public void setDisEnableTouch(boolean disEnableTouch) {
        this.disEnableTouch = disEnableTouch;
    }

    public float getSlideMaxWidth() {
        return slideMaxWidth;
    }

    public void setSlideMaxWidth(int slideMaxWidth) {
        this.slideMaxWidth = slideMaxWidth;
    }

    public interface OnSlideListener{
        void onStartSlide();
        void onSliding(int distance);
        void onEndSlide(int distance);
    }

    public void setOnSlideListener(OnSlideListener onSlideListener) {
        this.onSlideListener = onSlideListener;
    }
}
