package com.flyjingfish.openimagefulllib;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.danikula.videocache.StorageUtils;
import com.danikula.videocache.file.FileNameGenerator;
import com.danikula.videocache.file.Md5FileNameGenerator;
import com.flyjingfish.openimageglidelib.GlideDownloadMediaHelper;
import com.flyjingfish.openimageglidelib.LoadImageUtils;
import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.enums.MediaType;
import com.flyjingfish.openimagelib.listener.OnDownloadMediaListener;

import java.io.File;

public class FullGlideDownloadMediaHelper extends GlideDownloadMediaHelper {
    private static volatile FullGlideDownloadMediaHelper mInstance;

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
            final boolean[] isDestroy = new boolean[]{false};
            lifecycleOwner.getLifecycle().addObserver(new LifecycleEventObserver() {
                @Override
                public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                    if (event == Lifecycle.Event.ON_DESTROY){
                        isDestroy[0] = true;
                        source.getLifecycle().removeObserver(this);
                    }
                }
            });
            boolean isInitOkHttpClient = LoadImageUtils.INSTANCE.isInitOkHttpClient();
            if (onDownloadMediaListener != null) {
                onDownloadMediaListener.onDownloadStart(isInitOkHttpClient);
            }
            FileNameGenerator md5FileNameGenerator = new Md5FileNameGenerator();
            String name = md5FileNameGenerator.generate(downloadUrl);
            String path = StorageUtils.getIndividualCacheDirectory
                    (activity.getApplicationContext()).getAbsolutePath()
                    + File.separator + name;
            File file = new File(path);
            if (file.exists()){
                LoadImageUtils.INSTANCE.saveFile(activity.getApplicationContext(), file, true, sucPath -> {
                    if (isDestroy[0]){
                        return;
                    }
                    if (onDownloadMediaListener != null) {
                        if (!TextUtils.isEmpty(sucPath)){
                            onDownloadMediaListener.onDownloadSuccess(sucPath);
                        }else {
                            onDownloadMediaListener.onDownloadFailed();
                        }
                    }
                });
                return;
            }
        }
        super.download(activity, lifecycleOwner, openImageUrl, onDownloadMediaListener);
    }

}
