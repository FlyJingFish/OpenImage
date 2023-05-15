package com.flyjingfish.openimagelib;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.enums.MediaType;
import com.flyjingfish.openimagelib.enums.UpdateViewType;
import com.flyjingfish.openimagelib.listener.OnUpdateViewListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class OpenImageFragmentStateAdapter extends FragmentStateAdapter {

    private final PhotosViewModel photosViewModel;
    private final ViewPager2 viewPager2;
    private final FragmentActivity fragmentActivity;
    private final String updateKey;
    protected List<OpenImageDetail> openImageBeans;
    protected boolean wechatExitFillInEffect;
    private boolean transitionEnd;
    private OnUpdateIndicator onUpdateIndicator;
//    private final HashMap<Integer,Long> pageIds = new HashMap<>();

    public OpenImageFragmentStateAdapter(@NonNull FragmentActivity fragmentActivity, ViewPager2 viewPager2) {
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

    void setWechatExitFillInEffect(boolean wechatExitFillInEffect) {
        this.wechatExitFillInEffect = wechatExitFillInEffect;
    }

    void setNewData(List<OpenImageDetail> data) {
        if (data != null) {
            List<OpenImageDetail> openImageDetails = filterData(data, UpdateViewType.NONE);
            data.clear();
            data.addAll(openImageDetails);
        }
        openImageBeans = data;
        notifyData(null, null, UpdateViewType.NONE);
    }

    static int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    /**
     * 向后加数据
     *
     * @param data 新增的数据
     */
    public void addData(Collection<? extends OpenImageUrl> data) {
        addData(data, UpdateViewType.BACKWARD);
    }

    /**
     * 向后加数据
     *
     * @param data           新增的数据
     * @param updateViewType 不可传入 {@link UpdateViewType#FORWARD},传入{@link UpdateViewType#BACKWARD} 向后加数据并且可更新前一页面UI
     *                       传入{@link UpdateViewType#NONE} 向后加数据但是不可更新前一页面UI
     */
    public void addData(Collection<? extends OpenImageUrl> data, UpdateViewType updateViewType) {
        if (updateViewType == UpdateViewType.FORWARD) {
            updateViewType = UpdateViewType.BACKWARD;
        }
        List<OpenImageDetail> openImageDetails = filterData(data, updateViewType);
        if (openImageBeans != null) {
            openImageBeans.addAll(openImageDetails);
        } else {
            openImageBeans = openImageDetails;
        }
        ImageLoadUtils.getInstance().clearAllSmallCoverDrawable();
        ImageLoadUtils.getInstance().clearAllCoverDrawable();
        notifyData(data, openImageDetails, updateViewType);
    }

    /**
     * 向前加数据
     *
     * @param data 新增的数据
     */
    public void addFrontData(Collection<? extends OpenImageUrl> data) {
        addFrontData(data, UpdateViewType.FORWARD);
    }

    /**
     * 向前加数据
     *
     * @param data           新增的数据
     * @param updateViewType 不可传入 {@link UpdateViewType#BACKWARD},传入{@link UpdateViewType#FORWARD} 向前加数据并且可更新前一页面UI
     *                       传入{@link UpdateViewType#NONE} 向前加数据但是不可更新前一页面UI
     */
    public void addFrontData(Collection<? extends OpenImageUrl> data, final UpdateViewType updateViewType) {
        if (transitionEnd) {
            setFrontData(data, updateViewType);
        } else {
            photosViewModel.transitionEndLiveData.observe(fragmentActivity, new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    setFrontData(data, updateViewType);
                    photosViewModel.transitionEndLiveData.removeObserver(this);
                }
            });
        }
    }

    private void setFrontData(Collection<? extends OpenImageUrl> data, UpdateViewType updateViewType) {
        ImageLoadUtils.getInstance().clearAllSmallCoverDrawable();
        ImageLoadUtils.getInstance().clearAllCoverDrawable();
        if (updateViewType == UpdateViewType.BACKWARD) {
            updateViewType = UpdateViewType.FORWARD;
        }
        int cur = viewPager2.getCurrentItem();
        List<OpenImageDetail> openImageDetails = filterData(data, updateViewType);
        if (openImageBeans != null) {
            openImageBeans.addAll(0, openImageDetails);
        } else {
            openImageBeans = openImageDetails;
        }
        registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                viewPager2.setCurrentItem(openImageDetails.size() + cur, false);
                unregisterAdapterDataObserver(this);
            }
        });
        notifyData(data, openImageDetails, updateViewType);
    }

    private void notifyData(Collection<? extends OpenImageUrl> data, List<OpenImageDetail> openImageDetails, UpdateViewType updateViewType) {
        notifyDataSetChanged();
        OnUpdateViewListener onUpdateViewListener = ImageLoadUtils.getInstance().getOnUpdateViewListener(updateKey);
        if (onUpdateViewListener != null && data != null && openImageDetails != null) {
            onUpdateViewListener.onAdd(data, updateViewType);
        }
    }

    /**
     * 替换数据
     *
     * @param position     替换的位置
     * @param openImageUrl 新数据
     */
    public void replaceData(int position, OpenImageUrl openImageUrl) {
        if (position >= 0 && position < openImageBeans.size()) {
            ImageLoadUtils.getInstance().clearAllSmallCoverDrawable();
            ImageLoadUtils.getInstance().clearAllCoverDrawable();
            OpenImageDetail oldData = openImageBeans.get(position);
            OpenImageUrl oldDataUrl = oldData.openImageUrl;
            OpenImageDetail openImageDetail = new OpenImageDetail();
            openImageDetail.dataPosition = oldData.dataPosition;
            openImageDetail.viewPosition = oldData.viewPosition;
            openImageDetail.srcWidth = oldData.srcWidth;
            openImageDetail.srcHeight = oldData.srcHeight;
            openImageDetail.openImageUrl = openImageUrl;
            openImageBeans.set(position, openImageDetail);
            OnUpdateViewListener onUpdateViewListener = ImageLoadUtils.getInstance().getOnUpdateViewListener(updateKey);
            if (onUpdateViewListener != null) {
                onUpdateViewListener.onReplace(position, oldDataUrl, openImageUrl);
            }
            notifyItemChanged(position);
        }
    }

    /**
     * 删除数据
     *
     * @param openImageUrl 删除的旧数据
     */
    public void removeData(OpenImageUrl openImageUrl) {
        removeData(openImageUrl, true);
    }

    /**
     * 删除数据
     *
     * @param openImageUrl 删除的旧数据
     * @param smoothScroll 删除数据时是否展示滑动动画
     */
    public void removeData(OpenImageUrl openImageUrl, boolean smoothScroll) {
        int index = 0;
        for (OpenImageDetail openImageBean : openImageBeans) {
            if (openImageBean.openImageUrl == openImageUrl) {
                removeData(index, smoothScroll);
                return;
            }
            index++;
        }
    }

    /**
     * 删除数据
     *
     * @param position 删除数据的位置
     */
    public void removeData(int position) {
        removeData(position, true);
    }

    /**
     * 删除数据
     *
     * @param position     删除数据的位置
     * @param smoothScroll 删除数据时是否展示滑动动画
     */
    public void removeData(int position, boolean smoothScroll) {
        if (openImageBeans == null || position >= openImageBeans.size()) {
            return;
        }
        ImageLoadUtils.getInstance().clearAllSmallCoverDrawable();
        ImageLoadUtils.getInstance().clearAllCoverDrawable();
        if (position < openImageBeans.size() - 1) {
            viewPager2.setCurrentItem(position + 1, smoothScroll);
        } else if (position > 0) {
            viewPager2.setCurrentItem(position - 1, smoothScroll);
        }
        if (smoothScroll) {
            viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageScrollStateChanged(int state) {
                    super.onPageScrollStateChanged(state);
                    if (state == ViewPager2.SCROLL_STATE_IDLE) {
                        viewPager2.unregisterOnPageChangeCallback(this);
                        removeItem(position);
                    }
                }
            });
        } else {
            removeItem(position);
        }
    }

    private void removeItem(int position) {
        OpenImageDetail openImageDetail = openImageBeans.remove(position);
        for (int i = position; i < openImageBeans.size(); i++) {
            OpenImageDetail openImageBean = openImageBeans.get(i);
            openImageBean.dataPosition -= 1;
            openImageBean.viewPosition -= 1;
        }
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, openImageBeans.size());
        OnUpdateViewListener onUpdateViewListener = ImageLoadUtils.getInstance().getOnUpdateViewListener(updateKey);
        if (onUpdateViewListener != null) {
            onUpdateViewListener.onRemove(openImageDetail.openImageUrl);
        }
        if (onUpdateIndicator != null) {
            onUpdateIndicator.onUpdate();
        }
    }

    @Override
    public long getItemId(int position) {
        if (openImageBeans == null) {
            return 0;
        }
        return hash(openImageBeans.get(position));
    }

    /**
     * 获取适配器内的数据
     *
     * @return
     */
    @Nullable
    public List<? extends OpenImageUrl> getData() {
        List<OpenImageUrl> openImageUrls = null;
        if (openImageBeans != null) {
            openImageUrls = new ArrayList<>();
            for (OpenImageDetail openImageBean : openImageBeans) {
                openImageUrls.add(openImageBean.openImageUrl);
            }
        }
        return openImageUrls;
    }

    private List<OpenImageDetail> filterData(Collection<? extends OpenImageUrl> imageDetails, UpdateViewType updateViewType) {
        if (imageDetails != null) {
            List<OpenImageDetail> openImageDetails = new ArrayList<>();
            int oldDataPos = 0;
            int oldViewPos = 0;
            if (openImageBeans != null && updateViewType == UpdateViewType.BACKWARD) {
                oldDataPos = openImageBeans.get(openImageBeans.size() - 1).dataPosition + 1;
                oldViewPos = openImageBeans.get(openImageBeans.size() - 1).viewPosition + 1;
            }

            int i = 0;
            for (OpenImageUrl imageBean : imageDetails) {
                if (!(imageBean instanceof OpenImageDetail)) {
                    if (imageBean.getType() == MediaType.IMAGE || imageBean.getType() == MediaType.VIDEO) {
                        OpenImageDetail openImageDetail = new OpenImageDetail();
                        openImageDetail.openImageUrl = imageBean;
                        openImageDetail.dataPosition = oldDataPos + i;
                        if (updateViewType == UpdateViewType.NONE) {
                            openImageDetail.viewPosition = -1;
                        } else {
                            openImageDetail.viewPosition = oldViewPos + i;
                        }
                        openImageDetails.add(openImageDetail);
                    }
                } else {
                    openImageDetails.add((OpenImageDetail) imageBean);
                }
                i++;
            }
            if (openImageBeans != null && updateViewType == UpdateViewType.FORWARD) {
                for (OpenImageDetail openImageBean : openImageBeans) {
                    openImageBean.dataPosition = imageDetails.size() + openImageBean.dataPosition;
                    if (openImageBean.viewPosition >= 0) {
                        openImageBean.viewPosition = imageDetails.size() + openImageBean.viewPosition;
                    }
                }
            }
            return openImageDetails;
        } else {
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

    public void setOnUpdateIndicator(OnUpdateIndicator onUpdateIndicator) {
        this.onUpdateIndicator = onUpdateIndicator;
    }

    interface OnUpdateIndicator {
        void onUpdate();
    }
}
