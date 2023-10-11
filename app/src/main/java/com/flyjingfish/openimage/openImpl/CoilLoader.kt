package com.flyjingfish.openimage.openImpl

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import coil.Coil
import coil.ImageLoader
import coil.decode.DecodeResult
import coil.decode.Decoder
import coil.fetch.SourceResult
import coil.request.CachePolicy
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.Options
import com.flyjingfish.openimagecoillib.CoilLoadImageUtils
import com.flyjingfish.openimagecoillib.OnLocalRealFinishListener
import com.flyjingfish.openimagelib.listener.OnLoadBigImageListener

class CoilLoader(
    val context: Context,
    val imageUrl: String,
    val onLoadBigImageListener: OnLoadBigImageListener
) {
//    fun load(){
//        CoilLoadImageUtils.loadImageForSize(
//            context,
//            imageUrl,object : OnLocalRealFinishListener {
//                override fun onGoLoad(filePath: String, maxImageSize: IntArray, isWeb: Boolean) {
//                    if (isWeb) {
//                        loadWebImage(context,imageUrl,onLoadBigImageListener,this)
//
//                    } else {
//                        val imageLoader = Coil.imageLoader(context)
//                        val request = ImageRequest.Builder(context)
//                            .data(filePath)
//                            .size(maxImageSize[0],maxImageSize[1])
//                            .listener(object :ImageRequest.Listener{
//                                override fun onError(request: ImageRequest, result: ErrorResult) {
//                                    super.onError(request, result)
//                                    onLoadBigImageListener.onLoadImageFailed()
//                                }
//                            })
//                            .target { drawable ->
//                                // Handle the result.
//                                onLoadBigImageListener.onLoadImageSuccess(drawable, filePath)
//                            }
//                            .build()
//                        imageLoader.enqueue(request)
//
//                    }
//                }
//
//            }
//        )
//
//
//    }
//
//    fun loadWebImage(
//        context: Context,
//        imageUrl: String,
//        onLoadBigImageListener: OnLoadBigImageListener,
//        finishListener: OnLocalRealFinishListener
//    ) {
//        val imageLoader = Coil.imageLoader(context)
//        val request = ImageRequest.Builder(context)
//            .data(imageUrl)
//            .memoryCachePolicy(CachePolicy.DISABLED)
//            .decoderFactory { result: SourceResult, options: Options, imageLoader: ImageLoader ->
//                Decoder {
//                    CoilLoadImageUtils.loadImageForSize(context,result.source.file().toFile().absolutePath,finishListener)
//                    DecodeResult(ColorDrawable(Color.BLACK), false)
//                }
//            }
//            .listener(object : ImageRequest.Listener{
//                override fun onError(request: ImageRequest, result: ErrorResult) {
//                    super.onError(request, result)
//                    onLoadBigImageListener.onLoadImageFailed()
//                }
//            }).build()
//        imageLoader.enqueue(request)
//    }

}