package com.flyjingfish.openimagelib.listener;

import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.enums.UpdateViewType;

import java.util.Collection;

public interface OnUpdateViewListener {
    /**
     * 新加数据后回调此方法，你必须在继承自{@link com.flyjingfish.openimagelib.OpenImageActivity}的自定义 Activity 中调用{@link com.flyjingfish.openimagelib.OpenImageFragmentStateAdapter#addData(Collection)},
     * 或{@link com.flyjingfish.openimagelib.OpenImageFragmentStateAdapter#addFrontData(Collection)}，才会回调此方法
     *
     * @param data           新增数据
     * @param updateViewType 更新数据类型{@link UpdateViewType#FORWARD}表示你要把数据加到列表前边，{@link UpdateViewType#BACKWARD}表示你要把数据加到列表后边，
     *                       {@link UpdateViewType#NONE}表示不要更新前一页面列表
     */
    void onAdd(Collection<? extends OpenImageUrl> data, UpdateViewType updateViewType);

    /**
     * 删除数据后回调此方法
     * @param openImageUrl 删除的数据
     */
    void onRemove(OpenImageUrl openImageUrl);

    /**
     * 替换数据后回调此方法，如果想更新数据也用此方法
     * @param position 替换的数据位置
     * @param oldData 替换前的旧数据
     * @param newData 替换后的新数据
     */
    void onReplace(int position, OpenImageUrl oldData, OpenImageUrl newData);
}
