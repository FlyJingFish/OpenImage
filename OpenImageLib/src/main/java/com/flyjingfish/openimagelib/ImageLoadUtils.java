package com.flyjingfish.openimagelib;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;

import androidx.viewpager2.widget.ViewPager2;

import com.flyjingfish.openimagelib.enums.BackViewType;
import com.flyjingfish.openimagelib.listener.ImageFragmentCreate;
import com.flyjingfish.openimagelib.listener.ItemLoadHelper;
import com.flyjingfish.openimagelib.listener.OnItemClickListener;
import com.flyjingfish.openimagelib.listener.OnItemLongClickListener;
import com.flyjingfish.openimagelib.listener.OnLoadViewFinishListener;
import com.flyjingfish.openimagelib.listener.OnSelectMediaListener;
import com.flyjingfish.openimagelib.listener.UpperLayerFragmentCreate;
import com.flyjingfish.openimagelib.listener.VideoFragmentCreate;

import java.util.HashMap;
import java.util.List;

class ImageLoadUtils {
    private static volatile ImageLoadUtils mInstance;
    private HashMap<String, Boolean> imageLoadSuccessMap = new HashMap<>();
    private HashMap<String, ItemLoadHelper> itemLoadHelperHashMap = new HashMap<>();
    private HashMap<String, Drawable> coverDrawableHashMap = new HashMap<>();
    private HashMap<String, OnSelectMediaListener> onSelectMediaListenerHashMap = new HashMap<>();
    private HashMap<String, List<ViewPager2.PageTransformer>> pageTransformerMap = new HashMap<>();
    private HashMap<String, OnItemClickListener> onItemClickListenerHashMap = new HashMap<>();
    private HashMap<String, OnItemLongClickListener> onItemLongClickListenerHashMap = new HashMap<>();
    private HashMap<String, List<MoreViewOption>> moreViewOptionHashMap = new HashMap<>();
    private HashMap<String, OnBackView> onBackViewHashMap = new HashMap<>();
    private HashMap<String, ImageFragmentCreate> imageFragmentCreateHashMap = new HashMap<>();
    private HashMap<String, VideoFragmentCreate> videoFragmentCreateHashMap = new HashMap<>();
    private HashMap<String, UpperLayerOption> upperLayerFragmentCreateHashMap = new HashMap<>();
    private HashMap<String, Boolean> canOpenViewPageActivityHashMap = new HashMap<>();

    private ImageLoadUtils() {
    }

    public static ImageLoadUtils getInstance() {
        if (mInstance == null) {
            synchronized (ImageLoadUtils.class) {
                if (mInstance == null) {
                    mInstance = new ImageLoadUtils();
                }
            }
        }
        return mInstance;
    }

    public void setImageLoadSuccess(String url) {
        imageLoadSuccessMap.put(url, true);
    }

    public boolean getImageLoadSuccess(String url) {
        if (imageLoadSuccessMap.containsKey(url)) {
            Boolean suc = imageLoadSuccessMap.get(url);
            return suc != null ? suc : false;
        }
        return false;
    }

    public ItemLoadHelper getItemLoadHelper(String key) {
        return itemLoadHelperHashMap.get(key);
    }

    public void setItemLoadHelper(String key, ItemLoadHelper itemLoadHelper) {
        itemLoadHelperHashMap.put(key, itemLoadHelper);
    }

    public void clearItemLoadHelper(String key) {
        itemLoadHelperHashMap.remove(key);
    }

    public void setCoverDrawable(String key, Drawable drawable) {
        coverDrawableHashMap.put(key, drawable);
    }

    public Drawable getCoverDrawable(String key) {
        return coverDrawableHashMap.get(key);
    }

    public void clearCoverDrawable(String key) {
        coverDrawableHashMap.remove(key);
    }

    public interface OnBackView {
        ExitOnBackView.ShareExitViewBean onBack(int showPosition);

        void onTouchClose(boolean isTouchClose);

        void onScrollPos(int pos);

        void onStartTouchScale(int showPosition);
        void onEndTouchScale(int showPosition);
    }
    private OnRemoveListener4FixBug onRemoveListener4FixBug;
    public interface OnRemoveListener4FixBug{
        void onRemove();
    }

    public void notifyOnRemoveListener4FixBug() {
        if (onRemoveListener4FixBug != null){
            onRemoveListener4FixBug.onRemove();
        }
    }

    public void setOnRemoveListener4FixBug(OnRemoveListener4FixBug onRemoveListener4FixBug) {
        this.onRemoveListener4FixBug = onRemoveListener4FixBug;
    }

    public OnBackView getOnBackView(String key) {
        return onBackViewHashMap.get(key);
    }

    public void setOnBackView(String key, OnBackView onBackView) {
        onBackViewHashMap.put(key, onBackView);
    }

    public void clearOnBackView(String key) {
        onBackViewHashMap.remove(key);
    }

    public OnSelectMediaListener getOnSelectMediaListener(String key) {
        return onSelectMediaListenerHashMap.get(key);
    }

    public void setOnSelectMediaListener(String key, OnSelectMediaListener onSelectMediaListener) {
        this.onSelectMediaListenerHashMap.put(key, onSelectMediaListener);
    }

    public void clearOnSelectMediaListener(String key) {
        this.onSelectMediaListenerHashMap.remove(key);
    }

    public void setPageTransformers(String key, List<ViewPager2.PageTransformer> pageTransformers) {
        pageTransformerMap.put(key, pageTransformers);
    }

    public List<ViewPager2.PageTransformer> getPageTransformers(String key) {
        return pageTransformerMap.get(key);
    }

    public void clearPageTransformers(String key) {
        pageTransformerMap.remove(key);
    }

    public OnItemClickListener getOnItemClickListener(String key) {
        return onItemClickListenerHashMap.get(key);
    }

    public void setOnItemClickListener(String key, OnItemClickListener onItemClickListener) {
        this.onItemClickListenerHashMap.put(key, onItemClickListener);
    }

    public void clearOnItemClickListener(String key) {
        onItemClickListenerHashMap.remove(key);
    }

    public OnItemLongClickListener getOnItemLongClickListener(String key) {
        return onItemLongClickListenerHashMap.get(key);
    }

    public void setOnItemLongClickListener(String key, OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListenerHashMap.put(key, onItemLongClickListener);
    }

    public void clearOnItemLongClickListener(String key) {
        this.onItemLongClickListenerHashMap.remove(key);
    }

    public ImageFragmentCreate getImageFragmentCreate(String key) {
        return imageFragmentCreateHashMap.get(key);
    }

    public void setImageFragmentCreate(String key, ImageFragmentCreate imageFragmentCreate) {
        this.imageFragmentCreateHashMap.put(key, imageFragmentCreate);
    }

    public void clearImageFragmentCreate(String key) {
        this.imageFragmentCreateHashMap.remove(key);
    }

    public VideoFragmentCreate getVideoFragmentCreate(String key) {
        return videoFragmentCreateHashMap.get(key);
    }

    public void setVideoFragmentCreate(String key, VideoFragmentCreate videoFragmentCreate) {
        this.videoFragmentCreateHashMap.put(key, videoFragmentCreate);
    }

    public void clearVideoFragmentCreate(String key) {
        this.videoFragmentCreateHashMap.remove(key);
    }

    public List<MoreViewOption> getMoreViewOption(String key) {
        return moreViewOptionHashMap.get(key);
    }

    public void setMoreViewOption(String key, List<MoreViewOption> moreViewOptions) {
        this.moreViewOptionHashMap.put(key, moreViewOptions);
    }

    public void clearMoreViewOption(String key) {
        List<MoreViewOption> viewOptions = moreViewOptionHashMap.get(key);
        if (viewOptions != null){
            for (MoreViewOption moreViewOption : viewOptions) {
                if (moreViewOption != null){
                    moreViewOption.setView(null);
                }
            }
        }
        this.moreViewOptionHashMap.remove(key);
    }

    public UpperLayerOption getUpperLayerFragmentCreate(String key) {
        return upperLayerFragmentCreateHashMap.get(key);
    }

    public void setUpperLayerFragmentCreate(String key, UpperLayerOption upperLayerOption) {
        this.upperLayerFragmentCreateHashMap.put(key, upperLayerOption);
    }

    public void clearUpperLayerFragmentCreate(String key) {
        this.upperLayerFragmentCreateHashMap.remove(key);
    }


    public boolean isCanOpenViewPagerActivity(String key) {
        Boolean canOpen = canOpenViewPageActivityHashMap.get(key);
        if (canOpen == null){
            canOpen = true;
            setCanOpenViewPagerActivity(key,true);
        }
        return canOpen;
    }

    public void setCanOpenViewPagerActivity(String key, boolean canOpen) {
        canOpenViewPageActivityHashMap.put(key, canOpen);
    }

    public void clearCanOpenViewPagerActivity(String key) {
        canOpenViewPageActivityHashMap.remove(key);
    }
}
