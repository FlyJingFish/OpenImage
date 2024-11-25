package com.flyjingfish.openimagecoillib

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import coil3.ImageLoader
import coil3.asImage
import coil3.decode.DecodeResult
import coil3.decode.Decoder
import coil3.fetch.SourceFetchResult
import coil3.imageLoader
import coil3.request.CachePolicy
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.Options
import coil3.request.lifecycle
import com.flyjingfish.openimagecoillib.Coil3LoadImageUtils.isInitOkHttpClient
import com.flyjingfish.openimagelib.beans.OpenImageUrl
import com.flyjingfish.openimagelib.enums.MediaType
import com.flyjingfish.openimagelib.listener.DownloadMediaHelper
import com.flyjingfish.openimagelib.listener.OnDownloadMediaListener
import com.flyjingfish.openimagelib.utils.SaveImageUtils
import java.util.UUID

open class Coil3DownloadMediaHelper :DownloadMediaHelper{
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

        val imageLoader = activity.imageLoader
        val request = ImageRequest.Builder(activity)
            .data(downloadUrl)
            .lifecycle(lifecycleOwner)
            .memoryCachePolicy(CachePolicy.DISABLED)
            .decoderFactory { result: SourceFetchResult, _: Options, _: ImageLoader ->
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
                    DecodeResult(ColorDrawable(Color.BLACK).asImage(), false)
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