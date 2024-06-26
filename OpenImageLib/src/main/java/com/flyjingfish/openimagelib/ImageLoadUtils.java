package com.flyjingfish.openimagelib;

import android.graphics.drawable.Drawable;

import androidx.viewpager2.widget.ViewPager2;

import com.flyjingfish.openimagelib.beans.CloseParams;
import com.flyjingfish.openimagelib.beans.DownloadParams;
import com.flyjingfish.openimagelib.listener.ImageFragmentCreate;
import com.flyjingfish.openimagelib.listener.ItemLoadHelper;
import com.flyjingfish.openimagelib.listener.OnItemClickListener;
import com.flyjingfish.openimagelib.listener.OnItemLongClickListener;
import com.flyjingfish.openimagelib.listener.OnPermissionsInterceptListener;
import com.flyjingfish.openimagelib.listener.OnSelectMediaListener;
import com.flyjingfish.openimagelib.listener.OnUpdateViewListener;
import com.flyjingfish.openimagelib.listener.VideoFragmentCreate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ImageLoadUtils {
    private static volatile ImageLoadUtils mInstance;
    private final HashMap<String, Boolean> imageLoadSuccessMap = new HashMap<>();
    private final HashMap<String, ItemLoadHelper> itemLoadHelperHashMap = new HashMap<>();
    private final HashMap<String, Drawable> coverDrawableHashMap = new HashMap<>();
    private final HashMap<String, String> coverFilePathHashMap = new HashMap<>();
    private final HashMap<String, Drawable> smallCoverDrawableHashMap = new HashMap<>();
    private final HashMap<String, OnSelectMediaListener> onSelectMediaListenerHashMap = new HashMap<>();
    private final HashMap<String, List<ViewPager2.PageTransformer>> pageTransformerMap = new HashMap<>();
    private final HashMap<String, OnItemClickListener> onItemClickListenerHashMap = new HashMap<>();
    private final HashMap<String, OnItemLongClickListener> onItemLongClickListenerHashMap = new HashMap<>();
    private final HashMap<String, List<MoreViewOption>> moreViewOptionHashMap = new HashMap<>();
    private final HashMap<String, OnBackView> onBackViewHashMap = new HashMap<>();
    private final HashMap<String, ImageFragmentCreate> imageFragmentCreateHashMap = new HashMap<>();
    private final HashMap<String, VideoFragmentCreate> videoFragmentCreateHashMap = new HashMap<>();
    private final HashMap<String, UpperLayerOption> upperLayerFragmentCreateHashMap = new HashMap<>();
    private final HashMap<String, Boolean> canOpenViewPageActivityHashMap = new HashMap<>();
    private final HashMap<String, List<OpenImageDetail>> openDataMap = new HashMap<>();
    private final HashMap<String, OpenImageDetail> openDetailDataMap = new HashMap<>();
    private final HashMap<String, OnUpdateViewListener> onUpdateViewListenerHashMap = new HashMap<>();
    private final HashMap<String, DownloadParams> downloadParamsHashMap = new HashMap<>();
    private final HashMap<String, CloseParams> closeParamsHashMap = new HashMap<>();
    private final HashMap<String, OnPermissionsInterceptListener> onPermissionsInterceptListenerHashMap = new HashMap<>();
    private boolean isApkInDebug;

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

    public void setSmallCoverDrawable(String key, Drawable drawable) {
        smallCoverDrawableHashMap.put(key, drawable);
    }

    public Drawable getSmallCoverDrawable(String key) {
        return smallCoverDrawableHashMap.get(key);
    }

    public void clearSmallCoverDrawable(String key) {
        smallCoverDrawableHashMap.remove(key);
    }

    public void clearAllSmallCoverDrawable() {
        smallCoverDrawableHashMap.clear();
    }
    public void clearAllCoverDrawable() {
        coverDrawableHashMap.clear();
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


    public boolean isCanOpenOpenImageActivity(String key) {
        Boolean canOpen = canOpenViewPageActivityHashMap.get(key);
        if (canOpen == null){
            canOpen = true;
            setCanOpenOpenImageActivity(key,true);
        }
        return canOpen;
    }

    public void setCanOpenOpenImageActivity(String key, boolean canOpen) {
        canOpenViewPageActivityHashMap.put(key, canOpen);
    }

    public void clearCanOpenOpenImageActivity(String key) {
        canOpenViewPageActivityHashMap.remove(key);
    }

    public void setOpenImageDetailData(String key, List<OpenImageDetail> openImageDetails) {
        openDataMap.put(key, openImageDetails);
    }

    public List<OpenImageDetail> getOpenImageDetailData(String key) {
        return openDataMap.get(key);
    }

    public void clearOpenImageDetailData(String key) {
        openDataMap.remove(key);
    }

    public void setOpenImageDetail(String key, OpenImageDetail openImageDetail) {
        openDetailDataMap.put(key, openImageDetail);
    }

    public OpenImageDetail getOpenImageDetail(String key) {
        return openDetailDataMap.get(key);
    }

    public void clearOpenImageDetail(String contextKey) {
        List<String> keyList = new ArrayList<>();
        for (Map.Entry<String, OpenImageDetail> helper : openDetailDataMap.entrySet()) {
            if (helper.getKey().contains(contextKey)) {
                keyList.add(helper.getKey());
            }
        }
        for (String key : keyList) {
            openDetailDataMap.remove(key);
        }
    }

    public boolean isApkInDebug() {
        return isApkInDebug;
    }

    public void setApkInDebug(boolean apkInDebug) {
        isApkInDebug = apkInDebug;
    }

    public OnUpdateViewListener getOnUpdateViewListener(String key) {
        return onUpdateViewListenerHashMap.get(key);
    }

    public void setOnUpdateViewListener(String key, OnUpdateViewListener onUpdateViewListener) {
        this.onUpdateViewListenerHashMap.put(key, onUpdateViewListener);
    }

    public void clearOnUpdateViewListener(String key) {
        onUpdateViewListenerHashMap.remove(key);
    }

    public long getUniqueId(){
        return OpenImageDetailIdUtil.nextId();
    }

    public DownloadParams getDownloadParams(String key) {
        return downloadParamsHashMap.get(key);
    }

    public void setDownloadParams(String key, DownloadParams downloadParams) {

        this.downloadParamsHashMap.put(key, downloadParams);
    }

    public void clearDownloadParams(String key) {
        downloadParamsHashMap.remove(key);
    }

    public CloseParams getCloseParams(String key) {
        return closeParamsHashMap.get(key);
    }

    public void setCloseParams(String key, CloseParams closeParams) {

        this.closeParamsHashMap.put(key, closeParams);
    }

    public void clearCloseParams(String key) {
        closeParamsHashMap.remove(key);
    }

    public void setCoverFilePath(String key, String filePath) {
        coverFilePathHashMap.put(key, filePath);
    }

    public String getCoverFilePath(String key) {
        return coverFilePathHashMap.get(key);
    }

    public void clearCoverFilePath(String key) {
        coverFilePathHashMap.remove(key);
    }

    public OnPermissionsInterceptListener getPermissionsInterceptListener(String key) {
        return onPermissionsInterceptListenerHashMap.get(key);
    }

    public void setPermissionsInterceptListener(String key, OnPermissionsInterceptListener onPermissionsInterceptListener) {
        this.onPermissionsInterceptListenerHashMap.put(key, onPermissionsInterceptListener);
    }

    public void clearPermissionsInterceptListener(String key) {
        this.onPermissionsInterceptListenerHashMap.remove(key);
    }
}
