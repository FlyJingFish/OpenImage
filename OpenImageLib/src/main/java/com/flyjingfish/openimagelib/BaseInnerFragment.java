package com.flyjingfish.openimagelib;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.listener.DownloadMediaHelper;
import com.flyjingfish.openimagelib.listener.OnDownloadMediaListener;
import com.flyjingfish.openimagelib.listener.OnItemClickListener;
import com.flyjingfish.openimagelib.listener.OnItemLongClickListener;
import com.flyjingfish.openimagelib.listener.OnSelectMediaListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BaseInnerFragment extends Fragment {

    private PhotosViewModel basePhotosViewModel;
    protected List<OnItemClickListener> onItemClickListeners = new ArrayList<>();
    protected List<OnItemLongClickListener> onItemLongClickListeners = new ArrayList<>();
    private final List<String> onItemClickListenerKeys = new ArrayList<>();
    private final List<String> onItemLongClickListenerKeys = new ArrayList<>();
    protected float currentScale = 1f;
    private List<OpenImageUrl> downloadList;
    private final List<ActivityResultCallback<Map<String, Boolean>>> activityResultCallbacks = new ArrayList<>();
    private ActivityResultLauncher<String[]> launcher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        launcher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    Iterator<ActivityResultCallback<Map<String, Boolean>>> iterator = activityResultCallbacks.iterator();
                    while (iterator.hasNext()){
                        ActivityResultCallback<Map<String, Boolean>> activityResultCallback = iterator.next();
                        activityResultCallback.onActivityResult(result);
                        iterator.remove();
                    }
                });
    }

    private void addActivityResultCallback(ActivityResultCallback<Map<String, Boolean>> activityResultCallback){
        activityResultCallbacks.add(activityResultCallback);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        basePhotosViewModel = new ViewModelProvider(requireActivity()).get(PhotosViewModel.class);
        basePhotosViewModel.onAddItemListenerLiveData.observe(getViewLifecycleOwner(), s -> {
            OnItemClickListener onItemClickListener = ImageLoadUtils.getInstance().getOnItemClickListener(s);
            if (onItemClickListener != null){
                onItemClickListeners.add(onItemClickListener);
            }
        });

        basePhotosViewModel.onAddItemLongListenerLiveData.observe(getViewLifecycleOwner(), s -> {
            OnItemLongClickListener onItemLongClickListener = ImageLoadUtils.getInstance().getOnItemLongClickListener(s);
            if (onItemLongClickListener != null){
                onItemLongClickListeners.add(onItemLongClickListener);
            }
        });

        basePhotosViewModel.onRemoveItemListenerLiveData.observe(getViewLifecycleOwner(), s -> {
            OnItemClickListener onItemClickListener = ImageLoadUtils.getInstance().getOnItemClickListener(s);
            if (onItemClickListener != null){
                onItemClickListeners.remove(onItemClickListener);
            }
            ImageLoadUtils.getInstance().clearOnItemClickListener(s);
        });

        basePhotosViewModel.onRemoveItemLongListenerLiveData.observe(getViewLifecycleOwner(), s -> {
            OnItemLongClickListener onItemLongClickListener = ImageLoadUtils.getInstance().getOnItemLongClickListener(s);
            if (onItemLongClickListener != null){
                onItemLongClickListeners.remove(onItemLongClickListener);
            }
            ImageLoadUtils.getInstance().clearOnItemLongClickListener(s);
        });

        basePhotosViewModel.onTouchCloseLiveData.observe(getViewLifecycleOwner(), aFloat -> {
            if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED){
                onTouchClose(aFloat);
            }else {
                getViewLifecycleOwner().getLifecycle().addObserver(new LifecycleEventObserver() {
                    @Override
                    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                        if (event == Lifecycle.Event.ON_RESUME){
                            onTouchClose(aFloat);
                            source.getLifecycle().removeObserver(this);
                        }
                    }
                });
            }
        });
        basePhotosViewModel.onTouchScaleLiveData.observe(getViewLifecycleOwner(), aFloat -> {
            if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED){
                onTouchScale(aFloat);
            }else {
                getViewLifecycleOwner().getLifecycle().addObserver(new LifecycleEventObserver() {
                    @Override
                    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                        if (event == Lifecycle.Event.ON_RESUME){
                            onTouchScale(aFloat);
                            source.getLifecycle().removeObserver(this);
                        }
                    }
                });
            }
        });
    }

    /**
     * 触摸拖动关闭时回调此方法
     * @param scale 图片缩放比例
     */
    protected void onTouchClose(float scale){
        currentScale = scale;
    }
    /**
     * 触摸拖动图片时回调此方法
     * @param scale 图片缩放比例
     */
    protected void onTouchScale(float scale){
        currentScale = scale;
    }

    protected void addOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener){
        try {
            if (onItemLongClickListener != null){
                ImageLoadUtils.getInstance().setOnItemLongClickListener(onItemLongClickListener.toString(),onItemLongClickListener);
                basePhotosViewModel.onAddItemLongListenerLiveData.setValue(onItemLongClickListener.toString());
                onItemLongClickListenerKeys.add(onItemLongClickListener.toString());
            }
        } catch (Exception e) {
            hintRuntimeException();
        }
    }

    protected void addOnItemClickListener(OnItemClickListener onItemClickListener){
        try {
            if (onItemClickListener != null){
                ImageLoadUtils.getInstance().setOnItemClickListener(onItemClickListener.toString(),onItemClickListener);
                basePhotosViewModel.onAddItemListenerLiveData.setValue(onItemClickListener.toString());
                onItemClickListenerKeys.add(onItemClickListener.toString());
            }
        } catch (Exception e) {
            hintRuntimeException();
        }
    }

    protected void removeOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener){
        try {
            if (onItemLongClickListener != null){
                basePhotosViewModel.onRemoveItemLongListenerLiveData.setValue(onItemLongClickListener.toString());
            }
        } catch (Exception e) {
            hintRuntimeException();
        }
    }

    protected void removeOnItemClickListener(OnItemClickListener onItemClickListener){
        try {
            if (onItemClickListener != null){
                basePhotosViewModel.onRemoveItemListenerLiveData.setValue(onItemClickListener.toString());
            }
        } catch (Exception e) {
            hintRuntimeException();
        }
    }


    protected void addOnSelectMediaListener(OnSelectMediaListener onSelectMediaListener){
        try {
            if (onSelectMediaListener != null){
                ImageLoadUtils.getInstance().setOnSelectMediaListener(onSelectMediaListener.toString(),onSelectMediaListener);
                basePhotosViewModel.onAddOnSelectMediaListenerLiveData.setValue(onSelectMediaListener.toString());
            }
        } catch (Exception e) {
            hintRuntimeException();
        }
    }

    protected void removeOnSelectMediaListener(OnSelectMediaListener onSelectMediaListener){
        try {
            if (onSelectMediaListener != null){
                basePhotosViewModel.onRemoveOnSelectMediaListenerLiveData.setValue(onSelectMediaListener.toString());
            }
        } catch (Exception e) {
            hintRuntimeException();
        }
    }

    /**
     * 检测权限并下载图片或视频
     * @param openImageUrl 下载的项目
     * @param onDownloadMediaListener 下载监听
     * @param requestWriteExternalStoragePermissionsFail 请求存储权限失败后 Toast 的文案，如果为null 或 “” 则不显示
     */
    protected void checkPermissionAndDownload(OpenImageUrl openImageUrl,OnDownloadMediaListener onDownloadMediaListener,@Nullable String requestWriteExternalStoragePermissionsFail) {
        DownloadMediaHelper downloadMediaHelper = OpenImageConfig.getInstance().getDownloadMediaHelper();
        if (downloadMediaHelper == null) {
            if (ImageLoadUtils.getInstance().isApkInDebug()) {
                throw new IllegalArgumentException("DownloadMediaHelper 不可为null 请调用 OpenImageConfig 的 setDownloadMediaHelper 来设置");
            }
            return;
        }
        boolean isPermission = PermissionChecker.isCheckWriteReadStorage(requireContext());
        if (isPermission){
            downloadMedia(openImageUrl,onDownloadMediaListener);
        }else {
            final String[] permissions = PermissionConfig.getReadPermissionArray();
            addActivityResultCallback(result -> {
                boolean isGranted = false;
                if (result != null){
                    int grantCount = 0;
                    for (String permission : permissions) {
                        Boolean b = result.get(permission);
                        if (b != null && b){
                            grantCount ++;
                        }
                    }
                    isGranted = grantCount==permissions.length;
                }
                if (isGranted) {
                    downloadMedia(openImageUrl,onDownloadMediaListener);
                } else if (!TextUtils.isEmpty(requestWriteExternalStoragePermissionsFail)){
                    Toast.makeText(requireContext(),requestWriteExternalStoragePermissionsFail,Toast.LENGTH_SHORT).show();
                }
            });

            launcher.launch(permissions);
        }
    }

    /**
     * 不会检测权限直接下载图片或视频
     * @param openImageUrl 下载的项目
     * @param onDownloadMediaListener 下载监听
     */
    protected void downloadMedia(OpenImageUrl openImageUrl,OnDownloadMediaListener onDownloadMediaListener) {
        DownloadMediaHelper downloadMediaHelper = OpenImageConfig.getInstance().getDownloadMediaHelper();
        if (downloadMediaHelper == null) {
            if (ImageLoadUtils.getInstance().isApkInDebug()) {
                throw new IllegalArgumentException("DownloadMediaHelper 不可为null 请调用 OpenImageConfig 的 setDownloadMediaHelper 来设置");
            }
            return;
        }
        if (downloadList == null) {
            downloadList = new ArrayList<>();
        }
        if (downloadList.contains(openImageUrl)) {
            return;
        }
        downloadList.add(openImageUrl);
        NetworkHelper.INSTANCE.download(requireActivity(), getViewLifecycleOwner(), openImageUrl, new OnDownloadMediaListener() {
            @Override
            public void onDownloadStart(boolean isWithProgress) {
                if (onDownloadMediaListener != null){
                    onDownloadMediaListener.onDownloadStart(isWithProgress);
                }
            }

            @Override
            public void onDownloadSuccess(String path) {
                if (onDownloadMediaListener != null){
                    onDownloadMediaListener.onDownloadSuccess(path);
                }
                downloadList.remove(openImageUrl);
            }

            @Override
            public void onDownloadProgress(int percent) {
                if (onDownloadMediaListener != null){
                    onDownloadMediaListener.onDownloadProgress(percent);
                }
            }

            @Override
            public void onDownloadFailed() {
                if (onDownloadMediaListener != null){
                    onDownloadMediaListener.onDownloadFailed();
                }
                downloadList.remove(openImageUrl);
            }
        });
    }


    /**
     * 关闭页面
     */
    public void close() {
        close(false);
    }

    private void close(boolean isTouchClose) {
        try {
            basePhotosViewModel.closeViewLiveData.setValue(isTouchClose);
        } catch (Exception e) {
            hintRuntimeException();
        }
    }

    private void hintRuntimeException(){
        if (ImageLoadUtils.getInstance().isApkInDebug()){
            throw new RuntimeException("请确保你是在 onViewCreated 及其之后的生命周期中调用的此方法");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        onItemClickListeners.clear();
        onItemLongClickListeners.clear();
        for (String onItemLongClickListenerKey : onItemLongClickListenerKeys) {
            ImageLoadUtils.getInstance().clearOnItemLongClickListener(onItemLongClickListenerKey);
        }

        for (String onItemClickListenerKey : onItemClickListenerKeys) {
            ImageLoadUtils.getInstance().clearOnItemClickListener(onItemClickListenerKey);
        }
        onItemClickListenerKeys.clear();
        onItemLongClickListenerKeys.clear();
    }

    public boolean onKeyBackDown(){
        return true;
    }
}
