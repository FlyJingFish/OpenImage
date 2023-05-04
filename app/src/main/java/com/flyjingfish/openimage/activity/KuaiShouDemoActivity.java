package com.flyjingfish.openimage.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.flyjingfish.openimage.DataUtils;
import com.flyjingfish.openimage.MyApplication;
import com.flyjingfish.openimage.R;
import com.flyjingfish.openimage.bean.ImageEntity;
import com.flyjingfish.openimage.bean.MessageBean;
import com.flyjingfish.openimage.bean.TestBean;
import com.flyjingfish.openimage.databinding.ActivityKuaishouDemoBinding;
import com.flyjingfish.openimage.databinding.ActivityRecyclerviewBinding;
import com.flyjingfish.openimage.imageloader.MyImageLoader;
import com.flyjingfish.openimage.openImpl.FriendsVideoFragmentCreateImpl;
import com.flyjingfish.openimage.openImpl.KuaiShouActivity;
import com.flyjingfish.openimage.openImpl.KuaishouVideoFragmentCreateImpl;
import com.flyjingfish.openimagelib.OpenImage;
import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.listener.SourceImageViewIdGet;
import com.flyjingfish.openimagelib.transformers.ScaleInTransformer;
import com.flyjingfish.openimagelib.utils.ScreenUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class KuaiShouDemoActivity extends BaseActivity {

    private ActivityKuaishouDemoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityKuaishouDemoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.rv.rv.setLayoutManager(new GridLayoutManager(this, 2));
        loadData();

    }

    private void loadData() {
        MyApplication.cThreadPool.submit(() -> {
            List<MessageBean> datas = new ArrayList<>();

            String response1 = DataUtils.getFromAssets(this, "video_data.json");
            try {
                JSONArray jsonArray = new JSONArray(response1);
                for (int i = 0; i < jsonArray.length(); i++) {
                    MessageBean itemData = new MessageBean();
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    itemData.type = MessageBean.VIDEO;
                    itemData.videoUrl = jsonObject.getString("videoUrl");
                    itemData.coverUrl = jsonObject.getString("coverUrl");
                    datas.add(itemData);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            setData(datas);
        });
    }

    private void setData(List<MessageBean> datas) {
        runOnUiThread(() -> binding.rv.rv.setAdapter(new RvAdapter(datas)));

    }

    private class RvAdapter extends RecyclerView.Adapter<RvAdapter.MyHolder> {
        List<MessageBean> datas;

        public RvAdapter(List<MessageBean> datas) {
            this.datas = datas;
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kuaishouw, parent, false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder holder, int position) {
            ViewGroup.LayoutParams layoutParams = holder.ivImage.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = (int) ScreenUtils.dp2px(KuaiShouDemoActivity.this, 220);
            MyImageLoader.getInstance().load(holder.ivImage, datas.get(position).getCoverImageUrl(), R.mipmap.img_load_placeholder, R.mipmap.img_load_placeholder);
            holder.ivImage.setOnClickListener(v -> {
                OpenImage.with(KuaiShouDemoActivity.this)
                        .setClickRecyclerView(binding.rv.rv, new SourceImageViewIdGet() {
                            @Override
                            public int getImageViewId(OpenImageUrl data, int position) {
                                return R.id.iv_image;
                            }
                        }).setAutoScrollScanPosition(true)
                        .setSrcImageViewScaleType(ImageView.ScaleType.CENTER_CROP, true)
                        .setImageUrl(datas.get(position))
                        .setClickPosition(0,position)
                        .addPageTransformer(new ScaleInTransformer())
                        .setOpenImageStyle(R.style.KuaishouPhotosTheme)
                        .disableClickClose()
                        .setVideoFragmentCreate(new KuaishouVideoFragmentCreateImpl())
                        .setOpenImageActivityCls(KuaiShouActivity.class)
                        .setWechatExitFillInEffect(true)
                        .show();

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
