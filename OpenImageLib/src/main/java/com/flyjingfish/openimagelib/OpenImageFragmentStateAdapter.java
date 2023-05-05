package com.flyjingfish.openimagelib;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.flyjingfish.openimagelib.beans.OpenImageDetail;
import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.enums.MediaType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class OpenImageFragmentStateAdapter extends FragmentStateAdapter {

    protected List<OpenImageDetail> openImageBeans;

    public OpenImageFragmentStateAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public OpenImageFragmentStateAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public OpenImageFragmentStateAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    public void setNewData(List<OpenImageDetail> data) {
        if (data != null){
            List<OpenImageDetail> openImageDetails = filterData(data);
            data.clear();
            data.addAll(openImageDetails);
        }
        openImageBeans = data;
        notifyDataSetChanged();
    }

    public void addData(Collection<? extends OpenImageUrl> data) {
        List<OpenImageDetail> openImageDetails = filterData(data);
        if (openImageBeans != null){
            openImageBeans.addAll(openImageDetails);
        }else {
            openImageBeans = openImageDetails;
        }
        notifyDataSetChanged();
    }

    public List<OpenImageDetail> getData() {
        return openImageBeans;
    }

    public List<OpenImageDetail> filterData(Collection<? extends OpenImageUrl> imageDetails){
        if (imageDetails != null){
            List<OpenImageDetail> openImageDetails = new ArrayList<>();
            Iterator<? extends OpenImageUrl> iterator = imageDetails.iterator();
            OpenImageDetail startOpenImageDetail = null;
            if (openImageBeans != null){
                startOpenImageDetail = openImageBeans.get(0);
            }
            int i=0;
            while (iterator.hasNext()){
                OpenImageUrl imageBean = iterator.next();
                if (!(imageBean instanceof OpenImageDetail)){
                    if (imageBean.getType() == MediaType.IMAGE || imageBean.getType() == MediaType.VIDEO) {
                        OpenImageDetail openImageDetail = new OpenImageDetail();
                        openImageDetail.openImageUrl = imageBean;
                        if (startOpenImageDetail != null){
                            openImageDetail.dataPosition = startOpenImageDetail.dataPosition;
                            openImageDetail.viewPosition = startOpenImageDetail.viewPosition;
                            openImageDetail.srcHeight = startOpenImageDetail.srcHeight;
                            openImageDetail.srcWidth = startOpenImageDetail.srcWidth;
                        }
                        openImageDetails.add(openImageDetail);
                    }
                }else {
                    openImageDetails.add((OpenImageDetail) imageBean);
                }
                i++;
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
