package com.flyjingfish.openimagecoillib

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import coil3.imageLoader
import com.flyjingfish.openimagelib.OpenImageConfig
import com.flyjingfish.openimagelib.utils.OpenImageLogUtils

class OpenImageCoilInitProvider : ContentProvider() {
    override fun onCreate(): Boolean {
        OpenImageLogUtils.init(context!!.applicationContext)
        val isCoil3: Boolean = try {
            val imageLoader = context!!.imageLoader
            true
        } catch (e: NoClassDefFoundError) {
            false
        }
        //初始化大图加载器
        if (OpenImageConfig.getInstance().bigImageHelper == null) {
            if (isCoil3){
                OpenImageConfig.getInstance().bigImageHelper = Coil3BigImageHelper()
            }else{
                OpenImageConfig.getInstance().bigImageHelper = CoilBigImageHelper()
            }
        }
        //初始化下载原图或视频类
        if (OpenImageConfig.getInstance().downloadMediaHelper == null) {
            if (isCoil3){
                OpenImageConfig.getInstance().downloadMediaHelper = Coil3DownloadMediaHelper()
            }else{
                OpenImageConfig.getInstance().downloadMediaHelper = CoilDownloadMediaHelper()
            }
        }
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        return null
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        return 0
    }
}