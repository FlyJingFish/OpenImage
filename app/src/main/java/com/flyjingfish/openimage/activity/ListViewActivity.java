package com.flyjingfish.openimage.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.flyjingfish.openimage.DataUtils;
import com.flyjingfish.openimage.R;
import com.flyjingfish.openimage.bean.ImageEntity;
import com.flyjingfish.openimage.databinding.LayoutListviewBinding;
import com.flyjingfish.openimage.imageloader.MyImageLoader;
import com.flyjingfish.openimagelib.OpenImage;
import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.listener.SourceImageViewIdGet;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ListViewActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    public static ExecutorService cThreadPool = Executors.newFixedThreadPool(5);;
    private LayoutListviewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setAllowEnterTransitionOverlap(true);
        binding = LayoutListviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadData();
    }

    private void loadData() {
        cThreadPool.submit(() -> {
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
        runOnUiThread(() -> binding.listView.setAdapter(new MyAdapter(datas)));

    }

    protected class MyAdapter extends BaseAdapter{
        List<ImageEntity> datas;

        public MyAdapter(List<ImageEntity> datas) {
            this.datas = datas;
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vHolder;
            if (convertView == null){
                convertView = LayoutInflater.from(ListViewActivity.this).inflate(R.layout.item_listview,parent,false);
                vHolder = new ViewHolder();
                vHolder.imageView = convertView.findViewById(R.id.iv_image);
                convertView.setTag(vHolder);
            } else {
                vHolder = (ViewHolder) convertView.getTag();
            }
            MyImageLoader.getInstance().load(vHolder.imageView,datas.get(position).getCoverImageUrl(),R.mipmap.img_load_placeholder,R.mipmap.img_load_placeholder);
            vHolder.imageView.setOnClickListener(v -> {
                OpenImage.with(ListViewActivity.this).setClickListView(binding.listView,new SourceImageViewIdGet() {
                    @Override
                    public int getImageViewId(OpenImageUrl data, int position) {
                        return R.id.iv_image;
                    }
                }) .setAutoScrollScanPosition(true)
                        .setSrcImageViewScaleType(ImageView.ScaleType.CENTER_CROP,true)
                        .setImageUrlList(datas)
                        .setOpenImageStyle(R.style.DefaultPhotosTheme)
                        .setClickPosition(position).show();
            });
            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
        }
    }


}
