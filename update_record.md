# OpenImage 更新日志

## 2.4.7

1、完善本地图片地址适配问题

## 2.4.6

1、修复识别 file:// 本地图片地址的问题

## 2.4.5

1、适配状态栏

## 2.4.4

1、降低sdk版本要求

2、修改引用地址

## 2.4.2

1、修复一个报错日志的问题

## 2.4.1

1、fix [#47](https://github.com/FlyJingFish/OpenImage/issues/47)

## 2.4.0

1、fix [#45](https://github.com/FlyJingFish/OpenImage/issues/45)

2、针对 [#46](https://github.com/FlyJingFish/OpenImage/issues/46)，提出的问题不进行修复，而是直接升级最低sdk版本

## 2.3.9

1、优化超大图查看细节的加载大小保证不卡顿

## 2.3.8

1、优化超大图查看细节时的不清晰问题


## 2.3.7

1、尝试修复 [#34](https://github.com/FlyJingFish/OpenImage/issues/34)，待观察

## 2.3.6

1、修改图片和视频同步缩放的方式，防止出现缩放大小不同步的问题



## 2.3.5

1、新增支持 Coil3 

2、修复 Coil 不能显示 gif 图的问题

3、修改 glide coil 等图片加载框架的导入方式


## 2.3.4

1、修复点击事件重复两次问题

## 2.3.3

1、修复退出时视频放大后显示不正常的问题

## 2.3.2

1、修复未加载视频或未加载图片时的视频缩放问题

## 2.3.1

1、修复切换新增视频不能自动播放的问题

2、修复长尺寸视频缩放不正确的问题

## 2.3.0

1、升级AGP版本

2、升级播放器版本

## 2.2.9

1、新增可以缩放视频的功能，默认是打开此功能，设置如下设置可以恢复到之前

```java
VideoPlayerFragment.Scaling = false;

```

## 2.2.8

1、修复超大图是旋转后的图片时加载错误问题

2、修复Glide加载旋转后的超大图片时内存溢出问题

## 2.2.7

1、修复点击失效问题

## 2.2.6

1、修复页面重建导致的崩溃问题

2、升级 [ShapeImageView](https://github.com/FlyJingFish/ShapeImageView) 库至最新版本 1.5.6

## 2.2.5

1、fix [#33](https://github.com/FlyJingFish/OpenImage/issues/33)

2、新增修改双指触摸阈值方法 `OpenImageConfig.getScaledTouchSlop(slop)`

## 2.2.4

1、fix [#32](https://github.com/FlyJingFish/OpenImage/issues/32)

## 2.2.3

1、fix [#29](https://github.com/FlyJingFish/OpenImage/issues/29)

## 2.2.2

1、fix [#30](https://github.com/FlyJingFish/OpenImage/issues/30)

## 2.2.1

1、修复bug

2、优化视频播放

## 2.2.0

1、优化打开大图页面的启动速度，提升幅度理想情况下大约 30%

## 2.1.9

1、修复部分手机切换图片第二张不显示问题

2、修复部分手机退到桌面再回来后显示loading的问题

3、优化TouchCloseLayout代码

## 2.1.8

1、[修复不能获取View大小时，超长图不能显示为阅读模式的bug](https://github.com/FlyJingFish/OpenImage/issues/19)

2、完善 TouchCloseLayout

3、修复下载按钮在全屏时点击无效问题

4、升级 [ShapeImageView](https://github.com/FlyJingFish/ShapeImageView) 库至最新版本 1.5.5

## 2.1.7

1、新增 OpenImageCoilLib 库，使用方式如下：

- 如果您只是使用图片功能，则只需要如下引入方式即可：

```gradle
//OpenImageCoilLib 引入Coil（2.4.0）图片引擎,没有引入视频播放器；
implementation 'io.github.FlyJingFish.OpenImage:OpenImageCoilLib:2.1.7'
```

- 如果您想要使用图片和视频功能，则需要按如下配置（引入 OpenImageFullLib 时排除掉 OpenImageGlideLib，否则将会同时存在Glide和Coil）：

```gradle
//OpenImageFullLib 是完整版，如果您不想自定义图片引擎和视频播放器引擎可直接引用以下库
implementation ('io.github.FlyJingFish.OpenImage:OpenImageFullLib:2.1.7'){
    exclude module: 'OpenImageGlideLib'
}
//OpenImageCoilLib 引入Coil（2.4.0）图片引擎,没有引入视频播放器；
implementation 'io.github.FlyJingFish.OpenImage:OpenImageCoilLib:2.1.7'
```

2、重写一些代码，限制外部访问一些方法

## 2.1.6

1、修复删除到最后一个Item时不回调onRemove的问题

## 2.1.5

1、[修复大图旋转问题](https://github.com/FlyJingFish/OpenImage/issues/15)

2、修复部分手机双指放大时图片跳动问题

## 2.1.4

1、[新增同时加载缩略图](https://github.com/FlyJingFish/OpenImage/issues/14)，新版本默认开启，详细内容可看方法说明

- 全局设置

```java
OpenImageConfig.getInstance().setBothLoadCover()
```

- 局部设置

```java
OpenImage.with(context).setBothLoadCover()
```

2、修复 PhotoViewAttacher 内存泄漏问题

3、修复播放器UI显示问题

4、修改方法说明中错别字

## 2.1.3

1、[彻底优化缩放过程，防止缩放时切换图片](https://github.com/FlyJingFish/OpenImage/issues/13)


## 2.1.2

1、修复视频设置点击和长按监听不起作用的问题


## 2.1.1

1、[优化缩放过程，防止缩放时切换图片](https://github.com/FlyJingFish/OpenImage/issues/13)


## 2.1.0

1、修复下载较大视频时，中途异常导致下载失败的问题

2、修复下载视频时内存泄漏问题

## 2.0.9

1、[增加支持传入 Fragment，防止内存泄漏](https://github.com/FlyJingFish/OpenImage/issues/12)

- 点击的 View 在 Fragment 中使用时传入 Fragment
```java
OpenImage.with(Fragment fragment)

```

## 2.0.8

1、优化下载视频，缓存与下载功能相结合，总之下载视频时既省一倍流量又能使播放更加流畅

## 2.0.7

1、彻底修复预加载视频不能正常播放的问题

2、优化视频播放器显示（主要针对首次切到视频页面不显示播放按钮、进度条等控件）

## 2.0.6

1、修复设置预加载后视频播放不正常的bug

2、新增 OpenImageCoverVideoPlayer ，简化播放器设置封面

3、优化播放器播放时隐藏封面的逻辑

4、优化加载和下载原图或视频的过程，防止内存泄漏

5、修改加载点击的图片逻辑，防止因为加载更多数据导致白白被加载

6、记录播放位置，切换回来可以继续播放

## 2.0.5

1、[修复传入ImageView的list 后关闭页面崩溃的bug](https://github.com/FlyJingFish/OpenImage/issues/11)

2、修改 BaseInnerFragment 中 onTouchClose 和 onTouchScale 只在生命周期的 onResume 下才被调用

3、优化播放器，使其在缓冲时点击屏幕不隐藏 LoadingView

4、删除雪花id获取方法以减少运算

## 2.0.4

1、优化库的初始化速度

2、修改状态栏高度获取方法避免调用异常

## 2.0.3

1、新增预加载配置，对于图片和视频都适用，视频可在切换页面后秒播

- 全局设置
```java
    OpenImageConfig.getInstance().setPreloadCount(lazyPreload,preloadCount);
```
- 局部设置
```java
    OpenImage.with(Context context).setPreloadCount(lazyPreload,preloadCount);
```

2、优化视频播放速度，通过设置预加载切换页面后可实现视频秒播效果

3、修改状态栏高度获取方法，保证Android 11以上能获取到正确的高度

4、升级 [ShapeImageView](https://github.com/FlyJingFish/ShapeImageView) 库至最新版本 1.5.4

## 2.0.2

1、新增 setClickRecyclerView(RecyclerView, LayoutManagerFindVisiblePosition, SourceImageViewIdGet) 方法，传入 `LayoutManagerFindVisiblePosition` 即可支持更多自定义的 LayoutManager

使用方式：

```java
openImage.setClickRecyclerView(binding.rv.rv, new LayoutManagerFindVisiblePosition() {
            @Override
            public int findFirstVisibleItemPosition() {
                return customLayoutManager.findFirstVisibleItemPosition();
            }
    
            @Override
            public int findLastVisibleItemPosition() {
                return customLayoutManager.findLastVisibleItemPosition();
            }
        }, (data, position1) -> R.id.iv_image)
```

## 2.0.1

1、针对 getCoverImageUrl() 和 getImageUrl() 返回链接不同时，修复 Center 和 CenterInside 打开和关闭页面时的显示问题

2、优化一次能够显示超大图更多的细节，使滑动时尽量多的看到清晰图

## 2.0.0

1、优化拖动图片过程，避免部分情况下拖动图片被识别为切换图片

2、优化显示超大图细节速度和过程

## 1.9.9

1、优化查看超大图过程，使清晰细节图在拖拽缩放过程中尽可能多的显示

## 1.9.8

1、适配 Android 13 请求权限的代码

2、提供自定义请求权限的接口 --> OpenImage.setOnPermissionsInterceptListener()

## 1.9.7

1、修复多个大图页面叠加时下拉显示不正常的问题

2、修复传入一个数据后，在大图页面又增加数据没有显示指示器的问题

## 1.9.6

1、修改保存视频时优先寻找本地视频缓存文件，如果不存在再从网络下载

## 1.9.5

1、修复某些情况下小封面和大封面重叠显示出来的问题

## 1.9.4

1、修改超大图第一次双击放大的尺寸

## 1.9.3

1、修复部分机型无法加载超大图问题

2、新增支持 Assets 图片及超大图

## 1.9.2

1、完善查看超大图细节时频繁滑动触摸导致的细节图错位问题

## 1.9.1

**1、新增支持超大图，可查看超大图细节**

2、超大图功能可以选择关闭 --> OpenImageConfig.getInstance().setSupportSuperBigImage(false);


## 1.9.0

1、修复打开大图所在页面 finish() 后，大图页面切换图片崩溃的 bug

2、新增支持 Gif 动图

3、新增可选关闭按钮

4、新增控制触摸关闭全局设置和局部设置，新增控制点击关闭全局设置和局部设置

## 1.8.9

1、修复因为加载更多数据导致的下载进度显示错误的问题

2、BaseInnerFragment 的 checkPermissionAndDownload 下载前先检测是否获取了读写权限

## 1.8.8

1、修复下载进度在切换项目后的显示bug

2、BaseInnerFragment 和 BaseImageFragment 新增 下载图片或视频 的方法

## 1.8.7

1、新增可选下载图片或视频的按钮，对应方法是  OpenImage ---> setShowDownload （如果需要显示下载进度请前往[下载图片或视频的使用说明](https://github.com/FlyJingFish/OpenImage/wiki/3%E3%80%81%E4%B8%8B%E8%BD%BD%E5%9B%BE%E7%89%87%E6%88%96%E8%A7%86%E9%A2%91%E7%9A%84%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E)）

2、修复视频播放结束后播放器上帝播放按钮不能再次点击的 bug

3、升级 [ShapeImageView](https://github.com/FlyJingFish/ShapeImageView) 库至最新版本 1.5.2


## 1.8.6

1、新增支持 WebView 对应的方法是 OpenImage ---> setClickWebView

2、新增 OpenImage ---> setOnExitListener 设置退出大图页面返的监听

3、升级 [ShapeImageView](https://github.com/FlyJingFish/ShapeImageView) 库至最新版本 1.5.1

## 1.8.5

1、修改 OpenImageFragmentStateAdapter 的 getItemId 方法，使用雪花算法避免 Hash 值也会重复

## 1.8.4

1、修改 OpenImageFragmentStateAdapter 的 getItemId 方法，使用 Hash 值

## 1.8.3

1、OpenImageFragmentStateAdapter 新增 **删除**、**替换(更新)** 数据的方法

2、OnUpdateViewListener 新增 **onRemove**、 **onReplace(更新)** 方法，**onUpdate** 改名为 **onAdd**

## 1.8.2

1、关闭一些访问权限，避免重度定制使用者使用后出现问题

## 1.8.1

1、修复 android12 上 setNoneClickView 时返回后导致父activity动画被更改的问题

2、增加更新数据和View的方法（OpenImage中新增两个 setOpenImageActivityCls 方法 可用于更新）

3、修复添加数据之后指示器不变的bug

## 1.8.0

1、修复 OpenImageActivity 中打开页面是回调两次选中的bug

2、修复 使用 setClickViewPager 时，设置 setWechatExitFillInEffect(true) 没有微信返回补位效果的bug

3、修复 OpenImageActivity 加载更多数据后返回错位问题

4、OpenImageFragmentStateAdapter 添加 向前添加更多数据

## 1.7.9

1、修复完善播放器显示上的问题

## 1.7.8

1、修复封面 PhotoView 的 bug

2、完善 OpenImageFragmentStateAdapter

3、修复 OpenImageVideoPlayer 暂停播放时的问题

4、添加设置错误数据位置等的错误提示

## 1.7.7

1、OpenImageActivity 页面新增 Item 点击监听器、长按监听器、选中监听器

2、修复传入 ImageView 返回时的空指针 bug

3、新增设置触摸关闭控件方向，并完善触摸关闭控件

4、修复下拉关闭的 bug

5、新增相册适配器

6、新增快手示例

7、OpenImageActivity 页面新增 OpenImageFragmentStateAdapter


## 1.7.6

1、修复部分情况下 OnSelectMediaListener 返回的position不对的问题

2、完善 TouchCloseLayout 类

3、添加调用 BaseInnerFragment 中的方法必要时将会进行异常提示

## 1.7.5

1、1.7.4更新失败重新上新版本

2、优化加载图片过程，避免内存泄漏

## 1.7.4(此版本无法正常使用)

1、优化加载图片过程，避免内存泄漏（版本无法使用）

## 1.7.3

1、升级 [ShapeImageView](https://github.com/FlyJingFish/ShapeImageView) 库，处理某些小伙伴遇到的自定义属性冲突的问题

## 1.7.2

1、修复 Android 12 及以上打开大图所在页面返回时的动画异常问题

## 1.7.1

1、优化加载图片，删除掉设置缓存模式和小图加载器，让使用者更容易使用

## 1.7.0

1、修复使用 [ShapeImageView](https://github.com/FlyJingFish/ShapeImageView) 长图在返回时的显示问题

## 1.6.9

1、进一步完善可定制的 OpenImageActivity 页面

## 1.6.8

1、再次关闭一些不必要的类的访问权限

2、新增支持设置矩形圆角图和圆形图，设置后关闭大图页面时效果更加细腻

## 1.6.7

1、关闭 BaseActivity 和 BaseFragment 的访问权限

2、限制自定义图片或视频页面必须继承 BaseImageFragment

## 1.6.6

1、修复使用设置数据包含 Parcelable 类型数据时，打不开页面的问题


## 1.6.5

1、完善方法说明

2、修复 使用 setNoneClickView 时，数据问题

## 1.6.4

1、修改 ViewPagerActivity 类名为 OpenImageActivity

2、修改 OpenImage 的 setOpenImageActivityCls 方法，限制页面必须继承 OpenImageActivity

## 1.6.3

1、OpenImage 新增重载 setOpenImageActivityCls 方法，可给自定义大图页面传数据

2、当 OpenImage 设置 setNoneClickView 时 默认渐现渐隐动画

## 1.6.2

1、修复 Layer 相关 bug

2、修复部分情况下视频播放后，loading 依旧在显示的问题

## 1.6.1

1、针对 1.6.0 第1点中的 debug 模式判断方法修改，改为根据使用者的开发配置来判定是否是 debug 模式

2、升级 [ShapeImageView](https://github.com/FlyJingFish/ShapeImageView) 到1.4.9

## 1.6.0

1、修改 OpenImage.show() 时校验 setClickPosition 改为只在 Debug 模式下抛异常，正式环境默认采取无 ClickView 的模式打开大图


## 1.5.9

1、OpenImageFullLib 库新增支持全屏播放视频

2、图片视频页面 Fragment 和 UpperLayerFragment 新增 onKeyBackDown 方法（点击物理返回按钮时调用 true 可返回 false不可返回）


## 1.5.8

1、新增支持无点击 View 时也可打开大图页面，使用 setNoneClickView 方法

2、修改 UpperLayerFragmentCreate 的方法名 createVideoFragment --> createLayerFragment

3、上下滑动时拖动关闭的适配 Rtl


## 1.5.7

1、修复超长图在 AutoCrop 模式下的退出页面时显示问题

## 1.5.6

1、仓库地址转移到 Maven Central 

**本日志从1.5.6开始记录**