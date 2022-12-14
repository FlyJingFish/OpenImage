package com.flyjingfish.openimage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flyjingfish.openimage.activity.MessageActivity;
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

import java.util.ArrayList;
import java.util.List;

public class MsgRvAdapter extends RecyclerView.Adapter<MsgRvAdapter.MyHolder> {
    private List<MessageBean> messageBeans;

    public MsgRvAdapter(List<MessageBean> messageBeans) {
        this.messageBeans = messageBeans;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MessageBean.IMAGE){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_msg_image,parent,false);
        }else if (viewType == MessageBean.VIDEO){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_msg_video,parent,false);
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_msg_text,parent,false);
        }
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        MessageBean messageBean = messageBeans.get(position);
        int viewType = messageBean.type;
        View.OnClickListener onClickListener = v ->{
            //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????View???????????????
            String url1= "https://pics4.baidu.com/feed/50da81cb39dbb6fd95aa0c599b8d0d1e962b3708.jpeg?token=bf17224f51a6f4bb389e787f9c487940";
            String url2= "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.tt98.com%2Fd%2Ffile%2Fpic%2F201811082010742%2F5be40536abdd2.jpg&refer=http%3A%2F%2Fimg.tt98.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1661701773&t=2d03e79dd2eb007d30a330479093ecf4";
            List<MessageBean> beans = new ArrayList<>();
            MessageBean messageBean1 = new MessageBean(MessageBean.IMAGE,url1,url1);
            MessageBean messageBean2 = new MessageBean(MessageBean.IMAGE,url2,url2);
            MessageBean messageBean3 = new MessageBean(MessageBean.TEXT,url2,url2);
            beans.add(messageBean1);
            beans.add(messageBean3);
            beans.add(messageBean2);

            List<MessageBean> dataBeans = new ArrayList<>();
            dataBeans.addAll(beans);
            dataBeans.addAll(messageBeans);
            OpenImage.with(holder.itemView.getContext()).setClickRecyclerView((RecyclerView) holder.itemView.getParent(), new SourceImageViewIdGet<OpenImageUrl>() {
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
                    .setAutoScrollScanPosition(MessageActivity.openAutoScroll)
                    .setSrcImageViewScaleType(ImageView.ScaleType.CENTER_CROP,true)
                    .setImageUrlList(dataBeans).setImageDiskMode(MyImageLoader.imageDiskMode)
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
                    }).setWechatExitFillInEffect(MessageActivity.openWechatEffect)
                    .setOpenImageStyle(R.style.DefaultPhotosTheme)
                    .setClickPosition(position+beans.size(),position).show();
        } ;
        if (viewType == MessageBean.IMAGE){
            ItemMsgImageBinding binding = ItemMsgImageBinding.bind(holder.itemView);
            binding.ivImage.setOnClickListener(onClickListener);
            MyImageLoader.getInstance().loadRoundCorner(binding.ivImage,messageBean.getCoverImageUrl(),10,R.mipmap.img_load_placeholder, R.mipmap.img_load_placeholder);
        }else if (viewType == MessageBean.VIDEO){
            ItemMsgVideoBinding binding = ItemMsgVideoBinding.bind(holder.itemView);
            binding.ivVideo.setOnClickListener(onClickListener);
            MyImageLoader.getInstance().loadRoundCorner(binding.ivVideo,messageBean.getCoverImageUrl(),10,R.mipmap.img_load_placeholder, R.mipmap.img_load_placeholder);
        }else {
            ItemMsgTextBinding binding = ItemMsgTextBinding.bind(holder.itemView);
            binding.tvText.setText(messageBean.text);
        }

    }

    @Override
    public int getItemCount() {
        return messageBeans.size();
    }

    @Override
    public int getItemViewType(int position) {
        return messageBeans.get(position).type;
    }

    class MyHolder extends RecyclerView.ViewHolder{

        public MyHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
