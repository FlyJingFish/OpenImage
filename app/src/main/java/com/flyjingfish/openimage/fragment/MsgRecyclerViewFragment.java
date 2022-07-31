package com.flyjingfish.openimage.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.flyjingfish.openimage.DataUtils;
import com.flyjingfish.openimage.bean.MessageBean;
import com.flyjingfish.openimage.MyApplication;
import com.flyjingfish.openimage.adapter.MsgRvAdapter;
import com.flyjingfish.openimage.databinding.LayoutRecyclerviewBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MsgRecyclerViewFragment extends Fragment {
    private LayoutRecyclerviewBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutRecyclerviewBinding.inflate(inflater,container,false);
        binding.rv.setLayoutManager(new LinearLayoutManager(requireActivity()));
        loadData();
        return binding.getRoot();
    }

    private void loadData() {
        MyApplication.cThreadPool.submit(() -> {
            List<MessageBean> datas = new ArrayList<>();

            String response1 = DataUtils.getFromAssets(requireActivity(), "message_data.json");
            try {
                JSONArray jsonArray = new JSONArray(response1);
                for (int i = 0; i < jsonArray.length(); i++) {
                    MessageBean itemData = new MessageBean();
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    int type = jsonObject.getInt("type");
                    itemData.type = type;
                    if (type == MessageBean.TEXT){
                        itemData.text = jsonObject.getString("text");
                    }else if (type == MessageBean.IMAGE){
                        itemData.imageUrl = jsonObject.getString("imageUrl");
                        itemData.coverUrl = jsonObject.getString("coverUrl");
                    }else if (type == MessageBean.VIDEO){
                        itemData.videoUrl = jsonObject.getString("videoUrl");
                        itemData.coverUrl = jsonObject.getString("coverUrl");
                        itemData.smallCoverUrl = jsonObject.getString("smallCoverUrl");
                    }
                    datas.add(itemData);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            setData(datas);
        });
    }

    private void setData(List<MessageBean> datas) {
        requireActivity().runOnUiThread(() -> binding.rv.setAdapter(new MsgRvAdapter(datas)));

    }

}
