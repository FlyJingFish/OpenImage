package com.flyjingfish.openimagelib.photoview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;

import com.flyjingfish.openimagelib.utils.ScreenUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLDecoder;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class PhotoViewSuperBigImageHelper {
    private static final int REGION = 2;
    private final PhotoView photoView;
    private int imageWidth;
    private int imageHeight;
    private boolean isSuperBigImage;
    private SkiaImageRegionDecoder skiaImageRegionDecoder;
    private final ReadWriteLock decoderLock = new ReentrantReadWriteLock(true);
    private int[] originalImageSize;
    private boolean isWeb;
    private RectF matrixChangedRectF;
    private boolean isOnGlobalLayout;
    private String filePath;
    private boolean isInitDecoder;
    private static float TOTAL_CACHE_LENGTH;
    private static final String FILE_SCHEME = "file:///";
    private static final String ASSET_SCHEME = "file:///android_asset/";
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
                    task = new BitmapLoadTask(PhotoViewSuperBigImageHelper.this, skiaImageRegionDecoder, rect, originalImageSize);
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
        LoadImageUtils.INSTANCE.loadImageForSize(photoView.getContext(), filePath, (filePath1, originalImageSize, isWeb) -> {
            PhotoViewSuperBigImageHelper.this.originalImageSize = originalImageSize;
            PhotoViewSuperBigImageHelper.this.isWeb = isWeb;
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
            photoView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
    }

    private void init() {
        if (!isWeb && originalImageSize != null) {
            if (originalImageSize[0] > imageWidth && originalImageSize[1] > imageHeight) {
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
                    decoder.init(context, stringToUri(filePath));
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

        public Uri stringToUri(@NonNull String uri) {
            if (!uri.contains("://")) {
                if (uri.startsWith("/")) {
                    uri = uri.substring(1);
                }
                uri = FILE_SCHEME + uri;
            }
            return getImageUri(Uri.parse(uri));
        }

        private Uri getImageUri(@NonNull Uri uri) {
            String uriString = uri.toString();
            if (uriString.startsWith(FILE_SCHEME)) {
                File uriFile = new File(uriString.substring(FILE_SCHEME.length() - 1));
                if (!uriFile.exists()) {
                    try {
                        uri = Uri.parse(URLDecoder.decode(uriString, "UTF-8"));
                    } catch (UnsupportedEncodingException ignored) {
                    }
                }
            }
            return uri;
        }
    }


    private static class BitmapLoadTask extends AsyncTask<Void, Void, DecoderBitmap> {
        private final WeakReference<PhotoViewSuperBigImageHelper> viewRef;
        private final WeakReference<ImageRegionDecoder> decoderRef;
        private final RectF decoderRect;
        private final int[] originalImageSize;
        private final int viewWidth;
        private final int viewHeight;

        BitmapLoadTask(PhotoViewSuperBigImageHelper view, ImageRegionDecoder decoder, RectF decoderRect, int[] originalImageSize) {
            this.viewRef = new WeakReference<>(view);
            this.decoderRef = new WeakReference<>(decoder);
            this.decoderRect = new RectF(decoderRect.left, decoderRect.top, decoderRect.right, decoderRect.bottom);
            this.originalImageSize = originalImageSize;
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
                            int inSampleSize = BitmapUtils.getMaxInSampleSize(subsamplingRect.width(), subsamplingRect.height());
                            Bitmap bitmap = decoder.decodeRegion(subsamplingRect, inSampleSize);
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

        @Override
        protected void onPostExecute(DecoderBitmap decoderBitmap) {
            final PhotoViewSuperBigImageHelper subsamplingScaleImageView = viewRef.get();
            if (subsamplingScaleImageView != null) {
                subsamplingScaleImageView.setSubsamplingScaleBitmap(decoderBitmap);
            }
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
