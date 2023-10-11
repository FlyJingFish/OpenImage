package com.flyjingfish.openimage.openImpl;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.JELLY_BEAN_MR2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.StatFs;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.flyjingfish.openimagelib.listener.OnLoadBigImageListener;
import com.squareup.picasso.Picasso;

import java.io.File;

import okhttp3.Cache;
import okhttp3.HttpUrl;
import okio.ByteString;


public class PicassoLoader {
    private static final String PICASSO_CACHE = "picasso-cache";
    private static final int MIN_DISK_CACHE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final int MAX_DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB

    private final Context context;
    private final String imageUrl;
    private final OnLoadBigImageListener onLoadBigImageListener;
    private final Object tag = new Object();
    private final File cacheDir;
    com.squareup.picasso.Target myTarget = new com.squareup.picasso.Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            myTarget = null;//这句不能删除否则图片加载异常
            if (onLoadBigImageListener != null){
                String cacheFile = getPicassoCacheFile(cacheDir,imageUrl);
                Log.e("picasso-file",new File(cacheFile).exists()+"=="+ cacheFile);
                onLoadBigImageListener.onLoadImageSuccess(new BitmapDrawable(context.getResources(),bitmap),cacheFile);
            }
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
            if (onLoadBigImageListener != null){
                onLoadBigImageListener.onLoadImageFailed();
            }
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };
    public PicassoLoader(Context context, String imageUrl, OnLoadBigImageListener onLoadBigImageListener) {
        this.context = context;
        this.imageUrl = imageUrl;
        this.onLoadBigImageListener = onLoadBigImageListener;
        cacheDir = createDefaultCacheDir(context);
    }

    public void load(){
        boolean isWeb = BitmapUtils.isWeb(imageUrl);
        if (!isWeb){
            boolean isContent = BitmapUtils.isContent(imageUrl);
            int[] size = BitmapUtils.getImageSize(context, imageUrl);
            int[] maxImageSize = BitmapUtils.getMaxImageSize(size[0], size[1]);
            Picasso.get().load((isContent?"":"file://")+imageUrl)
                    .resize(maxImageSize[0],maxImageSize[1]).onlyScaleDown().tag(tag).into(myTarget);
        }else {
            int[] maxImageSize = BitmapUtils.getMaxImageSize(8000,20000);
            Picasso.get().load(imageUrl).resize(maxImageSize[0],maxImageSize[1])
                    .onlyScaleDown().centerInside().tag(tag).into(myTarget);
        }
//        Picasso.get().load(imageUrl).;
        Log.e("picasso-url",""+ urlForDiskName(imageUrl));

        if (context instanceof LifecycleOwner){
            LifecycleOwner owner = (LifecycleOwner) context;
            owner.getLifecycle().addObserver(new LifecycleEventObserver() {
                @Override
                public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                    if (event == Lifecycle.Event.ON_DESTROY){
                        Picasso.get().cancelTag(tag);
                        source.getLifecycle().removeObserver(this);
                    }
                }
            });
        }
    }

    public static String getPicassoCacheFile(File cacheDir,String imageUrl){
        return cacheDir.getAbsolutePath() + "/" + urlForDiskName(imageUrl)+".1";
    }

    public static String urlForDiskName(@NonNull String url) {
        return key(url);
    }

    /**
     *
     * @param url @see {@link Cache#key(HttpUrl)}
     * @return
     */
    public static String key(String url) {
        return ByteString.encodeUtf8(url).md5().hex();
    }

    /**
     * see {@link com.squareup.picasso.Utils#createDefaultCacheDir(Context)}
     * @param context
     * @return
     */
    public static File createDefaultCacheDir(Context context) {
        File cache = new File(context.getApplicationContext().getCacheDir(), PICASSO_CACHE);
        if (!cache.exists()) {
            //noinspection ResultOfMethodCallIgnored
            cache.mkdirs();
        }
        return cache;
    }


    /**
     * see {@link com.squareup.picasso.Utils#calculateDiskCacheSize(File)}
     * @param dir
     * @return
     */
    public static long calculateDiskCacheSize(File dir) {
        long size = MIN_DISK_CACHE_SIZE;

        try {
            StatFs statFs = new StatFs(dir.getAbsolutePath());
            //noinspection deprecation
            long blockCount =
                    SDK_INT < JELLY_BEAN_MR2 ? (long) statFs.getBlockCount() : statFs.getBlockCountLong();
            //noinspection deprecation
            long blockSize =
                    SDK_INT < JELLY_BEAN_MR2 ? (long) statFs.getBlockSize() : statFs.getBlockSizeLong();
            long available = blockCount * blockSize;
            // Target 2% of the total space.
            size = available / 50;
        } catch (IllegalArgumentException ignored) {
        }

        // Bound inside min/max size for disk cache.
        return Math.max(Math.min(size, MAX_DISK_CACHE_SIZE), MIN_DISK_CACHE_SIZE);
    }
}
