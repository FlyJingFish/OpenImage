package com.flyjingfish.openimage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.flyjingfish.openimage.bean.MessageBean;
import com.flyjingfish.openimage.imageloader.MyImageLoader;
import com.flyjingfish.openimage.R;
import com.flyjingfish.openimage.databinding.ItemMsgImageBinding;
import com.flyjingfish.openimage.databinding.ItemMsgTextBinding;
import com.flyjingfish.openimage.databinding.ItemMsgVideoBinding;
import com.flyjingfish.openimagelib.OpenImage;
import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.listener.ItemLoadHelper;
import com.flyjingfish.openimagelib.listener.OnLoadCoverImageListener;
import com.flyjingfish.openimagelib.listener.SourceImageViewIdGet;

import java.util.List;

public class MsgLvAdapter extends BaseAdapter {
    private List<MessageBean> messageBeans;
    ListView listView;

    public MsgLvAdapter(List<MessageBean> messageBeans,ListView listView) {
        this.messageBeans = messageBeans;
        this.listView = listView;
    }
    @Override
    public int getCount() {
        return messageBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return messageBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        return messageBeans.get(position).type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyHolder holder1 = null;
        MyHolder holder2 = null;
        MyHolder holder3 = null;
        int type = getItemViewType(position);
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            // 按当前所需的样式，确定new的布局
            switch (type) {
                case MessageBean.TEXT:

                    convertView = inflater.inflate(R.layout.item_msg_text,
                            parent, false);
                    holder1 = new MyHolder(convertView);
                    convertView.setTag(holder1);
                    break;
                case MessageBean.IMAGE:
                    convertView = inflater.inflate(R.layout.item_msg_image,
                            parent, false);
                    holder2 = new MyHolder(convertView);
                    convertView.setTag(holder2);
                    break;
                case MessageBean.VIDEO:
                    convertView = inflater.inflate(R.layout.item_msg_video,
                            parent, false);
                    holder3 = new MyHolder(convertView);
                    convertView.setTag(holder3);
                    break;
                default:
                    break;
            }

        } else {
            switch (type) {
                case MessageBean.TEXT:
                    holder1 = (MyHolder) convertView.getTag();
                    break;
                case MessageBean.IMAGE:
                    holder2 = (MyHolder) convertView.getTag();
                    break;
                case MessageBean.VIDEO:
                    holder3 = (MyHolder) convertView.getTag();
                    break;
            }
        }
        MessageBean messageBean = messageBeans.get(position);
        int viewType = messageBean.type;
        View.OnClickListener onClickListener = v -> OpenImage.with(parent.getContext()).setClickListView(listView, new SourceImageViewIdGet<OpenImageUrl>() {
            @Override
            public int getImageViewId(OpenImageUrl data, int position1) {
                MessageBean msgBean = (MessageBean) data;
                if (msgBean.type == MessageBean.IMAGE){
                    return R.id.iv_image;
                }else {
                    return R.id.iv_video;
                }
            }
        })
                .setSrcImageViewScaleType(ImageView.ScaleType.CENTER_CROP,true)
                .setImageUrlList(messageBeans).setImageDiskMode(MyImageLoader.imageDiskMode)
                .setItemLoadHelper(new ItemLoadHelper() {
                    @Override
                    public void loadImage(Context context, OpenImageUrl openImageUrl, String imageUrl, ImageView imageView, int overrideWidth, int overrideHeight, OnLoadCoverImageListener onLoadCoverImageListener) {
                        MyImageLoader.getInstance().loadRoundCorner(imageView, imageUrl,10, overrideWidth,overrideHeight,R.mipmap.img_load_placeholder, R.mipmap.img_load_placeholder,  new MyImageLoader.OnImageLoadListener() {
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
                .setOpenImageStyle(R.style.DefaultPhotosTheme)
                .setClickPosition(position).show();
        if (viewType == MessageBean.IMAGE){
            ItemMsgImageBinding binding = ItemMsgImageBinding.bind(holder2.itemView);
            binding.ivImage.setOnClickListener(onClickListener);
            MyImageLoader.getInstance().loadRoundCorner(binding.ivImage,messageBean.getImageUrl(),10,R.mipmap.img_load_placeholder, R.mipmap.img_load_placeholder);
        }else if (viewType == MessageBean.VIDEO){
            ItemMsgVideoBinding binding = ItemMsgVideoBinding.bind(holder3.itemView);
            binding.ivVideo.setOnClickListener(onClickListener);
            MyImageLoader.getInstance().loadRoundCorner(binding.ivVideo,messageBean.getCoverImageUrl(),10,R.mipmap.img_load_placeholder, R.mipmap.img_load_placeholder);
        }else {
            ItemMsgTextBinding binding = ItemMsgTextBinding.bind(holder1.itemView);
            binding.tvText.setText(messageBean.text);
        }
        return convertView;
    }

    class MyHolder{
        View itemView;
        public MyHolder(View itemView) {
            this.itemView = itemView;
        }
    }
}
