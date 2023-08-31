package com.flyjingfish.openimage.openImpl;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.flyjingfish.openimage.DataUtils;
import com.flyjingfish.openimage.MyApplication;
import com.flyjingfish.openimage.bean.MessageBean;
import com.flyjingfish.openimagelib.OpenImageActivity;
import com.flyjingfish.openimagelib.databinding.OpenImageActivityViewpagerBinding;
import com.flyjingfish.openimagelib.widget.TouchCloseLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MessageVpActivity extends OpenImageActivity {

    private OpenImageActivityViewpagerBinding rootBinding;
    public static final String BUNDLE_DATA_KEY = "bundle_data";
    public static final String MY_DATA_KEY = "my_data";

    @Override
    public View getContentView() {
        rootBinding = OpenImageActivityViewpagerBinding.inflate(getLayoutInflater());
        return rootBinding.getRoot();
    }

    @Override
    public View getBgView() {
        return rootBinding.vBg;
    }


    @Override
    public FrameLayout getViewPager2Container() {
        return rootBinding.flTouchView;
    }

    @Override
    public ViewPager2 getViewPager2() {
        return rootBinding.viewPager;
    }

    @Override
    public TouchCloseLayout getTouchCloseLayout() {
        return rootBinding.getRoot();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addOnSelectMediaListener((openImageUrl, position) -> {
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                if (state == ViewPager2.SCROLL_STATE_IDLE){
                   checkData();
                }
            }
        });
        checkData();
    }
    private void checkData() {
        if (viewPager.getCurrentItem() <= 0){
            loadData();
        }
    }
    public static int addCount;
    private void loadData() {
        if (addCount < 1){
            MyApplication.cThreadPool.submit(() -> {
                List<MessageBean> datas = new ArrayList<>();

                String response1 = DataUtils.getFromAssets(this, "message_data2.json");
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
                        }
                        datas.add(itemData);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setData(datas);
            });
        }
        addCount++;
    }

    private void setData(List<MessageBean> datas) {
        runOnUiThread(() -> {
            openImageAdapter.addFrontData(datas);
        });

    }
}
