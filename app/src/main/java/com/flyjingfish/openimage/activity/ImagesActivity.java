package com.flyjingfish.openimage.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.flyjingfish.openimage.DataUtils;
import com.flyjingfish.openimage.bean.ImageEntity;
import com.flyjingfish.openimage.MyApplication;
import com.flyjingfish.openimage.imageloader.MyImageLoader;
import com.flyjingfish.openimage.R;
import com.flyjingfish.openimage.databinding.ActivityImagesBinding;
import com.flyjingfish.openimagelib.OpenImage;
import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.listener.ItemLoadHelper;
import com.flyjingfish.openimagelib.listener.OnLoadCoverImageListener;
import com.flyjingfish.openimagelib.listener.OnSelectMediaListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ImagesActivity extends AppCompatActivity {

    private ActivityImagesBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImagesBinding.inflate(getLayoutInflater());
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
            MyImageLoader.getInstance().load(binding.iv1,datas.get(0).getCoverImageUrl(), R.mipmap.img_load_placeholder,R.mipmap.img_load_placeholder);
            MyImageLoader.getInstance().load(binding.iv2,datas.get(1).getCoverImageUrl(),R.mipmap.img_load_placeholder,R.mipmap.img_load_placeholder);
            MyImageLoader.getInstance().load(binding.iv3,datas.get(2).getCoverImageUrl(),R.mipmap.img_load_placeholder,R.mipmap.img_load_placeholder);
            MyImageLoader.getInstance().load(binding.iv4,datas.get(3).getCoverImageUrl(),R.mipmap.img_load_placeholder,R.mipmap.img_load_placeholder);
            MyImageLoader.getInstance().load(binding.iv5,datas.get(4).getCoverImageUrl(),R.mipmap.img_load_placeholder,R.mipmap.img_load_placeholder);
            MyImageLoader.getInstance().load(binding.iv6,datas.get(5).getCoverImageUrl(),R.mipmap.img_load_placeholder,R.mipmap.img_load_placeholder);
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = 0;
                    switch (v.getId()){
                        case R.id.iv1:
                            position = 0;
                            break;
                        case R.id.iv2:
                            position = 1;
                            break;
                        case R.id.iv3:
                            position = 2;
                            break;
                        case R.id.iv4:
                            position = 3;
                            break;
                        case R.id.iv5:
                            position = 4;
                            break;
                        case R.id.iv6:
                            position = 5;
                            break;
                    }
                    List<ImageView> imageViews = new ArrayList<>();
                    imageViews.add(binding.iv1);
                    imageViews.add(binding.iv2);
                    imageViews.add(binding.iv3);
                    imageViews.add(binding.iv4);
                    imageViews.add(binding.iv5);
                    imageViews.add(binding.iv6);
                    Iterator<ImageEntity> iterator = datas.iterator();
                    int index = 0;
                    while (iterator.hasNext()){
                        iterator.next();
                        if (index>5){
                            iterator.remove();
                        }
                        index ++;
                    }
                    OpenImage.with(ImagesActivity.this).setClickImageViews(imageViews).setSrcImageViewScaleType(ImageView.ScaleType.CENTER_CROP,true)
                            .setImageUrlList(datas).setImageDiskMode(MyImageLoader.imageDiskMode)
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
                            .setOnSelectMediaListener(new OnSelectMediaListener() {
                                boolean isFirstBacked = false;
                                @Override
                                public void onSelect(int position) {
                                    if (isFirstBacked){
                                        binding.scrollView.post(() -> binding.scrollView.scrollTo(0,imageViews.get(position).getTop()));
                                    }
                                    isFirstBacked = true;
                                }
                            })
                            .setOpenImageStyle(R.style.DefaultPhotosTheme)
                            .setClickPosition(position).show();
                }
            };

            binding.iv1.setOnClickListener(onClickListener);
            binding.iv2.setOnClickListener(onClickListener);
            binding.iv3.setOnClickListener(onClickListener);
            binding.iv4.setOnClickListener(onClickListener);
            binding.iv5.setOnClickListener(onClickListener);
            binding.iv6.setOnClickListener(onClickListener);

        });

    }
}
