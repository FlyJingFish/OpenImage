package com.flyjingfish.openimage.imageloader

import android.widget.ImageView
import androidx.annotation.DrawableRes
import coil3.asImage
import coil3.imageLoader
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.target
import coil3.request.transformations
import coil3.size.Size.Companion.ORIGINAL
import coil3.transform.CircleCropTransformation
import coil3.transform.RoundedCornersTransformation
import coil3.transform.Transformation
import com.bumptech.glide.request.target.Target
import com.flyjingfish.openimage.imageloader.MyImageLoader.OnImageLoadListener

object MyImageCoil3Loader {
    fun into(
        url: String,
        iv: ImageView,
        w: Int,
        h: Int,
        @DrawableRes p: Int,
        @DrawableRes err: Int,
        isCircle: Boolean,
        radiusDp: Float,
        isBlur: Boolean,
        requestListener: OnImageLoadListener?
    ) {
        val imageLoader = iv.context.imageLoader
        val builder: ImageRequest.Builder = ImageRequest.Builder(iv.context)
            .data(url)
            .listener(object : ImageRequest.Listener {
                override fun onError(request: ImageRequest, result: ErrorResult) {
                    super.onError(request, result)
                    requestListener?.onFailed()
                }

                override fun onSuccess(request: ImageRequest, result: SuccessResult) {
                    super.onSuccess(request, result)
                    requestListener?.onSuccess()
                }
            }).target(iv)
        if (isBlur || isCircle || radiusDp != -1f) {
            var transformations = mutableListOf<Transformation>()
            if (isBlur && !isCircle && radiusDp == -1f) {
                transformations = arrayOf(BlurTransformation3(iv.context, 10f, 1f)).toMutableList()
            } else if (isBlur && isCircle && radiusDp == -1f) {
                transformations = arrayOf(
                    BlurTransformation3(iv.context, 10f, 1f),
                    CircleCropTransformation()
                ).toMutableList()
            } else if (isBlur && !isCircle && radiusDp != -1f) {
                transformations = arrayOf(
                    BlurTransformation3(iv.context, 10f, 1f),
                    RoundedCornersTransformation(
                        MyImageLoader.dp2px(radiusDp).toFloat()
                    )
                ).toMutableList()
            } else if (!isBlur && isCircle && radiusDp == -1f) {
                transformations = arrayOf(CircleCropTransformation()).toMutableList()
            } else if (!isBlur && !isCircle && radiusDp != -1f) {
                transformations = arrayOf(
                    RoundedCornersTransformation(
                        MyImageLoader.dp2px(radiusDp).toFloat()
                    )
                ).toMutableList()
            }
            builder.transformations(transformations)
            if (w > 0 && h > 0) builder.size(w, h)
        } else if (w > 0 && h > 0) {
            builder.size(w, h)
        } else if (w == Target.SIZE_ORIGINAL && h == Target.SIZE_ORIGINAL) {
            builder.size(ORIGINAL)
        }
        if (p != -1) builder.placeholder(iv.context.resources.getDrawable(p).asImage())
        if (err != -1) builder.error(iv.context.resources.getDrawable(err).asImage())
        val request: ImageRequest = builder.build()
        imageLoader.enqueue(request)
    }
}