package com.flyjingfish.openimage.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flyjingfish.openimage.DataUtils;
import com.flyjingfish.openimage.MyApplication;
import com.flyjingfish.openimage.R;
import com.flyjingfish.openimage.bean.MessageBean;
import com.flyjingfish.openimage.databinding.ActivityKuaishouDemoBinding;
import com.flyjingfish.openimage.imageloader.MyImageLoader;
import com.flyjingfish.openimage.openImpl.KuaiShouActivity;
import com.flyjingfish.openimage.openImpl.KuaishouVideoFragmentCreateImpl;
import com.flyjingfish.openimagelib.OpenImage;
import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.enums.UpdateViewType;
import com.flyjingfish.openimagelib.listener.OnUpdateViewListener;
import com.flyjingfish.openimagelib.listener.SourceImageViewIdGet;
import com.flyjingfish.openimagelib.utils.ScreenUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KuaiShouDemoActivity extends BaseActivity {

    private ActivityKuaishouDemoBinding binding;
    public static Mode mode = Mode.Find;
    public enum Mode{
        Find,Search
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityKuaishouDemoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mode = Mode.Find;
        setSelect();
        binding.btnFind.setOnClickListener(v -> {
            mode = Mode.Find;
            setSelect();
            loadData();
        });
        binding.btnSearch.setOnClickListener(v -> {
            mode = Mode.Search;
            setSelect();
            loadData();
        });
        binding.rv.rv.setLayoutManager(new GridLayoutManager(this, 2));
        loadData();

    }

    private void setSelect(){
        binding.btnFind.setSelected(mode == Mode.Find);
        binding.btnSearch.setSelected(mode == Mode.Search);
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
                OpenImage openImage = OpenImage.with(KuaiShouDemoActivity.this)
                        .setClickRecyclerView(binding.rv.rv, new SourceImageViewIdGet() {
                            @Override
                            public int getImageViewId(OpenImageUrl data, int position) {
                                return R.id.iv_image;
                            }
                        })
                        .setSrcImageViewScaleType(ImageView.ScaleType.CENTER_CROP, true)
                        .setOpenImageStyle(R.style.KuaishouPhotosTheme)
                        .setVideoFragmentCreate(new KuaishouVideoFragmentCreateImpl())
                        .disableClickClose();
                if (mode == Mode.Search){
                    openImage
                            .setImageUrlList(datas)
                            .setClickPosition(position)
                            .setAutoScrollScanPosition(true)
                            .setWechatExitFillInEffect(false)
                            .setOpenImageActivityCls(KuaiShouActivity.class, new OnUpdateViewListener() {
                                @Override
                                public void onAdd(Collection<? extends OpenImageUrl> data, UpdateViewType updateViewType) {
                                    if (updateViewType == UpdateViewType.FORWARD){
                                        datas.addAll(0, (Collection<? extends MessageBean>) data);
                                        notifyDataSetChanged();
                                    }else if (updateViewType == UpdateViewType.BACKWARD){
                                        datas.addAll((Collection<? extends MessageBean>) data);
                                        notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void onRemove(OpenImageUrl openImageUrl) {
                                    datas.remove(openImageUrl);
                                    notifyDataSetChanged();
                                }

                                @Override
                                public void onReplace(int position, OpenImageUrl oldData, OpenImageUrl newData) {
                                    int index=0;
                                    for (MessageBean bean : datas) {
                                        if (bean == oldData){
                                            datas.set(index, (MessageBean) newData);
                                            notifyDataSetChanged();
                                            return;
                                        }
                                        index++;
                                    }
                                }

                            });
                }else {
                    openImage
                            .setImageUrl(datas.get(position))
                            .setClickPosition(0,position)
                            .setAutoScrollScanPosition(false)
                            .setWechatExitFillInEffect(true)
                            .setOpenImageActivityCls(KuaiShouActivity.class);
                }

                openImage.show();

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
