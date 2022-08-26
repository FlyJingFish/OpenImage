package com.flyjingfish.openimage.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.flyjingfish.openimage.DataUtils;
import com.flyjingfish.openimage.MyApplication;
import com.flyjingfish.openimage.bean.ImageEntity;
import com.flyjingfish.openimage.imageloader.MyImageLoader;
import com.flyjingfish.openimage.R;
import com.flyjingfish.openimage.databinding.ActivityRecyclerviewBinding;
import com.flyjingfish.openimagelib.OpenImage;
import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.listener.ItemLoadHelper;
import com.flyjingfish.openimagelib.listener.OnLoadCoverImageListener;
import com.flyjingfish.openimagelib.listener.SourceImageViewIdGet;
import com.flyjingfish.openimagelib.transformers.ScaleInTransformer;
import com.flyjingfish.openimagelib.utils.ScreenUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewActivity extends AppCompatActivity {

    private ActivityRecyclerviewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecyclerviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.rv.rv.setLayoutManager(new LinearLayoutManager(this));
        loadData();
        setSelect(0);
        binding.btnV.setOnClickListener(v -> {
            setSelect(0);
            binding.rv.rv.setLayoutManager(new LinearLayoutManager(this));
            loadData();
        });
        binding.btnH.setOnClickListener(v -> {
            setSelect(1);
            binding.rv.rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            loadData();
        });
        binding.btnG.setOnClickListener(v -> {
            setSelect(2);
            binding.rv.rv.setLayoutManager(new GridLayoutManager(this, 2));
            loadData();
        });
        binding.btnP.setOnClickListener(v -> {
            setSelect(3);
            binding.rv.rv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            loadData();
        });

    }

    private int layoutType;

    private void setSelect(int pos) {
        layoutType = pos;
        binding.btnV.setSelected(pos == 0);
        binding.btnH.setSelected(pos == 1);
        binding.btnG.setSelected(pos == 2);
        binding.btnP.setSelected(pos == 3);
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
        runOnUiThread(() -> binding.rv.rv.setAdapter(new RvAdapter(datas)));

    }

    private class RvAdapter extends RecyclerView.Adapter<RvAdapter.MyHolder> {
        List<ImageEntity> datas;

        public RvAdapter(List<ImageEntity> datas) {
            this.datas = datas;
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            if (layoutType == 3) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_staggered, parent, false);
            } else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listview, parent, false);
            }
            return new RvAdapter.MyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder holder, int position) {
            if (layoutType == 1) {
                ViewGroup.LayoutParams layoutParams = holder.ivImage.getLayoutParams();
                layoutParams.width = (int) ScreenUtils.dp2px(RecyclerViewActivity.this, 100);
                layoutParams.height = (int) ScreenUtils.dp2px(RecyclerViewActivity.this, 100);
            } else if (layoutType == 0 || layoutType == 2) {
                ViewGroup.LayoutParams layoutParams = holder.ivImage.getLayoutParams();
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                layoutParams.height = (int) ScreenUtils.dp2px(RecyclerViewActivity.this, 220);
            }
            if (layoutType == 3) {
                MyImageLoader.getInstance().load(holder.ivImage, datas.get(position).getCoverImageUrl(), R.mipmap.img_load_placeholder, R.mipmap.img_load_placeholder);
            } else {
                MyImageLoader.getInstance().load(holder.ivImage, datas.get(position).getCoverImageUrl(), R.mipmap.img_load_placeholder, R.mipmap.img_load_placeholder);

            }
            holder.ivImage.setOnClickListener(v -> {
                OpenImage.with(RecyclerViewActivity.this).setClickRecyclerView(binding.rv.rv, new SourceImageViewIdGet() {
                    @Override
                    public int getImageViewId(OpenImageUrl data, int position) {
                        return R.id.iv_image;
                    }
                }).setAutoScrollScanPosition(true)
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
}
