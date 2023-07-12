package com.flyjingfish.openimagefulllib;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.danikula.videocache.CacheListener;
import com.danikula.videocache.HttpProxyCacheServer;
import com.danikula.videocache.StorageUtils;
import com.danikula.videocache.file.FileNameGenerator;
import com.danikula.videocache.file.Md5FileNameGenerator;
import com.flyjingfish.openimageglidelib.GlideDownloadMediaHelper;
import com.flyjingfish.openimageglidelib.LoadImageUtils;
import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.enums.MediaType;
import com.flyjingfish.openimagelib.listener.OnDownloadMediaListener;
import com.shuyu.gsyvideoplayer.cache.CacheFactory;
import com.shuyu.gsyvideoplayer.cache.ProxyCacheManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FullGlideDownloadMediaHelper extends GlideDownloadMediaHelper {
    private static volatile FullGlideDownloadMediaHelper mInstance;
    private final ExecutorService cThreadPool = Executors.newFixedThreadPool(5);
    private File mVideoCacheDir = null;
    private boolean isDownloadWithCache = true;

    public static FullGlideDownloadMediaHelper getInstance() {
        if (mInstance == null) {
            synchronized (FullGlideDownloadMediaHelper.class) {
                if (mInstance == null) {
                    mInstance = new FullGlideDownloadMediaHelper();
                }
            }
        }
        return mInstance;
    }

    /**
     * 如果你设置了自定义缓存目录则需设置此项，否则功能会异常
     *
     * @param mCacheDir 视频缓存目录， null 则使用默认缓存目录
     */
    public void setVideoCacheDir(File mCacheDir) {
        this.mVideoCacheDir = mCacheDir;
    }

    public File getVideoCacheDir() {
        return mVideoCacheDir;
    }

    public boolean isDownloadWithCache() {
        return isDownloadWithCache;
    }

    /**
     *
     * 是否启用下载和缓存同时进行，这将节省一半流量(这项设置只针对视频)
     *
     * @param downloadWithCache 下载和缓存是否同时进行，默认 true
     */
    public void setDownloadWithCache(boolean downloadWithCache) {
        isDownloadWithCache = downloadWithCache;
    }

    @Override
    public void download(FragmentActivity activity, LifecycleOwner lifecycleOwner, OpenImageUrl openImageUrl, OnDownloadMediaListener onDownloadMediaListener) {
        final String downloadUrl = openImageUrl.getType() == MediaType.VIDEO ? openImageUrl.getVideoUrl() : openImageUrl.getImageUrl();
        if (openImageUrl.getType() == MediaType.VIDEO && CacheFactory.getCacheManager() instanceof ProxyCacheManager) {
            Context application = activity.getApplicationContext();
            final String key = UUID.randomUUID().toString();
            final Map<String, OnDownloadMediaListener> onDownloadMediaListenerHashMap = new HashMap<>();
            onDownloadMediaListenerHashMap.put(key, onDownloadMediaListener);
            final boolean[] isDestroy = new boolean[]{false};
            lifecycleOwner.getLifecycle().addObserver(new LifecycleEventObserver() {
                @Override
                public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        isDestroy[0] = true;
                        onDownloadMediaListenerHashMap.clear();
                        source.getLifecycle().removeObserver(this);
                    }
                }
            });
            FileNameGenerator md5FileNameGenerator = new Md5FileNameGenerator();
            String name = md5FileNameGenerator.generate(downloadUrl);
            File cachePath = mVideoCacheDir == null ? StorageUtils.getIndividualCacheDirectory(application) : mVideoCacheDir;
            File file = new File(cachePath, name);

            if (file.exists()) {
                if (onDownloadMediaListener != null) {
                    onDownloadMediaListener.onDownloadStart(false);
                }
                LoadImageUtils.INSTANCE.saveFile(application, file, true, sucPath -> {
                    if (isDestroy[0]) {
                        return;
                    }
                    OnDownloadMediaListener onDownloadMediaListener1;
                    if ((onDownloadMediaListener1 = onDownloadMediaListenerHashMap.get(key)) != null) {
                        if (!TextUtils.isEmpty(sucPath)) {
                            onDownloadMediaListener1.onDownloadSuccess(sucPath);
                        } else {
                            onDownloadMediaListener1.onDownloadFailed();
                        }
                    }
                });
                return;
            } else if (isDownloadWithCache) {
                if (downloadUrl.startsWith("http") && !downloadUrl.contains("127.0.0.1") && !downloadUrl.contains(".m3u8")) {
                    HttpProxyCacheServer proxyCacheServer = ProxyCacheManager.getProxy(activity.getApplicationContext(), mVideoCacheDir);
                    String url = proxyCacheServer.getProxyUrl(downloadUrl);
                    if (!TextUtils.equals(url, downloadUrl)) {
                        final CacheListener cacheListener = (cacheFile, url1, percentsAvailable) -> {
                            if (TextUtils.equals(url1, downloadUrl)) {
                                OnDownloadMediaListener onDownloadMediaListener1;
                                if ((onDownloadMediaListener1 = onDownloadMediaListenerHashMap.get(key)) != null) {
                                    onDownloadMediaListener1.onDownloadProgress(percentsAvailable);
                                }
                            }
                        };
                        proxyCacheServer.registerCacheListener(cacheListener, downloadUrl);
                        if (onDownloadMediaListener != null) {
                            onDownloadMediaListener.onDownloadStart(true);
                        }
                        cThreadPool.submit(() -> {
                            InputStream inputStream = null;
                            try {
                                URL localUrl = new URL(url);
                                inputStream = localUrl.openStream();
                                int bufferSize = 1024 * 2;
                                byte[] buffer = new byte[bufferSize];
                                int length = 0;
                                while ((length = inputStream.read(buffer)) != -1) {
                                    //由于我们只需要启动预取，因此不需要在这里执行任何操作，或者我们可以使用 ByteArrayOutputStream 将数据写入磁盘
                                }
                                proxyCacheServer.unregisterCacheListener(cacheListener, downloadUrl);
                                int retry = 0;
                                while (proxyCacheServer.getProxyUrl(downloadUrl).startsWith("http") && retry < 3) {
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException ignored) {

                                    }
                                    retry++;
                                }
                                boolean cacheFile = (!proxyCacheServer.getProxyUrl(downloadUrl).startsWith("http"));
                                if (cacheFile && file.exists()) {
                                    LoadImageUtils.INSTANCE.saveFileIgnoreThread(application, file, true, sucPath -> {
                                        if (isDestroy[0]) {
                                            return;
                                        }
                                        OnDownloadMediaListener onDownloadMediaListener1;
                                        if ((onDownloadMediaListener1 = onDownloadMediaListenerHashMap.get(key)) != null) {
                                            if (!TextUtils.isEmpty(sucPath)) {
                                                onDownloadMediaListener1.onDownloadSuccess(sucPath);
                                            } else {
                                                onDownloadMediaListener1.onDownloadFailed();
                                            }
                                        }
                                    });
                                } else {
                                    new Handler(Looper.getMainLooper()).post(() -> {
                                        if (isDestroy[0]) {
                                            return;
                                        }
                                        FullGlideDownloadMediaHelper.super.download(activity, lifecycleOwner, openImageUrl, onDownloadMediaListener);
                                    });
                                }

                            } catch (IOException e) {
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    if (isDestroy[0]) {
                                        return;
                                    }
                                    FullGlideDownloadMediaHelper.super.download(activity, lifecycleOwner, openImageUrl, onDownloadMediaListener);
                                });
                            } finally {
                                if (inputStream != null) {
                                    try {
                                        inputStream.close();
                                    } catch (IOException ignored) {
                                    }
                                }
                            }
                        });
                        return;
                    }
                }
            }
        }
        super.download(activity, lifecycleOwner, openImageUrl, onDownloadMediaListener);
    }


}
