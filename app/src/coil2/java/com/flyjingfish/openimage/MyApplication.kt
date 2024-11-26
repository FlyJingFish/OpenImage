package com.flyjingfish.openimage

import android.os.Build.VERSION
import coil.ComponentRegistry
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.util.DebugLogger
import com.flyjingfish.openimagecoillib.CoilLoadImageUtils
import java.util.concurrent.Executors

class MyApplication : BaseApplication(), ImageLoaderFactory  {

    override fun onCreate() {
        super.onCreate()
        mInstance = this

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

    companion object {
        @JvmField
        var mInstance: MyApplication? = null
        @JvmField
        val cThreadPool = Executors.newFixedThreadPool(5)
    }
}