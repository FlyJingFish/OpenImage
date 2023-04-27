# OpenImage 更新日志

## 1.7.5

1，1.7.4更新失败重新上新版本

2，优化加载图片过程，避免内存泄漏

## 1.7.4(此版本无法正常使用)

1，优化加载图片过程，避免内存泄漏（版本无法使用）

## 1.7.3

1，升级 ShapeImageView 库，处理某些小伙伴遇到的自定义属性冲突的问题

## 1.7.2

1，修复 Android 12 及以上打开大图所在页面返回时的动画异常问题

## 1.7.1

1，优化加载图片，删除掉设置缓存模式和小图加载器，让使用者更容易使用

## 1.7.0

1，修复使用 ShapeImageView 长图在返回时的显示问题

## 1.6.9

1，进一步完善可定制的 OpenImageActivity 页面

## 1.6.8

1，再次关闭一些不必要的类的访问权限

2，新增支持设置矩形圆角图和圆形图，设置后关闭大图页面时效果更加细腻

## 1.6.7

1，关闭 BaseActivity 和 BaseFragment 的访问权限

2，限制自定义图片或视频页面必须继承 BaseImageFragment

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

2、升级 ShapeImageView 到1.4.9

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