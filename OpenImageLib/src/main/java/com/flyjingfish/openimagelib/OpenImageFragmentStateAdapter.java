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
     * @param data 新增的数据
     */
    public void addData(Collection<? extends OpenImageUrl> data) {
        addData(data,UpdateViewType.BACKWARD);
    }

    /**
     * 向后加数据
     * @param data 新增的数据
     * @param updateViewType 不可传入 {@link UpdateViewType#FORWARD},传入{@link UpdateViewType#BACKWARD} 向后加数据并且可更新前一页面UI
     *                       传入{@link UpdateViewType#NONE} 向后加数据但是不可更新前一页面UI
     */
    public void addData(Collection<? extends OpenImageUrl> data,UpdateViewType updateViewType) {
        if (updateViewType == UpdateViewType.FORWARD){
            updateViewType = UpdateViewType.BACKWARD;
        }
        List<OpenImageDetail> openImageDetails = filterData(data,updateViewType);
        if (openImageBeans != null){
            openImageBeans.addAll(openImageDetails);
        }else {
            openImageBeans = openImageDetails;
        }
        notifyData(data,openImageDetails,updateViewType);
    }

    /**
     * 向前加数据
     * @param data 新增的数据
     */
    public void addFrontData(Collection<? extends OpenImageUrl> data) {
        addFrontData(data,UpdateViewType.FORWARD);
    }

    /**
     * 向前加数据
     * @param data 新增的数据
     * @param updateViewType  不可传入 {@link UpdateViewType#BACKWARD},传入{@link UpdateViewType#FORWARD} 向前加数据并且可更新前一页面UI
     *                        传入{@link UpdateViewType#NONE} 向前加数据但是不可更新前一页面UI
     */
    public void addFrontData(Collection<? extends OpenImageUrl> data,final UpdateViewType updateViewType) {
        if (transitionEnd){
            setFrontData(data,updateViewType);
        }else {
            photosViewModel.transitionEndLiveData.observe(fragmentActivity, new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    setFrontData(data,updateViewType);
                    photosViewModel.transitionEndLiveData.removeObserver(this);
                }
            });
        }
    }

    private void setFrontData(Collection<? extends OpenImageUrl> data,UpdateViewType updateViewType) {
        if (updateViewType == UpdateViewType.BACKWARD){
            updateViewType = UpdateViewType.FORWARD;
        }
        int cur = viewPager2.getCurrentItem();
        List<OpenImageDetail> openImageDetails = filterData(data,updateViewType);
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
        notifyData(data,openImageDetails,updateViewType);
    }
    
    private void notifyData(Collection<? extends OpenImageUrl> data,List<OpenImageDetail> openImageDetails,UpdateViewType updateViewType){
        notifyDataSetChanged();
        OnUpdateViewListener onUpdateViewListener = ImageLoadUtils.getInstance().getOnUpdateViewListener(updateKey);
        if (onUpdateViewListener != null && data != null && openImageDetails != null){
            onUpdateViewListener.onUpdate(data,updateViewType);
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
            int oldDataPos = 0;
            int oldViewPos = 0;
            if (openImageBeans != null && updateViewType == UpdateViewType.BACKWARD){
                oldDataPos = openImageBeans.get(openImageBeans.size()-1).dataPosition + 1;
                oldViewPos = openImageBeans.get(openImageBeans.size()-1).viewPosition + 1;
            }

            int i=0;
            for (OpenImageUrl imageBean : imageDetails) {
                if (!(imageBean instanceof OpenImageDetail)){
                    if (imageBean.getType() == MediaType.IMAGE || imageBean.getType() == MediaType.VIDEO) {
                        OpenImageDetail openImageDetail = new OpenImageDetail();
                        openImageDetail.openImageUrl = imageBean;
                        openImageDetail.dataPosition = oldDataPos + i;
                        if (updateViewType == UpdateViewType.NONE){
                            openImageDetail.viewPosition = -1;
                        }else{
                            openImageDetail.viewPosition = oldViewPos + i;
                        }
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
                    if (openImageBean.viewPosition>=0){
                        openImageBean.viewPosition = imageDetails.size()+openImageBean.viewPosition;
                    }
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
