package com.flyjingfish.openimagelib;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.flyjingfish.openimagelib.beans.OpenImageDetail;
import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.enums.MediaType;
import com.flyjingfish.openimagelib.enums.UpdateViewType;
import com.flyjingfish.openimagelib.listener.OnUpdateViewListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Handler;

public class OpenImageFragmentStateAdapter extends FragmentStateAdapter {

    private final PhotosViewModel photosViewModel;
    private final ViewPager2 viewPager2;
    private final FragmentActivity fragmentActivity;
    private final String updateKey;
    protected List<OpenImageDetail> openImageBeans;
    protected boolean wechatExitFillInEffect;
    private boolean transitionEnd;

    public OpenImageFragmentStateAdapter(@NonNull FragmentActivity fragmentActivity,ViewPager2 viewPager2) {
        super(fragmentActivity);
        this.fragmentActivity = fragmentActivity;
        updateKey = fragmentActivity.getIntent().getStringExtra(OpenParams.ON_UPDATE_VIEW);
        photosViewModel = new ViewModelProvider(fragmentActivity).get(PhotosViewModel.class);
        photosViewModel.transitionEndLiveData.observe(fragmentActivity, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                transitionEnd = true;
                photosViewModel.transitionEndLiveData.removeObserver(this);
            }
        });
        this.viewPager2 = viewPager2;
    }

    public boolean isWechatExitFillInEffect() {
        return wechatExitFillInEffect;
    }

    public void setWechatExitFillInEffect(boolean wechatExitFillInEffect) {
        this.wechatExitFillInEffect = wechatExitFillInEffect;
    }

    public void setNewData(List<OpenImageDetail> data) {
        if (data != null){
            List<OpenImageDetail> openImageDetails = filterData(data,UpdateViewType.NONE);
            data.clear();
            data.addAll(openImageDetails);
        }
        openImageBeans = data;
        notifyData(null,null,UpdateViewType.NONE);
    }

    /**
     * 向后加数据
     * @param data
     */
    public void addData(Collection<? extends OpenImageUrl> data) {
        List<OpenImageDetail> openImageDetails = filterData(data,UpdateViewType.BACKWARD);
        if (openImageBeans != null){
            openImageBeans.addAll(openImageDetails);
        }else {
            openImageBeans = openImageDetails;
        }
        notifyData(data,openImageDetails,UpdateViewType.BACKWARD);
    }

    /**
     * 向前加数据
     * @param data
     */
    public void addFrontData(Collection<? extends OpenImageUrl> data) {
        if (transitionEnd){
            setFrontData(data);
        }else {
            photosViewModel.transitionEndLiveData.observe(fragmentActivity, new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    setFrontData(data);
                    photosViewModel.transitionEndLiveData.removeObserver(this);
                }
            });
        }
    }

    private void setFrontData(Collection<? extends OpenImageUrl> data) {
        int cur = viewPager2.getCurrentItem();
        List<OpenImageDetail> openImageDetails = filterData(data,UpdateViewType.FORWARD);
        if (openImageBeans != null){
            openImageBeans.addAll(0,openImageDetails);
        }else {
            openImageBeans = openImageDetails;
        }
        registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                viewPager2.setCurrentItem(openImageDetails.size() + cur,false);
                unregisterAdapterDataObserver(this);
            }
        });
        notifyData(data,openImageDetails,UpdateViewType.FORWARD);
    }
    
    public void notifyData(Collection<? extends OpenImageUrl> data,List<OpenImageDetail> openImageDetails,UpdateViewType updateViewType){
        notifyDataSetChanged();
        OnUpdateViewListener onUpdateViewListener = ImageLoadUtils.getInstance().getOnUpdateViewListener(updateKey);
        if (onUpdateViewListener != null && data != null && openImageDetails != null){
            UpdateViewType type = onUpdateViewListener.onUpdate(data,updateViewType);
//            if (type == UpdateViewType.FORWARD){
//                for (OpenImageDetail openImageDetail : openImageDetails) {
//
//                }
//            }
        }
    }

    /**
     * 获取适配器内的数据
     * @return
     */
    public List<OpenImageDetail> getData() {
        return openImageBeans;
    }

    private List<OpenImageDetail> filterData(Collection<? extends OpenImageUrl> imageDetails,UpdateViewType updateViewType){
        if (imageDetails != null){
            List<OpenImageDetail> openImageDetails = new ArrayList<>();
            Iterator<? extends OpenImageUrl> iterator = imageDetails.iterator();
            int oldSize = 0;
            if (openImageBeans != null && updateViewType == UpdateViewType.BACKWARD){
                oldSize = openImageBeans.get(openImageBeans.size()-1).dataPosition + 1;
            }

            int i=0;
            while (iterator.hasNext()){
                OpenImageUrl imageBean = iterator.next();
                if (!(imageBean instanceof OpenImageDetail)){
                    if (imageBean.getType() == MediaType.IMAGE || imageBean.getType() == MediaType.VIDEO) {
                        OpenImageDetail openImageDetail = new OpenImageDetail();
                        openImageDetail.openImageUrl = imageBean;
                        openImageDetail.dataPosition = oldSize + i;
                        openImageDetail.viewPosition = -1;
                        openImageDetails.add(openImageDetail);
                    }
                }else {
                    openImageDetails.add((OpenImageDetail) imageBean);
                }
                i++;
            }
            if (openImageBeans != null && updateViewType == UpdateViewType.FORWARD){
                for (OpenImageDetail openImageBean : openImageBeans) {
                    openImageBean.dataPosition = imageDetails.size()+openImageBean.dataPosition;
                }
            }
            return openImageDetails;
        }else {
            return null;
        }
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return null;
    }

    @Override
    public int getItemCount() {
        return openImageBeans != null ? openImageBeans.size() : 0;
    }
}
