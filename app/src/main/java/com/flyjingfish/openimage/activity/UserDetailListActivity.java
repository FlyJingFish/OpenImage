package com.flyjingfish.openimage.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flyjingfish.openimage.DataUtils;
import com.flyjingfish.openimage.MyApplication;
import com.flyjingfish.openimage.R;
import com.flyjingfish.openimage.bean.ImageEntity;
import com.flyjingfish.openimage.bean.User;
import com.flyjingfish.openimage.databinding.ActivityFirendsBinding;
import com.flyjingfish.openimage.imageloader.MyImageLoader;
import com.flyjingfish.openimage.openImpl.UserDetailActivity;
import com.flyjingfish.openimage.openImpl.UserDetailImageFragmentCreateImpl;
import com.flyjingfish.openimagelib.OpenImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserDetailListActivity extends BaseActivity {
    private ActivityFirendsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setAllowEnterTransitionOverlap(true);
        binding = ActivityFirendsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.rv.setLayoutManager(new LinearLayoutManager(this));
        loadData();
    }

    private void loadData() {
        MyApplication.cThreadPool.submit(() -> {
            List<User> datas = new ArrayList<>();
            String response = DataUtils.getFromAssets(this, "user_detail.json");
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    User itemData = new User();
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    itemData.id = jsonObject.optInt("id");
                    itemData.name = jsonObject.optString("name");
                    JSONArray images = jsonObject.optJSONArray("photos");
                    for (int j = 0; j < images.length(); j++) {
                        String url = images.optString(j);
                        itemData.photos.add(new ImageEntity(url));
                    }
                    datas.add(itemData);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            setData(datas);
        });
    }

    private void setData(List<User> datas) {
        runOnUiThread(() -> binding.rv.setAdapter(new RvAdapter(datas)));

    }

    private class RvAdapter extends RecyclerView.Adapter<RvAdapter.MyHolder> {
        List<User> datas;

        public RvAdapter(List<User> datas) {
            this.datas = datas;
        }

        @NonNull
        @Override
        public RvAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kuaishouw, parent, false);
            return new RvAdapter.MyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RvAdapter.MyHolder holder, int position) {
            List<ImageEntity> photos = datas.get(position).photos;
            MyImageLoader.getInstance().load(holder.ivImage, photos.get(0).getCoverImageUrl(), R.mipmap.img_load_placeholder, R.mipmap.img_load_placeholder);
            holder.tvTitle.setText(datas.get(position).name);
            holder.ivImage.setOnClickListener(v -> {

                Bundle bundle = new Bundle();
                bundle.putSerializable(UserDetailActivity.MY_DATA_KEY, datas.get(position));

                OpenImage.with(UserDetailListActivity.this)
                        .setClickImageView(holder.ivImage)
                        .setSrcImageViewScaleType(ImageView.ScaleType.CENTER_CROP, true)
                        .setOpenImageStyle(R.style.UserDetailPhotosTheme)
                        .disableTouchClose()
                        .disableClickClose()
                        .setImageUrl(datas.get(position).photos.get(0))
                        .setClickPosition(0)
                        .setAutoScrollScanPosition(false)
                        .setWechatExitFillInEffect(true)
                        .setImageFragmentCreate(new UserDetailImageFragmentCreateImpl())
                        .setOpenImageActivityCls(UserDetailActivity.class, UserDetailActivity.BUNDLE_DATA_KEY, bundle)
                        .show();

            });
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }

        class MyHolder extends RecyclerView.ViewHolder {
            ImageView ivImage;
            TextView tvTitle;

            public MyHolder(@NonNull View itemView) {
                super(itemView);
                ivImage = itemView.findViewById(R.id.iv_image);
                tvTitle = itemView.findViewById(R.id.tv_title);
            }
        }
    }

}
