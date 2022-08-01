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
第三步. 使用它
```java
OpenImage.with(RecyclerViewActivity.this).setClickRecyclerView(binding.rv.rv,new SourceImageViewIdGet() {
                   @Override
                   public int getImageViewId(OpenImageUrl data, int position) {
                       return R.id.iv_image;
                   }
               })
               .setSrcImageViewScaleType(ImageView.ScaleType.CENTER_CROP,true)
               .setImageUrlList(datas).setImageDiskMode(MyImageLoader.imageDiskMode)
               .setItemLoadHelper(new ItemLoadHelper() {
                   @Override
                   public void loadImage(Context context, OpenImageUrl openImageUrl, String imageUrl, ImageView imageView, int overrideWidth, int overrideHeight, OnLoadCoverImageListener onLoadCoverImageListener) {

                       MyImageLoader.getInstance().load(imageView, imageUrl,overrideWidth,overrideHeight, R.mipmap.img_load_placeholder, R.mipmap.img_load_placeholder, new MyImageLoader.OnImageLoadListener() {
                           @Override
                           public void onSuccess() {
                               onLoadCoverImageListener.onLoadImageSuccess();
                           }

                           @Override
                           public void onFailed() {
                               onLoadCoverImageListener.onLoadImageFailed();
                           }
                       });
                   }
               })
               .setOpenImageStyle(R.style.DefaultPhotosTheme)
               .setClickPosition(position).show();
```
# 点击以下链接查看更多使用说明

[点此查看更多使用说明](https://github.com/FlyJingFish/OpenImage/wiki)

# 效果演示</br>



RecyclerView常见LayoutManager  | 聊天页面  | 打开视频
 ---- | ----- | ------  
 <img src="https://github.com/FlyJingFish/OpenImage/blob/master/screenshot/SVID_20220731_203152_1.gif" alt="show" />  | <img src="https://github.com/FlyJingFish/OpenImage/blob/master/screenshot/SVID_20220731_203549_1.gif" alt="show" /> | <img src="https://github.com/FlyJingFish/OpenImage/blob/master/screenshot/SVID_20220731_203923_1.gif" alt="show" />  
 
 
 <img src="/screenshot/Screenshot_20220731_203125_com.flyjingfish.openim.jpg" width="405" height="842" alt="show" />
