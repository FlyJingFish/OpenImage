package com.flyjingfish.openimagecoillib

internal interface OnLocalRealFinishListener {
    fun onGoLoad(filePath: String?, maxImageSize: IntArray, isWeb: Boolean)
}