package com.flyjingfish.openimage.imageloader

import android.widget.ImageView
import androidx.annotation.DrawableRes
import coil3.imageLoader
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.transformations
import coil3.size.Size.Companion.ORIGINAL
import coil3.transform.CircleCropTransformation
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

            })
            .target(iv)
        if (isBlur || isCircle || radiusDp != -1f) {
            var transformations = arrayOfNulls<coil.transform.Transformation>(0)
            if (isBlur && !isCircle && radiusDp == -1f) {
                transformations = arrayOf(BlurTransformation(iv.context, 10f, 1f))
            } else if (isBlur && isCircle && radiusDp == -1f) {
                transformations = arrayOf(
                    BlurTransformation(iv.context, 10f, 1f),
                    CircleCropTransformation()
                )
            } else if (isBlur && !isCircle && radiusDp != -1f) {
                transformations = arrayOf(
                    BlurTransformation(iv.context, 10f, 1f),
                    coil.transform.RoundedCornersTransformation(
                        MyImageLoader.dp2px(radiusDp).toFloat()
                    )
                )
            } else if (!isBlur && isCircle && radiusDp == -1f) {
                transformations = arrayOf(CircleCropTransformation())
            } else if (!isBlur && !isCircle && radiusDp != -1f) {
                transformations = arrayOf(
                    coil.transform.RoundedCornersTransformation(
                        MyImageLoader.dp2px(radiusDp).toFloat()
                    )
                )
            }
            builder.transformations(*transformations)
            if (w > 0 && h > 0) builder.size(w, h)
        } else if (w > 0 && h > 0) {
            builder.size(w, h)
        } else if (w == Target.SIZE_ORIGINAL && h == Target.SIZE_ORIGINAL) {
            builder.size(ORIGINAL)
        }
        if (p != -1) builder.placeholder(p)
        if (err != -1) builder.error(err)
        val request: ImageRequest = builder.build()
        imageLoader.enqueue(request)
    }
}