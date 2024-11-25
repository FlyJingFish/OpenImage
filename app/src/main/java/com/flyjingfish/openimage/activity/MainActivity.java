package com.flyjingfish.openimage.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.danikula.videocache.StorageUtils;
import com.flyjingfish.openimage.MyApplication;
import com.flyjingfish.openimage.R;
import com.flyjingfish.openimage.databinding.ActivityMainBinding;
import com.flyjingfish.openimage.imageloader.MyImageLoader;
import com.flyjingfish.openimage.openImpl.AppDownloadFileHelper;
import com.flyjingfish.openimage.openImpl.AppGlideBigImageHelper;
import com.flyjingfish.openimage.openImpl.PicassoDownloader;
import com.flyjingfish.openimage.openImpl.PicassoLoader;
import com.flyjingfish.openimagecoillib.Coil3BigImageHelper;
import com.flyjingfish.openimagecoillib.Coil3DownloadMediaHelper;
import com.flyjingfish.openimagecoillib.CoilBigImageHelper;
import com.flyjingfish.openimagecoillib.CoilDownloadMediaHelper;
import com.flyjingfish.openimagefulllib.FullGlideDownloadMediaHelper;
import com.flyjingfish.openimageglidelib.GlideBigImageHelper;
import com.flyjingfish.openimageglidelib.GlideDownloadMediaHelper;
import com.flyjingfish.openimagelib.OpenImageConfig;
import com.flyjingfish.openimagelib.utils.ActivityCompatHelper;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import coil.Coil;
import coil3.ImageLoadersKt;
import coil3.SingletonImageLoaders_androidKt;

public class MainActivity extends BaseActivity {
    private ActivityMainBinding binding;
    //  "/storage/emulated/0/Tencent/QQ_Images/a62d96cb210ccb9.jpg",
    //  "content://media/external/images/media/1735525",
    //  "/storage/emulated/0/Tencent/QQ_Images/7ff9ceb717b1dafc.jpg",
//"https://mirror.aarnet.edu.au/pub/TED-talks/911Mothers_2010W-480p.mp4"
    @Override
    public boolean isShowBack() {
        return false;
    }

    @Override
    public String getTitleString() {
        return getString(R.string.app_name);
    }

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setAllowEnterTransitionOverlap(true);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (MyImageLoader.loader_os_type == MyImageLoader.GLIDE) {
            binding.rbGlide.setChecked(true);
        } else if (MyImageLoader.loader_os_type == MyImageLoader.COIL){
            binding.rbCoil.setChecked(true);
        } else{
            binding.rbPicasso.setChecked(true);
        }
        if (MyImageLoader.imageDiskMode == MyImageLoader.ImageDiskMode.CONTAIN_ORIGINAL) {
            binding.rbOriginal.setChecked(true);
        } else if (MyImageLoader.imageDiskMode == MyImageLoader.ImageDiskMode.RESULT) {
            binding.rbResult.setChecked(true);
        } else {
            binding.rbNone.setChecked(true);
        }
        binding.cbRead.setChecked(OpenImageConfig.getInstance().isReadMode());
        binding.cbRead.setOnCheckedChangeListener((buttonView, isChecked) -> OpenImageConfig.getInstance().setReadMode(isChecked));
        binding.rgImageOs.setOnCheckedChangeListener((group, checkedId) -> {
            MyImageLoader.loader_os_type = (checkedId == R.id.rb_glide ? MyImageLoader.GLIDE : (checkedId == R.id.rb_coil ? MyImageLoader.COIL : MyImageLoader.PICASSO));
            if (MyImageLoader.loader_os_type == MyImageLoader.GLIDE){
                OpenImageConfig.getInstance().setDownloadMediaHelper(FullGlideDownloadMediaHelper.getInstance());
                FullGlideDownloadMediaHelper.getInstance().setDefaultDownloadMediaHelper(new GlideDownloadMediaHelper());
                OpenImageConfig.getInstance().setBigImageHelper(new GlideBigImageHelper());
            }else if (MyImageLoader.loader_os_type == MyImageLoader.COIL){
                boolean isCoil3;
                try {
                    SingletonImageLoaders_androidKt.getImageLoader(this);
                    isCoil3 = true;
                } catch (NoClassDefFoundError e) {
                    isCoil3 = false;
                }
                OpenImageConfig.getInstance().setDownloadMediaHelper(FullGlideDownloadMediaHelper.getInstance());
                if (isCoil3){
                    FullGlideDownloadMediaHelper.getInstance().setDefaultDownloadMediaHelper(new Coil3DownloadMediaHelper());
                    OpenImageConfig.getInstance().setBigImageHelper(new Coil3BigImageHelper());
                }else {
                    FullGlideDownloadMediaHelper.getInstance().setDefaultDownloadMediaHelper(new CoilDownloadMediaHelper());
                    OpenImageConfig.getInstance().setBigImageHelper(new CoilBigImageHelper());
                }

            }else {
                OpenImageConfig.getInstance().setDownloadMediaHelper(new AppDownloadFileHelper());
                OpenImageConfig.getInstance().setBigImageHelper(new AppGlideBigImageHelper());
            }
        });
        binding.rgCacheType.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rb_original:
                    MyImageLoader.imageDiskMode = MyImageLoader.ImageDiskMode.CONTAIN_ORIGINAL;
                    break;
                case R.id.rb_result:
                    MyImageLoader.imageDiskMode = MyImageLoader.ImageDiskMode.RESULT;
                    break;
                case R.id.rb_none:
                    MyImageLoader.imageDiskMode = MyImageLoader.ImageDiskMode.NONE;
                    break;
            }
        });
        binding.btnClearGlideCache.setOnClickListener(v -> clearCache());

        binding.btnFriends.setOnClickListener(v -> jump(v, FriendsActivity.class));
        binding.btnListview.setOnClickListener(v -> jump(v, ListViewActivity.class));
        binding.btnGridview.setOnClickListener(v -> jump(v, GridViewActivity.class));
        binding.btnMessage.setOnClickListener(v -> jump(v, MessageActivity.class));
        binding.btnRecyclerview.setOnClickListener(v -> jump(v, RecyclerViewActivity.class));
        binding.btnIvs.setOnClickListener(v -> jump(v, ImagesActivity.class));
        binding.btnViewPager.setOnClickListener(v -> jump(v, ViewPagerDemoActivity.class));
        binding.btnScaleType.setOnClickListener(v -> jump(v, ScaleTypeActivity.class));
        binding.btnConner.setOnClickListener(v -> jump(v, ConnerActivity.class));
        binding.btnCustom.setOnClickListener(v -> jump(v, CustomActivity.class));
        binding.btnKuaishou.setOnClickListener(v -> jump(v, KuaiShouDemoActivity.class));
        binding.btnWebView.setOnClickListener(v -> jump(v, WebViewActivity.class));
        binding.btnUserDetail.setOnClickListener(v -> jump(v, UserDetailListActivity.class));
        binding.btnMemoryTest.setOnClickListener(v -> jump(v, MemoryTestActivity.class));
        binding.btnMemoryTest.setVisibility(ActivityCompatHelper.isApkInDebug(this)?View.VISIBLE:View.GONE);
        binding.btnPhotoView.setOnClickListener(v -> jump(v, PhotoViewActivity.class));
        binding.btnPhotoView.setVisibility(ActivityCompatHelper.isApkInDebug(this)?View.VISIBLE:View.GONE);
    }

    private void jump(View v, Class<?> cls) {
        Intent intent = new Intent(this, cls);
        intent.putExtra(TITLE, ((TextView) v).getText().toString());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    private void clearCache() {

        try {
            Coil.imageLoader(this).getMemoryCache().clear();
        } catch (Exception e) {
        }
        Glide.get(MainActivity.this).clearMemory();
        clearPicassoMemory();
        MyApplication.cThreadPool.submit(() -> {
            try {
                Coil.imageLoader(this).getDiskCache().clear();
            } catch (Exception e) {
            }
            Glide.get(MainActivity.this).clearDiskCache();
            clearPicassoCache();
            clearPicassoVideoCache();
            clearVideoCache();
            handler.post(() -> Toast.makeText(MainActivity.this,"清理缓存完成",Toast.LENGTH_SHORT).show());
        });

    }

    private void clearPicassoMemory(){
        Picasso picasso = Picasso.get();
        try {
            Class c = picasso.getClass();
            Field cacheField = c.getDeclaredField("cache");
            cacheField.setAccessible(true);
            Object obj = cacheField.get(picasso);
            Class c2 = LruCache.class;
            Method method = c2.getMethod("clear");
            method.setAccessible(true);
            method.invoke(obj);
        } catch (NoSuchFieldException | InvocationTargetException | IllegalAccessException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private void clearVideoCache(){
        String path = StorageUtils.getIndividualCacheDirectory
                (getApplicationContext()).getAbsolutePath()
                + File.separator;
        deleteDirectory(new File(path));
    }
    private void clearPicassoCache(){
        deleteDirectory(PicassoLoader.createDefaultCacheDir(getApplication()));
    }

    private void clearPicassoVideoCache(){
        deleteDirectory(PicassoDownloader.createVideoCacheDir(getApplication()));
    }

    private void deleteDirectory(File tempFile) {
        try {
            if (!tempFile.exists()) return;
            if (tempFile.isDirectory()) {
                File[] files = tempFile.listFiles();
                if (files == null || files.length == 0) {
                    tempFile.delete();
                    return;
                }
                for (File file : files) {
                    if (file.isFile()) {
                        file.delete();
                    } else if (file.isDirectory()) {
                        deleteDirectory(file);
                    }
                }
                tempFile.delete();
            } else {
                tempFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}