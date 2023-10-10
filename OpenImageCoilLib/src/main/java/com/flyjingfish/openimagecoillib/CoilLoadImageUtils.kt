package com.flyjingfish.openimagecoillib

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import coil.Coil
import coil.ImageLoader
import coil.decode.DecodeResult
import coil.decode.Decoder
import coil.fetch.SourceResult
import coil.request.CachePolicy
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.Options
import com.flyjingfish.openimagelib.listener.OnLoadBigImageListener
import com.flyjingfish.openimagelib.utils.FileUtils
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.Executors

object CoilLoadImageUtils {
    private val cThreadPool = Executors.newFixedThreadPool(5)
    private val handler = Handler(Looper.getMainLooper())
    private var okHttpClient: OkHttpClient? = null

    @Synchronized
    fun initOkHttpClient():OkHttpClient {
        val client = ProgressManager.getInstance().with(OkHttpClient.Builder())
            .build()
        okHttpClient = client
        return client
    }

    fun getOkHttpClient(): OkHttpClient {
        val client = okHttpClient
        return client ?: initOkHttpClient()
    }

    fun isInitOkHttpClient(): Boolean {
        return okHttpClient != null
    }

    fun loadImageForSize(
        context: Context?,
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

    fun loadWebImage(
        context: Context,
        imageUrl: String?,
        onLoadBigImageListener: OnLoadBigImageListener,
        finishListener: OnLocalRealFinishListener
    ) {
        val imageLoader = Coil.imageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(imageUrl)
            .memoryCachePolicy(CachePolicy.DISABLED)
            .decoderFactory { result: SourceResult, options: Options, imageLoader: ImageLoader ->
                Decoder {
                    loadImageForSize(context,result.source.file().toFile().absolutePath,finishListener)
                    DecodeResult(ColorDrawable(Color.BLACK), false)
                }
            }
            .listener(object : ImageRequest.Listener{
                override fun onError(request: ImageRequest, result: ErrorResult) {
                    super.onError(request, result)
                    onLoadBigImageListener.onLoadImageFailed()
                }
            }).build()
        imageLoader.enqueue(request)
    }

    fun saveFile(context: Context?, resource: File?, video: Boolean, onSaveFinish: OnSaveFinish) {
        cThreadPool.submit {
            saveFileIgnoreThread(
                context,
                resource,
                video,
                onSaveFinish
            )
        }
    }

    fun saveFileIgnoreThread(
        context: Context?,
        resource: File?,
        video: Boolean,
        onSaveFinish: OnSaveFinish?
    ) {
        val sucPath = FileUtils.save(context, resource, video)
        if (onSaveFinish != null) {
            handler.post { onSaveFinish.onFinish(sucPath) }
        }
    }

    interface OnSaveFinish {
        fun onFinish(sucPath: String?)
    }
}