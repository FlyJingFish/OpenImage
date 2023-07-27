package com.flyjingfish.openimagelib.photoview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.transition.Transition;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewParent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.OverScroller;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.flyjingfish.openimagelib.OpenImageActivity;
import com.flyjingfish.openimagelib.OpenImageConfig;
import com.flyjingfish.openimagelib.PhotosViewModel;
import com.flyjingfish.openimagelib.utils.OpenImageLogUtils;
import com.flyjingfish.shapeimageviewlib.ShapeImageView;

import java.util.HashSet;

/**
 * The component of {@link PhotoView} which does the work allowing for zooming, scaling, panning, etc.
 * It is made public in case you need to subclass something other than AppCompatImageView and still
 * gain the functionality that {@link PhotoView} offers
 */
public class PhotoViewAttacher implements View.OnTouchListener,
        View.OnLayoutChangeListener {

    private static final float DEFAULT_MAX_SCALE = 3.0f;
    private static final float DEFAULT_MID_SCALE = 1.75f;
    private static final float DEFAULT_MIN_SCALE = 1.0f;
    private static final int DEFAULT_ZOOM_DURATION = 200;

    private static final int SINGLE_TOUCH = 1;

    private Interpolator mInterpolator = new AccelerateDecelerateInterpolator();
    private int mZoomDuration = DEFAULT_ZOOM_DURATION;
    private boolean isSetMaxScale = false;
    private float mMinScale = DEFAULT_MIN_SCALE;
    private float mMidScale = DEFAULT_MID_SCALE;
    private float mMaxScale = DEFAULT_MAX_SCALE;

    private boolean mAllowParentInterceptOnEdge = true;
    private boolean mBlockParentIntercept = false;

    private final ImageView mImageView;

    // Gesture Detectors
    private GestureDetector mGestureDetector;
    private CustomGestureDetector mScaleDragDetector;

    // These are set so we don't keep allocating them on the heap
    private final Matrix mBaseMatrix = new Matrix();
    private final Matrix mDrawMatrix = new Matrix();
    private final Matrix mSuppMatrix = new Matrix();
    private final Matrix mBigImageMatrix = new Matrix();
    private final RectF mDisplayRect = new RectF();
    private final float[] mMatrixValues = new float[9];

    // Listeners
    private OnMatrixChangedListener mMatrixChangeListener;
    private OnPhotoTapListener mPhotoTapListener;
    private OnOutsidePhotoTapListener mOutsidePhotoTapListener;
    private OnViewTapListener mViewTapListener;
    private View.OnClickListener mOnClickListener;
    private OnLongClickListener mLongClickListener;
    private OnScaleChangedListener mScaleChangeListener;
    private OnSingleFlingListener mSingleFlingListener;
    private OnViewDragListener mOnViewDragListener;

    private FlingRunnable mCurrentFlingRunnable;
    private float mBaseRotation;

    private boolean mZoomEnabled = true;
    private boolean mScreenOrientationChange = false;
    private ScaleType mScaleType = ScaleType.FIT_CENTER;
    private ShapeImageView.ShapeScaleType mSrcScaleType = ShapeImageView.ShapeScaleType.FIT_CENTER;
    private float mTargetWidth;
    private float mTargetHeight;
    private float mTargetViewHeight;
    private float mStartWidth;
    private float mStartHeight;
    private ScreenOrientationEvent screenOrientationEvent;
    private float exitSuperScaleX;
    private float exitSuperScaleY;
    private float exitSuperTransX;
    private float exitSuperTransY;
    private float exitStartWidth;
    private float exitStartHeight;
    public boolean isCanLayout;
    private int exitDrawableWidth, exitDrawableHeight;
    private ViewPager2 viewPager2;

    public void setStartWidth(float mStartWidth) {
        this.mStartWidth = mStartWidth;
    }

    public void setStartHeight(float mStartHeight) {
        this.mStartHeight = mStartHeight;
    }

    public float getStartWidth() {
        return mStartWidth;
    }

    public float getStartHeight() {
        return mStartHeight;
    }

    private final OnGestureListener onGestureListener = new OnGestureListener() {
        @Override
        public void onDrag(float dx, float dy, float moveX, float moveY) {
            ViewParent parent = mImageView.getParent();
            if (mScaleDragDetector.isScaling()) {
                if (parent != null) {
                    parent.requestDisallowInterceptTouchEvent(true);
                }
                return; // Do not drag if we are already scaling
            }
            if (mOnViewDragListener != null) {
                mOnViewDragListener.onDrag(dx, dy);
            }
            mSuppMatrix.postTranslate(dx, dy);
            mBigImageMatrix.postTranslate(dx, dy);
            checkAndDisplayMatrix();
            RectF displayRect = getDisplayRect(getDrawMatrix());
            /*
             * Here we decide whether to let the ImageView's parent to start taking
             * over the touch event.
             *
             * First we check whether this function is enabled. We never want the
             * parent to take over if we're scaling. We then check the edge we're
             * on, and the direction of the scroll (i.e. if we're pulling against
             * the edge, aka 'overscrolling', let the parent take over).
             */
            if (mAllowParentInterceptOnEdge && !mScaleDragDetector.isScaling() && !mBlockParentIntercept && displayRect != null) {
                int imageWidth = getImageViewWidth(mImageView);
                int imageHeight = getImageViewHeight(mImageView);
                if (parent != null) {
                    boolean moveXBiggerY = Math.abs(moveX) > Math.abs(moveY);
                    boolean moveYBiggerX = Math.abs(moveY) > Math.abs(moveX);
                    parent.requestDisallowInterceptTouchEvent(
                            !(
                                    ((Math.abs(displayRect.right - imageWidth) < 0.1 || displayRect.right < imageWidth) && moveXBiggerY && moveX < 0)
                                            || ((Math.abs(displayRect.left) < 0.1 || displayRect.left > 0) && moveXBiggerY && moveX > 0)
                                            || ((Math.abs(displayRect.bottom - imageHeight) < 0.1 || displayRect.bottom < imageHeight) && moveYBiggerX && moveY < 0)
                                            || ((Math.abs(displayRect.top) < 0.1 || displayRect.top > 0) && moveYBiggerX && moveY > 0)
                            )
                    );
                }
            } else {
                if (parent != null) {
                    parent.requestDisallowInterceptTouchEvent(true);
                }
            }
//            ViewParent parent = mImageView.getParent();
//            if (mAllowParentInterceptOnEdge && !mScaleDragDetector.isScaling() && !mBlockParentIntercept && displayRect != null) {
//                int imageWidth = getImageViewWidth(mImageView);
//                int imageHeight = getImageViewHeight(mImageView);
//                float displayWidth = displayRect.width();
//                float displayHeight = displayRect.height();
//                boolean bigViewWidth = displayWidth > imageWidth;
//                boolean bigViewHeight = displayHeight > imageHeight;
//                boolean dyBigDx = Math.abs(dy) > Math.abs(dx);
//                boolean dxBigDy = Math.abs(dx) > Math.abs(dy);
//                if ((!bigViewWidth) && ((displayRect.top >= 0 && dy >= 1) || (displayRect.bottom <= imageHeight && dy <= -1))
//                        || (!bigViewHeight) && ((displayRect.left >= 0 && dx >= 1) || (displayRect.right <= imageWidth && dx <= -1))
//                        || (bigViewWidth && !bigViewHeight) && (dyBigDx && (displayRect.top >= 0 || displayRect.bottom <= imageHeight))
//                        || (!bigViewWidth && bigViewHeight) && (dxBigDy && (displayRect.left >= 0 || displayRect.right <= imageWidth))
//                        || ((bigViewHeight && bigViewWidth) && (displayRect.right == imageWidth || displayRect.left == 0 || displayRect.top == 0 || displayRect.bottom == imageHeight))) {
//                    if (!(bigViewHeight && bigViewWidth) || ((displayRect.top == 0 && dyBigDx)
//                            || (displayRect.right == imageWidth && dxBigDy)
//                            || (displayRect.bottom == imageHeight && dyBigDx)
//                            || (displayRect.left == 0 && dxBigDy))) {
//                        if (parent != null) {
//                            parent.requestDisallowInterceptTouchEvent(false);
//                        }
//                    } else {
//                        if (parent != null) {
//                            parent.requestDisallowInterceptTouchEvent(true);
//                        }
//                    }
//
//                } else {
//                    if (parent != null) {
//                        parent.requestDisallowInterceptTouchEvent(true);
//                    }
//                }
//            } else {
//                if (parent != null) {
//                    parent.requestDisallowInterceptTouchEvent(true);
//                }
//            }
        }

        @Override
        public void onFling(float startX, float startY, float velocityX, float velocityY) {
            mCurrentFlingRunnable = new FlingRunnable(mImageView.getContext());
            mCurrentFlingRunnable.fling(getImageViewWidth(mImageView),
                    getImageViewHeight(mImageView), (int) velocityX, (int) velocityY);
            mImageView.post(mCurrentFlingRunnable);
        }

        @Override
        public void onScale(boolean doubleFinger, float scaleFactor, float focusX, float focusY) {
            if (getScale() < mMaxScale || scaleFactor < 1f) {
                if (mScaleChangeListener != null) {
                    mScaleChangeListener.onScaleChange(scaleFactor, focusX, focusY);
                }
                mSuppMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY);
                mBigImageMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY);
                checkAndDisplayMatrix();
            }
            setViewPager2UserInputEnabled(false);
        }

    };

    private void setViewPager2UserInputEnabled(boolean enabled){
        if (viewPager2 != null){
            viewPager2.setUserInputEnabled(enabled);
        }
        OpenImageLogUtils.logE("setViewPager2UserInputEnabled","="+enabled);
    }

    private ViewPager2 findViewPager2(View view){
        if (view == null){
            return null;
        }
        ViewParent viewParent = view.getParent();
        if (viewParent instanceof ViewPager2){
            return (ViewPager2) viewParent;
        }else {
            return findViewPager2((View) viewParent);
        }
    }

    public PhotoViewAttacher(ImageView imageView) {
        mImageView = imageView;
        imageView.setOnTouchListener(this);
        imageView.addOnLayoutChangeListener(this);
        imageView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                if (viewPager2 == null || !viewPager2.isAttachedToWindow()){
                    viewPager2 = findViewPager2(v);
                }
            }

            @Override
            public void onViewDetachedFromWindow(View v) {

            }
        });
        if (imageView.isInEditMode()) {
            return;
        }
        ensureCanLayout();
        mBaseRotation = 0.0f;
        // Create Gesture Detectors...
        mScaleDragDetector = new CustomGestureDetector(imageView.getContext(), onGestureListener);
        mGestureDetector = new GestureDetector(imageView.getContext(), new GestureDetector.SimpleOnGestureListener() {

            // forward long click listener
            @Override
            public void onLongPress(MotionEvent e) {
                if (mLongClickListener != null) {
                    mLongClickListener.onLongClick(mImageView);
                }
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2,
                                   float velocityX, float velocityY) {
                if (mSingleFlingListener != null) {
                    if (getScale() > DEFAULT_MIN_SCALE) {
                        return false;
                    }
                    if (e1.getPointerCount() > SINGLE_TOUCH
                            || e2.getPointerCount() > SINGLE_TOUCH) {
                        return false;
                    }
                    return mSingleFlingListener.onFling(e1, e2, velocityX, velocityY);
                }
                return false;
            }

        });
        mGestureDetector.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (mOnClickListener != null) {
                    mOnClickListener.onClick(mImageView);
                }
                final RectF displayRect = getDisplayRect();
                final float x = e.getX(), y = e.getY();
                if (mViewTapListener != null) {
                    mViewTapListener.onViewTap(mImageView, x, y);
                }
                if (displayRect != null) {
                    // Check to see if the user tapped on the photo
                    if (displayRect.contains(x, y)) {
                        float xResult = (x - displayRect.left)
                                / displayRect.width();
                        float yResult = (y - displayRect.top)
                                / displayRect.height();
                        if (mPhotoTapListener != null) {
                            mPhotoTapListener.onPhotoTap(mImageView, xResult, yResult);
                        }
                        return true;
                    } else {
                        if (mOutsidePhotoTapListener != null) {
                            mOutsidePhotoTapListener.onOutsidePhotoTap(mImageView);
                        }
                    }
                }
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent ev) {
                try {
                    float scale = getScale();
                    float x = ev.getX();
                    float y = ev.getY();
                    if (scale < getMediumScale()) {
                        setScale(getMediumScale(), x, y, true);
                    } else if (scale >= getMediumScale() && scale < getMaximumScale()) {
                        setScale(getMaximumScale(), x, y, true);
                    } else {
                        setScale(getMinimumScale(), x, y, true);
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    // Can sometimes happen when getX() and getY() is called
                }
                return true;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                // Wait for the confirmed onDoubleTap() instead
                return false;
            }
        });

        screenOrientationEvent = new ScreenOrientationEvent(mImageView.getContext());
        registerDisplayListener();
    }

    static HashSet<String> onTransitionEndSet = new HashSet<>();

    private void ensureCanLayout() {
        final Activity activity = ((Activity) mImageView.getContext());
        if (activity instanceof OpenImageActivity) {
            final OpenImageActivity openImageActivity = ((OpenImageActivity) activity);
            final PhotosViewModel photosViewModel = new ViewModelProvider(openImageActivity).get(PhotosViewModel.class);
            final Observer<Boolean> onCanLayoutObserver = aBoolean -> isCanLayout = aBoolean;
            final Observer<Boolean> transitionEndObserver = aBoolean -> setCanLayoutListener();
            mImageView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {
                    photosViewModel.onCanLayoutLiveData.observe(openImageActivity, onCanLayoutObserver);
                    photosViewModel.transitionEndLiveData.observe(openImageActivity, transitionEndObserver);
                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    photosViewModel.onCanLayoutLiveData.removeObserver(onCanLayoutObserver);
                    photosViewModel.transitionEndLiveData.removeObserver(transitionEndObserver);
                }
            });
        } else if (activity instanceof FragmentActivity) {
            final String activityKey = activity.toString();
            final Transition transition = activity.getWindow().getSharedElementEnterTransition();
            ((FragmentActivity) activity).getLifecycle().addObserver(new LifecycleEventObserver() {
                @Override
                public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        onTransitionEndSet.remove(activityKey);
                        source.getLifecycle().removeObserver(this);
                    }
                }
            });
            if (transition != null) {
                if (onTransitionEndSet.contains(activityKey)) {
                    setCanLayoutListener();
                    return;
                }
                transition.addListener(new MyTransitionListener() {
                    @Override
                    public void onTransitionEnd(Transition transition) {
                        onTransitionEndSet.add(activityKey);
                        setCanLayoutListener();
                    }
                });
            } else {
                setCanLayoutListener();
            }
        } else {
            final String activityKey = activity.toString();
            final Transition transition = activity.getWindow().getSharedElementEnterTransition();
            if (transition != null) {
                if (onTransitionEndSet.contains(activityKey)) {
                    isCanLayout = true;
                    return;
                }
                transition.addListener(new MyTransitionListener() {
                    @Override
                    public void onTransitionEnd(Transition transition) {
                        onTransitionEndSet.add(activityKey);
                        isCanLayout = true;
                    }
                });
            } else {
                isCanLayout = true;
            }

        }
    }

    private static class MyTransitionListener implements Transition.TransitionListener {

        @Override
        public void onTransitionStart(Transition transition) {

        }

        @Override
        public void onTransitionEnd(Transition transition) {
            transition.removeListener(this);
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
    }

    private void setCanLayoutListener() {
        if (mImageView.isAttachedToWindow()) {
            isCanLayout = true;
            return;
        }
        mImageView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                mImageView.removeOnAttachStateChangeListener(this);
                isCanLayout = true;
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                mImageView.removeOnAttachStateChangeListener(this);
            }
        });
    }

    public void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener newOnDoubleTapListener) {
        this.mGestureDetector.setOnDoubleTapListener(newOnDoubleTapListener);
    }

    public void setOnScaleChangeListener(OnScaleChangedListener onScaleChangeListener) {
        this.mScaleChangeListener = onScaleChangeListener;
    }

    public void setOnSingleFlingListener(OnSingleFlingListener onSingleFlingListener) {
        this.mSingleFlingListener = onSingleFlingListener;
    }

    @Deprecated
    public boolean isZoomEnabled() {
        return mZoomEnabled;
    }

    public RectF getDisplayRect() {
        checkMatrixBounds();
        return getDisplayRect(getDrawMatrix());
    }

    public boolean setDisplayMatrix(Matrix finalMatrix) {
        if (finalMatrix == null) {
            throw new IllegalArgumentException("Matrix cannot be null");
        }
        if (mImageView.getDrawable() == null) {
            return false;
        }
        mSuppMatrix.set(finalMatrix);
        checkAndDisplayMatrix();
        return true;
    }

    public void setBaseRotation(final float degrees) {
        mBaseRotation = degrees % 360;
        update();
        setRotationBy(mBaseRotation);
        checkAndDisplayMatrix();
    }

    public void setRotationTo(float degrees) {
        mSuppMatrix.setRotate(degrees % 360);
        checkAndDisplayMatrix();
    }

    public void setRotationBy(float degrees) {
        mSuppMatrix.postRotate(degrees % 360);
        checkAndDisplayMatrix();
    }

    public float getMinimumScale() {
        return mMinScale;
    }

    public float getMediumScale() {
        return mMidScale;
    }

    public float getMaximumScale() {
        return mMaxScale;
    }

    public float getScale() {
        return (float) Math.sqrt((float) Math.pow(getValue(mSuppMatrix, Matrix.MSCALE_X), 2) + (float) Math.pow
                (getValue(mSuppMatrix, Matrix.MSKEW_Y), 2));
    }

    public ScaleType getScaleType() {
        return mScaleType;
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int
            oldRight, int oldBottom) {
        // Update our base matrix, as the bounds have changed
        if (left != oldLeft || top != oldTop || right != oldRight || bottom != oldBottom) {
            if (mScreenOrientationChange) {
                mTargetWidth = 0;
            }
            if (right > left && (mTargetWidth == 0 || isCanLayout)) {
                mTargetWidth = right - left;
                mTargetHeight = bottom - top;
                mTargetViewHeight = mTargetHeight;
            }
            updateBaseMatrix(mImageView.getDrawable());
        }
        mScreenOrientationChange = false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent ev) {
        boolean handled = false;
        isTouched = true;
        if (mZoomEnabled && Util.hasDrawable((ImageView) v)) {
            switch (ev.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_POINTER_DOWN:
                    setViewPager2UserInputEnabled(false);
                    break;
                case MotionEvent.ACTION_DOWN:
                    setViewPager2UserInputEnabled(true);
                    ViewParent parent = v.getParent();
                    // First, disable the Parent from intercepting the touch
                    // event
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                    // If we're flinging, and the user presses down, cancel
                    // fling
                    cancelFling();
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    // If the user has zoomed less than min scale, zoom back
                    // to min scale
                    if (getScale() < mMinScale) {
                        RectF rect = getDisplayRect();
                        if (rect != null) {
                            v.post(new AnimatedZoomRunnable(getScale(), mMinScale,
                                    rect.centerX(), rect.centerY()));
                            handled = true;
                        }
                    } else if (getScale() > mMaxScale) {
                        RectF rect = getDisplayRect();
                        if (rect != null) {
                            v.post(new AnimatedZoomRunnable(getScale(), mMaxScale,
                                    rect.centerX(), rect.centerY()));
                            handled = true;
                        }
                    }
                    break;
            }
            // Try the Scale/Drag detector
            if (mScaleDragDetector != null) {
                boolean wasScaling = mScaleDragDetector.isScaling();
                boolean wasDragging = mScaleDragDetector.isDragging();
                handled = mScaleDragDetector.onTouchEvent(ev);
                boolean didntScale = !wasScaling && !mScaleDragDetector.isScaling();
                boolean didntDrag = !wasDragging && !mScaleDragDetector.isDragging();
                mBlockParentIntercept = didntScale && didntDrag;
            }
            // Check to see if the user double tapped
            if (mGestureDetector != null && mGestureDetector.onTouchEvent(ev)) {
                handled = true;
            }

        }
        return handled;
    }

    public void setAllowParentInterceptOnEdge(boolean allow) {
        mAllowParentInterceptOnEdge = allow;
    }

    public void setMinimumScale(float minimumScale) {
        Util.checkZoomLevels(minimumScale, mMidScale, mMaxScale);
        mMinScale = minimumScale;
    }

    public void setMediumScale(float mediumScale) {
        Util.checkZoomLevels(mMinScale, mediumScale, mMaxScale);
        mMidScale = mediumScale;
    }

    public void setMaximumScale(float maximumScale) {
        Util.checkZoomLevels(mMinScale, mMidScale, maximumScale);
        mMaxScale = maximumScale;
        isSetMaxScale = true;
    }

    public void setScaleLevels(float minimumScale, float mediumScale, float maximumScale) {
        Util.checkZoomLevels(minimumScale, mediumScale, maximumScale);
        mMinScale = minimumScale;
        mMidScale = mediumScale;
        mMaxScale = maximumScale;
    }

    public void setOnLongClickListener(OnLongClickListener listener) {
        mLongClickListener = listener;
    }

    public void setOnClickListener(View.OnClickListener listener) {
        mOnClickListener = listener;
    }

    public void setOnMatrixChangeListener(OnMatrixChangedListener listener) {
        mMatrixChangeListener = listener;
    }

    public void setOnPhotoTapListener(OnPhotoTapListener listener) {
        mPhotoTapListener = listener;
    }

    public void setOnOutsidePhotoTapListener(OnOutsidePhotoTapListener mOutsidePhotoTapListener) {
        this.mOutsidePhotoTapListener = mOutsidePhotoTapListener;
    }

    public void setOnViewTapListener(OnViewTapListener listener) {
        mViewTapListener = listener;
    }

    public void setOnViewDragListener(OnViewDragListener listener) {
        mOnViewDragListener = listener;
    }

    public void setScale(float scale) {
        setScale(scale, false);
    }

    public void setScale(float scale, boolean animate) {
        setScale(scale,
                (mImageView.getRight()) / 2,
                (mImageView.getBottom()) / 2,
                animate);
    }

    public void setScale(float scale, float focalX, float focalY,
                         boolean animate) {
        // Check to see if the scale is within bounds
        if (scale < mMinScale || scale > mMaxScale) {
            throw new IllegalArgumentException("Scale must be within the range of minScale and maxScale");
        }
        if (animate) {
            mImageView.post(new AnimatedZoomRunnable(getScale(), scale,
                    focalX, focalY));
        } else {
            mSuppMatrix.setScale(scale, scale, focalX, focalY);
            mBigImageMatrix.setScale(scale, scale, focalX, focalY);
            checkAndDisplayMatrix();
        }
    }

    /**
     * Set the zoom interpolator
     *
     * @param interpolator the zoom interpolator
     */
    public void setZoomInterpolator(Interpolator interpolator) {
        mInterpolator = interpolator;
    }

    public void setScaleType(ScaleType scaleType) {
        if (scaleType != mScaleType) {
            mScaleType = scaleType;
            update();
        }
    }

    public void setSrcScaleType(ShapeImageView.ShapeScaleType scaleType) {
        mSrcScaleType = scaleType;
        if (isExitMode) {
            if (scaleType == ShapeImageView.ShapeScaleType.START_CROP
                    || scaleType == ShapeImageView.ShapeScaleType.END_CROP
                    || scaleType == ShapeImageView.ShapeScaleType.AUTO_START_CENTER_CROP
                    || scaleType == ShapeImageView.ShapeScaleType.AUTO_END_CENTER_CROP) {
                update();
            }
        } else {
            update();
        }
    }

    public ShapeImageView.ShapeScaleType getSrcScaleType(){
        return mSrcScaleType;
    }

    public boolean isZoomable() {
        return mZoomEnabled;
    }

    public void setZoomable(boolean zoomable) {
        mZoomEnabled = zoomable;
        update();
    }

    public void update() {
        Drawable drawable = mImageView.getDrawable();
        updateBaseMatrix(drawable);
    }

//    private void checkMinMaxValue(Drawable drawable){
//        RectF displayRect = getDisplayRect(getDrawMatrix());
//        float displayWidth = displayRect.width();
//        float displayHeight = displayRect.height();
//
//        if (!isReadBigImaged && displayWidth > 0 && displayWidth > 0){
//            //需要检查图片放大的最大尺寸能不能看到每一个像素
//            float maxImageWidth = displayWidth * mMaxScale;
//            float maxImageHeight = displayHeight * mMaxScale;
//            if (maxImageWidth < viewWidth || maxImageHeight < viewHeight){//说明放大的最大尺寸不好
//                float maxScaleParam = Math.max(viewWidth/maxImageWidth,viewHeight/maxImageHeight);
////                    if (maxScaleParam > mMaxScale){
//                mMinScale = DEFAULT_MIN_SCALE;
//                mMidScale = widthScale / heightScale;
//                mMaxScale = DEFAULT_MAX_SCALE / DEFAULT_MID_SCALE * mMidScale;
////                        float maxScaleCache = maxScaleParam;
////                        float minScaleCache = DEFAULT_MIN_SCALE;
////                        float midScaleCache = (minScaleCache + maxScaleCache)/2;
////                        mMaxScale = maxScaleCache;
////                        mMinScale = minScaleCache;
////                        mMinScale = midScaleCache;
////                    }
//            }
//        }
//    }

    /**
     * Get the display matrix
     *
     * @param matrix target matrix to copy to
     */
    public void getDisplayMatrix(Matrix matrix) {
        matrix.set(getDrawMatrix());
    }

    /**
     * Get the current support matrix
     */
    public void getSuppMatrix(Matrix matrix) {
        matrix.set(mSuppMatrix);
    }

    public void setExitDrawableWidthHeight(int width,int height) {
        exitDrawableWidth = width;
        exitDrawableHeight = height;
    }

    private Matrix getDrawMatrix() {
        mDrawMatrix.set(mBaseMatrix);
        mDrawMatrix.postConcat(mSuppMatrix);
        return mDrawMatrix;
    }

    public Matrix getImageMatrix() {
        return mDrawMatrix;
    }

    public void setZoomTransitionDuration(int milliseconds) {
        this.mZoomDuration = milliseconds;
    }

    /**
     * Helper method that 'unpacks' a Matrix and returns the required value
     *
     * @param matrix     Matrix to unpack
     * @param whichValue Which value from Matrix.M* to return
     * @return returned value
     */
    private float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[whichValue];
    }

    /**
     * Resets the Matrix back to FIT_CENTER, and then displays its contents
     */
    private void resetMatrix() {
        Drawable drawable = mImageView.getDrawable();
        ShapeImageView.ShapeScaleType autoScaleType = null;
        if (drawable != null && (mSrcScaleType == ShapeImageView.ShapeScaleType.AUTO_START_CENTER_CROP || mSrcScaleType == ShapeImageView.ShapeScaleType.AUTO_END_CENTER_CROP)) {
            final int drawableWidth = drawable.getIntrinsicWidth();
            final int drawableHeight = drawable.getIntrinsicHeight();
            float imageHeightWidthRatio = drawableHeight * 1f / drawableWidth;
            float viewHeightWidthRatio = mStartHeight / mStartWidth;
            float ratio = imageHeightWidthRatio / viewHeightWidthRatio;
            if (ratio >= mAutoCropHeightWidthRatio) {
                autoScaleType = mSrcScaleType == ShapeImageView.ShapeScaleType.AUTO_START_CENTER_CROP ? ShapeImageView.ShapeScaleType.START_CROP : ShapeImageView.ShapeScaleType.END_CROP;
            } else {
                autoScaleType = ShapeImageView.ShapeScaleType.CENTER_CROP;
            }
        }
        if (isExitMode && (mSrcScaleType == ShapeImageView.ShapeScaleType.START_CROP || mSrcScaleType == ShapeImageView.ShapeScaleType.END_CROP || autoScaleType != null)) {
            final float viewWidth = getImageViewWidth(mImageView);
            final float viewHeight = getImageViewHeight(mImageView);
            float addWidthScale;
            float addHeightScale;
            if (exitStartWidth / mTargetWidth < exitStartHeight / mTargetViewHeight || isBigImage) {
                addWidthScale = (viewWidth - exitStartWidth) * 1f / (mTargetWidth - exitStartWidth);
                addHeightScale = addWidthScale;
            } else {
                addHeightScale = (viewHeight - exitStartHeight) * 1f / (mTargetViewHeight - exitStartHeight);
                addWidthScale = addHeightScale;
            }
            addWidthScale = Math.max(0f, addWidthScale);
            addHeightScale = Math.max(0f, addHeightScale);

            mSuppMatrix.reset();

            float scaleX = 1 + (exitSuperScaleX - 1) * addWidthScale;
            float scaleY = 1 + (exitSuperScaleY - 1) * addHeightScale;
            mSuppMatrix.postScale(scaleX, scaleY);
            mSuppMatrix.postTranslate(exitSuperTransX * addWidthScale, exitSuperTransY * addHeightScale);

        } else {
            mSuppMatrix.reset();
        }
        setRotationBy(mBaseRotation);
        setImageViewMatrix(getDrawMatrix());
        checkMatrixBounds();


    }


    private void setImageViewMatrix(Matrix matrix) {
        mImageView.setImageMatrix(matrix);
        // Call MatrixChangedListener if needed
        if (mMatrixChangeListener != null) {
            RectF displayRect = getDisplayRect(matrix);
            if (displayRect != null) {
                mMatrixChangeListener.onMatrixChanged(displayRect);
            }
        }
    }

    /**
     * Helper method that simply checks the Matrix, and then displays the result
     */
    private void checkAndDisplayMatrix() {
        if (checkMatrixBounds()) {
            setImageViewMatrix(getDrawMatrix());
        }
    }

    /**
     * Helper method that maps the supplied Matrix to the current Drawable
     *
     * @param matrix - Matrix to map Drawable against
     * @return RectF - Displayed Rectangle
     */
    private RectF getDisplayRect(Matrix matrix) {
        Drawable d = mImageView.getDrawable();
        if (d != null) {
            mDisplayRect.set(0, 0, d.getIntrinsicWidth(),
                    d.getIntrinsicHeight());
            matrix.mapRect(mDisplayRect);
            return mDisplayRect;
        }
        return null;
    }

    private boolean isExitMode = false;
    private boolean isNoneClickView = false;
    private float exitFloat = 1f;

    public void setExitFloat(float exitFloat) {
        this.exitFloat = exitFloat;
    }

    public void setExitMode(boolean exitMode) {
        isExitMode = exitMode;
    }

    public boolean isExitMode() {
        return isExitMode;
    }

    public boolean isNoneClickView() {
        return isNoneClickView;
    }

    public void setNoneClickView(boolean noneClickView) {
        isNoneClickView = noneClickView;
    }

    /**
     * Calculate Matrix for FIT_CENTER
     *
     * @param drawable - Drawable being displayed
     */
    private void updateBaseMatrix(Drawable drawable) {
        if (drawable == null) {
            return;
        }
        final float viewWidth = getImageViewWidth(mImageView);
        final float viewHeight = getImageViewHeight(mImageView);
        final int drawableWidth = drawable.getIntrinsicWidth();
        final int drawableHeight = drawable.getIntrinsicHeight();
        mBaseMatrix.reset();
        final float widthScale = viewWidth / drawableWidth;
        final float heightScale = viewHeight / drawableHeight;

        float imageHeightWidthRatio = drawableHeight * 1f / drawableWidth;
        float viewHeightWidthRatio = mStartHeight / mStartWidth;
        float ratio = imageHeightWidthRatio / viewHeightWidthRatio;
        ShapeImageView.ShapeScaleType autoScaleType = null;
        if (mSrcScaleType == ShapeImageView.ShapeScaleType.AUTO_START_CENTER_CROP || mSrcScaleType == ShapeImageView.ShapeScaleType.AUTO_END_CENTER_CROP) {
            if (ratio >= mAutoCropHeightWidthRatio) {
                autoScaleType = mSrcScaleType == ShapeImageView.ShapeScaleType.AUTO_START_CENTER_CROP ? ShapeImageView.ShapeScaleType.START_CROP : ShapeImageView.ShapeScaleType.END_CROP;
            } else {
                autoScaleType = ShapeImageView.ShapeScaleType.CENTER_CROP;
            }
        }

        if (mScaleType == ScaleType.CENTER) {
            if (isExitMode) {
                float exitDrawableScale = 1f;
                if (exitDrawableWidth != 0 && exitDrawableHeight != 0){
                    exitDrawableScale = exitDrawableWidth *1f/drawableWidth;
                }
                float exitScale = (1 / exitFloat) * exitDrawableScale;
                mBaseMatrix.postScale(exitScale, exitScale);
                mBaseMatrix.postTranslate((viewWidth - drawableWidth * exitScale) / 2F,
                        (viewHeight - drawableHeight * exitScale) / 2F);
            } else {
                mBaseMatrix.postTranslate((viewWidth - drawableWidth) / 2F,
                        (viewHeight - drawableHeight) / 2F);
            }

        } else if (mScaleType == ScaleType.MATRIX && isExitMode) {
            float scale = Math.max(widthScale, heightScale);
            mBaseMatrix.postScale(scale, scale);
            if (mSrcScaleType == ShapeImageView.ShapeScaleType.START_CROP || autoScaleType == ShapeImageView.ShapeScaleType.START_CROP) {
                mBaseMatrix.postTranslate(0, 0);
            } else if (mSrcScaleType == ShapeImageView.ShapeScaleType.END_CROP || autoScaleType == ShapeImageView.ShapeScaleType.END_CROP) {
                mBaseMatrix.postTranslate((viewWidth - drawableWidth * scale),
                        (viewHeight - drawableHeight * scale));
            } else if (autoScaleType == ShapeImageView.ShapeScaleType.CENTER_CROP) {
                mBaseMatrix.postTranslate((viewWidth - drawableWidth * scale) / 2F,
                        (viewHeight - drawableHeight * scale) / 2F);
            }

        } else if (mScaleType == ScaleType.CENTER_CROP) {
            float scale = Math.max(widthScale, heightScale);
            mBaseMatrix.postScale(scale, scale);
            mBaseMatrix.postTranslate((viewWidth - drawableWidth * scale) / 2F,
                    (viewHeight - drawableHeight * scale) / 2F);

        } else if (mScaleType == ScaleType.CENTER_INSIDE) {
            float exitDrawableScale = 1f;
            if (isExitMode && exitDrawableWidth != 0 && exitDrawableHeight != 0) {
                final float widthScale1 = mStartWidth / exitDrawableWidth;
                final float heightScale1 = mStartHeight / exitDrawableHeight;
                float scale1 = Math.min(1.0f, Math.min(widthScale1, heightScale1));
                final float widthScale2 = mStartWidth / drawableWidth;
                final float heightScale2 = mStartHeight / drawableHeight;
                float scale2 = Math.min(1.0f, Math.min(widthScale2, heightScale2));
                exitDrawableScale = (exitDrawableWidth * scale1) / (drawableWidth * scale2);
            }
            float scale = Math.min(1.0f, Math.min(widthScale * exitDrawableScale, heightScale * exitDrawableScale));
            mBaseMatrix.postScale(scale, scale);
            mBaseMatrix.postTranslate((viewWidth - drawableWidth * scale) / 2F,
                    (viewHeight - drawableHeight * scale) / 2F);

        } else {
            float scaleImageHW = drawableHeight * 1f / drawableWidth;
            float scaleViewHW = viewHeight * 1f / viewWidth;
            float maxScale = Math.max(widthScale, heightScale);

            RectF mTempSrc = new RectF(0, 0, drawableWidth, drawableHeight);
            RectF mTempDst;
            if (isNoneClickView) {
                if (OpenImageConfig.getInstance().isReadMode()) {
                    boolean bigImageRule = maxScale * drawableHeight > OpenImageConfig.getInstance().getReadModeRule() * Math.max(viewWidth, viewHeight);
                    if (bigImageRule) {
                        mTempDst = new RectF(0, 0, viewWidth, viewWidth * scaleImageHW);
                        isBigImage = true;
                    } else {
                        if (scaleImageHW > 1) {
                            if (maxScale * drawableHeight > DEFAULT_MID_SCALE * viewHeight) {
                                mMinScale = DEFAULT_MIN_SCALE;
                                //设置中等缩放为适宽的缩放
                                mMidScale = widthScale / heightScale;
                                if (!isSetMaxScale) {
                                    mMaxScale = DEFAULT_MAX_SCALE / DEFAULT_MID_SCALE * mMidScale;
                                }
                            }
                        } else {
                            if (maxScale * drawableWidth > DEFAULT_MID_SCALE * viewWidth) {
                                mMinScale = DEFAULT_MIN_SCALE;
                                //设置中等缩放为适宽的缩放
                                mMidScale = heightScale / widthScale;
                                if (!isSetMaxScale) {
                                    mMaxScale = DEFAULT_MAX_SCALE / DEFAULT_MID_SCALE * mMidScale;
                                }
                            }
                        }
                        mTempDst = new RectF(0, 0, viewWidth, viewHeight);
                    }
                } else {
                    mTempDst = new RectF(0, 0, viewWidth, viewHeight);
                }

                if ((int) mBaseRotation % 180 != 0) {
                    mTempSrc = new RectF(0, 0, drawableHeight, drawableWidth);
                }
            } else if (isExitMode) {
                if (isBigImage && (mSrcScaleType == ShapeImageView.ShapeScaleType.START_CROP || mSrcScaleType == ShapeImageView.ShapeScaleType.END_CROP || autoScaleType == ShapeImageView.ShapeScaleType.START_CROP || autoScaleType == ShapeImageView.ShapeScaleType.END_CROP || autoScaleType == ShapeImageView.ShapeScaleType.CENTER_CROP)) {
                    mTempDst = new RectF(0, 0, viewWidth, viewWidth * scaleImageHW);
                } else {
                    mTempDst = new RectF(0, 0, viewWidth, viewHeight);
                }
                exitSuperScaleX = getValue(mSuppMatrix, Matrix.MSCALE_X);
                exitSuperScaleY = getValue(mSuppMatrix, Matrix.MSCALE_Y);
                exitSuperTransX = getValue(mSuppMatrix, Matrix.MTRANS_X);
                exitSuperTransY = getValue(mSuppMatrix, Matrix.MTRANS_Y);
                exitStartWidth = mStartWidth / exitFloat;
                exitStartHeight = mStartWidth / exitFloat;
            } else {
                if (mScaleType == ScaleType.FIT_CENTER && (mTargetWidth > 0 || startDstRectF == null)) {
                    float targetWidth = Math.max(mTargetWidth, viewWidth);
                    float targetHeight = Math.max(mTargetViewHeight, viewHeight);
                    float scaleStartViewHW = mStartHeight * 1f / mStartWidth;
                    if (mSrcScaleType == ShapeImageView.ShapeScaleType.CENTER_CROP || autoScaleType == ShapeImageView.ShapeScaleType.CENTER_CROP) {
                        if (scaleImageHW > scaleStartViewHW) {
                            float height = mStartWidth * scaleImageHW;
                            float tansY = (height - mStartHeight) / 2;
                            startDstRectF = new RectF(0, -tansY, mStartWidth, height - tansY);
                        } else {
                            float width = mStartHeight / scaleImageHW;
                            float tansX = (width - mStartWidth) / 2;
                            startDstRectF = new RectF(-tansX, 0, width - tansX, mStartHeight);

                        }
                    } else if (mSrcScaleType == ShapeImageView.ShapeScaleType.START_CROP || autoScaleType == ShapeImageView.ShapeScaleType.START_CROP) {
                        if (scaleImageHW > scaleStartViewHW) {
                            float height = mStartWidth * scaleImageHW;
                            startDstRectF = new RectF(0, 0, mStartWidth, height);
                        } else {
                            float width = mStartHeight / scaleImageHW;
                            startDstRectF = new RectF(0, 0, width, mStartHeight);

                        }
                    } else if (mSrcScaleType == ShapeImageView.ShapeScaleType.END_CROP || autoScaleType == ShapeImageView.ShapeScaleType.END_CROP) {
                        if (scaleImageHW > scaleStartViewHW) {
                            float height = mStartWidth * scaleImageHW;
                            float tansY = (height - mStartHeight) / 2;
                            startDstRectF = new RectF(0, -tansY * 2, mStartWidth, height - tansY * 2);
                        } else {
                            float width = mStartHeight / scaleImageHW;
                            float tansX = (width - mStartWidth) / 2;
                            startDstRectF = new RectF(-tansX * 2, 0, width - tansX * 2, mStartHeight);
                        }
                    } else if (mSrcScaleType == ShapeImageView.ShapeScaleType.CENTER_INSIDE) {
                        if (drawableWidth < mStartWidth && drawableHeight < mStartHeight) {
                            float left = (mStartWidth - drawableWidth) / 2;
                            float top = (mStartHeight - drawableHeight) / 2;
                            startDstRectF = new RectF(left, top, drawableWidth + left, drawableHeight + top);
                        } else {
                            startDstRectF = new RectF(0, 0, mStartWidth, mStartHeight);
                        }
                    } else if (mSrcScaleType == ShapeImageView.ShapeScaleType.CENTER) {
                        startDstRectF = new RectF(0, 0, drawableWidth, drawableHeight);
                    } else {
                        startDstRectF = new RectF(0, 0, mStartWidth, mStartHeight);
                    }

                    final float widthScale1 = targetWidth / drawableWidth;
                    final float heightScale1 = targetHeight / drawableHeight;
                    float maxScale1 = Math.max(widthScale1, heightScale1);

                    if (OpenImageConfig.getInstance().isReadMode()) {
                        boolean bigImageRule = maxScale1 * drawableHeight > OpenImageConfig.getInstance().getReadModeRule() * Math.max(targetWidth, targetHeight);
                        if (bigImageRule) {
                            mTargetWidth = targetWidth;
                            mTargetHeight = targetWidth * scaleImageHW;
                            isBigImage = true;
                        } else {
                            if (scaleImageHW > 1) {
                                if (maxScale * drawableHeight > DEFAULT_MID_SCALE * targetHeight) {
                                    mMinScale = DEFAULT_MIN_SCALE;
                                    //设置中等缩放为适宽的缩放
                                    mMidScale = widthScale / heightScale;
                                    if (!isSetMaxScale) {
                                        mMaxScale = DEFAULT_MAX_SCALE / DEFAULT_MID_SCALE * mMidScale;
                                    }
                                }
                            } else {
                                if (maxScale * drawableWidth > DEFAULT_MID_SCALE * targetWidth) {
                                    mMinScale = DEFAULT_MIN_SCALE;
                                    //设置中等缩放为适宽的缩放
                                    mMidScale = heightScale / widthScale;
                                    if (!isSetMaxScale) {
                                        mMaxScale = DEFAULT_MAX_SCALE / DEFAULT_MID_SCALE * mMidScale;
                                    }
                                }
                            }
                            mTargetWidth = targetWidth;
                            mTargetHeight = targetHeight;
                        }
                    } else {
                        mTargetWidth = targetWidth;
                        mTargetHeight = targetHeight;
                    }
                }

                if (mScaleType == ScaleType.FIT_CENTER && startDstRectF != null && mStartWidth != 0 && mStartHeight != 0) {
                    float addWidthScale;
                    float addHeightScale;
                    if (mStartWidth / mTargetWidth < mStartHeight / mTargetViewHeight || isBigImage) {
                        addWidthScale = (viewWidth - mStartWidth) * 1f / (mTargetWidth - mStartWidth);
                        addHeightScale = addWidthScale;
                    } else {
                        addHeightScale = (viewHeight - mStartHeight) * 1f / (mTargetViewHeight - mStartHeight);
                        addWidthScale = addHeightScale;
                    }

                    float startWidth = startDstRectF.width();
                    float startHeight = startDstRectF.height();

                    float currentWidth = startWidth + (mTargetWidth - startWidth) * addWidthScale;
                    float currentHeight = startHeight + (mTargetHeight - startHeight) * addHeightScale;
                    float tansX = (currentWidth - viewWidth) / 2;
                    float tansY = (currentHeight - viewHeight) / 2;
                    if (isBigImage) {
                        tansY = tansY - addHeightScale * tansY;
                    }


                    if (mSrcScaleType == ShapeImageView.ShapeScaleType.FIT_XY) {
                        float scaleEndViewHW = mTargetHeight / mTargetWidth;
                        float targetHeight, targetWidth;
                        if (scaleEndViewHW > scaleImageHW) {
                            targetHeight = mTargetWidth * scaleImageHW;
                            targetWidth = mTargetWidth;
                        } else {
                            targetHeight = mTargetHeight;
                            targetWidth = mTargetHeight / scaleImageHW;
                        }

                        float width = viewWidth - mTargetWidth * addWidthScale * (1 - (targetWidth * 1f / mTargetWidth));
                        float height;
                        if (isBigImage) {
//                            height = mTargetHeight-mTargetHeight*addHeightScale*(1-(targetHeight *1f/ mTargetHeight));
                            height = viewHeight + addHeightScale * (mTargetHeight - viewHeight);
                        } else {
                            height = viewHeight - mTargetHeight * addHeightScale * (1 - (targetHeight * 1f / mTargetHeight));
                        }
                        mTempDst = new RectF(0, 0, width, height);
                    } else if (mSrcScaleType == ShapeImageView.ShapeScaleType.START_CROP || autoScaleType == ShapeImageView.ShapeScaleType.START_CROP) {
                        mTempDst = new RectF(0, 0, currentWidth, currentHeight);
                    } else if (mSrcScaleType == ShapeImageView.ShapeScaleType.END_CROP || autoScaleType == ShapeImageView.ShapeScaleType.END_CROP) {
                        mTempDst = new RectF(-tansX * 2, -tansY * 2, currentWidth - tansX * 2, currentHeight - tansY * 2);
                    } else {
                        mTempDst = new RectF(-tansX, -tansY, currentWidth - tansX, currentHeight - tansY);
                    }
                } else {
                    mTempDst = new RectF(0, 0, viewWidth, viewHeight);
                }
            }


            if ((int) mBaseRotation % 180 != 0) {
                mTempSrc = new RectF(0, 0, drawableHeight, drawableWidth);
            }
            switch (mScaleType) {
                case FIT_CENTER:
                    if (mSrcScaleType == ShapeImageView.ShapeScaleType.FIT_XY && mStartWidth != 0 && mStartHeight != 0) {
                        if (viewWidth < mTargetWidth || viewHeight < mTargetHeight) {
                            mBaseMatrix.setRectToRect(mTempSrc, mTempDst, ScaleToFit.FILL);
                        } else {
                            mBaseMatrix.setRectToRect(mTempSrc, mTempDst, ScaleToFit.CENTER);
                        }
                    } else {
                        mBaseMatrix.setRectToRect(mTempSrc, mTempDst, ScaleToFit.CENTER);
                    }
                    break;
                case FIT_START:
                    mBaseMatrix.setRectToRect(mTempSrc, mTempDst, ScaleToFit.START);
                    break;
                case FIT_END:
                    mBaseMatrix.setRectToRect(mTempSrc, mTempDst, ScaleToFit.END);
                    break;
                case FIT_XY:
                    mBaseMatrix.setRectToRect(mTempSrc, mTempDst, ScaleToFit.FILL);
                    break;
                default:
                    break;
            }

        }
        resetMatrix();

    }


    private RectF startDstRectF;
    private boolean isBigImage;
    private boolean isTouched;

    private boolean checkMatrixBounds() {
        final RectF rect = getDisplayRect(getDrawMatrix());
        if (rect == null) {
            return false;
        }
        final int viewWidth = getImageViewWidth(mImageView);
        final int viewHeight = getImageViewHeight(mImageView);
        final float height = rect.height(), width = rect.width();
        float deltaX = 0, deltaY = 0;
        if (!isExitMode && !isTouched && mScaleType == ScaleType.FIT_CENTER) {
            float addWidthScale;
            float addHeightScale;
            if (mStartWidth / mTargetWidth < mStartHeight / mTargetViewHeight || isBigImage) {
                addWidthScale = (viewWidth - mStartWidth) / (mTargetWidth - mStartWidth);
                addHeightScale = addWidthScale;
            } else {
                addHeightScale = (viewHeight - mStartHeight) / (mTargetViewHeight - mStartHeight);
                addWidthScale = addHeightScale;
            }
            float scaleImageHW = height / width;
            float scaleEndViewHW = mTargetHeight / mTargetWidth;
            float targetHeight, targetWidth;
            if (scaleEndViewHW > scaleImageHW) {
                targetHeight = mTargetWidth * scaleImageHW;
                targetWidth = mTargetWidth;
            } else {
                targetHeight = mTargetHeight;
                targetWidth = mTargetHeight / scaleImageHW;
            }
            if (height <= viewHeight) {
                if (ShapeImageView.ShapeScaleType.FIT_START == mSrcScaleType) {
                    deltaY = -rect.top + addHeightScale * ((mTargetHeight - targetHeight) / 2);
                } else if (ShapeImageView.ShapeScaleType.FIT_END == mSrcScaleType) {
                    deltaY = -addHeightScale * (mTargetHeight - targetHeight) / 2 + viewHeight - height - rect.top;
                } else {
                    deltaY = (viewHeight - height) / 2 - rect.top;
                }
            } else if (rect.top > 0) {
                deltaY = -rect.top;
            } else if (rect.bottom < viewHeight) {
                deltaY = viewHeight - rect.bottom;
            }
            if (width <= viewWidth) {
                if (ShapeImageView.ShapeScaleType.FIT_START == mSrcScaleType) {
                    deltaX = -rect.left + addWidthScale * ((mTargetWidth - targetWidth) / 2);
                } else if (ShapeImageView.ShapeScaleType.FIT_END == mSrcScaleType) {
                    deltaX = -addWidthScale * (mTargetWidth - targetWidth) / 2 + viewWidth - width - rect.left;
                } else {
                    deltaX = (viewWidth - width) / 2 - rect.left;
                }
            } else if (rect.left > 0) {
                deltaX = -rect.left;
            } else if (rect.right < viewWidth) {
                deltaX = viewWidth - rect.right;
            }
        } else {
            if (height <= viewHeight) {
                switch (mScaleType) {
                    case FIT_START:
                        deltaY = -rect.top;
                        break;
                    case FIT_END:
                        deltaY = viewHeight - height - rect.top;
                        break;
                    default:
                        deltaY = (viewHeight - height) / 2 - rect.top;
                        break;
                }
            } else if (rect.top > 0) {
                deltaY = -rect.top;
            } else if (rect.bottom < viewHeight) {
                deltaY = viewHeight - rect.bottom;
            }
            if (width <= viewWidth) {
                switch (mScaleType) {
                    case FIT_START:
                        deltaX = -rect.left;
                        break;
                    case FIT_END:
                        deltaX = viewWidth - width - rect.left;
                        break;
                    default:
                        deltaX = (viewWidth - width) / 2 - rect.left;
                        break;
                }
            } else if (rect.left > 0) {
                deltaX = -rect.left;
            } else if (rect.right < viewWidth) {
                deltaX = viewWidth - rect.right;
            }
        }


        mSuppMatrix.postTranslate(deltaX, deltaY);
        mBigImageMatrix.postTranslate(deltaX, deltaY);
        return true;
    }

    private int getImageViewWidth(ImageView imageView) {
        return imageView.getWidth() - ViewUtils.getViewPaddingLeft(imageView) - ViewUtils.getViewPaddingRight(imageView);
    }

    private int getImageViewHeight(ImageView imageView) {
        return imageView.getHeight() - imageView.getPaddingTop() - imageView.getPaddingBottom();
    }

    private void cancelFling() {
        if (mCurrentFlingRunnable != null) {
            mCurrentFlingRunnable.cancelFling();
            mCurrentFlingRunnable = null;
        }
    }

    private class AnimatedZoomRunnable implements Runnable {

        private final float mFocalX, mFocalY;
        private final long mStartTime;
        private final float mZoomStart, mZoomEnd;

        public AnimatedZoomRunnable(final float currentZoom, final float targetZoom,
                                    final float focalX, final float focalY) {
            mFocalX = focalX;
            mFocalY = focalY;
            mStartTime = System.currentTimeMillis();
            mZoomStart = currentZoom;
            mZoomEnd = targetZoom;
        }

        @Override
        public void run() {
            float t = interpolate();
            float scale = mZoomStart + t * (mZoomEnd - mZoomStart);
            float deltaScale = scale / getScale();
            onGestureListener.onScale(false, deltaScale, mFocalX, mFocalY);
            // We haven't hit our target scale yet, so post ourselves again
            if (t < 1f) {
                Compat.postOnAnimation(mImageView, this);
            }
        }

        private float interpolate() {
            float t = 1f * (System.currentTimeMillis() - mStartTime) / mZoomDuration;
            t = Math.min(1f, t);
            t = mInterpolator.getInterpolation(t);
            return t;
        }
    }

    private class FlingRunnable implements Runnable {

        private final OverScroller mScroller;
        private int mCurrentX, mCurrentY;

        public FlingRunnable(Context context) {
            mScroller = new OverScroller(context);
        }

        public void cancelFling() {
            mScroller.forceFinished(true);
        }

        public void fling(int viewWidth, int viewHeight, int velocityX,
                          int velocityY) {
            final RectF rect = getDisplayRect();
            if (rect == null) {
                return;
            }
            final int startX = Math.round(-rect.left);
            final int minX, maxX, minY, maxY;
            if (viewWidth < rect.width()) {
                minX = 0;
                maxX = Math.round(rect.width() - viewWidth);
            } else {
                minX = maxX = startX;
            }
            final int startY = Math.round(-rect.top);
            if (viewHeight < rect.height()) {
                minY = 0;
                maxY = Math.round(rect.height() - viewHeight);
            } else {
                minY = maxY = startY;
            }
            mCurrentX = startX;
            mCurrentY = startY;
            // If we actually can move, fling the scroller
            if (startX != maxX || startY != maxY) {
                mScroller.fling(startX, startY, velocityX, velocityY, minX,
                        maxX, minY, maxY, 0, 0);
            }
        }

        @Override
        public void run() {
            if (mScroller.isFinished()) {
                return; // remaining post that should not be handled
            }
            if (mScroller.computeScrollOffset()) {
                final int newX = mScroller.getCurrX();
                final int newY = mScroller.getCurrY();
                mSuppMatrix.postTranslate(mCurrentX - newX, mCurrentY - newY);
                mBigImageMatrix.postTranslate(mCurrentX - newX, mCurrentY - newY);
                checkAndDisplayMatrix();
                mCurrentX = newX;
                mCurrentY = newY;
                // Post On animation
                Compat.postOnAnimation(mImageView, this);
            }
        }
    }

    public void unRegisterDisplayListener() {
        if (screenOrientationEvent != null) {
            screenOrientationEvent.unRegisterDisplayListener();
        }
    }

    public void registerDisplayListener() {
        if (screenOrientationEvent != null) {
            screenOrientationEvent.unRegisterDisplayListener();
            screenOrientationEvent.registerDisplayListener(onOrientationListener);
        }
    }

    private final ScreenOrientationEvent.OnOrientationListener onOrientationListener = new ScreenOrientationEvent.OnOrientationListener() {
        @Override
        public void onOrientationChanged() {
            mScreenOrientationChange = true;
        }
    };

    public boolean isScreenOrientationChange() {
        return mScreenOrientationChange;
    }

    public void setScreenOrientationChange(boolean screenOrientationChange) {
        this.mScreenOrientationChange = screenOrientationChange;
    }

    private float mAutoCropHeightWidthRatio;

    public void setAutoCropHeightWidthRatio(float autoCropHeightWidthRatio) {
        this.mAutoCropHeightWidthRatio = autoCropHeightWidthRatio;
    }

    Matrix getBigImageMatrix() {
        return mBigImageMatrix;
    }

    void resetBigImageMatrix() {
        mBigImageMatrix.reset();
    }
}