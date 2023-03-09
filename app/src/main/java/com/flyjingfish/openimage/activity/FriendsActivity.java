package com.flyjingfish.openimage.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.flyjingfish.openimage.DataUtils;
import com.flyjingfish.openimage.bean.ImageEntity;
import com.flyjingfish.openimage.bean.ImageItem;
import com.flyjingfish.openimage.MyApplication;
import com.flyjingfish.openimage.adapter.FriendsAdapter;
import com.flyjingfish.openimage.databinding.ActivityFirendsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FriendsActivity extends BaseActivity {
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
            List<ImageItem> datas = new ArrayList<>();
            String response = DataUtils.getFromAssets(this, "data.json");
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    ImageItem itemData = new ImageItem();
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    itemData.type = jsonObject.optInt("type");
                    itemData.text = jsonObject.optString("text");
                    if (itemData.type == ImageItem.IMAGE){
                        JSONArray images = jsonObject.optJSONArray("images");
                        for (int j = 0; j < images.length(); j++) {
                            String url = images.optString(j);
                            itemData.images.add(new ImageEntity(url));
                        }
                    }else {
                        itemData.coverUrl = jsonObject.optString("coverUrl");
                        itemData.videoUrl = jsonObject.optString("videoUrl");
                    }
                    datas.add(itemData);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            setData(datas);
        });
    }

    private void setData(List<ImageItem> datas) {
        runOnUiThread(() -> binding.rv.setAdapter(new FriendsAdapter(datas,this)));

    }

}
