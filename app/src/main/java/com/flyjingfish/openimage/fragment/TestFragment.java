package com.flyjingfish.openimage.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.flyjingfish.openimage.DataUtils;
import com.flyjingfish.openimage.MyApplication;
import com.flyjingfish.openimage.R;
import com.flyjingfish.openimage.bean.ImageEntity;
import com.flyjingfish.openimage.bean.TestBean;
import com.flyjingfish.openimage.databinding.FragmentTestBinding;
import com.flyjingfish.openimage.imageloader.MyImageLoader;
import com.flyjingfish.openimagelib.OpenImage;
import com.flyjingfish.openimagelib.listener.LayoutManagerFindVisiblePosition;
import com.flyjingfish.openimagelib.transformers.ScaleInTransformer;
import com.flyjingfish.openimagelib.utils.ScreenUtils;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestFragment extends Fragment {

    private com.flyjingfish.openimage.databinding.FragmentTestBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTestBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnBack.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
        });
        binding.llRoot.rv.rv.setLayoutManager(new LinearLayoutManager(requireActivity()));
        loadData();
        setSelect(0);
        binding.llRoot.btnV.setOnClickListener(v -> {
            setSelect(0);
            binding.llRoot.rv.rv.setLayoutManager(new LinearLayoutManager(requireActivity()));
            loadData();
        });
        binding.llRoot.btnH.setOnClickListener(v -> {
            setSelect(1);
            binding.llRoot.rv.rv.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
            loadData();
        });
        binding.llRoot.btnG.setOnClickListener(v -> {
            setSelect(2);
            binding.llRoot.rv.rv.setLayoutManager(new GridLayoutManager(requireActivity(), 2));
            loadData();
        });
        binding.llRoot.btnP.setOnClickListener(v -> {
            setSelect(3);
            binding.llRoot.rv.rv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            loadData();
        });

        binding.llRoot.btnC.setOnClickListener(v -> {
            setSelect(4);
            customLayoutManager = new FlexboxLayoutManager(requireActivity()){
                @Override
                public RecyclerView.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
                    if (lp instanceof RecyclerView.LayoutParams){
                        return new LayoutParams(lp);
                    }else if (lp instanceof ViewGroup.MarginLayoutParams){
                        return new LayoutParams(lp);
                    }else {
                        return new LayoutParams(lp);
                    }
                }
            };
            customLayoutManager.setFlexWrap(FlexWrap.WRAP);
            customLayoutManager.setFlexDirection(FlexDirection.ROW);
            customLayoutManager.setJustifyContent(JustifyContent.FLEX_START);
            binding.llRoot.rv.rv.setLayoutManager(customLayoutManager);
            loadData();
        });
    }

    private FlexboxLayoutManager customLayoutManager;


    private int layoutType;

    private void setSelect(int pos) {
        layoutType = pos;
        binding.llRoot.btnV.setSelected(pos == 0);
        binding.llRoot.btnH.setSelected(pos == 1);
        binding.llRoot.btnG.setSelected(pos == 2);
        binding.llRoot.btnP.setSelected(pos == 3);
        binding.llRoot.btnC.setSelected(pos == 4);
    }

    private void loadData() {
        MyApplication.cThreadPool.submit(() -> {
            List<ImageEntity> datas = new ArrayList<>();

            String response1 = DataUtils.getFromAssets(requireActivity(), "listview_data.json");
            try {
                JSONArray jsonArray = new JSONArray(response1);
                for (int i = 0; i < jsonArray.length(); i++) {
                    ImageEntity itemData = new ImageEntity();
                    itemData.testBean = new TestBean();
                    itemData.testBean.test="11";
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
        requireActivity().runOnUiThread(() -> binding.llRoot.rv.rv.setAdapter(new RvAdapter(datas)));

    }

    private class RvAdapter extends RecyclerView.Adapter<RvAdapter.MyHolder> {
        List<ImageEntity> datas;
        int[] randoms ;

        public RvAdapter(List<ImageEntity> datas) {
            this.datas = datas;
            randoms = new int[datas.size()];
            for (int i = 0; i < datas.size(); i++) {
                int random = 1 + new Random().nextInt(3);
                randoms[i] = random;
            }
        }

        @NonNull
        @Override
        public RvAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            if (layoutType == 4) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_flex, parent, false);
            }else if (layoutType == 3) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_staggered, parent, false);
            } else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listview, parent, false);
            }
            return new RvAdapter.MyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RvAdapter.MyHolder holder, int position) {
            if (layoutType == 4) {
                ViewGroup.LayoutParams layoutParams = holder.ivImage.getLayoutParams();
                layoutParams.width = (int) ScreenUtils.dp2px(requireActivity(), 86 * randoms[position]);
                holder.ivImage.setLayoutParams(layoutParams);
            }else if (layoutType == 1) {
                ViewGroup.LayoutParams layoutParams = holder.ivImage.getLayoutParams();
                layoutParams.width = (int) ScreenUtils.dp2px(requireActivity(), 100);
                layoutParams.height = (int) ScreenUtils.dp2px(requireActivity(), 100);
                holder.ivImage.setLayoutParams(layoutParams);
            } else if (layoutType == 0 || layoutType == 2) {
                ViewGroup.LayoutParams layoutParams = holder.ivImage.getLayoutParams();
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                layoutParams.height = (int) ScreenUtils.dp2px(requireActivity(), 220);
                holder.ivImage.setLayoutParams(layoutParams);
            }
            MyImageLoader.getInstance().load(holder.ivImage, datas.get(position).getCoverImageUrl(), R.mipmap.img_load_placeholder, R.mipmap.img_load_placeholder);
            holder.ivImage.setOnClickListener(v -> {
                OpenImage openImage = OpenImage.with(TestFragment.this);
                if (layoutType == 4){
                    openImage.setClickRecyclerView(binding.llRoot.rv.rv, new LayoutManagerFindVisiblePosition() {
                        @Override
                        public int findFirstVisibleItemPosition() {
                            return customLayoutManager.findFirstVisibleItemPosition();
                        }

                        @Override
                        public int findLastVisibleItemPosition() {
                            return customLayoutManager.findLastVisibleItemPosition();
                        }
                    }, (data, position1) -> R.id.iv_image);
                }else {
                    openImage.setClickRecyclerView(binding.llRoot.rv.rv, (data, position12) -> R.id.iv_image);
                }
                openImage.setAutoScrollScanPosition(true)
                        .setSrcImageViewScaleType(holder.ivImage.getScaleType(), true)
                        .setImageUrlList(datas)
                        .addPageTransformer(new ScaleInTransformer())
                        .setOpenImageStyle(R.style.DefaultPhotosTheme)
                        .setShowDownload()
                        .setOnExitListener(() -> Toast.makeText(requireActivity(),"onExit",Toast.LENGTH_SHORT).show())
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
