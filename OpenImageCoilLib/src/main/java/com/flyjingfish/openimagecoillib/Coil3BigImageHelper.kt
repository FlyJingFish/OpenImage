package com.flyjingfish.openimagecoillib

import android.content.Context
import coil3.asDrawable
import coil3.imageLoader
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.size.Size
import com.flyjingfish.openimagelib.listener.BigImageHelper
import com.flyjingfish.openimagelib.listener.OnLoadBigImageListener

open class Coil3BigImageHelper:BigImageHelper {
    override fun loadImage(
        context: Context,
        imageUrl: String?,
        onLoadBigImageListener: OnLoadBigImageListener
    ) {
        Coil3LoadImageUtils.loadImageForSize(
            context,
            imageUrl,
            object : OnLocalRealFinishListener {
                override fun onGoLoad(filePath: String?, maxImageSize: IntArray, isWeb: Boolean,rotate:Int) {
                    if (isWeb) {
                        Coil3LoadImageUtils.loadWebImage(
                            context,
                            imageUrl,
                            onLoadBigImageListener,
                            this
                        )
                    } else {
                        val imageLoader = context.imageLoader
                        val requestBuilder = ImageRequest.Builder(context)
                            .data(filePath)
                        if (maxImageSize[0] == Int.MIN_VALUE || maxImageSize[1] == Int.MIN_VALUE){
                            requestBuilder.size(Size.ORIGINAL)
                        }else{
                            if (maxImageSize[0] > 0 && maxImageSize[1] > 0){
                                if (rotate == 90 || rotate == 270) {
                                    requestBuilder.size(maxImageSize[1], maxImageSize[0])
                                } else {
                                    requestBuilder.size(maxImageSize[0], maxImageSize[1])
                                }
                            }
                        }
                        val request = requestBuilder.listener(object : ImageRequest.Listener{
                                override fun onError(request: ImageRequest, result: ErrorResult) {
                                    super.onError(request, result)
                                    onLoadBigImageListener.onLoadImageFailed()
                                }
                            })
                            .target { drawable ->
                                onLoadBigImageListener.onLoadImageSuccess(drawable.asDrawable(context.resources), filePath)
                            }
                            .build()
                        imageLoader.enqueue(request)
                    }
                }
            })
    }
}