package com.flyjingfish.openimage

import android.content.Context
import android.os.Build.VERSION
import coil3.SingletonImageLoader
import coil3.gif.AnimatedImageDecoder
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import com.flyjingfish.openimagecoillib.Coil3LoadImageUtils
import java.util.concurrent.Executors

class MyApplication : BaseApplication(), SingletonImageLoader.Factory  {

    override fun onCreate() {
        super.onCreate()
        mInstance = this

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