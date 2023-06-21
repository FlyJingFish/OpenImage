package com.flyjingfish.openimagelib.listener;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 如果你的{@link androidx.recyclerview.widget.RecyclerView}设置了除以下三个<br/>
 * {@link androidx.recyclerview.widget.LinearLayoutManager}、<br/>
 * {@link androidx.recyclerview.widget.GridLayoutManager}、<br/>
 * {@link androidx.recyclerview.widget.StaggeredGridLayoutManager}<br/>
 * 之外的{@link androidx.recyclerview.widget.RecyclerView.LayoutManager}，<br/>
 * 你需要在{@link com.flyjingfish.openimagelib.OpenImage#setClickRecyclerView(RecyclerView, LayoutManagerFindVisiblePosition, SourceImageViewIdGet)}设置这个接口才可以，否则将会抛出异常
 */
public interface LayoutManagerFindVisiblePosition {
    /**
     * @return 返回 RecyclerView 在屏幕上第一个可见 Item 的位置
     */
    int findFirstVisibleItemPosition();

    /**
     * @return 返回 RecyclerView 在屏幕上最后一个可见 Item 的位置
     */
    int findLastVisibleItemPosition();
}
