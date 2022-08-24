package com.flyjingfish.openimage.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.flyjingfish.openimage.DataUtils;
import com.flyjingfish.openimage.MyApplication;
import com.flyjingfish.openimage.R;
import com.flyjingfish.openimage.bean.ImageEntity;
import com.flyjingfish.openimage.databinding.ActivityViewPagerDemoBinding;
import com.flyjingfish.openimage.databinding.ItemImageBinding;
import com.flyjingfish.openimage.imageloader.MyImageLoader;
import com.flyjingfish.openimagelib.OpenImage;
import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.listener.ItemLoadHelper;
import com.flyjingfish.openimagelib.listener.OnLoadCoverImageListener;
import com.flyjingfish.openimagelib.listener.SourceImageViewGet;
import com.flyjingfish.openimagelib.listener.SourceImageViewIdGet;
import com.flyjingfish.openimagelib.transformers.ScaleInTransformer;
import com.youth.banner.adapter.BannerAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerDemoActivity extends AppCompatActivity {

    private ActivityViewPagerDemoBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewPagerDemoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadData();
    }
    private void loadData() {
        MyApplication.cThreadPool.submit(() -> {
            List<ImageEntity> datas = new ArrayList<>();

            String response1 = DataUtils.getFromAssets(this, "listview_data.json");
            try {
                JSONArray jsonArray = new JSONArray(response1);
                for (int i = 0; i < jsonArray.length(); i++) {
                    ImageEntity itemData = new ImageEntity();
                    String url = jsonArray.getString(i);
                    itemData.url = url;
                    datas.add(itemData);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            setData(datas);
        });
    }

    private void setData(List<ImageEntity> datas) {
        runOnUiThread(() -> {
            binding.viewPager.setAdapter(new ViewPagerAdapter(datas));
            binding.viewPager2.setAdapter(new ViewPager2Adapter(datas));
            binding.banner.addBannerLifecycleObserver(this);
            binding.banner.setAdapter(new MyBannerAdapter(datas));
            binding.banner.isAutoLoop(true);
            binding.banner.start();
        });

    }

    private class ViewPagerAdapter extends PagerAdapter implements SourceImageViewGet<OpenImageUrl> {
        List<ImageEntity> datas;
        private View mItemView;

        public ViewPagerAdapter(List<ImageEntity> datas) {
            this.datas = datas;
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ItemImageBinding imageBinding = ItemImageBinding.inflate(LayoutInflater.from(container.getContext()),container,true);
            View view = imageBinding.getRoot();
            MyImageLoader.getInstance().load(imageBinding.ivImage, datas.get(position).getCoverImageUrl(), R.mipmap.img_load_placeholder, R.mipmap.img_load_placeholder);
            imageBinding.ivImage.setOnClickListener(v -> {
                OpenImage.with(ViewPagerDemoActivity.this).setClickViewPager(binding.viewPager,ViewPagerAdapter.this)
                        .setShowSrcImageView(false)
                        .setAutoScrollScanPosition(true)
                        .setSrcImageViewScaleType(ImageView.ScaleType.CENTER_CROP, true)
                        .setImageUrlList(datas).setImageDiskMode(MyImageLoader.imageDiskMode)
                        .setItemLoadHelper(new ItemLoadHelper() {
                            @Override
                            public void loadImage(Context context, OpenImageUrl openImageUrl, String imageUrl, ImageView imageView, int overrideWidth, int overrideHeight, OnLoadCoverImageListener onLoadCoverImageListener) {

                                MyImageLoader.getInstance().load(imageView, imageUrl, overrideWidth, overrideHeight, R.mipmap.img_load_placeholder, R.mipmap.img_load_placeholder, new MyImageLoader.OnImageLoadListener() {
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
                        }).addPageTransformer(new ScaleInTransformer())
                        .setOpenImageStyle(R.style.DefaultPhotosTheme)
                        .setClickPosition(position).show();
            });
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @Override
        public void  setPrimaryItem(ViewGroup container, int  position, Object object) {
            mItemView = (View)object;
        }

        @Override
        public ImageView getImageView(OpenImageUrl data, int position) {
            ImageView imageView = null;
            if (mItemView != null){
                imageView = mItemView.findViewById(R.id.iv_image);
            }
            return imageView;
        }

    }

    private class ViewPager2Adapter extends RecyclerView.Adapter<ViewPager2Adapter.MyHolder> {
        List<ImageEntity> datas;

        public ViewPager2Adapter(List<ImageEntity> datas) {
            this.datas = datas;
        }

        @NonNull
        @Override
        public ViewPager2Adapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
            return new ViewPager2Adapter.MyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewPager2Adapter.MyHolder holder, int position) {
            MyImageLoader.getInstance().load(holder.ivImage, datas.get(position).getCoverImageUrl(), R.mipmap.img_load_placeholder, R.mipmap.img_load_placeholder);
            holder.ivImage.setOnClickListener(v -> {
                OpenImage.with(ViewPagerDemoActivity.this).setClickViewPager2(binding.viewPager2, new SourceImageViewIdGet<OpenImageUrl>() {
                    @Override
                    public int getImageViewId(OpenImageUrl data, int position) {
                        return R.id.iv_image;
                    }
                }).setShowSrcImageView(false)
                .setAutoScrollScanPosition(true)
                .setSrcImageViewScaleType(ImageView.ScaleType.CENTER_CROP, true)
                .setImageUrlList(datas).setImageDiskMode(MyImageLoader.imageDiskMode)
                .setItemLoadHelper(new ItemLoadHelper() {
                    @Override
                    public void loadImage(Context context, OpenImageUrl openImageUrl, String imageUrl, ImageView imageView, int overrideWidth, int overrideHeight, OnLoadCoverImageListener onLoadCoverImageListener) {

                        MyImageLoader.getInstance().load(imageView, imageUrl, overrideWidth, overrideHeight, R.mipmap.img_load_placeholder, R.mipmap.img_load_placeholder, new MyImageLoader.OnImageLoadListener() {
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
                }).addPageTransformer(new ScaleInTransformer())
                .setOpenImageStyle(R.style.DefaultPhotosTheme)
                .setClickPosition(position).show();
            });
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }

        class MyHolder extends RecyclerView.ViewHolder {
            ImageView ivImage;

            public MyHolder(@NonNull View itemView) {
                super(itemView);
                ivImage = itemView.findViewById(R.id.iv_image);
            }
        }
    }

    public class MyBannerAdapter extends BannerAdapter<ImageEntity, MyBannerAdapter.MyBannerHolder> {

        public MyBannerAdapter(List<ImageEntity> datas) {
            super(datas);
        }

        @Override
        public MyBannerHolder onCreateHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
            return new MyBannerHolder(view);
        }

        @Override
        public void onBindView(MyBannerHolder holder, ImageEntity data, int position, int size) {
            MyImageLoader.getInstance().load(holder.ivImage, mDatas.get(position).getCoverImageUrl(), R.mipmap.img_load_placeholder, R.mipmap.img_load_placeholder);
            holder.ivImage.setOnClickListener(v -> {
                OpenImage.with(ViewPagerDemoActivity.this).setClickViewPager2(binding.banner.getViewPager2(), new SourceImageViewIdGet<OpenImageUrl>() {
                    @Override
                    public int getImageViewId(OpenImageUrl data, int position) {
                        return R.id.iv_image;
                    }
                }).setShowSrcImageView(false)
                        .setAutoScrollScanPosition(true)
                        .setSrcImageViewScaleType(ImageView.ScaleType.CENTER_CROP, true)
                        .setImageUrlList(mDatas).setImageDiskMode(MyImageLoader.imageDiskMode)
                        .setItemLoadHelper(new ItemLoadHelper() {
                            @Override
                            public void loadImage(Context context, OpenImageUrl openImageUrl, String imageUrl, ImageView imageView, int overrideWidth, int overrideHeight, OnLoadCoverImageListener onLoadCoverImageListener) {

                                MyImageLoader.getInstance().load(imageView, imageUrl, overrideWidth, overrideHeight, R.mipmap.img_load_placeholder, R.mipmap.img_load_placeholder, new MyImageLoader.OnImageLoadListener() {
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
                        }).addPageTransformer(new ScaleInTransformer())
                        .setOpenImageStyle(R.style.DefaultPhotosTheme)
                        .setClickPosition(position).show();
            });
        }

        public class MyBannerHolder extends RecyclerView.ViewHolder {
            public ImageView ivImage;

            public MyBannerHolder(@NonNull View view) {
                super(view);
                this.ivImage =  view.findViewById(R.id.iv_image);
            }
        }
    }
}
