package com.flyjingfish.openimage.openImpl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.flyjingfish.openimage.MyApplication;
import com.flyjingfish.openimage.openImpl.download.ProgressInfo;
import com.flyjingfish.openimage.openImpl.download.ProgressListener;
import com.flyjingfish.openimage.openImpl.download.ProgressManager;
import com.flyjingfish.openimage.openImpl.download.SaveImageUtils;
import com.flyjingfish.openimageglidelib.BitmapUtils;
import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.enums.MediaType;
import com.flyjingfish.openimagelib.listener.OnDownloadMediaListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import okio.Sink;

public class PicassoDownloader {
    private final Context context;
    private final String downloadUrl;
    private final Object tag = new Object();
    private final OpenImageUrl openImageUrl;
    private final OnDownloadMediaListener onDownloadMediaListener;
    private final File cacheDir;
    private final File cacheVideoDir;
    private final LifecycleOwner lifecycleOwner;
    private final boolean[] isDestroy = new boolean[]{false};
    private static final String PICASSO_VIDEO_CACHE = "picasso-video-cache";

    com.squareup.picasso.Target myTarget = new com.squareup.picasso.Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            myTarget = null;//这句不能删除否则图片加载异常
            if (isDestroy[0]) {
                return;
            }
            String cacheFile = PicassoLoader.getPicassoCacheFile(cacheDir,downloadUrl);
            SaveImageUtils.INSTANCE.saveFile(context.getApplicationContext(), new File(cacheFile), openImageUrl.getType() == MediaType.VIDEO, new SaveImageUtils.OnSaveFinish() {
                @Override
                public void onFinish(String sucPath) {
                    if (onDownloadMediaListener != null) {
                        if (!TextUtils.isEmpty(sucPath)) {
                            onDownloadMediaListener.onDownloadSuccess(sucPath);
                        } else {
                            onDownloadMediaListener.onDownloadFailed();
                        }
                    }
                }
            });
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
            if (isDestroy[0]) {
                return;
            }
            if (onDownloadMediaListener != null) {
                onDownloadMediaListener.onDownloadFailed();
            }
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    public static File createVideoCacheDir(Context context) {
        File cache = new File(context.getApplicationContext().getCacheDir(), PICASSO_VIDEO_CACHE);
        if (!cache.exists()) {
            //noinspection ResultOfMethodCallIgnored
            cache.mkdirs();
        }
        return cache;
    }

    public PicassoDownloader(Context context, LifecycleOwner lifecycleOwner, OpenImageUrl openImageUrl, OnDownloadMediaListener onDownloadMediaListener) {
        this.context = context;
        this.lifecycleOwner = lifecycleOwner;
        this.openImageUrl = openImageUrl;
        this.downloadUrl = openImageUrl.getType() == MediaType.VIDEO ? openImageUrl.getVideoUrl() : openImageUrl.getImageUrl();
        this.onDownloadMediaListener = onDownloadMediaListener;
        cacheDir = PicassoLoader.createDefaultCacheDir(context);
        cacheVideoDir = createVideoCacheDir(context);
    }

    public void download() {
        boolean isWeb = BitmapUtils.isWeb(downloadUrl);
        if (onDownloadMediaListener != null) {
            onDownloadMediaListener.onDownloadStart(true);
        }
        ProgressManager.getInstance().addResponseListener(downloadUrl, new ProgressListener() {
            @Override
            public void onProgress(ProgressInfo progressInfo) {
                if (onDownloadMediaListener != null) {
                    if (isDestroy[0]){
                        return;
                    }
                    onDownloadMediaListener.onDownloadProgress(progressInfo.getPercent());
                }
            }

            @Override
            public void onError(long id, Exception e) {

            }
        });
        if (openImageUrl.getType() == MediaType.VIDEO) {
            String cacheFile = cacheVideoDir.getAbsolutePath() + "/" + PicassoLoader.urlForDiskName(downloadUrl) + ".22";
            File localFile = new File(cacheFile);
            if (localFile.exists()){
                SaveImageUtils.INSTANCE.saveFile(context.getApplicationContext(), new File(cacheFile), openImageUrl.getType() == MediaType.VIDEO, new SaveImageUtils.OnSaveFinish() {
                    @Override
                    public void onFinish(String sucPath) {
                        if (isDestroy[0]) {
                            return;
                        }
                        if (onDownloadMediaListener != null) {
                            if (!TextUtils.isEmpty(sucPath)) {
                                onDownloadMediaListener.onDownloadSuccess(sucPath);
                            } else {
                                onDownloadMediaListener.onDownloadFailed();
                            }
                        }
                    }
                });
                return;
            }

            Request request = new Request.Builder().url(downloadUrl).build();

            MyApplication.mInstance.getOkHttpClient().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (isDestroy[0]) {
                        return;
                    }
                    if (onDownloadMediaListener != null) {
                        onDownloadMediaListener.onDownloadFailed();
                    }
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Sink sink;
                    BufferedSink bufferedSink = null;
                    //这是里的mContext是我提前获取了android的context
                    File localFile = new File(cacheFile);
                    try {
                        sink = Okio.sink(localFile);
                        bufferedSink = Okio.buffer(sink);
                        bufferedSink.writeAll(response.body().source());
                        bufferedSink.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (bufferedSink != null) {
                            bufferedSink.close();
                        }
                    }

                    SaveImageUtils.INSTANCE.saveFile(context.getApplicationContext(), new File(cacheFile), openImageUrl.getType() == MediaType.VIDEO, new SaveImageUtils.OnSaveFinish() {
                        @Override
                        public void onFinish(String sucPath) {
                            if (isDestroy[0]) {
                                return;
                            }
                            if (onDownloadMediaListener != null) {
                                if (!TextUtils.isEmpty(sucPath)) {
                                    onDownloadMediaListener.onDownloadSuccess(sucPath);
                                } else {
                                    onDownloadMediaListener.onDownloadFailed();
                                }
                            }
                        }
                    });
                }
            });

        } else {
            if (!isWeb) {
                boolean isContent = BitmapUtils.isContent(downloadUrl);
                Picasso.get().load((isContent ? "" : "file://") + downloadUrl).tag(tag).into(myTarget);
            } else {
                Picasso.get().load(downloadUrl).tag(tag).into(myTarget);
            }

        }
        lifecycleOwner.getLifecycle().addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    isDestroy[0] = false;
                    Picasso.get().cancelTag(tag);
                    source.getLifecycle().removeObserver(this);
                    ProgressManager.getInstance().removeResponseListeners(downloadUrl);
                }
            }
        });

    }

}
