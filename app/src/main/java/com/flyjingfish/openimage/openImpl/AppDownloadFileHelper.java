package com.flyjingfish.openimage.openImpl;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;

import com.flyjingfish.openimage.imageloader.MyImageLoader;
import com.flyjingfish.openimagefulllib.FullGlideDownloadMediaHelper;
import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.listener.OnDownloadMediaListener;

public class AppDownloadFileHelper extends FullGlideDownloadMediaHelper {
    @Override
    public void download(FragmentActivity activity, LifecycleOwner lifecycleOwner, OpenImageUrl openImageUrl, OnDownloadMediaListener onDownloadMediaListener) {
        if (MyImageLoader.loader_os_type == MyImageLoader.GLIDE){
            super.download(activity, lifecycleOwner, openImageUrl, onDownloadMediaListener);
        }else if (MyImageLoader.loader_os_type == MyImageLoader.COIL){
            new CoilDownloader(activity,lifecycleOwner, openImageUrl, onDownloadMediaListener).download();
        }else {
            new PicassoDownloader(activity,lifecycleOwner, openImageUrl, onDownloadMediaListener).download();
        }
    }
}
