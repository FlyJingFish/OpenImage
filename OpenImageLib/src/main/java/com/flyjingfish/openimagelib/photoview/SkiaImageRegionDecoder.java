package com.flyjingfish.openimagelib.photoview;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class SkiaImageRegionDecoder implements ImageRegionDecoder {

    private BitmapRegionDecoder decoder;
    private final ReadWriteLock decoderLock = new ReentrantReadWriteLock(true);

    public static final String FILE_PREFIX = "file://";
    public static final String ASSET_PREFIX = FILE_PREFIX + "/android_asset/";
    public static final String RESOURCE_PREFIX = ContentResolver.SCHEME_ANDROID_RESOURCE + "://";

    private static final String FILE_SCHEME = "file:///";
    private static final String ASSET_SCHEME = "file:///android_asset/";

    private final Bitmap.Config bitmapConfig;
    private boolean isInit;

    @Keep
    @SuppressWarnings("unused")
    public SkiaImageRegionDecoder() {
        this(null);
    }

    @SuppressWarnings({"WeakerAccess", "SameParameterValue"})
    public SkiaImageRegionDecoder(@Nullable Bitmap.Config bitmapConfig) {
        if (bitmapConfig != null) {
            this.bitmapConfig = bitmapConfig;
        } else {
            this.bitmapConfig = Bitmap.Config.RGB_565;
        }
    }

    @Override
    @NonNull
    public Point init(Context context, @NonNull Uri uri) throws Exception {
        String uriString = uri.toString();
        if (uriString.startsWith(RESOURCE_PREFIX)) {
            Resources res;
            String packageName = uri.getAuthority();
            if (context.getPackageName().equals(packageName)) {
                res = context.getResources();
            } else {
                PackageManager pm = context.getPackageManager();
                res = pm.getResourcesForApplication(packageName);
            }

            int id = 0;
            List<String> segments = uri.getPathSegments();
            int size = segments.size();
            if (size == 2 && segments.get(0).equals("drawable")) {
                String resName = segments.get(1);
                id = res.getIdentifier(resName, "drawable", packageName);
            } else if (size == 1 && TextUtils.isDigitsOnly(segments.get(0))) {
                try {
                    id = Integer.parseInt(segments.get(0));
                } catch (NumberFormatException ignored) {
                }
            }

            decoder = BitmapRegionDecoder.newInstance(context.getResources().openRawResource(id), false);
        } else if (uriString.startsWith(ASSET_PREFIX)) {
            String assetName = uriString.substring(ASSET_PREFIX.length());
            decoder = BitmapRegionDecoder.newInstance(context.getAssets().open(assetName, AssetManager.ACCESS_RANDOM), false);
        } else if (uriString.startsWith(FILE_PREFIX)) {
            decoder = BitmapRegionDecoder.newInstance(uriString.substring(FILE_PREFIX.length()), false);
        } else {
            InputStream inputStream = null;
            try {
                ContentResolver contentResolver = context.getContentResolver();
                inputStream = contentResolver.openInputStream(uri);
                if (inputStream == null) {
                    throw new Exception("Content resolver returned null stream. Unable to initialise with uri.");
                }
                decoder = BitmapRegionDecoder.newInstance(inputStream, false);
            } finally {
                if (inputStream != null) {
                    try { inputStream.close(); } catch (Exception e) { /* Ignore */ }
                }
            }
        }
        isInit = true;
        return new Point(decoder.getWidth(), decoder.getHeight());
    }

    public boolean isInit() {
        return isInit;
    }

    @Override
    @NonNull
    public Bitmap decodeRegion(@NonNull Rect sRect, int sampleSize) {
        getDecodeLock().lock();
        try {
            if (decoder != null && !decoder.isRecycled()) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = sampleSize;
                options.inPreferredConfig = bitmapConfig;
                Bitmap bitmap = decoder.decodeRegion(sRect, options);
                if (bitmap == null) {
                    throw new RuntimeException("Skia image decoder returned null bitmap - image format may not be supported");
                }
                return bitmap;
            } else {
                throw new IllegalStateException("Cannot decode region after decoder has been recycled");
            }
        } finally {
            getDecodeLock().unlock();
        }
    }

    @Override
    public synchronized boolean isReady() {
        return decoder != null && !decoder.isRecycled();
    }

    @Override
    public synchronized void recycle() {
        decoderLock.writeLock().lock();
        try {
            decoder.recycle();
            decoder = null;
        } finally {
            decoderLock.writeLock().unlock();
        }
    }

    private Lock getDecodeLock() {
        return decoderLock.readLock();
    }


    public static Uri stringToUri(@NonNull String uri) {
        if (!uri.contains("://")) {
            if (uri.startsWith("/")) {
                uri = uri.substring(1);
            }
            uri = FILE_SCHEME + uri;
        }
        return getImageUri(Uri.parse(uri));
    }

    private static Uri getImageUri(@NonNull Uri uri) {
        String uriString = uri.toString();
        if (uriString.startsWith(FILE_SCHEME)) {
            File uriFile = new File(uriString.substring(FILE_SCHEME.length() - 1));
            if (!uriFile.exists()) {
                try {
                    uri = Uri.parse(URLDecoder.decode(uriString, "UTF-8"));
                } catch (UnsupportedEncodingException ignored) {
                }
            }
        }
        return uri;
    }
}
