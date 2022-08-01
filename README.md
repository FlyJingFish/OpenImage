# OpenImage 图片浏览库
[![](https://jitpack.io/v/FlyJingFish/OpenImage.svg)](https://jitpack.io/#FlyJingFish/OpenImage)


属于你的侵入性低的图片浏览器，完美的过渡动画，同样支持视频，我写出来一个视频使用Demo，本库本身不带这个功能，
您可以自定义加载图片的内核，例如Glide，Picasso

第一步. 根目录build.gradle

```gradle
allprojects {
    repositories {
        maven { url "https://www.jitpack.io" }
    }
}

buildscript {
    repositories {
        maven { url "https://www.jitpack.io" }
    }	
}
```

第二步. 需要引用的build.gradle

```gradle
dependencies {
    implementation 'com.github.FlyJingFish:OpenImage:latest.release.here'
}
```
 
# 点击以下链接查看详细使用说明

[点此查看使用说明](https://github.com/FlyJingFish/OpenImage/wiki)

# 效果演示</br>



RecyclerView常见LayoutManager  | 聊天页面  | 打开视频
 ---- | ----- | ------  
 <img src="https://github.com/FlyJingFish/OpenImage/blob/master/screenshot/SVID_20220731_203152_1.gif" alt="show" />  | <img src="https://github.com/FlyJingFish/OpenImage/blob/master/screenshot/SVID_20220731_203549_1.gif" alt="show" /> | <img src="https://github.com/FlyJingFish/OpenImage/blob/master/screenshot/SVID_20220731_203923_1.gif" alt="show" />  
 
 
 <img src="/screenshot/Screenshot_20220731_203125_com.flyjingfish.openim.jpg" width="405" height="842" alt="show" />
