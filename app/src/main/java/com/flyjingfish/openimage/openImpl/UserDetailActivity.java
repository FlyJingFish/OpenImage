package com.flyjingfish.openimage.openImpl;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.flyjingfish.openimage.R;
import com.flyjingfish.openimage.bean.User;
import com.flyjingfish.openimage.databinding.MyActivityUserDetailBinding;
import com.flyjingfish.openimagelib.BaseInnerFragment;
import com.flyjingfish.openimagelib.OpenImage;
import com.flyjingfish.openimagelib.OpenImageActivity;
import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.listener.OnItemClickListener;
import com.flyjingfish.openimagelib.listener.SourceImageViewIdGet;
import com.flyjingfish.openimagelib.widget.TouchCloseLayout;

public class UserDetailActivity extends OpenImageActivity {

    private MyActivityUserDetailBinding rootBinding;
    public static final String BUNDLE_DATA_KEY = "bundle_data";
    public static final String MY_DATA_KEY = "my_data";
    private User user;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public View getContentView() {
        rootBinding = MyActivityUserDetailBinding.inflate(getLayoutInflater());
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
        return rootBinding.touchLayout;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getBundleExtra(BUNDLE_DATA_KEY);
        user = (User) bundle.getSerializable(MY_DATA_KEY);
        rootBinding.tvName.setText(user.name);
        rootBinding.tvTitle.setOnClickListener(v -> close(false));
        addOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseInnerFragment fragment, OpenImageUrl openImageUrl, int position) {
                Log.e("MyBigImageActivity","onItemClick");
                //依旧能再打开相册，就问你6不6
                OpenImage.with(UserDetailActivity.this)
                        .setClickViewPager2(getViewPager2(), new SourceImageViewIdGet<OpenImageUrl>() {
                            @Override
                            public int getImageViewId(OpenImageUrl data, int position) {
                                return R.id.photo_view;
                            }
                        })
                        .setSrcImageViewScaleType(ImageView.ScaleType.CENTER_CROP, true)
                        .setOpenImageStyle(R.style.DefaultPhotosTheme)
                        .setImageUrlList(openImageAdapter.getData())
                        .setClickPosition(position)
                        .setAutoScrollScanPosition(true)
                        .show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onShareTransitionEnd() {
        super.onShareTransitionEnd();
        //在这里执行之后才可以更新数据，否则可能显示有瑕疵
        handler.postDelayed(() -> {
            openImageAdapter.addData(user.photos.subList(1,user.photos.size()));
        },50);


    }
}
