# OpenImage 图片查看大图库

[![Maven central](https://img.shields.io/maven-central/v/io.github.FlyJingFish.OpenImage/OpenImageLib)](https://central.sonatype.com/search?q=io.github.FlyJingFish.OpenImage)
[![GitHub stars](https://img.shields.io/github/stars/FlyJingFish/OpenImage.svg)](https://github.com/FlyJingFish/OpenImage/stargazers)
[![GitHub forks](https://img.shields.io/github/forks/FlyJingFish/OpenImage.svg)](https://github.com/FlyJingFish/OpenImage/network/members)
[![GitHub issues](https://img.shields.io/github/issues/FlyJingFish/OpenImage.svg)](https://github.com/FlyJingFish/OpenImage/issues)
[![GitHub license](https://img.shields.io/github/license/FlyJingFish/OpenImage.svg)](https://github.com/FlyJingFish/OpenImage/blob/master/LICENSE)

## 属于你的侵入性低的大图查看器，高仿微信完美的过渡动画，支持自定义视频播放器，也可以自定义加载图片的内核，例如Glide，Picasso或其他的

## [点此下载apk,也可扫下边二维码下载](https://github.com/FlyJingFish/OpenImage/blob/master/apk/release/app-release.apk?raw=true)

<img src="https://github.com/FlyJingFish/OpenImage/blob/master/screenshot/download_qrcode.png" alt="show" width="200px" alt="show" />

## 效果演示
 
RecyclerView场景  | 聊天页面
 ------ | ------   
 <img src="https://github.com/FlyJingFish/OpenImage/blob/master/screenshot/SVID_20220731_203152_1.gif" alt="show" width="320px" alt="show" />  | <img src="https://github.com/FlyJingFish/OpenImage/blob/master/screenshot/SVID_20220731_203549_1.gif" alt="show" width="320px" alt="show" /> |
 
打开视频  | 朋友圈 
 ------ | ------   
 <img src="https://github.com/FlyJingFish/OpenImage/blob/master/screenshot/SVID_20220731_203923_1.gif" alt="show" width="320px" alt="show" />  | <img src="https://github.com/FlyJingFish/OpenImage/blob/master/screenshot/friends_demo.gif" alt="show" width="320px" alt="show" /> |


 

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

## 前言

1、建议使用Glide效果更好，另外建议开启原图缓存（有些版本是自动缓存原图的）Glide通过设置diskCacheStrategy 为DiskCacheStrategy.ALL或DiskCacheStrategy.DATA

2、当然如果您加载的是本地图片可直接忽略第1点

## 使用步骤

### 第一步、选择适合你的库



#### 首先、在项目根目录下的build.gradle添加(1.5.6及之后版本仓库地址改为Maven Central)

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

**你可以选择下面三种的其中一种，在module下的build.gradle添加。**

<img src="https://github.com/FlyJingFish/OpenImage/blob/master/screenshot/warning_maven_central.svg"/>

#### A、直接引入完整版（同时支持查看图片和视频）

请注意如果使用以下导入方式，如果你的项目组存在[Glide](https://github.com/bumptech/glide)请升级至 **4.12.0** 或者更高的版本，如果存在[GSYVideoPlayer](https://github.com/CarGuo/GSYVideoPlayer)请升级至 **8.3.3** 或者更高的版本，否则会冲突

```
//OpenImageFullLib 是完整版，如果您不想自定义图片引擎和视频播放器引擎可直接引用以下库
implementation 'io.github.FlyJingFish.OpenImage:OpenImageFullLib:1.7.0'
```
#### B、引入只带有图片引擎的版本（只支持查看图片）

请注意如果使用以下导入方式，如果你的项目组存在[Glide](https://github.com/bumptech/glide)请升级至 **4.12.0** 或者更高的版本，否则会冲突

```
//OpenImageGlideLib 引入Glide（4.12.0）图片引擎,没有引入视频播放器；如需定制视频播放功能，详细看Wiki文档，如果不想定制可直接使用上边的库
implementation 'io.github.FlyJingFish.OpenImage:OpenImageGlideLib:1.7.0'
```

#### C、引入基础版本（不可以直接查看图片和视频，完全需要自定义）

**自己定义加载大图时请注意内存溢出问题，详情可看Wiki文档（[点此查看常见问题](https://github.com/FlyJingFish/OpenImage/wiki/%E5%B8%B8%E8%A7%81%E9%97%AE%E9%A2%98)）**

```
//OpenImageLib 是基础库，没有引入图片引擎和视频播放器
//至少需要实现BigImageHelper来定制您的图片引擎，如需定制视频播放功能，详细看Wiki文档
implementation 'io.github.FlyJingFish.OpenImage:OpenImageLib:1.7.0'

```

### 第二步. 简单一步调用即可

**你可以选择下面两种图片数据的其中一种**

#### A、直接将数据转化为 String 的List (这个方式适合只看图片或视频，不可显示混合数据)

```java

List<String> dataList = new ArrayList<>();
for (ImageEntity data : datas) {
    dataList.add(data.getImageUrl());
}

//在点击时调用（以下以RecyclerView为例介绍）
OpenImage.with(activity)
        //点击ImageView所在的RecyclerView（也支持设置setClickViewPager2，setClickViewPager，setClickGridView，setClickListView，setClickImageView，setNoneClickView）
        .setClickRecyclerView(recyclerView,new SourceImageViewIdGet() {
           @Override
           public int getImageViewId(OpenImageUrl data, int position) {
               return R.id.iv_image;//点击的ImageView的Id
           }
       })
       //点击的ImageView的ScaleType类型（如果设置不对，打开的动画效果将是错误的）
       .setSrcImageViewScaleType(ImageView.ScaleType.CENTER_CROP,true)
       //RecyclerView的数据
       .setImageUrlList(dataList, MediaType.IMAGE)
       //点击的ImageView所在数据的位置
       .setClickPosition(position)
       //开始展示大图
       .show();
```

#### B、在您的数据实体类上实现OpenImageUrl接口(这个方式适合显示图片和视频的混合数据，建议使用这个方式)

**PS:列表中展示的图片链接和展示大图时所用链接是不同时，这种方式可以有更好的过渡效果**

```java
public class ImageEntity implements OpenImageUrl {
    public String photoUrl;//图片大图
    public String smallPhotoUrl;//图片小图
    public String coverUrl;//视频封面大图
    public String smallCoverUrl;//视频封面小图
    public String videoUrl;//视频链接
    public int resouceType; //0图片1视频 

    @Override
    public String getImageUrl() {
        return resouceType == 1 ? coverUrl : photoUrl;//大图链接（或视频的封面大图链接）
    }

    @Override
    public String getVideoUrl() {
        return videoUrl;//视频链接
    }

    @Override
    public String getCoverImageUrl() {//这个代表前边列表展示的图片
        return resouceType == 1 ? smallCoverUrl : smallPhotoUrl;//封面小图链接（或视频的封面小图链接）
    }

    @Override
    public MediaType getType() {
        return resouceType == 1 ? MediaType.VIDEO : MediaType.IMAGE;//数据是图片还是视频
    }
}

```

**然后调用显示**

```java

//在点击时调用（以下以RecyclerView为例介绍）
OpenImage.with(activity)
        //点击ImageView所在的RecyclerView（也支持设置setClickViewPager2，setClickViewPager，setClickGridView，setClickListView，setClickImageView，setNoneClickView）
        .setClickRecyclerView(recyclerView,new SourceImageViewIdGet() {
           @Override
           public int getImageViewId(OpenImageUrl data, int position) {
               return R.id.iv_image;//点击的ImageView的Id
           }
       })
       //点击的ImageView的ScaleType类型（如果设置不对，打开的动画效果将是错误的）
       .setSrcImageViewScaleType(ImageView.ScaleType.CENTER_CROP,true)
       //RecyclerView的数据
       .setImageUrlList(datas)
       //点击的ImageView所在数据的位置
       .setClickPosition(position)
       //开始展示大图
       .show();
```

**如果没有可以传的View（即不使用动画打开大图页面）**

```java

//在点击时调用（以下以RecyclerView为例介绍）
OpenImage.with(activity)
        //打开大图页面时没有点击的ImageView则用这个
        .setNoneClickView()
        //图片的数据
        .setImageUrlList(datas)
        //默认展示数据的位置
        .setClickPosition(position)
        //开始展示大图
        .show();
```

**PS.完整调用示例**

```java

//在点击时调用，按需使用即可（以下以RecyclerView为例介绍）
OpenImage.with(activity)
         //点击ImageView所在的RecyclerView（也支持设置setClickViewPager2，setClickViewPager，setClickGridView，setClickListView，setClickImageView）
         .setClickRecyclerView(recyclerView,new SourceImageViewIdGet() {
           @Override
           public int getImageViewId(OpenImageUrl data, int position) {
               return R.id.iv_image;//点击的ImageView的Id
           }
        })
        //点击的ImageView的ScaleType类型（如果设置不对，打开的动画效果将是错误的）
        .setSrcImageViewScaleType(ImageView.ScaleType.CENTER_CROP,true)
        //RecyclerView的数据
        .setImageUrlList(datas)
        //点击的ImageView所在数据的位置
        .setClickPosition(position)
        //clickDataPosition 点击的数据所在位置 clickViewPosition 点击的视图所在位置（和上边方法二选一，详细使用方法可看wiki文档）
        //这个方法主要是针对的是像聊天页面那种图文混合的数据，可以看 "聊天页面" Demo
        .setClickPosition(clickDataPosition, clickViewPosition)
        //可不设置（定制页面样式，详细可看Wiki文档）
        .setOpenImageStyle(R.style.DefaultPhotosTheme)
        //设置显示在页面上层的fragment（可不设置）
        .setUpperLayerFragmentCreate(new FriendLayerFragmentCreateImpl(),bundle,false,false)
        //设置自定义的视频播放fragment页面（可不设置）
        .setVideoFragmentCreate(new VideoFragmentCreateImpl())
        //设置加载失败时显示的图片（可不设置）
        .setErrorResId(R.mipmap.ic_launcher)
        //设置退出页面时，如果页面无对应的ImageView则回到点击位置（类似微信聊天页面的效果）（可不设置）
        .setWechatExitFillInEffect(true)
        //设置true后关闭时，将看不到前一页面正在查看的图片（可不设置）
        .setShowSrcImageView(true)
        //设置自定义的大图外壳页面（可不设置）
        .setOpenImageActivityCls(MyBigImageActivity.class)
        //设置切换图片时前一页面跟随滚动（可不设置）
        .setAutoScrollScanPosition(true)
        //设置画廊效果，参数为左右两侧漏出的宽度（可不设置）
        .setGalleryEffect(10)
        //设置切换图片监听（可不设置）
        .setOnSelectMediaListener(new OnSelectMediaListener() {
            @Override
            public void onSelect(OpenImageUrl openImageUrl, int position) {

            }
        })
        //设置点击监听（可不设置）
        .setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseFragment fragment, OpenImageUrl openImageUrl, int position) {
    
            }
        })
        //设置长按图片监听（可不设置）
        .setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public void onItemLongClick(BaseFragment fragment, OpenImageUrl openImageUrl, int position) {
            
            }
        })
        //设置切换大图时的效果（可不设置，本库中目前只有这一个，如需其他效果可参照ScaleInTransformer自行定义效果）
        .addPageTransformer(new ScaleInTransformer())
        //开始展示大图
        .show();
```
### 额外步骤

#### A、如果您引用的库是 OpenImageLib 您需要实现BigImageHelper接口并设置它，它是加载大图的关键（以下以Glide为例）
**（如果您使用的是 OpenImageFullLib 或 OpenImageGlideLib 则不需要这一步）**

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
        //这个地方只是示例，如果你的项目存在超大图，请注意需要自行处理（否则可能内存溢出或崩溃）
        //不想自己搞的，可直接用 OpenImageGlideLib 或 OpenImageFullLib
        RequestOptions requestOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    //这句是为了加载原图，如果你的原图可能是超大图，请注意内存溢出问题，不想自己搞的，可直接用 OpenImageGlideLib 或 OpenImageFullLib
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
                    .format(DecodeFormat.PREFER_RGB_565);
            Glide.with(context)
                    .load(imageUrl).apply(requestOptions).into(imageView);

    }

}
```

#### B、如果您引用的库是 OpenImageLib 或 OpenImageGlideLib 需要查看视频的功能，需要以下步骤
**（如果您使用的是 OpenImageFullLib 则不需要这一步）**

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
       if (scale == 1){
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

## 更新日志

[点此查看更新日志](https://github.com/FlyJingFish/OpenImage/blob/master/update_record.md)

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

## Demo 一览

 <img src="/screenshot/Screenshot_20220731_203125_com.flyjingfish.openim.jpg" width="320" alt="show" />
 
## [点此下载apk](https://github.com/FlyJingFish/OpenImage/blob/master/apk/release/app-release.apk?raw=true)
 
### 常见问题

##### [点此查看常见问题](https://github.com/FlyJingFish/OpenImage/wiki/%E5%B8%B8%E8%A7%81%E9%97%AE%E9%A2%98)

### Thanks

- [PhotoView](https://github.com/Baseflow/PhotoView)


### 联系方式

* 有问题可以加群大家一起交流（QQ：221045694）

<img src="/screenshot/qq.jpg" width="220"/>

