package com.flyjingfish.openimage.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.flyjingfish.openimage.R;
import com.flyjingfish.openimage.activity.MessageActivity;
import com.flyjingfish.openimage.bean.MessageBean;
import com.flyjingfish.openimage.databinding.ItemMsgImageBinding;
import com.flyjingfish.openimage.databinding.ItemMsgTextBinding;
import com.flyjingfish.openimage.databinding.ItemMsgVideoBinding;
import com.flyjingfish.openimage.imageloader.MyImageLoader;
import com.flyjingfish.openimagelib.OpenImage;
import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.listener.SourceImageViewIdGet;

import java.util.ArrayList;
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
        View.OnClickListener onClickListener = v -> {
            String url1= "https://pics4.baidu.com/feed/50da81cb39dbb6fd95aa0c599b8d0d1e962b3708.jpeg?token=bf17224f51a6f4bb389e787f9c487940";
            String url2= "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.tt98.com%2Fd%2Ffile%2Fpic%2F201811082010742%2F5be40536abdd2.jpg&refer=http%3A%2F%2Fimg.tt98.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1661701773&t=2d03e79dd2eb007d30a330479093ecf4";
            List<MessageBean> otherData = new ArrayList<>();
            MessageBean messageBean1 = new MessageBean(MessageBean.IMAGE,url1,url1);
            MessageBean messageBean2 = new MessageBean(MessageBean.IMAGE,url2,url2);
            MessageBean messageBean3 = new MessageBean(MessageBean.TEXT,url2,url2);//文本或其他类型的数据不必排除
            otherData.add(messageBean1);
            otherData.add(messageBean3);
            otherData.add(messageBean2);

            List<MessageBean> allShowData = new ArrayList<>();
            allShowData.addAll(otherData);//recyclerView 适配器以外的数据
            allShowData.addAll(messageBeans);//recyclerView 适配器的数据
            OpenImage.with(parent.getContext()).setClickListView(listView, new SourceImageViewIdGet<OpenImageUrl>() {
                        @Override
                        public int getImageViewId(OpenImageUrl data, int position1) {
                            MessageBean msgBean = (MessageBean) data;
                            if (msgBean.type == MessageBean.IMAGE){
                                return R.id.iv_image;
                            }else {
                                return R.id.iv_video;
                            }
                        }
                    }) .setAutoScrollScanPosition(MessageActivity.openAutoScroll)
                    .setSrcImageViewScaleType(ImageView.ScaleType.CENTER_CROP,true)
                    .setImageUrlList(allShowData).setWechatExitFillInEffect(MessageActivity.openWechatEffect)
//                    .setOpenImageStyle(R.style.DefaultPhotosTheme)
                    .setShowDownload()
                    .setClickPosition(position+otherData.size(),position).show();
        };
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
