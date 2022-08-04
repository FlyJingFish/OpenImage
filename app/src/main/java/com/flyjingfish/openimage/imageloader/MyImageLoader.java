package com.flyjingfish.openimage.imageloader;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.flyjingfish.openimage.GlideApp;
import com.flyjingfish.openimage.MyApplication;
import com.flyjingfish.openimagelib.enums.ImageDiskMode;
import com.flyjingfish.openimagelib.utils.ActivityCompatHelper;
import com.flyjingfish.openimagelib.utils.ScreenUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class MyImageLoader {
    public static final int GLIDE = 1;
    public static final int PICASSO = 2;
    public static int loader_os_type = GLIDE;
    public static ImageDiskMode imageDiskMode = ImageDiskMode.CONTAIN_ORIGINAL;
    private MyImageLoader() {
    }

    private static MyImageLoader mInstance;

    public static MyImageLoader getInstance() {
        if (null == mInstance) {
            synchronized (MyImageLoader.class) {
                if (null == mInstance) {
                    mInstance = new MyImageLoader();
                }
            }
        }
        return mInstance;
    }

    public void load(ImageView iv, String url, @DrawableRes int p, @DrawableRes int err) {
        if (!ActivityCompatHelper.assertValidRequest(iv.getContext()))
            return;
        into(url, iv, 0, 0, p, err, false, -1, false);
    }

    public void load(ImageView iv, String url, int w, int h, @DrawableRes int p, @DrawableRes int err,OnImageLoadListener requestListener) {
        if (!ActivityCompatHelper.assertValidRequest(iv.getContext()))
            return;
        into(url, iv, w, h, p, err, false, -1, false,requestListener);
    }

    public void loadRoundCorner(ImageView iv, String url, float radiusDp, @DrawableRes int p, @DrawableRes int err) {
        if (!ActivityCompatHelper.assertValidRequest(iv.getContext()))
            return;
        into(url, iv, 0, 0, p, err, false, radiusDp, false);
    }

    public void loadRoundCorner(ImageView iv, String url, float radiusDp, int w, int h, @DrawableRes int p, @DrawableRes int err,OnImageLoadListener requestListener) {
        if (!ActivityCompatHelper.assertValidRequest(iv.getContext()))
            return;
        into(url, iv, w, h, p, err, false, radiusDp, false,requestListener);
    }

    private void into(String url, ImageView iv, int w, int h, @DrawableRes int p, @DrawableRes int err, boolean isCircle, float radiusDp, boolean isBlur) {
        into(url, iv, w, h, p, err, isCircle, radiusDp, isBlur,null);
    }

    private void into(String url,ImageView iv, int w, int h, @DrawableRes int p, @DrawableRes int err, boolean isCircle, float radiusDp, boolean isBlur, OnImageLoadListener requestListener) {
        if (loader_os_type == GLIDE){
            RequestBuilder<Drawable> requestBuilder = GlideApp.with(iv).load(url);
            if (isBlur || isCircle || radiusDp != -1) {
                Transformation[] transformations = new Transformation[0];
                if (isBlur && !isCircle && radiusDp == -1) {
                    transformations = new Transformation[]{new CenterCrop(), new BlurTransformation()};
                } else if (isBlur && isCircle && radiusDp == -1) {
                    transformations = new Transformation[]{new CenterCrop(), new BlurTransformation(), new CircleCrop()};
                } else if (isBlur && !isCircle && radiusDp != -1) {
                    transformations = new Transformation[]{new CenterCrop(), new BlurTransformation(), new RoundedCorners(dp2px(radiusDp))};
                } else if (!isBlur && isCircle && radiusDp == -1) {
                    transformations = new Transformation[]{new CenterCrop(), new CircleCrop()};
                } else if (!isBlur && !isCircle && radiusDp != -1) {
                    transformations = new Transformation[]{new CenterCrop(), new RoundedCorners(dp2px(radiusDp))};
                }
                RequestOptions mRequestOptions = new RequestOptions().transform(transformations);

                requestBuilder.apply(mRequestOptions);
                if (w > 0 && h > 0)
                    mRequestOptions.override(w, h);
            } else if (w > 0 && h > 0) {
                requestBuilder.override(w, h);
            } else if (w == Target.SIZE_ORIGINAL && h == Target.SIZE_ORIGINAL) {
                requestBuilder.override(w, h);
            }
            if (imageDiskMode == ImageDiskMode.RESULT){
                requestBuilder.diskCacheStrategy(DiskCacheStrategy.RESOURCE);
            }if (imageDiskMode == ImageDiskMode.CONTAIN_ORIGINAL){
                requestBuilder.diskCacheStrategy(DiskCacheStrategy.ALL);
            }else if (imageDiskMode == ImageDiskMode.NONE){
                requestBuilder.diskCacheStrategy(DiskCacheStrategy.NONE);
            }
            if (p != -1)
                requestBuilder.placeholder(p);
            if (err != -1)
                requestBuilder.error(err);
            if (requestListener != null){
                requestBuilder.addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        requestListener.onFailed();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        requestListener.onSuccess();
                        return false;
                    }
                });
            }
            requestBuilder.into(iv);
        }else if (loader_os_type == PICASSO){
            RequestCreator requestCreator = Picasso.get().load(url);
            if (isBlur || isCircle || radiusDp != -1) {
                List<com.squareup.picasso.Transformation> transformations = new ArrayList<>();
                requestCreator.centerCrop();
                if (isBlur && !isCircle && radiusDp == -1) {
                    transformations.add(new jp.wasabeef.picasso.transformations.BlurTransformation(iv.getContext()));
                } else if (isBlur && isCircle && radiusDp == -1) {
                    transformations.add(new jp.wasabeef.picasso.transformations.BlurTransformation(iv.getContext()));
                    transformations.add(new jp.wasabeef.picasso.transformations.CropCircleTransformation());
                } else if (isBlur && !isCircle && radiusDp != -1) {
                    transformations.add(new jp.wasabeef.picasso.transformations.BlurTransformation(iv.getContext()));
                    transformations.add(new jp.wasabeef.picasso.transformations.RoundedCornersTransformation(dp2px(radiusDp),0, RoundedCornersTransformation.CornerType.ALL));
                } else if (!isBlur && isCircle && radiusDp == -1) {
                    transformations.add(new jp.wasabeef.picasso.transformations.CropCircleTransformation());
                } else if (!isBlur && !isCircle && radiusDp != -1) {
                    transformations.add(new jp.wasabeef.picasso.transformations.RoundedCornersTransformation(dp2px(radiusDp),0, RoundedCornersTransformation.CornerType.ALL));
                }
                if (transformations.size() >0){
                    requestCreator.transform(transformations);
                }
                requestCreator.resize(200, 200);

            } else if (w > 0 && h > 0) {
                requestCreator.centerCrop();
                requestCreator.resize(w, h);
            }
            if (imageDiskMode == ImageDiskMode.NONE){
                requestCreator.networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE);
            }


            if (p != -1)
                requestCreator.placeholder(p);
            if (err != -1)
                requestCreator.error(err);

            if (requestListener != null){
                requestCreator.into(iv, new Callback() {
                    @Override
                    public void onSuccess() {
                        requestListener.onSuccess();
                    }

                    @Override
                    public void onError(Exception e) {
                        requestListener.onFailed();
                    }
                });
            }else {
                requestCreator.into(iv);
            }
        }
    }

    public interface OnImageLoadListener{
        void onSuccess();
        void onFailed();
    }

    public static int dp2px(float value) {
        return (int) ScreenUtils.dp2px(MyApplication.mInstance,value);
    }

}
