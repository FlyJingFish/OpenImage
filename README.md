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

### 第一步、Jitpack 引入方法

#### 首先、在项目根目录下的build.gradle添加

```gradle
allprojects {
    repositories {
        ...
        maven { url "https://www.jitpack.io" }
    }
}
```

**你可以选择下面三种的其中一种，在module下的build.gradle添加。**

#### A、直接引入完整版（同时支持查看图片和视频）
```
//OpenImageFullLib 是完整版，如果您不想自定义图片引擎和视频播放器引擎可直接引用以下库
//Glide版本4.12.0 视频播放器 GSYVideoPlayer 版本8.3.3
implementation 'com.github.FlyJingFish.OpenImage:OpenImageFullLib:v1.2.92'
```

#### B、引入只带有图片引擎的版本（只支持查看图片）
```
//OpenImageGlideLib 引入Glide（4.12.0）图片引擎,没有引入视频播放器
//如需定制视频播放功能，详细看Wiki文档，如果不想定制可直接使用上边的库
implementation 'com.github.FlyJingFish.OpenImage:OpenImageGlideLib:v1.2.92'
```

#### C、引入基础版本（不可以直接查看图片和视频，完全需要自定义）
```
//OpenImageLib 是基础库，没有引入图片引擎和视频播放器
//至少需要实现BigImageHelper来定制您的图片引擎，如需定制视频播放功能，详细看Wiki文档
implementation 'com.github.FlyJingFish.OpenImage:OpenImageLib:v1.2.92'

```

### 第二步. 你的数据需要实现 OpenImageUrl 接口

```java
public class ImageEntity implements OpenImageUrl {
    public String url;

    public ImageEntity(String url) {
        this.url = url;
    }

    public ImageEntity() {
    }

    @Override
    public String getImageUrl() {
        return url;
    }

    @Override
    public String getVideoUrl() {
        return null;
    }

    @Override
    public String getCoverImageUrl() {
        return url;
    }

    @Override
    public MediaType getType() {
        return MediaType.IMAGE;
    }
}

```

### 第三步. 简单一步调用即可

```java

//在点击时调用
OpenImage.with(activity)
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

### 额外步骤

#### A、如果您引用的库是 OpenImageLib 您需要实现BigImageHelper接口并设置它，它是加载大图的关键（以下以Glide为例）
**（如果您使用的是OpenImageFullLib或OpenImageGlideLib则不需要这一步）**

```java
 public class MyApplication extends Application {
     @Override
     public void onCreate() {
         super.onCreate();
         //初始化大图加载器
         OpenImageConfig.getInstance().setBigImageHelper(new BigImageHelperImpl());
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

#### B、如果您引用的库是 OpenImageLib 或 OpenImageGlideLib 需要查看视频的功能，需要以下步骤
**（如果您使用的是OpenImageFullLib则不需要这一步）**

```java

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化视频创建类
        OpenImageConfig.getInstance().setVideoFragmentCreate(new VideoFragmentCreateImpl());
    }
}
public class VideoFragmentCreateImpl implements VideoFragmentCreate {
   @Override
   public BaseFragment createVideoFragment() {
       return new VideoPlayerFragment();
   }
}

public class VideoPlayerFragment extends BaseImageFragment<ENDownloadView> {

   private FragmentVideoBinding binding;
   private String playerKey;
   private boolean isLoadImageFinish;
   protected boolean isPlayed;

   @Override
   protected ImageView getSmallCoverImageView() {//返回小封面图
       return binding.videoPlayer.getSmallCoverImageView();
   }

   @Override
   protected ImageView getPhotoView() {//返回大封面图，必须在小封面图下边
       return binding.videoPlayer.getCoverImageView();
   }

   @Override
   protected ENDownloadView getLoadingView() {//返回loadingView
       return (ENDownloadView) binding.videoPlayer.getLoadingView();
   }

   @Override
   protected void hideLoading(ENDownloadView pbLoading) {//隐藏loading需要特殊处理的重写这个
       super.hideLoading(pbLoading);
       pbLoading.release();
       binding.videoPlayer.getStartButton().setVisibility(View.VISIBLE);
   }

   @Override
   protected void showLoading(ENDownloadView pbLoading) {//显示loading需要特殊处理的重写这个
       super.showLoading(pbLoading);
       pbLoading.start();
       binding.videoPlayer.getStartButton().setVisibility(View.GONE);
   }

   @Override
   public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
       super.onViewCreated(view, savedInstanceState);
       binding.videoPlayer.findViewById(R.id.back).setOnClickListener(v -> close());
       playerKey = binding.videoPlayer.getVideoKey();
       binding.videoPlayer.goneAllWidget();
       isPlayed = false;
   }
   @Override
   protected void onTouchClose(float scale) {//下拉关闭回调
       super.onTouchClose(scale);
       binding.videoPlayer.findViewById(R.id.surface_container).setVisibility(View.GONE);
       binding.videoPlayer.goneAllWidget();
   }

   @Override
   protected void onTouchScale(float scale) {//下拉时回调
       super.onTouchScale(scale);
       binding.videoPlayer.goneAllWidget();
       if (scale == 0){
           binding.videoPlayer.showAllWidget();
       }
   }

   @Override
   protected void loadImageFinish(boolean isLoadImageSuccess) {
       isLoadImageFinish = true;
       play();
   }

   private void play(){
       if (isTransitionEnd && isLoadImageFinish && !isPlayed){//这里可以不等封面图加载完就播放，这个是为了更好的效果
           if (getLifecycle().getCurrentState() == Lifecycle.State.RESUMED){
               toPlay4Resume();
           }else {
               getLifecycle().addObserver(new LifecycleEventObserver() {
                   @Override
                   public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                       if (event == Lifecycle.Event.ON_RESUME){
                           toPlay4Resume();
                           source.getLifecycle().removeObserver(this);
                       }
                   }
               });
           }

           isPlayed = true;
       }
   }

   protected void toPlay4Resume(){
       binding.videoPlayer.playUrl(openImageBean.getVideoUrl());
       binding.videoPlayer.startPlayLogic();
   }

   @Override
   protected void onTransitionEnd() {
       super.onTransitionEnd();
       play();
   }

   @Override
   public void onResume() {
       super.onResume();
       if (playerKey != null) {
           GSYVideoController.resumeByKey(playerKey);
       }
   }

   @Override
   public void onPause() {
       super.onPause();
       if (playerKey != null) {
           GSYVideoController.pauseByKey(playerKey);
       }
   }

   @Override
   public void onDestroyView() {
       super.onDestroyView();
       if (playerKey != null) {
           GSYVideoController.cancelByKeyAndDeleteKey(playerKey);
       }
   }

   @Override
   public View getExitImageView() {//退出页面时需要保证封面图可见
       binding.videoPlayer.getThumbImageViewLayout().setVisibility(View.VISIBLE);
       return super.getExitImageView();
   }
}

```

# 点击以下链接查看更多使用说明

[点此查看更多使用说明](https://github.com/FlyJingFish/OpenImage/wiki)

# 混淆

### 一，如果您使用的是 OpenImageFullLib 需要遵循以下三方库的规则

[GSYVideoPlayer](https://github.com/FlyJingFish/GSYVideoPlayer)

[Glide](https://github.com/bumptech/glide)

### 二，如果您使用的是 OpenImageGlideLib 需要遵循以下三方库的规则
[Glide](https://github.com/bumptech/glide)

### 三，如果您使用的是 OpenImageLib 则不需要添加任何混淆规则

### 为了方便我将所有的混淆规则直接粘贴如下：

GSYVideoPlayer 的混淆规则：

```
-keep class com.shuyu.gsyvideoplayer.video.** { *; }
-dontwarn com.shuyu.gsyvideoplayer.video.**
-keep class com.shuyu.gsyvideoplayer.video.base.** { *; }
-dontwarn com.shuyu.gsyvideoplayer.video.base.**
-keep class com.shuyu.gsyvideoplayer.utils.** { *; }
-dontwarn com.shuyu.gsyvideoplayer.utils.**
-keep class tv.danmaku.ijk.** { *; }
-dontwarn tv.danmaku.ijk.**

-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, java.lang.Boolean);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
```

Glide 的混淆规则：

```
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}

```

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

