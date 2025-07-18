package com.flyjingfish.openimagecoillib

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import coil.Coil
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.decode.DecodeResult
import coil.decode.Decoder
import coil.fetch.SourceResult
import coil.request.CachePolicy
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.Options
import coil.request.SuccessResult
import coil.size.Size
import com.flyjingfish.openimagelib.listener.OnLoadBigImageListener
import com.flyjingfish.openimagelib.utils.BitmapUtils
import okhttp3.OkHttpClient
import okhttp3.internal.closeQuietly
import java.io.File
import java.util.concurrent.Executors

object CoilLoadImageUtils {
    private val cThreadPool = Executors.newFixedThreadPool(5)
    private val handler = Handler(Looper.getMainLooper())
    private var okHttpClient: OkHttpClient? = null

    @Synchronized
    internal fun initOkHttpClient():OkHttpClient {
        val client = ProgressManager.getInstance().with(OkHttpClient.Builder())
            .build()
        okHttpClient = client
        return client
    }

    fun getOkHttpClient(): OkHttpClient {
        val client = okHttpClient
        return client ?: initOkHttpClient()
    }

    internal fun isInitOkHttpClient(): Boolean {
        return okHttpClient != null
    }

    internal fun loadImageForSize(
        context: Context,
        imageUrl: String?,
        finishListener: OnLocalRealFinishListener
    ) {
        val isWeb = BitmapUtils.isWeb(imageUrl)
        if (!isWeb) {
            cThreadPool.submit {
                val size =
                    BitmapUtils.getImageSize(context, imageUrl)
                val maxImageSize =
                    BitmapUtils.getMaxImageSize(
                        size[0],
                        size[1]
                    )
                handler.post {
                    if (context is LifecycleOwner) {
                        if (context.lifecycle
                                .currentState != Lifecycle.State.DESTROYED
                        ) {
                            finishListener.onGoLoad(imageUrl, maxImageSize, false)
                        }
                    } else if (context is Activity) {
                        if (!context.isFinishing && !context.isDestroyed) {
                            finishListener.onGoLoad(imageUrl, maxImageSize, false)
                        }
                    }
                }
            }
        } else {
            val maxImageSize = intArrayOf(
                Int.MIN_VALUE,
                Int.MIN_VALUE
            )
            finishListener.onGoLoad(imageUrl, maxImageSize, true)
        }
    }

    @OptIn(ExperimentalCoilApi::class)
    internal fun loadWebImage(
        context: Context,
        imageUrl: String?,
        onLoadBigImageListener: OnLoadBigImageListener,
        finishListener: OnLocalRealFinishListener
    ) {
        val imageLoader = Coil.imageLoader(context)
        try {
            val snap = imageLoader.diskCache?.openSnapshot(imageUrl!!)
            val dataPath = snap?.data?.toFile()?.absolutePath
            snap?.closeQuietly()
            if (!TextUtils.isEmpty(dataPath) && !dataPath!!.endsWith(".tmp") && File(dataPath).exists()){
                loadImageForSize(context,dataPath,finishListener)
                return
            }
        } catch (_: Exception) {

        }
        val listener =  object : ImageRequest.Listener{
            var path: String? = null
            override fun onError(request: ImageRequest, result: ErrorResult) {
                super.onError(request, result)
                onLoadBigImageListener.onLoadImageFailed()
            }

            override fun onSuccess(request: ImageRequest, result: SuccessResult) {
                super.onSuccess(request, result)
                var dataPath: String?
                try {
                    val snap = imageLoader.diskCache?.openSnapshot(imageUrl!!)
                    dataPath = snap?.data?.toFile()?.absolutePath
                    snap?.closeQuietly()
                } catch (e: Exception) {
                    dataPath = path
                }
                loadImageForSize(context,dataPath,finishListener)
            }
        }
        val request = ImageRequest.Builder(context)
            .data(imageUrl)
            .memoryCachePolicy(CachePolicy.DISABLED)
            .size(Size.ORIGINAL)
            .decoderFactory { result: SourceResult, _: Options, _: ImageLoader ->
                Decoder {
                    listener.path = result.source.file().toFile().absolutePath
                    DecodeResult(ColorDrawable(Color.BLACK), false)
                }
            }
            .listener(listener)
            .build()

        imageLoader.enqueue(request)
    }
}