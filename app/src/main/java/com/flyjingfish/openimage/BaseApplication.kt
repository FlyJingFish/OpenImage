package com.flyjingfish.openimage

import android.app.Application
import com.flyjingfish.openimage.openImpl.PicassoLoader
import com.flyjingfish.openimage.openImpl.download.ProgressManager
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import okhttp3.Cache
import okhttp3.OkHttpClient

open class BaseApplication : Application()  {
    var okHttpClient: OkHttpClient? = null
        private set

    override fun onCreate() {
        super.onCreate()
        //        OpenImageConfig.getInstance().setBigImageHelper(new AppGlideBigImageHelper());
//        AppDownloadFileHelper appDownloadFileHelper = new AppDownloadFileHelper();
//        OpenImageConfig.getInstance().setDownloadMediaHelper(appDownloadFileHelper);
        initPicasso()
        okHttpClient = ProgressManager.getInstance().with(OkHttpClient.Builder()).build()

    }

    private fun initPicasso() {
        val cacheDir = PicassoLoader.createDefaultCacheDir(this)
        val maxSize = PicassoLoader.calculateDiskCacheSize(cacheDir)
        val okHttpClient = ProgressManager.getInstance()
            .with(OkHttpClient.Builder().cache(Cache(cacheDir, maxSize)))
            .build()
        val picasso = Picasso.Builder(this).downloader(OkHttp3Downloader(okHttpClient)).build()
        Picasso.setSingletonInstance(picasso)
    }


}