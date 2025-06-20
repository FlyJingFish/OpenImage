package com.flyjingfish.openimage.activity

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.flyjingfish.openimage.R
import com.flyjingfish.openimagelib.OpenImage
import com.flyjingfish.openimagelib.beans.ClickViewParam
import com.flyjingfish.openimagelib.enums.MediaType
import com.flyjingfish.openimagelib.utils.ScreenUtils

class ComposeActivity : ComponentActivity() {
    private val imageUrl =
        "https://pics4.baidu.com/feed/50da81cb39dbb6fd95aa0c599b8d0d1e962b3708.jpeg?token=bf17224f51a6f4bb389e787f9c487940"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ImageWithPosition()
        }
    }

    @Composable
    fun ImageWithPosition() {
        var imageOffset by remember { mutableStateOf(IntOffset.Zero) }
        Box(
            modifier = Modifier
                .fillMaxSize()             // 宽高铺满父容器（整个屏幕）
                .background(Color.LightGray),  // 设置背景色，方便看效果
            contentAlignment = Alignment.Center  // 内容居中
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "图片",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .onGloballyPositioned { coordinates ->
                        val bounds = coordinates.boundsInWindow()
                        imageOffset = IntOffset(bounds.left.toInt(), bounds.top.toInt())
                    }
                    .clickable {
                        println("图片在屏幕上的位置：x=${imageOffset.x}, y=${imageOffset.y}")
                        OpenImage
                            .with(this@ComposeActivity)
                            .setClickWebView(
                                window.decorView,
                                ClickViewParam(
                                    ScreenUtils.dp2px(this@ComposeActivity,200f).toInt(),
                                    ScreenUtils.dp2px(this@ComposeActivity,200f).toInt(),
                                    imageOffset.y,
                                    imageOffset.x,
                                    window.decorView.width
                                )
                            )
                            .setClickPosition(0)
                            .setOpenImageStyle(R.style.DefaultPhotosTheme)
                            .setSrcImageViewScaleType(ImageView.ScaleType.CENTER_CROP, true)
                            .setImageUrl(imageUrl, MediaType.IMAGE)
                            .show()
                    }
                    .size(200.dp)
                    .background(Color.LightGray), // 设置背景色
            )
        }

    }
}