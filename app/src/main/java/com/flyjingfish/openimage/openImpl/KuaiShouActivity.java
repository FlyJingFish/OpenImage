package com.flyjingfish.openimage.openImpl;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.flyjingfish.openimage.DataUtils;
import com.flyjingfish.openimage.MyApplication;
import com.flyjingfish.openimage.R;
import com.flyjingfish.openimage.bean.MessageBean;
import com.flyjingfish.openimage.databinding.ItemMsgTextBinding;
import com.flyjingfish.openimage.databinding.MyActivityKuaishouBinding;
import com.flyjingfish.openimage.dialog.BaseInputDialog;
import com.flyjingfish.openimage.dialog.InputDialog;
import com.flyjingfish.openimagelib.OpenImageActivity;
import com.flyjingfish.openimagelib.utils.ScreenUtils;
import com.flyjingfish.openimagelib.widget.TouchCloseLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class KuaiShouActivity extends OpenImageActivity {

    private MyActivityKuaishouBinding rootBinding;
    public static final String BUNDLE_DATA_KEY = "bundle_data";
    public static final String MY_DATA_KEY = "my_data";
    private BottomSheetBehavior behavior;
    private KuaishouViewModel kuaishouViewModel;

    @Override
    public View getContentView() {
        rootBinding = MyActivityKuaishouBinding.inflate(getLayoutInflater());
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
        return rootBinding.touchCloseLayout;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kuaishouViewModel = new ViewModelProvider(this).get(KuaishouViewModel.class);
        kuaishouViewModel.clickLikeLiveData.observe(this, aBoolean -> {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) rootBinding.tvTop.getLayoutParams();
        layoutParams.topMargin = ScreenUtils.getStatusBarHeight(this);
        rootBinding.tvTop.setLayoutParams(layoutParams);
        behavior = BottomSheetBehavior.from(rootBinding.rlComment);
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        final int height = (int) ScreenUtils.dp2px(KuaiShouActivity.this, 400);
        final int startHeight = (int) ScreenUtils.dp2px(KuaiShouActivity.this, 55);
        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        rootBinding.vTouch.setVisibility(View.VISIBLE);
                        rootBinding.touchCloseLayout.setDisEnableTouchClose(true);
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        rootBinding.vTouch.setVisibility(View.GONE);
                        rootBinding.touchCloseLayout.setDisEnableTouchClose(false);
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.e("onSlide","=="+(1+slideOffset));
                int endHeight = (int) (startHeight + (1+slideOffset)*(height-startHeight));
                setViewHeight(rootBinding.vComment,endHeight);
                rootBinding.tvTop.setTranslationY(-(1+slideOffset)*(height-startHeight));
                kuaishouViewModel.btnsTranslationYLiveData.setValue(-(1+slideOffset)*(height-startHeight)/2);
                kuaishouViewModel.btnsAlphaLiveData.setValue(-slideOffset);

            }
        });
        rootBinding.vTouch.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN){
                behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
            return true;
        });
        rootBinding.tvCommentClose.setOnClickListener(v -> {
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        });

        addOnItemClickListener((fragment, openImageUrl, position) -> {
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        });

        addOnSelectMediaListener((openImageUrl, position) -> {
            rootBinding.tvTop.setText("标题呦～（第"+position+"个图)");
        });
        rootBinding.llMu.setOnClickListener(v -> {
            InputDialog inputDialog = InputDialog.getDialog(rootBinding.tvMu.getText().toString());
            inputDialog.setOnContentCallBack(new BaseInputDialog.OnContentCallBack() {
                @Override
                public void onSendContent(String content) {

                }

                @Override
                public void onContent(String content) {
                    rootBinding.tvMu.setText(content);
                }
            });
            inputDialog.show(getSupportFragmentManager(),"inputDialog");
        });

        rootBinding.llCommentBottom.setOnClickListener(v -> {
            InputDialog inputDialog = InputDialog.getDialog(rootBinding.tvComment.getText().toString());
            inputDialog.setOnContentCallBack(new BaseInputDialog.OnContentCallBack() {
                @Override
                public void onSendContent(String content) {

                }

                @Override
                public void onContent(String content) {
                    rootBinding.tvComment.setText(content);
                }
            });
            inputDialog.show(getSupportFragmentManager(),"inputDialog");
        });

        RvAdapter rvAdapter = new RvAdapter();
        rootBinding.rvComment.setAdapter(rvAdapter);
        rootBinding.rvComment.setLayoutManager(new LinearLayoutManager(this));

        rootBinding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                //加载更多网络数据
                if (position > openImageAdapter.getItemCount()-3){
                    getNetworkMoreData();
                }
            }
        });

        rootBinding.tvTop.setOnClickListener(v -> {
            close(false);
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void setViewHeight(View view ,int height){
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = height;
        view.setLayoutParams(layoutParams);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && behavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void getNetworkMoreData() {
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
        runOnUiThread(() -> {
            openImageAdapter.addData(datas);
        });

    }

    private static class RvAdapter extends RecyclerView.Adapter<RvAdapter.MyHolder> {

        @NonNull
        @Override
        public RvAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_msg_text, parent, false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RvAdapter.MyHolder holder, int position) {
            ItemMsgTextBinding binding = ItemMsgTextBinding.bind(holder.itemView);
            binding.tvText.setText("第"+position+"条评论");
        }

        @Override
        public int getItemCount() {
            return 20;
        }

        static class MyHolder extends RecyclerView.ViewHolder{

            public MyHolder(@NonNull View itemView) {
                super(itemView);
            }
        }
    }
}
