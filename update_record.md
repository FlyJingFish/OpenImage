# OpenImage 更新日志

## 1.6.6

1、修复使用 设置数据包含 Parcelable 类型数据 时，打不开页面的问题


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

1、修复Layer相关bug

2、修复部分情况下视频播放后，loading依旧在显示的问题

## 1.6.1

1、针对 1.6.0 第1点中的debug模式判断方法修改，改为根据使用者的开发配置来判定是否是debug模式

2、升级ShapeImageView到1.4.9

## 1.6.0

1、修改OpenImage.show() 时校验setClickPosition改为只在Debug模式下抛异常，正式环境默认采取无ClickView的模式打开大图


## 1.5.9

1、OpenImageFullLib库新增支持全屏播放视频

2、图片视频页面Fragment 和 UpperLayerFragment 新增onKeyBackDown方法（点击物理返回按钮时调用 true 可返回 false不可返回）


## 1.5.8

1、新增支持无点击View是也可打开大图页面

2、修改UpperLayerFragmentCreate 的方法名 createVideoFragment --> createLayerFragment

3、上下滑动时拖动关闭的适配Rtl


## 1.5.7

1、修复超长图在 AutoCrop 模式下的退出页面时显示问题

## 1.5.6

1、仓库地址转移到 Maven Central 

**本日志从1.5.6开始记录**