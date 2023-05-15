package com.flyjingfish.openimage.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flyjingfish.openimage.R;
import com.flyjingfish.openimage.activity.MessageActivity;
import com.flyjingfish.openimage.bean.MessageBean;
import com.flyjingfish.openimage.databinding.ItemMsgImageBinding;
import com.flyjingfish.openimage.databinding.ItemMsgTextBinding;
import com.flyjingfish.openimage.databinding.ItemMsgVideoBinding;
import com.flyjingfish.openimage.imageloader.MyImageLoader;
import com.flyjingfish.openimage.openImpl.MessageVpActivity;
import com.flyjingfish.openimagelib.OpenImage;
import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.enums.UpdateViewType;
import com.flyjingfish.openimagelib.listener.OnUpdateViewListener;
import com.flyjingfish.openimagelib.listener.SourceImageViewIdGet;

import java.util.Collection;
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
            //添加聊天以外的图片，数据不必将除视频或图片之外的数据排除掉，排除掉将不能对应View的点击位置
//            String url1= "https://pics4.baidu.com/feed/50da81cb39dbb6fd95aa0c599b8d0d1e962b3708.jpeg?token=bf17224f51a6f4bb389e787f9c487940";
//            String url2= "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.tt98.com%2Fd%2Ffile%2Fpic%2F201811082010742%2F5be40536abdd2.jpg&refer=http%3A%2F%2Fimg.tt98.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1661701773&t=2d03e79dd2eb007d30a330479093ecf4";
//            List<MessageBean> otherData = new ArrayList<>();
//            MessageBean messageBean1 = new MessageBean(MessageBean.IMAGE,url1,url1);
//            MessageBean messageBean2 = new MessageBean(MessageBean.IMAGE,url2,url2);
//            MessageBean messageBean3 = new MessageBean(MessageBean.TEXT,url2,url2);//文本或其他类型的数据不必排除
//            otherData.add(messageBean1);
//            otherData.add(messageBean3);
//            otherData.add(messageBean2);
//
//            List<MessageBean> allShowData = new ArrayList<>();
//            allShowData.addAll(otherData);//recyclerView 适配器以外的数据
//            allShowData.addAll(messageBeans);//recyclerView 适配器的数据
            //以上两种数据都不必排除其他类型数据
            OpenImage.with(holder.itemView.getContext()).setClickRecyclerView((RecyclerView) holder.itemView.getParent(), new SourceImageViewIdGet<OpenImageUrl>() {
                        @Override
                        public int getImageViewId(OpenImageUrl data, int position1) {
                            MessageBean msgBean = (MessageBean) data;
                            //图片和视频显示的 ImageView 的 id 不一样也可以，根据数据类型返回即可
                            if (msgBean.type == MessageBean.IMAGE){
                                return R.id.iv_image;
                            }else {
                                return R.id.iv_video;
                            }
                        }
                    })
                    .setAutoScrollScanPosition(MessageActivity.openAutoScroll)
                    .setSrcImageViewScaleType(ImageView.ScaleType.CENTER_CROP,true)
                    .setImageUrlList(messageBeans).setWechatExitFillInEffect(MessageActivity.openWechatEffect)
                    .setOpenImageStyle(R.style.DefaultPhotosTheme)
                    .setOpenImageActivityCls(MessageVpActivity.class, new OnUpdateViewListener() {
                        @Override
                        public void onAdd(Collection<? extends OpenImageUrl> data, UpdateViewType updateViewType) {
                            if (updateViewType == UpdateViewType.FORWARD){
                                messageBeans.addAll(0, (Collection<? extends MessageBean>) data);
                                notifyDataSetChanged();
                            }else if (updateViewType == UpdateViewType.BACKWARD){
                                messageBeans.addAll((Collection<? extends MessageBean>) data);
                                notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onRemove(OpenImageUrl openImageUrl) {
                            messageBeans.remove(openImageUrl);
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onReplace(int position,OpenImageUrl oldData, OpenImageUrl newData) {
                            int index=0;
                            for (MessageBean bean : messageBeans) {
                                if (bean == oldData){
                                    messageBeans.set(index, (MessageBean) newData);
                                    notifyDataSetChanged();
                                    return;
                                }
                                index++;
                            }
                        }
                    })
                    //前者是点击所在 allShowData 数据位置，后者是点击的 RecyclerView 中位置
                    .setClickPosition(position).show();
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
