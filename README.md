# OpenImage 图片查看大图库
[![](https://jitpack.io/v/FlyJingFish/OpenImage.svg)](https://jitpack.io/#FlyJingFish/OpenImage)


## 属于你的侵入性低的大图查看器，高仿微信完美的过渡动画，同样支持视频，本库本身不带这个功能，我写出来一个视频使用Demo，详看代码，另外您可以自定义加载图片的内核，例如Glide，Picasso或其他的

### 建议使用Glide效果更好，另外建议开启原图缓存（有些版本是自动缓存原图的） diskCacheStrategy(DiskCacheStrategy.ALL或DiskCacheStrategy.DATA) 本库设置为ImageDiskMode.CONTAIN_ORIGINAL

[点此下载apk](https://github.com/FlyJingFish/OpenImage/blob/master/apk/release/app-release.apk)

<img src="https://github.com/FlyJingFish/OpenImage/blob/master/screenshot/SVID_20220731_203152_1.gif" width="405px" height="842px" alt="show" /> 

## 特色功能

1，支持自定义图片加载引擎

2，支持多种图片缓存模式

3，支持聊天界面的查看大图功能

4，支持微信聊天页面大图不在聊天页面时回到点击位置的效果

5，支持全部 ImageView.ScaleType 显示模式的图片打开大图效果，并且新增startCrop、endCrop、autoStartCenterCrop、autoEndCenterCrop四种显示模式

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

第三步. 简单一步调用即可

```java

//在点击时调用
OpenImage.with(RecyclerViewActivity.this)
                .setClickRecyclerView(recyclerView,new SourceImageViewIdGet() {//点击ImageView所在的RecyclerView
                   @Override
                   public int getImageViewId(OpenImageUrl data, int position) {
                       return R.id.iv_image;//点击的ImageView的Id
                   }
               })
               .setSrcImageViewScaleType(ImageView.ScaleType.CENTER_CROP,true)//点击的ImageView的ScaleType类型
               .setImageUrlList(datas)//RecyclerView的数据
               .setClickPosition(position)//点击的ImageView所在数据的位置
               .setImageDiskMode(ImageDiskMode.CONTAIN_ORIGINAL)//可不设置,默认ImageDiskMode.CONTAIN_ORIGINAL
               .setItemLoadHelper(new ItemLoadHelper() {//可不设置（setImageDiskMode设置为RESULT或NONE时必须设置）
                   @Override
                   public void loadImage(Context context, OpenImageUrl openImageUrl, String imageUrl, ImageView imageView, int overrideWidth, int overrideHeight, OnLoadCoverImageListener onLoadCoverImageListener) {
                        //如果使用的Glide缓存模式是ImageDiskMode.RESULT(只保存目标图片大小),必须在加载图片时传入大小，详看Demo
                        GlideApp.with(imageView).load(imageUrl)
                            .override(overrideWidth, overrideHeight)
                            .addListener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        onLoadCoverImageListener.onLoadImageFailed();
                                        return false;
                                        }
                                
                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        onLoadCoverImageListener.onLoadImageSuccess();
                                        return false;
                            }
                        }).into(imageView);
                   }
               })
               .setOpenImageStyle(R.style.DefaultPhotosTheme)//可不设置（定制页面样式）
               .show();//开始展示大图
```

第四步，您需要实现BigImageHelper接口并设置它，它是加载大图的关键（以下以Glide为例）

```java
 public class MyApplication extends Application {
     @Override
     public void onCreate() {
         super.onCreate();
         //初始化大图加载器
         OpenImageConfig.getInstance().setBigImageHelper(new BigImageHelperImpl());
         //初始化视频加载，如果有多个请每次在调用openImage.show之前设置一遍
         OpenImageConfig.getInstance().setVideoFragmentCreate(new VideoFragmentCreateImpl());
     }
 }
 
 public class BigImageHelperImpl implements BigImageHelper {
    @Override
    public void loadImage(Context context, String imageUrl, OnLoadBigImageListener onLoadBigImageListener) {
        RequestOptions requestOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .format(DecodeFormat.PREFER_RGB_565);
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

# 点击以下链接查看更多使用说明

[点此查看更多使用说明](https://github.com/FlyJingFish/OpenImage/wiki)

## 版本限制
最低SDK版本：minSdkVersion >= 21

# 效果演示</br>



RecyclerView场景  | 聊天页面  | 打开视频
 ---- | ----- | ------  
 <img src="https://github.com/FlyJingFish/OpenImage/blob/master/screenshot/SVID_20220731_203152_1.gif" alt="show" />  | <img src="https://github.com/FlyJingFish/OpenImage/blob/master/screenshot/SVID_20220731_203549_1.gif" alt="show" /> | <img src="https://github.com/FlyJingFish/OpenImage/blob/master/screenshot/SVID_20220731_203923_1.gif" alt="show" />  
 
 
 <img src="/screenshot/Screenshot_20220731_203125_com.flyjingfish.openim.jpg" width="405px" height="940px" alt="show" />
 
### 常见问题

##### [点此查看常见问题](https://github.com/FlyJingFish/OpenImage/wiki/%E5%B8%B8%E8%A7%81%E9%97%AE%E9%A2%98)

### Thanks

- [PhotoView](https://github.com/Baseflow/PhotoView)


### 联系方式

* 有问题可以加群大家一起交流（QQ：221045694）

<img src="/screenshot/qq.jpg" width="220"/>

