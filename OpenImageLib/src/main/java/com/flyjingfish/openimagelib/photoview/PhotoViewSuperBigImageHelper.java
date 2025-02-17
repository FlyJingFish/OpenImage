package com.flyjingfish.openimagelib.photoview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;

import com.flyjingfish.openimagelib.utils.BitmapUtils;
import com.flyjingfish.openimagelib.utils.OpenImageLogUtils;
import com.flyjingfish.openimagelib.utils.ScreenUtils;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class PhotoViewSuperBigImageHelper {
    private static final int REGION = 2;
    private final PhotoView photoView;
    private int imageWidth;
    private int imageHeight;
    private int viewWidth;
    private int viewHeight;
    private boolean isSuperBigImage;
    private SkiaImageRegionDecoder skiaImageRegionDecoder;
    private final ReadWriteLock decoderLock = new ReentrantReadWriteLock(true);
    private int[] originalImageSize;
    private boolean isWeb;
    private int rotate;
    private RectF matrixChangedRectF;
    private boolean isOnGlobalLayout;
    private String filePath;
    private boolean isInitDecoder;
    private static float TOTAL_CACHE_LENGTH;
    private final Matrix mDrawMatrix = new Matrix();
    private final Matrix mBaseMatrix = new Matrix();

    PhotoViewSuperBigImageHelper(PhotoView photoView) {
        this.photoView = photoView;
        TOTAL_CACHE_LENGTH = ScreenUtils.dp2px(photoView.getContext(), 100);
        photoView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {

            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                mHandler.removeCallbacksAndMessages(null);
            }
        });
        photoView.getViewTreeObserver().addOnGlobalLayoutListener(new MyOnGlobalLayoutListener());
    }

    void onMatrixChanged(RectF rectF) {
        matrixChangedRectF = rectF;
        toGetBigImage();
    }

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {

        private BitmapLoadTask task;

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == REGION && isSuperBigImage && imageWidth != 0 && imageHeight != 0 && isInitDecoder) {//1333=1000
                RectF rect = (RectF) msg.obj;
                if (rect.width() > imageWidth || rect.height() > imageHeight) {//说明该取图了
                    if (task != null) {
                        task.cancel(true);
                    }
                    task = new BitmapLoadTask(PhotoViewSuperBigImageHelper.this, skiaImageRegionDecoder, rect, originalImageSize,rotate);
                    execute(task);
                } else {
                    photoView.clearBitmap();
                }
            }
        }
    };


    void setSubsamplingScaleBitmap(DecoderBitmap decoderBitmap) {
        RectF decoderRect;
        if (decoderBitmap != null && matrixChangedRectF != null && (decoderRect = decoderBitmap.decoderRect) != null
                && decoderRect.left == matrixChangedRectF.left && decoderRect.right == matrixChangedRectF.right
                && decoderRect.top == matrixChangedRectF.top && decoderRect.bottom == matrixChangedRectF.bottom) {
            Bitmap bitmap = decoderBitmap.bitmap;
            Bitmap subsamplingScaleBitmap = photoView.getSubsamplingScaleBitmap();
            if (subsamplingScaleBitmap != null && subsamplingScaleBitmap != bitmap) {
                subsamplingScaleBitmap.recycle();
            }
            RectF showingViewRect = decoderBitmap.showViewRect;
            photoView.getAttacher().resetBigImageMatrix();
            mBaseMatrix.reset();
            mDrawMatrix.reset();
            mBaseMatrix.setRectToRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()),
                    showingViewRect, Matrix.ScaleToFit.FILL);
            mDrawMatrix.set(mBaseMatrix);
            photoView.setSubsamplingScaleBitmap(bitmap, mDrawMatrix);
        } else {
            if (decoderBitmap != null && decoderBitmap.bitmap != null && !decoderBitmap.bitmap.isRecycled()) {
                Bitmap bitmap = decoderBitmap.bitmap;
                bitmap.recycle();
            }
            toGetBigImage();
        }
    }


    void setImageFilePath(String filePath) {
        this.filePath = filePath;
        Drawable drawable;
        if ((drawable = getDrawable()) == null || filePath == null) {
            return;
        }
        String mimeType = BitmapUtils.getImageTypeWithMime(photoView.getContext(), filePath);
        if ("gif".equalsIgnoreCase(mimeType)) {
            return;
        }
        imageWidth = drawable.getIntrinsicWidth();
        imageHeight = drawable.getIntrinsicHeight();
        LoadImageUtils.INSTANCE.loadImageForSize(photoView.getContext(), filePath, (filePath1, originalImageSize, isWeb,rotate) -> {
            PhotoViewSuperBigImageHelper.this.originalImageSize = originalImageSize;
            PhotoViewSuperBigImageHelper.this.isWeb = isWeb;
            PhotoViewSuperBigImageHelper.this.rotate = rotate;
            if (isOnGlobalLayout) {
                init();
                return;
            }
            photoView.getViewTreeObserver().addOnGlobalLayoutListener(new MyOnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    init();
                }
            });

        });
    }

    private class MyOnGlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {
        @Override
        public void onGlobalLayout() {
            TOTAL_CACHE_LENGTH = Math.max(photoView.getWidth()/2f,ScreenUtils.dp2px(photoView.getContext(), 100));
            isOnGlobalLayout = true;
            viewWidth = photoView.getWidth();
            viewHeight = photoView.getHeight();
            photoView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
    }

    private void init() {
        if (!isWeb && originalImageSize != null) {
            boolean isBigImage = originalImageSize[0] > imageWidth && originalImageSize[1] > imageHeight;
            if (rotate == 90 || rotate == 270){
                isBigImage = originalImageSize[1] > imageWidth && originalImageSize[0] > imageHeight;
            }

            if (isBigImage) {
                int viewWidth = getWidth();
                int viewHeight = getHeight();
                final float widthScale = viewWidth * 1f / imageWidth;
                final float heightScale = viewHeight * 1f / imageHeight;
                float scale = Math.min(widthScale, heightScale);


                skiaImageRegionDecoder = new SkiaImageRegionDecoder();
                isSuperBigImage = true;
                float maxScale = Math.max(originalImageSize[0], originalImageSize[1]) * 1f / Math.max(imageWidth, imageHeight) / scale;

                try {
                    photoView.setMaximumScale(maxScale);
                } catch (Exception ignored) {
                }
                DecoderInitTask task = new DecoderInitTask(photoView.getContext(), this, skiaImageRegionDecoder, filePath);
                execute(task);
            }
        }
    }

    private Drawable getDrawable() {
        return photoView.getDrawable();
    }

    private final Executor executor = AsyncTask.THREAD_POOL_EXECUTOR;

    private void execute(AsyncTask<Void, Void, ?> asyncTask) {
        asyncTask.executeOnExecutor(executor);
    }

    private static class DecoderInitTask extends AsyncTask<Void, Void, Boolean> {
        private final WeakReference<PhotoViewSuperBigImageHelper> viewRef;
        private final WeakReference<Context> contextRef;
        private final WeakReference<ImageRegionDecoder> decoderRef;
        private final String filePath;

        DecoderInitTask(Context context, PhotoViewSuperBigImageHelper view, ImageRegionDecoder decoder, String filePath) {
            this.viewRef = new WeakReference<>(view);
            this.contextRef = new WeakReference<>(context);
            this.decoderRef = new WeakReference<>(decoder);
            this.filePath = filePath;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Context context = contextRef.get();
                ImageRegionDecoder decoder = decoderRef.get();
                if (context != null && decoder != null) {
                    decoder.init(context, SkiaImageRegionDecoder.stringToUri(filePath));
                    return true;
                }
            } catch (Exception ignored) {
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean xyo) {
            final PhotoViewSuperBigImageHelper subsamplingScaleImageView = viewRef.get();
            if (subsamplingScaleImageView != null) {
                subsamplingScaleImageView.isInitDecoder = xyo;
            }
        }


    }


    private static class BitmapLoadTask extends AsyncTask<Void, Void, DecoderBitmap> {
        private final WeakReference<PhotoViewSuperBigImageHelper> viewRef;
        private final WeakReference<ImageRegionDecoder> decoderRef;
        private final RectF decoderRect;
        private final int[] originalImageSize;
        private final int viewWidth;
        private final int viewHeight;
        private final int rotate;

        BitmapLoadTask(PhotoViewSuperBigImageHelper view, ImageRegionDecoder decoder, RectF decoderRect, int[] originalImageSize,int rotate) {
            this.viewRef = new WeakReference<>(view);
            this.decoderRef = new WeakReference<>(decoder);
            this.decoderRect = new RectF(decoderRect.left, decoderRect.top, decoderRect.right, decoderRect.bottom);
            this.originalImageSize = originalImageSize;
            this.rotate = rotate;
            viewWidth = view.getWidth();
            viewHeight = view.getHeight();
        }

        @Override
        protected DecoderBitmap doInBackground(Void... params) {
            try {
                PhotoViewSuperBigImageHelper view = viewRef.get();
                ImageRegionDecoder decoder = decoderRef.get();
                if (decoder != null && view != null && decoder.isReady() && !isCancelled()) {
                    view.decoderLock.readLock().lock();
                    try {
                        if (decoder.isReady()) {
                            RectF rect = decoderRect;
                            int left, top, right, bottom;
                            int left1, top1, right1, bottom1;
                            if (rect.left > 0) {
                                left = 0;
                                left1 = (int) rect.left;
                            } else {
                                left = (int) Math.abs(rect.left);
                                left1 = 0;
                            }
                            if (rect.top > 0) {
                                top = 0;
                                top1 = (int) rect.top;
                            } else {
                                top = (int) Math.abs(rect.top);
                                top1 = 0;
                            }
                            if (rect.right > viewWidth) {
                                right = (int) (rect.width() - (rect.right - viewWidth));
                                right1 = viewWidth;
                            } else {
                                right = (int) rect.width();
                                right1 = (int) rect.right;
                            }
                            if (rect.bottom > viewHeight) {
                                bottom = (int) (rect.height() - (rect.bottom - viewHeight));
                                bottom1 = viewHeight;
                            } else {
                                bottom = (int) rect.height();
                                bottom1 = (int) rect.bottom;
                            }
                            float scale = rect.height() / originalImageSize[1];
                            if (rotate == 90 || rotate == 270){
                                scale = rect.height() / originalImageSize[0];
                            }
                            float cacheLengthLeft, cacheLengthTop, cacheLengthRight, cacheLengthBottom;
                            if (left - TOTAL_CACHE_LENGTH > 0) {
                                cacheLengthLeft = TOTAL_CACHE_LENGTH;
                            } else {
                                cacheLengthLeft = left;
                            }
                            if (top - TOTAL_CACHE_LENGTH > 0) {
                                cacheLengthTop = TOTAL_CACHE_LENGTH;
                            } else {
                                cacheLengthTop = top;
                            }
                            if (right + TOTAL_CACHE_LENGTH > rect.width()) {
                                cacheLengthRight = rect.width() - right;
                            } else {
                                cacheLengthRight = TOTAL_CACHE_LENGTH;
                            }
                            if (bottom + TOTAL_CACHE_LENGTH > rect.height()) {
                                cacheLengthBottom = rect.height() - bottom;
                            } else {
                                cacheLengthBottom = TOTAL_CACHE_LENGTH;
                            }

//                    Rect subsamplingRect = new Rect((int) (left/scale), (int) (top/scale), (int) (right/scale), (int) (bottom/scale));
//                    showRect = new Rect(left1,top1,right1,bottom1);
                            Rect subsamplingRect = new Rect((int) ((left - cacheLengthLeft) / scale), (int) ((top - cacheLengthTop) / scale), (int) ((right + cacheLengthRight) / scale), (int) ((bottom + cacheLengthBottom) / scale));
                            RectF showViewRect = new RectF((left1 - cacheLengthLeft), (top1 - cacheLengthTop), (right1 + cacheLengthRight), (bottom1 + cacheLengthBottom));
//                            int inSampleSize = BitmapUtils.getMaxInSampleSize(subsamplingRect.width(), subsamplingRect.height());
                            int scaleH = (int) (subsamplingRect.height()*1f/viewHeight);
                            int scaleW = (int) (subsamplingRect.width()*1f/viewWidth);
                            int inSampleSize = Math.max(Math.min(scaleH,scaleW),1);
                            RectF subsamplingRectF= new RectF(subsamplingRect.left,subsamplingRect.top,subsamplingRect.right,subsamplingRect.bottom);
                            rotateRect(subsamplingRectF,rotate,originalImageSize);
                            Bitmap bitmap = null;
                            try {
                                bitmap = decoder.decodeRegion(new Rect((int) subsamplingRectF.left, (int) subsamplingRectF.top, (int) subsamplingRectF.right, (int) subsamplingRectF.bottom), inSampleSize);
                                int pivotX = bitmap.getWidth() / 2; // 旋转中心的X坐标
                                int pivotY = bitmap.getHeight() / 2; // 旋转中心的Y坐标

                                bitmap = rotateBitmap(bitmap, rotate, pivotX, pivotY);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            return new DecoderBitmap(bitmap, showViewRect, rect);
                        }
                    } finally {
                        view.decoderLock.readLock().unlock();
                    }
                }
            } catch (Exception | OutOfMemoryError ignored) {
            }
            return null;
        }



        public static Bitmap rotateBitmap(Bitmap source, float angle, int pivotX, int pivotY) {
            Matrix matrix = new Matrix();
            matrix.postRotate(angle, pivotX, pivotY);
            Bitmap newBitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
            OpenImageLogUtils.logE("rotateBitmap","source="+source.getWidth()+","+source.getHeight()+"newBitmap="+newBitmap.getWidth()+","+newBitmap.getHeight());
            return newBitmap;
        }
        @Override
        protected void onPostExecute(DecoderBitmap decoderBitmap) {
            final PhotoViewSuperBigImageHelper subsamplingScaleImageView = viewRef.get();
            if (subsamplingScaleImageView != null && decoderBitmap != null) {
                subsamplingScaleImageView.setSubsamplingScaleBitmap(decoderBitmap);
            }
        }

    }
    public static void rotateRect(RectF subsamplingRectF,int rotate,int[] size){
        RectF rectF = new RectF(subsamplingRectF.left,subsamplingRectF.top,subsamplingRectF.right,subsamplingRectF.bottom);
        if (rotate == 90){
            subsamplingRectF.left = rectF.top;
            subsamplingRectF.top = size[1]-rectF.right;
            subsamplingRectF.right = rectF.bottom;
            subsamplingRectF.bottom = rectF.width()+subsamplingRectF.top;
        }else if (rotate == 270){
            subsamplingRectF.left = size[0] - rectF.bottom;
            subsamplingRectF.top = rectF.left;
            subsamplingRectF.right = rectF.height()+subsamplingRectF.left;
            subsamplingRectF.bottom = rectF.right;
        }else if (rotate == 180){
            subsamplingRectF.left = size[0] - rectF.left;
            subsamplingRectF.top = size[1] - rectF.top;
            subsamplingRectF.right = subsamplingRectF.left+rectF.width();
            subsamplingRectF.bottom = subsamplingRectF.top+rectF.height();
        }
    }
    private static class DecoderBitmap {
        Bitmap bitmap;
        RectF showViewRect;
        RectF decoderRect;

        public DecoderBitmap(Bitmap bitmap, RectF showViewRect, RectF decoderRect) {
            this.bitmap = bitmap;
            this.showViewRect = showViewRect;
            this.decoderRect = decoderRect;
        }
    }

    private int getHeight() {
        return photoView.getHeight();
    }

    private int getWidth() {
        return photoView.getWidth();
    }

    private void toGetBigImage() {
        if (matrixChangedRectF != null && isSuperBigImage) {
            Message message = Message.obtain();
            message.what = REGION;
            message.obj = matrixChangedRectF;
            mHandler.removeMessages(REGION);
            mHandler.sendMessageDelayed(message, 100);
        }
    }

    public Matrix getBaseMatrix() {
        return mBaseMatrix;
    }
}
