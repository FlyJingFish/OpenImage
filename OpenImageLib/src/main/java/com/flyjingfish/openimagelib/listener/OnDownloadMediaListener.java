package com.flyjingfish.openimagelib.listener;


public interface OnDownloadMediaListener {
    void onDownloadStart(boolean isWithProgress);
    void onDownloadSuccess(String path);

    void onDownloadProgress(int percent);
    void onDownloadFailed();
}
