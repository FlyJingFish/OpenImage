package com.flyjingfish.openimage.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flyjingfish.openimage.bean.ImageEntity;
import com.flyjingfish.openimage.bean.ImageItem;
import com.flyjingfish.openimage.imageloader.MyImageLoader;
import com.flyjingfish.openimage.R;
import com.flyjingfish.openimage.databinding.ItemImageBinding;
import com.flyjingfish.openimage.openImpl.FriendLayerFragmentCreateImpl;
import com.flyjingfish.openimagelib.OpenImage;
import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.listener.ItemLoadHelper;
import com.flyjingfish.openimagelib.listener.OnLoadCoverImageListener;
import com.flyjingfish.openimagelib.listener.SourceImageViewIdGet;
import com.flyjingfish.openimagelib.utils.ScreenUtils;

import java.util.List;

public class FriendImageAdapter extends RecyclerView.Adapter<RvBaseHolder> {
    private List<ImageEntity> data;
    private ImageItem imageItem;
    private int spanCount;

    public FriendImageAdapter(ImageItem imageItem, int spanCount) {
        this.imageItem = imageItem;
        this.data = imageItem.images;
        this.spanCount = spanCount;
    }

    @NonNull
    @Override
    public RvBaseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RvBaseHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RvBaseHolder holder, int position) {
        ItemImageBinding binding = ItemImageBinding.bind(holder.itemView);
        ViewGroup.LayoutParams layoutParams = binding.ivImage.getLayoutParams();
        int width;
        int height;
        if (spanCount == 3) {//90
            height = (int) ((ScreenUtils.getScreenWidth(holder.itemView.getContext()) - ScreenUtils.dp2px(holder.itemView.getContext(), 120)) / 3);
            width = height;
        } else if (spanCount == 2) {
            height = (int) ((ScreenUtils.getScreenWidth(holder.itemView.getContext()) - ScreenUtils.dp2px(holder.itemView.getContext(), 110)) / 2);
            width = height;
        } else {
            width = (int) ((ScreenUtils.getScreenWidth(holder.itemView.getContext()) - ScreenUtils.dp2px(holder.itemView.getContext(), 90)) / 1.5);
            height = width;
        }
        layoutParams.width = width;
        layoutParams.height = height;
        binding.ivImage.setLayoutParams(layoutParams);
        Bundle bundle = new Bundle();
        bundle.putSerializable("ImageItem",imageItem);

        ImageEntity imageEntity = data.get(position);
        View.OnClickListener onClickListener = v -> OpenImage.with(holder.itemView.getContext()).setClickRecyclerView((RecyclerView) holder.itemView.getParent(), new SourceImageViewIdGet<OpenImageUrl>() {
            @Override
            public int getImageViewId(OpenImageUrl data, int position1) {
                return R.id.iv_image;
            }
        }).setSrcImageViewScaleType(ImageView.ScaleType.CENTER_CROP, true)
                .setImageUrlList(data).setImageDiskMode(MyImageLoader.imageDiskMode)
                .setAutoScrollScanPosition(true)
                .setItemLoadHelper(new ItemLoadHelper() {
                    @Override
                    public void loadImage(Context context, OpenImageUrl openImageUrl, String imageUrl, ImageView imageView, int overrideWidth, int overrideHeight, OnLoadCoverImageListener onLoadCoverImageListener) {
                        MyImageLoader.getInstance().loadRoundCorner(imageView, imageUrl, 10, overrideWidth, overrideHeight, R.mipmap.img_load_placeholder, R.mipmap.img_load_placeholder, new MyImageLoader.OnImageLoadListener() {
                            @Override
                            public void onSuccess() {
                                onLoadCoverImageListener.onLoadImageSuccess();
                            }

                            @Override
                            public void onFailed() {
                                onLoadCoverImageListener.onLoadImageFailed();
                            }
                        });
                    }
                })
                .disableClickClose()
                .setUpperLayerFragmentCreate(new FriendLayerFragmentCreateImpl(),bundle,false,false)
                .setOpenImageStyle(R.style.DefaultPhotosTheme)
                .setClickPosition(position).show();
        binding.ivImage.setOnClickListener(onClickListener);
        MyImageLoader.getInstance().loadRoundCorner(binding.ivImage, imageEntity.getCoverImageUrl(), 10, R.mipmap.img_load_placeholder, R.mipmap.img_load_placeholder);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
