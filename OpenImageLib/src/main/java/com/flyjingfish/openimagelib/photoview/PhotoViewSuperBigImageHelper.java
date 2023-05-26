package com.flyjingfish.openimagelib.photoview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class PhotoViewSuperBigImageHelper {
    private static final int REGION = 2;
    private PhotoView photoView;
    private int imageWidth;
    private int imageHeight;
    private boolean isSuperBigImage;
    private SkiaImageRegionDecoder skiaImageRegionDecoder;
    private final ReadWriteLock decoderLock = new ReentrantReadWriteLock(true);
    private int[] originalImageSize;
    private RectF matrixChangedRectF;
    private RectF showMatrixChangedRectF;

    public PhotoViewSuperBigImageHelper(PhotoView photoView) {
        this.photoView = photoView;
        photoView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {

            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                mHandler.removeCallbacksAndMessages(null);
            }
        });
    }

    void onMatrixChanged(RectF rectF){
        matrixChangedRectF = rectF;
    }

    public void setSubsamplingScaleBitmap(Bitmap subsamplingScaleBitmap) {
        if (showMatrixChangedRectF == null){
            showMatrixChangedRectF = new RectF(matrixChangedRectF.left,matrixChangedRectF.top,matrixChangedRectF.right,matrixChangedRectF.bottom);
        }else {
            showMatrixChangedRectF.set(matrixChangedRectF.left,matrixChangedRectF.top,matrixChangedRectF.right,matrixChangedRectF.bottom);
        }
        photoView.setSubsamplingScaleBitmap(subsamplingScaleBitmap,showRect);
    }

    private Rect showRect;
    private final Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == REGION && isSuperBigImage && imageWidth != 0 && imageHeight != 0){//1333=1000
                RectF rect = (RectF) msg.obj;
                int viewWidth = getWidth();
                int viewHeight = getHeight();
                if (rect.width() > imageWidth || rect.height() > imageHeight){//说明该取图了

                    int left,top,right,bottom;
                    int left1,top1,right1,bottom1;
                    if (rect.left>0){
                        left = 0;
                        left1 = (int) rect.left;
                    }else {
                        left = (int) Math.abs(rect.left);
                        left1 = 0;
                    }
                    if (rect.top>0){
                        top = 0;
                        top1 = (int) rect.top;
                    }else {
                        top = (int) Math.abs(rect.top);
                        top1 = 0;
                    }
                    if (rect.right > viewWidth){
                        right = (int) (rect.width() - (rect.right - viewWidth));
                        right1 = viewWidth;
                    }else {
                        right = (int) rect.width();
                        right1 = (int) rect.right;
                    }
                    if (rect.bottom > viewHeight){
                        bottom = (int) (rect.height() - (rect.bottom - viewHeight));
                        bottom1 = viewHeight;
                    }else {
                        bottom = (int) rect.height();
                        bottom1 = (int) rect.bottom;
                    }
                    float scale = rect.height()/originalImageSize[1];
                    Rect subsamplingRect = new Rect((int) (left/scale), (int) (top/scale), (int) (right/scale), (int) (bottom/scale));
                    showRect = new Rect(left1,top1,right1,bottom1);
                    TileLoadTask task = new TileLoadTask(PhotoViewSuperBigImageHelper.this, skiaImageRegionDecoder, subsamplingRect,showRect);
                    execute(task);
                    Log.e("handleMessage",rect+"==="+subsamplingRect+"===="+rect.width()+"="+rect.height()+"="+viewWidth+"="+viewHeight);
                }else {
                    setSubsamplingScaleBitmap(null);
                }
            }
        }
    };


    public void setImageFilePath(String filePath) {
        Drawable drawable = getDrawable();
        if (drawable == null){
            return;
        }
        imageWidth = drawable.getIntrinsicWidth();
        imageHeight = drawable.getIntrinsicHeight();
        final float imageScaleWh = imageWidth * 1f/ imageHeight;
        LoadImageUtils.INSTANCE.loadImageForSize(photoView.getContext(), filePath, (filePath1, originalImageSize, isWeb) -> {
            PhotoViewSuperBigImageHelper.this.originalImageSize = originalImageSize;
            photoView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    Log.e("setImageFilePath",originalImageSize[0]+"="+originalImageSize[1]+"="+ imageWidth +"="+ imageHeight);
                    photoView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int viewWidth = getWidth();
                    int viewHeight = getHeight();
                    final float viewScaleWh = viewWidth * 1f/viewHeight;
                    if (!isWeb && originalImageSize != null){
                        if (originalImageSize[0] > imageWidth && originalImageSize[1] > imageHeight){
                            skiaImageRegionDecoder = new SkiaImageRegionDecoder();
                            isSuperBigImage = true;
                            float maxScale = Math.max(originalImageSize[0],originalImageSize[1]) * 1f / Math.max(imageWidth, imageHeight);
                            float max = photoView.getMaximumScale();
                            float mid = photoView.getMediumScale();
                            float min = photoView.getMinimumScale();

                            Log.e("setImageFilePath1",maxScale+"="+photoView.getMaximumScale()+"="+photoView.getMediumScale());
                            photoView.setScaleLevels(min,(min+maxScale)/2,maxScale);
//                            setScaleLevels(1,2,3);
                            TilesInitTask task = new TilesInitTask(photoView.getContext(), skiaImageRegionDecoder, Uri.parse(filePath));
                            execute(task);
                        }
                    }
                }
            });

        });
    }

    private Drawable getDrawable() {
        return photoView.getDrawable();
    }

    private Executor executor = AsyncTask.THREAD_POOL_EXECUTOR;
    private void execute(AsyncTask<Void, Void, ?> asyncTask) {
        asyncTask.executeOnExecutor(executor);
    }
    private static class TilesInitTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<Context> contextRef;
        private final WeakReference<ImageRegionDecoder> decoderRef;
        private final Uri source;

        TilesInitTask(Context context, ImageRegionDecoder decoder, Uri source) {
            this.contextRef = new WeakReference<>(context);
            this.decoderRef = new WeakReference<>(decoder);
            this.source = source;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                String sourceUri = source.toString();
                Context context = contextRef.get();
                ImageRegionDecoder decoder = decoderRef.get();
                if (context != null && decoder != null) {
                    Point dimensions = decoder.init(context, source);
                    return null;
                }
            } catch (Exception ignored) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void xyo) {
        }
    }

    private static class TileLoadTask extends AsyncTask<Void, Void, Bitmap> {
        private final WeakReference<PhotoViewSuperBigImageHelper> viewRef;
        private final WeakReference<ImageRegionDecoder> decoderRef;
        private final Rect fileSRect;
        private final Rect showRect;

        TileLoadTask(PhotoViewSuperBigImageHelper view, ImageRegionDecoder decoder,Rect fileSRect,Rect showRect) {
            this.viewRef = new WeakReference<>(view);
            this.decoderRef = new WeakReference<>(decoder);
            this.fileSRect = fileSRect;
            this.showRect = showRect;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                PhotoViewSuperBigImageHelper view = viewRef.get();
                ImageRegionDecoder decoder = decoderRef.get();
                if (decoder != null && view != null && decoder.isReady()) {
                    view.decoderLock.readLock().lock();
                    try {
                        if (decoder.isReady()) {
                            int inSampleSize = BitmapUtils.getMaxInSampleSize(fileSRect.width(), fileSRect.height());
                            // Update tile's file sRect according to rotation
                            return decoder.decodeRegion(fileSRect, inSampleSize);
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
        protected void onPostExecute(Bitmap bitmap) {
            final PhotoViewSuperBigImageHelper subsamplingScaleImageView = viewRef.get();
            if (subsamplingScaleImageView != null) {
                subsamplingScaleImageView.setSubsamplingScaleBitmap(bitmap);
            }
        }
    }

    private int getHeight() {
        return photoView.getHeight();
    }

    private int getWidth() {
        return photoView.getWidth();
    }

    void onTouchEnd(){
        toGetBigImage();
    }

    void moving(){
        if (showRect != null && matrixChangedRectF != null &&showMatrixChangedRectF != null){
            float moveX = matrixChangedRectF.left - showMatrixChangedRectF.left;
            float moveY = matrixChangedRectF.top - showMatrixChangedRectF.top;
            int left = (int) (showRect.left + moveX);
            int top = (int) (showRect.top + moveY);
            int right = (int) (showRect.right + moveX);
            int bottom = (int) (showRect.bottom + moveY);
            Rect rect = new Rect(left,top,right,bottom);
            photoView.setShowRect(rect);
        }
    }

//    void scaling(){
//        if (showRect != null && matrixChangedRectF != null &&showMatrixChangedRectF != null){
//            float scaleX = matrixChangedRectF.width() - showMatrixChangedRectF.width();
//            float scaleY = matrixChangedRectF.height() - showMatrixChangedRectF.height();
//            int left = (int) (showRect.left + moveX);
//            int top = (int) (showRect.top + moveY);
//            int right = (int) (showRect.right + moveX);
//            int bottom = (int) (showRect.bottom + moveY);
//            Rect rect = new Rect(left,top,right,bottom);
//            photoView.setShowRect(rect);
//        }
//    }

    private void toGetBigImage(){
        if (matrixChangedRectF != null){
            Message message = Message.obtain();
            message.what = REGION;
            message.obj = matrixChangedRectF;
            mHandler.removeMessages(REGION);
            mHandler.sendMessageDelayed(message,200);
        }
    }
}
