package com.flyjingfish.openimage.openImpl;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;

import com.flyjingfish.openimage.imageloader.MyImageLoader;
import com.flyjingfish.openimagelib.OpenImageConfig;
import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.listener.DownloadMediaHelper;
import com.flyjingfish.openimagelib.listener.OnDownloadMediaListener;

public class AppDownloadFileHelper implements DownloadMediaHelper {
    @Override
    public void download(FragmentActivity activity, LifecycleOwner lifecycleOwner, OpenImageUrl openImageUrl, OnDownloadMediaListener onDownloadMediaListener) {
        if (MyImageLoader.loader_os_type == MyImageLoader.GLIDE||MyImageLoader.loader_os_type == MyImageLoader.COIL){
            OpenImageConfig.getInstance().getDownloadMediaHelper().download(activity, lifecycleOwner, openImageUrl, onDownloadMediaListener);
        }else {
            new PicassoDownloader(activity,lifecycleOwner, openImageUrl, onDownloadMediaListener).download();
        }
    }
}
