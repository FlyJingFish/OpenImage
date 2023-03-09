package com.flyjingfish.openimage.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.flyjingfish.openimage.MyApplication;
import com.flyjingfish.openimage.R;
import com.flyjingfish.openimage.databinding.ActivityMainBinding;
import com.flyjingfish.openimage.imageloader.MyImageLoader;
import com.flyjingfish.openimagelib.OpenImageConfig;
import com.flyjingfish.openimagelib.enums.ImageDiskMode;

public class MainActivity extends BaseActivity {
    private ActivityMainBinding binding;
    //  "/storage/emulated/0/Tencent/QQ_Images/a62d96cb210ccb9.jpg",
    //  "content://media/external/images/media/1735525",

    @Override
    public boolean isShowBack() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setAllowEnterTransitionOverlap(true);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (MyImageLoader.loader_os_type == MyImageLoader.GLIDE){
            binding.rbGlide.setChecked(true);
        }else {
            binding.rbPicasso.setChecked(true);
        }
        if (MyImageLoader.imageDiskMode == ImageDiskMode.CONTAIN_ORIGINAL){
            binding.rbOriginal.setChecked(true);
        }else if (MyImageLoader.imageDiskMode == ImageDiskMode.RESULT){
            binding.rbResult.setChecked(true);
        }else {
            binding.rbNone.setChecked(true);
        }
        binding.cbRead.setChecked(OpenImageConfig.getInstance().isReadMode());
        binding.cbRead.setOnCheckedChangeListener((buttonView, isChecked) -> OpenImageConfig.getInstance().setReadMode(isChecked));
        binding.rgImageOs.setOnCheckedChangeListener((group, checkedId) -> MyImageLoader.loader_os_type = (checkedId == R.id.rb_glide?MyImageLoader.GLIDE:MyImageLoader.PICASSO));
        binding.rgCacheType.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId){
                case R.id.rb_original:
                    MyImageLoader.imageDiskMode = ImageDiskMode.CONTAIN_ORIGINAL;
                    break;
                case R.id.rb_result:
                    MyImageLoader.imageDiskMode = ImageDiskMode.RESULT;
                    break;
                case R.id.rb_none:
                    MyImageLoader.imageDiskMode = ImageDiskMode.NONE;
                    break;
            }
        });
        binding.btnClearGlideCache.setOnClickListener(v -> clearCache());

        binding.btnFriends.setOnClickListener(v -> jump(v,FriendsActivity.class));
        binding.btnListview.setOnClickListener(v -> jump(v, ListViewActivity.class));
        binding.btnGridview.setOnClickListener(v -> jump(v, GridViewActivity.class));
        binding.btnMessage.setOnClickListener(v -> jump(v, MessageActivity.class));
        binding.btnRecyclerview.setOnClickListener(v -> jump(v, RecyclerViewActivity.class));
        binding.btnIvs.setOnClickListener(v -> jump(v, ImagesActivity.class));
        binding.btnViewPager.setOnClickListener(v -> jump(v, ViewPagerDemoActivity.class));
        binding.btnScaleType.setOnClickListener(v -> jump(v, ScaleTypeActivity.class));
    }

    private void jump(View v, Class<?> cls){
        Intent intent = new Intent(this,cls);
        intent.putExtra(TITLE, ((TextView) v).getText().toString());
        startActivity(intent);
    }

    private void clearCache(){
        Glide.get(MainActivity.this).clearMemory();
        MyApplication.cThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                Glide.get(MainActivity.this).clearDiskCache();
            }
        });

    }

}