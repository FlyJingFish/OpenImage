package com.flyjingfish.openimage.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.flyjingfish.openimage.MyApplication;
import com.flyjingfish.openimage.R;
import com.flyjingfish.openimage.databinding.ActivityMainBinding;
import com.flyjingfish.openimage.imageloader.MyImageLoader;
import com.flyjingfish.openimage.openImpl.BigImageHelperImpl;
import com.flyjingfish.openimage.openImpl.VideoFragmentCreateImpl;
import com.flyjingfish.openimagelib.ImageFragment;
import com.flyjingfish.openimagelib.OpenImageConfig;
import com.flyjingfish.openimagelib.enums.ImageDiskMode;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setAllowEnterTransitionOverlap(true);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //初始化
        OpenImageConfig.getInstance().setBigImageHelper(new BigImageHelperImpl());
        OpenImageConfig.getInstance().setVideoFragmentCreate(new VideoFragmentCreateImpl());
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
        binding.cbRead.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                OpenImageConfig.getInstance().setReadMode(isChecked);
            }
        });
        binding.rgImageOs.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                MyImageLoader.loader_os_type = (checkedId == R.id.rb_glide?MyImageLoader.GLIDE:MyImageLoader.PICASSO);
                if (checkedId == R.id.rb_picasso){
                    if (binding.rbResult.isChecked()){
                        binding.rbOriginal.setChecked(true);
                        Toast.makeText(MainActivity.this,"Picasso不支持只保存目标大小的图",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        binding.rgCacheType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_original:
                        MyImageLoader.imageDiskMode = ImageDiskMode.CONTAIN_ORIGINAL;
                        break;
                    case R.id.rb_result:
                        if (MyImageLoader.loader_os_type == MyImageLoader.PICASSO){
                            binding.rbGlide.setChecked(true);
                            Toast.makeText(MainActivity.this,"Picasso不支持只保存目标大小的图",Toast.LENGTH_LONG).show();
                        }
                        MyImageLoader.imageDiskMode = ImageDiskMode.RESULT;
                        break;
                    case R.id.rb_none:
                        MyImageLoader.imageDiskMode = ImageDiskMode.NONE;
                        break;
                }
            }
        });
        binding.btnClearGlideCache.setOnClickListener(v -> clearCache());

        binding.btnFriends.setOnClickListener(v -> startActivity(new Intent(this,FriendsActivity.class)));
        binding.btnListview.setOnClickListener(v -> startActivity(new Intent(this, ListViewActivity.class)));
        binding.btnGridview.setOnClickListener(v -> startActivity(new Intent(this, GridViewActivity.class)));
        binding.btnMessage.setOnClickListener(v -> startActivity(new Intent(this, MessageActivity.class)));
        binding.btnRecyclerview.setOnClickListener(v -> startActivity(new Intent(this, RecyclerViewActivity.class)));
        binding.btnIvs.setOnClickListener(v -> startActivity(new Intent(this, ImagesActivity.class)));
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