package com.flyjingfish.openimagelib;

import android.graphics.drawable.Drawable;

import com.flyjingfish.openimagelib.beans.ContentViewOriginModel;
import com.flyjingfish.openimagelib.listener.ItemLoadHelper;
import com.flyjingfish.openimagelib.listener.OnSelectMediaListener;

import java.util.HashMap;
import java.util.List;

class ImageLoadUtils {
    private static volatile ImageLoadUtils mInstance;
    private HashMap<String, Boolean> imageLoadSuccessMap = new HashMap<>();
    private HashMap<String, ItemLoadHelper> itemLoadHelperHashMap = new HashMap<>();
    private HashMap<String, Drawable> coverDrawableHashMap = new HashMap<>();
    private HashMap<String, OnSelectMediaListener> onSelectMediaListenerHashMap = new HashMap<>();
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

    public void setCoverDrawable(String key, Drawable drawable){
        coverDrawableHashMap.put(key,drawable);
    }

    public Drawable getCoverDrawable(String key){
        return coverDrawableHashMap.get(key);
    }

    public void clearCoverDrawable(String key){
        coverDrawableHashMap.remove(key);
    }

    public interface OnBackView{
        void onBack();
        void onScrollPos(int pos);
        List<ContentViewOriginModel> onGetContentViewOriginModel();
    }
    OnBackView onBackView;

    public OnBackView getOnBackView() {
        return onBackView;
    }

    public void setOnBackView(OnBackView onBackView) {
        this.onBackView = onBackView;
    }

    public OnSelectMediaListener getOnSelectMediaListener(String key) {
        return onSelectMediaListenerHashMap.get(key);
    }

    public void setOnSelectMediaListener(String key,OnSelectMediaListener onSelectMediaListener) {
        this.onSelectMediaListenerHashMap.put(key,onSelectMediaListener);
    }
    public void clearOnSelectMediaListener(String key) {
        this.onSelectMediaListenerHashMap.remove(key);
    }

}
