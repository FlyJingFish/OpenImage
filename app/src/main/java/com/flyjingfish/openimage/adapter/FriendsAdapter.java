package com.flyjingfish.openimage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flyjingfish.openimage.bean.ImageItem;
import com.flyjingfish.openimage.imageloader.MyImageLoader;
import com.flyjingfish.openimage.R;
import com.flyjingfish.openimage.databinding.ItemFriendImagesBinding;
import com.flyjingfish.openimage.databinding.ItemFriendVideoBinding;
import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.OpenImage;
import com.flyjingfish.openimagelib.listener.ItemLoadHelper;
import com.flyjingfish.openimagelib.listener.OnLoadCoverImageListener;
import com.flyjingfish.openimagelib.utils.ScreenUtils;

import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<RvBaseHolder> {

    private List<ImageItem> mDatas;
    private Context context;

    public FriendsAdapter(List<ImageItem> datas, Context context) {
        mDatas = datas;
        this.context = context;
    }

    @Override
    public RvBaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        if (viewType == ImageItem.IMAGE) {
            view = inflater.inflate(R.layout.item_friend_images, parent, false);
        } else {
            view = inflater.inflate(R.layout.item_friend_video, parent, false);
        }
        return new RvBaseHolder(view);
    }

    @Override
    public void onBindViewHolder(RvBaseHolder holder, int position) {
        final ImageItem data = mDatas.get(position);
        TextView content = holder.getView(R.id.item_content);
        content.setText(data.text);
        switch (getItemViewType(position)) {
            case ImageItem.IMAGE:
                buildImage(data, holder, position);
                break;
            case ImageItem.VIDEO:
                buildVideo(data, holder, position);
                break;
        }
    }

    private void buildVideo(final ImageItem data, RvBaseHolder holder, int position) {
        TextView content = holder.getView(R.id.item_name);
        ItemFriendVideoBinding binding = ItemFriendVideoBinding.bind(holder.itemView);
        ViewGroup.LayoutParams layoutParams = binding.ivImage.getLayoutParams();
        int width = (int) ((ScreenUtils.getScreenWidth(holder.itemView.getContext()) - ScreenUtils.dp2px(holder.itemView.getContext(), 90)) / 1.5);
        int height = width;
        layoutParams.width = width;
        layoutParams.height = height;
        binding.ivImage.setLayoutParams(layoutParams);
        content.setText("视频");

        View.OnClickListener onClickListener = v -> OpenImage.with(holder.itemView.getContext()).setClickImageView(binding.ivImage)
                .setSrcImageViewScaleType(ImageView.ScaleType.CENTER_CROP, true)
                .setImageUrl(data).setImageDiskMode(MyImageLoader.imageDiskMode)
                .setItemLoadHelper(new ItemLoadHelper() {
                    @Override
                    public void loadImage(Context context, OpenImageUrl openImageUrl, String imageUrl, ImageView imageView, int overrideWidth, int overrideHeight, OnLoadCoverImageListener onLoadCoverImageListener) {
                        MyImageLoader.getInstance().loadRoundCorner(imageView, imageUrl, 10, overrideWidth, overrideHeight, R.mipmap.img_load_placeholder, R.mipmap.img_load_placeholder,  new MyImageLoader.OnImageLoadListener() {
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
                .setAutoScrollScanPosition(true)
                .setOpenImageStyle(R.style.DefaultPhotosTheme)
                .setClickPosition(0).show();
        binding.ivImage.setOnClickListener(onClickListener);
        MyImageLoader.getInstance().loadRoundCorner(binding.ivImage, data.getCoverImageUrl(), 10, R.mipmap.img_load_placeholder, R.mipmap.img_load_placeholder);

    }

    private void buildImage(final ImageItem data, RvBaseHolder holder, int position) {
        TextView content = holder.getView(R.id.item_name);

        ItemFriendImagesBinding binding = ItemFriendImagesBinding.bind(holder.itemView);
        int dataCount = data.images.size();
        int spanCount;
        if (dataCount == 1) {
            content.setText("长图");
            spanCount = 1;
        } else {
            content.setText("小图");
            spanCount = dataCount == 4 ? 2 : 3;
        }
        FriendImageAdapter friendImageAdapter = new FriendImageAdapter(data.images, spanCount);
        binding.itemImage.addItemDecoration(new SpaceDecoration((int) ScreenUtils.dp2px(context, 10)));
        binding.itemImage.setLayoutManager(new GridLayoutManager(context, spanCount));
        binding.itemImage.setAdapter(friendImageAdapter);
    }

    @Override
    public int getItemViewType(int position) {
        return mDatas.get(position).getViewType();
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

}
