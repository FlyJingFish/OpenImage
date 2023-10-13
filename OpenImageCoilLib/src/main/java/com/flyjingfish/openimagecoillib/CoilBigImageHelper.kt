package com.flyjingfish.openimagecoillib

import android.content.Context
import coil.Coil
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.size.Size
import com.flyjingfish.openimagelib.listener.BigImageHelper
import com.flyjingfish.openimagelib.listener.OnLoadBigImageListener

open class CoilBigImageHelper:BigImageHelper {
    override fun loadImage(
        context: Context,
        imageUrl: String?,
        onLoadBigImageListener: OnLoadBigImageListener
    ) {
        CoilLoadImageUtils.loadImageForSize(
            context,
            imageUrl,
            object : OnLocalRealFinishListener {
                override fun onGoLoad(filePath: String?, maxImageSize: IntArray, isWeb: Boolean) {
                    if (isWeb) {
                        CoilLoadImageUtils.loadWebImage(
                            context,
                            imageUrl,
                            onLoadBigImageListener,
                            this
                        )
                    } else {
                        val imageLoader = Coil.imageLoader(context)
                        val requestBuilder = ImageRequest.Builder(context)
                            .data(filePath)
                        if (maxImageSize[0] == Int.MIN_VALUE || maxImageSize[1] == Int.MIN_VALUE){
                            requestBuilder.size(Size.ORIGINAL)
                        }else{
                            requestBuilder.size(maxImageSize[0],maxImageSize[1])
                        }
                        val request = requestBuilder.listener(object : ImageRequest.Listener{
                                override fun onError(request: ImageRequest, result: ErrorResult) {
                                    super.onError(request, result)
                                    onLoadBigImageListener.onLoadImageFailed()
                                }
                            })
                            .target { drawable ->
                                onLoadBigImageListener.onLoadImageSuccess(drawable, filePath)
                            }
                            .build()
                        imageLoader.enqueue(request)
                    }
                }
            })
    }
}