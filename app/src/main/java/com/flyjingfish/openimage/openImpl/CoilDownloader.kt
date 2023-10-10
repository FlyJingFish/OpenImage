package com.flyjingfish.openimage.openImpl

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
import com.flyjingfish.openimage.openImpl.download.ProgressInfo
import com.flyjingfish.openimage.openImpl.download.ProgressListener
import com.flyjingfish.openimage.openImpl.download.ProgressManager
import com.flyjingfish.openimagelib.beans.OpenImageUrl
import com.flyjingfish.openimagelib.enums.MediaType
import com.flyjingfish.openimagelib.listener.OnDownloadMediaListener
import com.flyjingfish.openimagelib.utils.SaveImageUtils
import java.util.UUID

class CoilDownloader(
    val activity : FragmentActivity, val lifecycleOwner: LifecycleOwner, val openImageUrl: OpenImageUrl, val onDownloadMediaListener: OnDownloadMediaListener
) {
    val downloadUrl:String = if (openImageUrl.type == MediaType.VIDEO) openImageUrl.videoUrl else openImageUrl.imageUrl
    fun download(){
        onDownloadMediaListener.onDownloadStart(true)
//        val okHttpClient = ProgressManager.getInstance().with(
//            OkHttpClient.Builder()
//        )
//            .build()
//        val imageLoader = ImageLoader.Builder(activity)
//            .okHttpClient { okHttpClient }
//            .build()
        val isDestroy = booleanArrayOf(false)
        ProgressManager.getInstance().addResponseListener(downloadUrl, object : ProgressListener {
            override fun onProgress(progressInfo: ProgressInfo) {
                if (isDestroy[0]) {
                    return
                }
                onDownloadMediaListener.onDownloadProgress(progressInfo.percent)
            }

            override fun onError(id: Long, e: Exception) {}
        })
        val key = UUID.randomUUID().toString()
        val onDownloadMediaListenerHashMap: MutableMap<String, OnDownloadMediaListener> = HashMap()
        onDownloadMediaListenerHashMap[key] = onDownloadMediaListener

        val imageLoader = Coil.imageLoader(activity)
        val request = ImageRequest.Builder(activity)
            .data(downloadUrl)
            .lifecycle(lifecycleOwner)
            .memoryCachePolicy(CachePolicy.DISABLED)
            .decoderFactory { result: SourceResult, options: Options, imageLoader: ImageLoader ->
                Decoder {
                    SaveImageUtils.INSTANCE.saveFile(
                        activity, result.source.file().toFile(), openImageUrl.type == MediaType.VIDEO
                    ) { sucPath: String? ->
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
                    onDownloadMediaListener.onDownloadFailed()
                }
            }).build()
        imageLoader.enqueue(request)

        lifecycleOwner.lifecycle.addObserver(object :LifecycleEventObserver{
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    isDestroy[0] = false
                    source.lifecycle.removeObserver(this)
                    ProgressManager.getInstance().removeResponseListeners(downloadUrl)
                }
            }

        })
    }



}