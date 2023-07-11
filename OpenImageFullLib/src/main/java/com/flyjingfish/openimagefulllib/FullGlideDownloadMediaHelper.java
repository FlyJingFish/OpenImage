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
    static FullGlideDownloadMediaHelper getInstance(){
        if (mInstance == null){
            synchronized (FullGlideDownloadMediaHelper.class){
                if (mInstance == null){
                    mInstance = new FullGlideDownloadMediaHelper();
                }
            }
        }
        return mInstance;
    }

    @Override
    public void download(FragmentActivity activity, LifecycleOwner lifecycleOwner, OpenImageUrl openImageUrl, OnDownloadMediaListener onDownloadMediaListener) {
        final String downloadUrl = openImageUrl.getType() == MediaType.VIDEO?openImageUrl.getVideoUrl():openImageUrl.getImageUrl();
        if (openImageUrl.getType() == MediaType.VIDEO){
            final String key = UUID.randomUUID().toString();
            final Map<String, OnDownloadMediaListener> onDownloadMediaListenerHashMap = new HashMap<>();
            onDownloadMediaListenerHashMap.put(key,onDownloadMediaListener);
            final boolean[] isDestroy = new boolean[]{false};
            lifecycleOwner.getLifecycle().addObserver(new LifecycleEventObserver() {
                @Override
                public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                    if (event == Lifecycle.Event.ON_DESTROY){
                        isDestroy[0] = true;
                        onDownloadMediaListenerHashMap.clear();
                        source.getLifecycle().removeObserver(this);
                    }
                }
            });
            boolean isInitOkHttpClient = LoadImageUtils.INSTANCE.isInitOkHttpClient();
            FileNameGenerator md5FileNameGenerator = new Md5FileNameGenerator();
            String name = md5FileNameGenerator.generate(downloadUrl);
            String path = StorageUtils.getIndividualCacheDirectory
                    (activity.getApplicationContext()).getAbsolutePath()
                    + File.separator + name;
            File file = new File(path);
            Context application = activity.getApplicationContext();
            if (file.exists()){
                if (onDownloadMediaListener != null) {
                    onDownloadMediaListener.onDownloadStart(isInitOkHttpClient);
                }
                LoadImageUtils.INSTANCE.saveFile(application, file, true, sucPath -> {
                    if (isDestroy[0]){
                        return;
                    }
                    OnDownloadMediaListener onDownloadMediaListener1;
                    if ((onDownloadMediaListener1 = onDownloadMediaListenerHashMap.get(key)) != null) {
                        if (!TextUtils.isEmpty(sucPath)){
                            onDownloadMediaListener1.onDownloadSuccess(sucPath);
                        }else {
                            onDownloadMediaListener1.onDownloadFailed();
                        }
                    }
                });
                return;
            }else {
                if (downloadUrl.startsWith("http") && !downloadUrl.contains("127.0.0.1") && !downloadUrl.contains(".m3u8")) {
                    HttpProxyCacheServer proxyCacheServer = ProxyCacheManager.getProxy(activity.getApplicationContext(), null);
                    String url = proxyCacheServer.getProxyUrl(downloadUrl);
                    if (!TextUtils.equals(url,downloadUrl)){
                        final CacheListener cacheListener = (cacheFile, url1, percentsAvailable) -> {
                            if (TextUtils.equals(url1,downloadUrl)){
                                OnDownloadMediaListener onDownloadMediaListener1;
                                if ((onDownloadMediaListener1 = onDownloadMediaListenerHashMap.get(key)) != null) {
                                    onDownloadMediaListener1.onDownloadProgress(percentsAvailable);
                                }
                            }
                        };
                        proxyCacheServer.registerCacheListener(cacheListener,downloadUrl);
                        if (onDownloadMediaListener != null) {
                            onDownloadMediaListener.onDownloadStart(isInitOkHttpClient);
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
                                proxyCacheServer.unregisterCacheListener(cacheListener,downloadUrl);
                                int retry = 0;
                                while (proxyCacheServer.getProxyUrl(downloadUrl).startsWith("http") && retry < 3){
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException ignored) {

                                    }
                                    retry ++;
                                }
                                boolean cacheFile = (!proxyCacheServer.getProxyUrl(downloadUrl).startsWith("http"));
                                if (cacheFile && file.exists()){
                                    LoadImageUtils.INSTANCE.saveFileIgnoreThread(application, file, true, sucPath -> {
                                        if (isDestroy[0]){
                                            return;
                                        }
                                        OnDownloadMediaListener onDownloadMediaListener1;
                                        if ((onDownloadMediaListener1 = onDownloadMediaListenerHashMap.get(key)) != null) {
                                            if (!TextUtils.isEmpty(sucPath)){
                                                onDownloadMediaListener1.onDownloadSuccess(sucPath);
                                            }else {
                                                onDownloadMediaListener1.onDownloadFailed();
                                            }
                                        }
                                    });
                                }else {
                                    new Handler(Looper.getMainLooper()).post(() -> {
                                        if (isDestroy[0]){
                                            return;
                                        }
                                        FullGlideDownloadMediaHelper.super.download(activity, lifecycleOwner, openImageUrl, onDownloadMediaListener);
                                    });
                                }

                            } catch (IOException e) {
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    if (isDestroy[0]){
                                        return;
                                    }
                                    FullGlideDownloadMediaHelper.super.download(activity, lifecycleOwner, openImageUrl, onDownloadMediaListener);
                                });
                            }finally {
                                if (inputStream != null){
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
