# OpenImage 图片查看大图库
[![](https://jitpack.io/v/FlyJingFish/OpenImage.svg)](https://jitpack.io/#FlyJingFish/OpenImage)


## 属于你的侵入性低的大图查看器，高仿微信完美的过渡动画，同样支持视频，本库本身不带这个功能，我写出来一个视频使用Demo，详看代码，另外您可以自定义加载图片的内核，例如Glide，Picasso或其他的

### 建议使用Glide效果更好，另外建议开启原图缓存 diskCacheStrategy(DiskCacheStrategy.ALL或DiskCacheStrategy.DATA) 本库设置为ImageDiskMode.CONTAIN_ORIGINAL

[点此下载apk](https://github.com/FlyJingFish/OpenImage/blob/master/apk/release/app-release.apk)

<img src="https://github.com/FlyJingFish/OpenImage/blob/master/screenshot/SVID_20220731_203152_1.gif" width="405px" height="842px" alt="show" /> 

## 特色功能

1，支持自定义图片加载引擎

2，支持多种图片缓存模式

3，支持聊天界面的查看大图功能

4，支持微信聊天页面大图不在聊天页面时回到点击位置的效果

5，支持全部 ImageView.ScaleType 显示模式的图片打开大图效果

6，支持图片和视频混合数据

7，支持传入包含图片的 RecyclerView、ViewPager、ViewPager2、ListView、GridView 和 多个ImageView 的调用方式，傻瓜式调用，无需关心图片切换后该返回到哪个位置

8，支持大图和大图阅读模式

9，支持自定义大图切换效果（PageTransformer）

## 使用步骤

第一步. 根目录build.gradle

```gradle
allprojects {
    repositories {
        ...
        maven { url "https://www.jitpack.io" }
    }
}
```

第二步. 需要引用的build.gradle （最新版本[![](https://jitpack.io/v/FlyJingFish/OpenImage.svg)](https://jitpack.io/#FlyJingFish/OpenImage)）

```gradle
dependencies {
    implementation 'com.github.FlyJingFish:OpenImage:latest.release.here'
}
```

第三步，您需要实现BigImageHelper接口并设置它，它是加载大图的关键（以下以Glide为例）

```java
 OpenImageConfig.getInstance().setBigImageHelper(new BigImageHelperImpl());
 
 public class BigImageHelperImpl implements BigImageHelper {
    @Override
    public void loadImage(Context context, String imageUrl, OnLoadBigImageListener onLoadBigImageListener) {
        Glide.with(context)
                    .load(imageUrl).apply(requestOptions).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    onLoadBigImageListener.onLoadImageFailed();
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    onLoadBigImageListener.onLoadImageSuccess(resource);
                    return false;
                }
            }).into(new CustomTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {

                }
            });

    }

    @Override
    public void loadImage(Context context, String imageUrl, ImageView imageView) {
         RequestOptions requestOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .format(DecodeFormat.PREFER_RGB_565);
            Glide.with(context)
                    .load(imageUrl).apply(requestOptions).into(imageView);

    }

}
```

第四步. 使用它
```java

RecyclerView 适配器加载图片
MyImageLoader.getInstance().load(holder.ivImage,datas.get(position).getCoverImageUrl(),R.mipmap.img_load_placeholder,R.mipmap.img_load_placeholder);

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
                        //如果使用的Glide缓存模式是ImageDiskMode.RESULT(只保存目标图片大小),必须在加载图片时传入大小，详看Demo
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



RecyclerView场景  | 聊天页面  | 打开视频
 ---- | ----- | ------  
 <img src="https://github.com/FlyJingFish/OpenImage/blob/master/screenshot/SVID_20220731_203152_1.gif" alt="show" />  | <img src="https://github.com/FlyJingFish/OpenImage/blob/master/screenshot/SVID_20220731_203549_1.gif" alt="show" /> | <img src="https://github.com/FlyJingFish/OpenImage/blob/master/screenshot/SVID_20220731_203923_1.gif" alt="show" />  
 
 
 <img src="/screenshot/Screenshot_20220731_203125_com.flyjingfish.openim.jpg" width="405px" height="842px" alt="show" />
