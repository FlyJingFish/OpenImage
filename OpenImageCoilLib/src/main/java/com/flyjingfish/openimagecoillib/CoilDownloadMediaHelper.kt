package com.flyjingfish.openimagecoillib

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
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
import com.flyjingfish.openimagecoillib.CoilLoadImageUtils.isInitOkHttpClient
import com.flyjingfish.openimagelib.beans.OpenImageUrl
import com.flyjingfish.openimagelib.enums.MediaType
import com.flyjingfish.openimagelib.listener.DownloadMediaHelper
import com.flyjingfish.openimagelib.listener.OnDownloadMediaListener
import com.flyjingfish.openimagelib.utils.SaveImageUtils
import java.util.UUID

open class CoilDownloadMediaHelper :DownloadMediaHelper{
    override fun download(
        activity: FragmentActivity,
        lifecycleOwner: LifecycleOwner,
        openImageUrl: OpenImageUrl,
        onDownloadMediaListener: OnDownloadMediaListener
    ) {
        val downloadUrl =
            if (openImageUrl.type == MediaType.VIDEO) openImageUrl.videoUrl else openImageUrl.imageUrl
        val isInitOkHttpClient = isInitOkHttpClient()
        onDownloadMediaListener.onDownloadStart(isInitOkHttpClient)
        val key = UUID.randomUUID().toString()
        val onDownloadMediaListenerHashMap: MutableMap<String, OnDownloadMediaListener> = HashMap()
        onDownloadMediaListenerHashMap[key] = onDownloadMediaListener
        lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    onDownloadMediaListenerHashMap.clear()
                    source.lifecycle.removeObserver(this)
                    ProgressManager.getInstance().removeResponseListeners(downloadUrl)
                }
            }
        })
        if (isInitOkHttpClient) {
            ProgressManager.getInstance()
                .addResponseListener(downloadUrl, object : ProgressListener {
                    override fun onProgress(progressInfo: ProgressInfo) {
                        onDownloadMediaListenerHashMap[key]?.onDownloadProgress(progressInfo.percent)
                    }

                    override fun onError(id: Long, e: Exception) {}
                })
        }

        val imageLoader = Coil.imageLoader(activity)
        val request = ImageRequest.Builder(activity)
            .data(downloadUrl)
            .lifecycle(lifecycleOwner)
            .memoryCachePolicy(CachePolicy.DISABLED)
            .decoderFactory { result: SourceResult, _: Options, _: ImageLoader ->
                Decoder {
                    SaveImageUtils.INSTANCE.saveFile(
                        activity, result.source.file().toFile(), openImageUrl.type == MediaType.VIDEO
                    ) { sucPath ->
                        if (!TextUtils.isEmpty(sucPath)) {
                            onDownloadMediaListenerHashMap[key]?.onDownloadSuccess(sucPath)
                        } else {
                            onDownloadMediaListenerHashMap[key]?.onDownloadFailed()
                        }
                    }
                    DecodeResult(ColorDrawable(Color.BLACK), false)
                }
            }
            .listener(object : ImageRequest.Listener{
                override fun onError(request: ImageRequest, result: ErrorResult) {
                    super.onError(request, result)
                    onDownloadMediaListenerHashMap[key]?.onDownloadFailed()
                }
            }).build()
        imageLoader.enqueue(request)
    }
}