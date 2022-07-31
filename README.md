# OpenImage
图片浏览库
属于你的图片浏览器，完美的过渡动画，同样支持视频，我写出来一个视频使用Demo，本库本身不带这个功能，
您可以自定义加载图片的内核，例如Glide，Picasso....我写了这两种Demo供大家参考，本库侵入性低

第一步. 根目录build.gradle 

allprojects { 
    repositories { 
      ... 
      maven { url 'https://jitpack.io'
    }
} 

第二步. 需要引用的build.gradle

dependencies {
    implementation 'com.github.FlyJingFish:FormatTextViewLib:v1.4'
}

使用说明：

1，您需要实现BigImageHelper接口并设置它，它是加载大图的关键
OpenImageConfig.getInstance().setBigImageHelper(new BigImageHelperImpl());

2，如果有需要打开视频请实现VideoFragmentCreate 并继承BaseImageFragment否则动画效果可能不够完善
OpenImageConfig.getInstance().setVideoFragmentCreate(new VideoFragmentCreateImpl());

3，如果有需要定制请实现ImageFragmentCreate 并继承BaseImageFragment否则动画效果可能不够完善
OpenImageConfig.getInstance().setImageFragmentCreate(new ImageFragment());

4，打开图片时
  OpenImage.with(RecyclerViewActivity.this).setClickRecyclerView(binding.rv.rv,new SourceImageViewIdGet() {
        @Override
        public int getImageViewId(OpenImageUrl data, int position) {
            return R.id.iv_image;//返回图片的Id
        }
    })//点击所在的RecyclerView，也可以是ListView，GridView，单个或多个ImageView
    .setSrcImageViewScaleType(ImageView.ScaleType.CENTER_CROP,true)//点击的图片显示模式
    .setImageUrlList(datas)//图片数据
    .setImageDiskMode(ImageDiskMode.CONTAIN_ORIGINAL)//您所设置的图片硬盘缓存模式
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
    .setOpenImageStyle(R.style.DefaultPhotosTheme)//定义页面样式
    .setClickPosition(position)//点击的数据位置
    .show();
5，设置页面样式
//页面背景颜色
<attr format="reference|color" name="openImage.background"/>
//切换图片方向
<attr format="enum" name="openImage.viewPager.orientation">
    <enum name="horizontal" value="0" />
    <enum name="vertical" value="1" />
</attr>
<attr format="dimension" name="openImage.indicator.marginStart"/>
<attr format="dimension" name="openImage.indicator.marginEnd"/>
<attr format="dimension" name="openImage.indicator.marginTop"/>
<attr format="dimension" name="openImage.indicator.marginBottom"/>
<attr format="dimension" name="openImage.indicator.marginLeft"/>
<attr format="dimension" name="openImage.indicator.marginRight"/>
<attr format="flags" name="openImage.indicator.gravity">
    <flag name="top" value="0x30" />
    <flag name="bottom" value="0x50" />
    <flag name="left" value="0x03" />
    <flag name="right" value="0x05" />
    <flag name="center_vertical" value="0x10" />
    <flag name="center_horizontal" value="0x01" />
    <flag name="center" value="0x11" />
    <flag name="start" value="0x00800003" />
    <flag name="end" value="0x00800005" />
</attr>
<attr format="enum" name="openImage.indicator.type">
    <enum name="text" value="0"/>
    <enum name="image" value="1"/>
</attr>
<attr format="reference" name="openImage.indicator.textFormat"/>
<attr format="reference|color" name="openImage.indicator.textColor"/>
<attr format="dimension" name="openImage.indicator.textSize"/>
<attr format="reference|color" name="openImage.indicator.imageRes"/>
<attr format="dimension" name="openImage.indicator.image.interval"/>
<attr format="enum" name="openImage.indicator.image.orientation">
    <enum name="horizontal" value="0" />
    <enum name="vertical" value="1" />
</attr>
<attr format="enum" name="openImage.statusBar.fontStyle">
    <enum name="light" value="0" />
    <enum name="dark" value="1" />
</attr>
