package com.flyjingfish.openimagelib;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import com.flyjingfish.openimagelib.enums.ImageDiskMode;
import com.flyjingfish.openimagelib.enums.MediaType;
import com.flyjingfish.openimagelib.listener.OnLoadBigImageListener;
import com.flyjingfish.openimagelib.listener.OnLoadCoverImageListener;
import com.flyjingfish.openimagelib.photoview.PhotoView;
import com.flyjingfish.openimagelib.utils.ScreenUtils;

public abstract class BaseImageFragment<T extends View> extends BaseFragment {

    protected PhotoView smallCoverImageView;
    protected PhotoView photoView;
    protected T loadingView;

    protected abstract PhotoView getSmallCoverImageView();

    protected abstract PhotoView getPhotoView();

    protected abstract T getLoadingView();

    protected void showLoading(T pbLoading) {
        pbLoading.setVisibility(View.VISIBLE);
        isLoading = true;
    }

    protected void hideLoading(T pbLoading) {
        pbLoading.setVisibility(View.GONE);
        isLoading = false;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        smallCoverImageView = getSmallCoverImageView();
        photoView = getPhotoView();
        loadingView = getLoadingView();
        smallCoverImageView.setSrcScaleType(srcScaleType);
        photoView.setSrcScaleType(srcScaleType);
        photoView.setStartWidth(imageDetail.srcWidth);
        photoView.setStartHeight(imageDetail.srcHeight);
        smallCoverImageView.setStartWidth(imageDetail.srcWidth);
        smallCoverImageView.setStartHeight(imageDetail.srcHeight);
        smallCoverImageView.setZoomable(false);
        if (imageDetail.getType() != MediaType.IMAGE) {
            photoView.setZoomable(false);
        } else {
            photoView.setZoomable(true);
        }

        showLoading(loadingView);
        loadingView.setVisibility(View.GONE);
        if (ImageLoadUtils.getInstance().getImageLoadSuccess(imageDetail.getImageUrl())
                || imageDiskMode == ImageDiskMode.NONE) {
            smallCoverImageView.setVisibility(View.GONE);
            photoView.setAlpha(1f);
        } else if (imageDiskMode == ImageDiskMode.CONTAIN_ORIGINAL) {
            if (TextUtils.equals(imageDetail.getImageUrl(), imageDetail.getCoverImageUrl())) {
                smallCoverImageView.setVisibility(View.GONE);
                photoView.setAlpha(1f);
            } else {
                if (clickPosition == showPosition && coverDrawable != null) {
                    smallCoverImageView.setImageDrawable(coverDrawable);
                } else {
                    OpenImageConfig.getInstance().getBigImageHelper().loadImage(requireContext(), imageDetail.getCoverImageUrl(), smallCoverImageView);
                }
                smallCoverImageView.setAlpha(1f);
                photoView.setAlpha(0f);
            }
        } else if (imageDiskMode == ImageDiskMode.RESULT && imageDetail.srcWidth != 0 && imageDetail.srcHeight != 0) {
            ViewGroup.LayoutParams layoutParams = smallCoverImageView.getLayoutParams();
            if (srcScaleType == ImageView.ScaleType.CENTER_CROP || srcScaleType == ImageView.ScaleType.FIT_XY) {
                layoutParams.width = imageDetail.srcWidth;
                layoutParams.height = imageDetail.srcHeight;
                smallCoverImageView.setLayoutParams(layoutParams);
                smallCoverImageView.setScaleType(srcScaleType);
            } else if (srcScaleType == ImageView.ScaleType.CENTER) {
                smallCoverImageView.setScaleType(srcScaleType);
            } else if (srcScaleType == ImageView.ScaleType.FIT_CENTER || srcScaleType == ImageView.ScaleType.FIT_START || srcScaleType == ImageView.ScaleType.FIT_END) {
                smallCoverImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
            smallCoverImageView.setVisibility(View.VISIBLE);
            smallCoverImageView.setAlpha(1f);
            photoView.setAlpha(0f);
            if (itemLoadHelper != null) {
                itemLoadHelper.loadImage(requireContext(), imageDetail.openImageUrl, imageDetail.getCoverImageUrl(), smallCoverImageView, imageDetail.srcWidth, imageDetail.srcHeight, new OnLoadCoverImageListener() {
                    @Override
                    public void onLoadImageSuccess() {
                    }

                    @Override
                    public void onLoadImageFailed() {
                        Drawable drawable = smallCoverImageView.getDrawable();
                        if (photoView.getDrawable() == null && drawable != null) {
                            photoView.setImageDrawable(drawable);
                        }
                    }
                });
            }
        } else {
            smallCoverImageView.setVisibility(View.GONE);
            photoView.setAlpha(1f);
        }
        if (clickPosition == showPosition && TextUtils.equals(imageDetail.getImageUrl(), imageDetail.getCoverImageUrl()) && coverDrawable != null) {
            onImageSuccess(coverDrawable);
        } else {
            OpenImageConfig.getInstance().getBigImageHelper().loadImage(requireContext(), imageDetail.getImageUrl(), new OnLoadBigImageListener() {
                @Override
                public void onLoadImageSuccess(Drawable drawable) {
                    onImageSuccess(drawable);
                }

                @Override
                public void onLoadImageFailed() {
                    mHandler.post(() -> {
                        if (isTransitionEnd) {
                            setInitImageError();
                        }

                        isLoadSuccess = false;
                        isInitImage = true;
                    });
                }
            });
        }
        if (!disableClickClose) {
            photoView.setOnClickListener(view1 -> close());
        }
        if (onItemClickListener != null) {
            photoView.setOnClickListener(v -> onItemClickListener.onItemClick(this, openImageUrl, showPosition));
        }
        if (onItemLongClickListener != null) {
            photoView.setOnLongClickListener(v -> {
                onItemLongClickListener.onItemLongClick(this, openImageUrl, showPosition);
                return true;
            });
        }
    }

    private void onImageSuccess(Drawable drawable) {
        mHandler.post(() -> {
            photoView.setImageDrawable(drawable);
            int imageWidth = drawable.getIntrinsicWidth(), imageHeight = drawable.getIntrinsicHeight();
            if (!ImageLoadUtils.getInstance().getImageLoadSuccess(imageDetail.getImageUrl()) && imageDiskMode == ImageDiskMode.RESULT) {
                initCoverAnim(imageWidth, imageHeight, true);
                if (isTransitionEnd && coverAnim != null) {
                    coverAnim.start();
                } else if (!isTransitionEnd) {
                    smallCoverImageView.setVisibility(View.GONE);
                    photoView.setAlpha(1f);
                    isStartCoverAnim = false;
                    loadPrivateImageFinish(true);
                } else {
                    isStartCoverAnim = true;
                }
            } else {
                hideLoading(loadingView);
                smallCoverImageView.setVisibility(View.GONE);
                photoView.setAlpha(1f);
                loadPrivateImageFinish(true);
            }

            isLoadSuccess = true;
            isInitImage = true;
            ImageLoadUtils.getInstance().setImageLoadSuccess(imageDetail.getImageUrl());
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isLoading && !isLoadSuccess && isInitImage) {
            showLoading(loadingView);
            OpenImageConfig.getInstance().getBigImageHelper().loadImage(requireContext(), imageDetail.getImageUrl(), new OnLoadBigImageListener() {
                @Override
                public void onLoadImageSuccess(Drawable drawable) {
                    mHandler.post(() -> {
                        photoView.setImageDrawable(drawable);
                        loadPrivateImageFinish(true);
                        hideLoading(loadingView);
                        isLoadSuccess = true;
                        isInitImage = true;
                        ImageLoadUtils.getInstance().setImageLoadSuccess(imageDetail.getImageUrl());
                    });

                }

                @Override
                public void onLoadImageFailed() {
                    mHandler.post(() -> {
                        hideLoading(loadingView);
                        if (errorResId != 0) {
                            photoView.setImageResource(errorResId);
                        } else {
                            Drawable drawable = smallCoverImageView.getDrawable();
                            if (drawable != null) {
                                photoView.setImageDrawable(drawable);
                            }
                        }
                        smallCoverImageView.setVisibility(View.GONE);
                        photoView.setAlpha(1f);
                        loadPrivateImageFinish(false);

                        isLoadSuccess = false;
                        isInitImage = true;
                    });
                }
            });
        } else if (isLoading && isTransitionEnd) {
            loadingView.setVisibility(View.VISIBLE);
        }
    }

    protected abstract void loadImageFinish(boolean isLoadImageSuccess);

    private void loadPrivateImageFinish(boolean isLoadImageSuccess) {
        loadImageFinish(isLoadImageSuccess);
    }

    @Override
    protected void onTransitionEnd() {
        super.onTransitionEnd();
        if (isInitImage && coverAnim != null && isLoadSuccess && isStartCoverAnim) {
            coverAnim.start();
        } else if (isInitImage && !isLoadSuccess) {
            setInitImageError();
        }
        if (isLoading) {
            loadingView.setVisibility(View.VISIBLE);
        }
        ViewCompat.setTransitionName(photoView, "");
        ViewCompat.setTransitionName(smallCoverImageView, "");
    }

    protected void setInitImageError() {
        hideLoading(loadingView);
        if (errorResId != 0) {
            smallCoverImageView.setVisibility(View.GONE);
            photoView.setImageResource(errorResId);
            photoView.setAlpha(1f);
            loadPrivateImageFinish(false);
        } else {
            Drawable drawable = smallCoverImageView.getDrawable();
            if (drawable != null) {
                photoView.setImageDrawable(drawable);
                initCoverAnim(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), false);
                if (coverAnim != null) {
                    coverAnim.start();
                } else {
                    loadPrivateImageFinish(false);
                }
            } else {
                smallCoverImageView.setVisibility(View.GONE);
                photoView.setAlpha(1f);
                loadPrivateImageFinish(false);
            }
        }

    }

    protected void initCoverAnim(int imageWidth, int imageHeight, final boolean isLoadImageSuccess) {
        if (imageDiskMode == ImageDiskMode.RESULT && (srcScaleType == ImageView.ScaleType.CENTER_CROP || srcScaleType == ImageView.ScaleType.FIT_XY) && imageDetail.srcWidth != 0 && imageDetail.srcHeight != 0) {
            float scaleHW = imageDetail.srcHeight * 1f / imageDetail.srcWidth;
            float originalScaleHW = imageHeight * 1f / imageWidth;
            float coverWidth;
            float coverHeight;
            if (srcScaleType == ImageView.ScaleType.CENTER_CROP) {
                if (originalScaleHW > scaleHW) {//原图高度比点击的ImageView长，缓存的图片是以宽度为准
                    coverWidth = ScreenUtils.getScreenWidth(requireContext());
                    coverHeight = coverWidth * scaleHW;
                } else {//原图高度比点击的ImageView短，缓存的图片是以高度为准
                    float targetWidth = ScreenUtils.getScreenWidth(requireContext());
                    float targetHeight = targetWidth * originalScaleHW;
                    coverHeight = targetHeight;
                    coverWidth = targetHeight / scaleHW;
                }
            } else {
                coverWidth = ScreenUtils.getScreenWidth(requireContext());
                coverHeight = coverWidth * originalScaleHW;
            }
            ObjectAnimator coverAnim1 = ObjectAnimator.ofFloat(smallCoverImageView, "scaleX", 1f, coverWidth * 1f / imageDetail.srcWidth);
            ObjectAnimator coverAnim2 = ObjectAnimator.ofFloat(smallCoverImageView, "scaleY", 1f, coverHeight * 1f / imageDetail.srcHeight);
            ObjectAnimator coverAnim3 = ObjectAnimator.ofFloat(smallCoverImageView, "alpha", 1f, 0f);
            ObjectAnimator vpPhotoAnim1 = ObjectAnimator.ofFloat(photoView, "alpha", 1f, 1f);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(100);
            animatorSet.playTogether(coverAnim1, coverAnim2);
            AnimatorSet animatorSet1 = new AnimatorSet();
            animatorSet1.playTogether(coverAnim3, vpPhotoAnim1);
            animatorSet1.setDuration(100);
            coverAnim = new AnimatorSet();
            coverAnim.playSequentially(animatorSet, animatorSet1);
            coverAnim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    loadPrivateImageFinish(isLoadImageSuccess);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        } else {
            smallCoverImageView.setVisibility(View.GONE);
            photoView.setAlpha(1f);
        }

        hideLoading(loadingView);
    }

    @Override
    public View getExitImageView() {
        smallCoverImageView.setExitMode(true);
        photoView.setExitMode(true);
        if (isLoadSuccess) {
            smallCoverImageView.setVisibility(View.GONE);
            photoView.setVisibility(View.VISIBLE);
            photoView.setAlpha(1f);
            return photoView;
        } else {
            if (smallCoverImageView.getVisibility() == View.VISIBLE && smallCoverImageView.getAlpha() == 1f) {
                return smallCoverImageView;
            } else {
                return photoView;
            }
        }
    }

}
