package com.flyjingfish.openimageglidelib;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.enums.MediaType;
import com.flyjingfish.openimagelib.listener.DownloadMediaHelper;
import com.flyjingfish.openimagelib.listener.OnDownloadMediaListener;

import java.io.File;

public class GlideDownloadMediaHelper implements DownloadMediaHelper {
    @Override
    public void download(FragmentActivity activity, OpenImageUrl openImageUrl, OnDownloadMediaListener onDownloadMediaListener) {
        String downloadUrl = openImageUrl.getType() == MediaType.VIDEO?openImageUrl.getVideoUrl():openImageUrl.getImageUrl();
        if (onDownloadMediaListener != null) {
            onDownloadMediaListener.onDownloadStart();
        }
        Glide.with(activity).asFile()
                .load(downloadUrl).into(new CustomTarget<File>() {

                    @Override
                    public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                        boolean suc = FileUtils.save(activity,resource,openImageUrl.getType() == MediaType.VIDEO);
                        if (onDownloadMediaListener != null) {
                            if (suc){
                                onDownloadMediaListener.onDownloadSuccess();
                            }else {
                                onDownloadMediaListener.onDownloadFailed();
                            }
                        }
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        if (onDownloadMediaListener != null) {
                            onDownloadMediaListener.onDownloadFailed();
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

}
