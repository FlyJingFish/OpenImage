package com.flyjingfish.openimage

import android.app.Application
import android.content.Context
import android.os.Build.VERSION
import coil.ComponentRegistry
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.util.DebugLogger
import coil3.SingletonImageLoader
import coil3.gif.AnimatedImageDecoder
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import com.flyjingfish.openimage.openImpl.PicassoLoader
import com.flyjingfish.openimage.openImpl.download.ProgressManager
import com.flyjingfish.openimagecoillib.Coil3DownloadMediaHelper
import com.flyjingfish.openimagecoillib.Coil3LoadImageUtils
import com.flyjingfish.openimagecoillib.CoilLoadImageUtils
import com.flyjingfish.openimagefulllib.FullGlideDownloadMediaHelper
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.util.concurrent.Executors

class MyApplication : Application(), ImageLoaderFactory,SingletonImageLoader.Factory  {
    var okHttpClient: OkHttpClient? = null
        private set

    override fun onCreate() {
        super.onCreate()
        mInstance = this
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

    override fun newImageLoader(): ImageLoader {
        val builder = ComponentRegistry.Builder()
        if (VERSION.SDK_INT >= 28) {
            builder.add(ImageDecoderDecoder.Factory());
        } else {
            builder.add(GifDecoder.Factory());
        }
        return ImageLoader.Builder(this).logger(DebugLogger())
            .okHttpClient(CoilLoadImageUtils.getOkHttpClient())
            .components(builder.build())
            .build()
    }

    override fun newImageLoader(context: Context): coil3.ImageLoader {
        val builder = coil3.ComponentRegistry.Builder()
        if (VERSION.SDK_INT >= 28) {
            builder.add(AnimatedImageDecoder.Factory())
        } else {
            builder.add(coil3.gif.GifDecoder.Factory())
        }
        builder.add(
            OkHttpNetworkFetcherFactory(
                callFactory = {
                    Coil3LoadImageUtils.getOkHttpClient()
                }
            )
        )
        return coil3.ImageLoader.Builder(this)
            .components(builder.build())
            .build();
    }

    companion object {
        @JvmField
        var mInstance: MyApplication? = null
        @JvmField
        val cThreadPool = Executors.newFixedThreadPool(5)
    }
}