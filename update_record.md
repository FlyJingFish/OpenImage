# OpenImage 更新日志

## 1.6.1

1、针对 1.6.0 第1点中的debug模式判断方法修改，改为根据使用者的开发配置来判定是否是debug模式

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