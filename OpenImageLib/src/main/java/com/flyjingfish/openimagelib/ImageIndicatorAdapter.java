package com.flyjingfish.openimagelib;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flyjingfish.openimagelib.databinding.OpenImageIndicatorImageBinding;
import com.flyjingfish.openimagelib.enums.OpenImageOrientation;

class ImageIndicatorAdapter extends RecyclerView.Adapter<ImageIndicatorAdapter.IndicatorViewHolder> {
    private int total;
    private int selectPosition;
    private float interval;
    private int imageRes;
    private OpenImageOrientation orientation;

    public ImageIndicatorAdapter(int total, float interval,int imageRes,OpenImageOrientation orientation) {
        this.total = total;
        this.interval = interval;
        this.imageRes = imageRes;
        this.orientation = orientation;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @NonNull
    @Override
    public IndicatorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new IndicatorViewHolder(OpenImageIndicatorImageBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false).getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull IndicatorViewHolder holder, int position) {
        OpenImageIndicatorImageBinding binding = OpenImageIndicatorImageBinding.bind(holder.itemView);
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) binding.ivShowPos.getLayoutParams();
        if (orientation == OpenImageOrientation.HORIZONTAL){
            layoutParams.leftMargin = (int) (interval/2);
            layoutParams.rightMargin = (int) (interval/2);
        }else {
            layoutParams.topMargin = (int) (interval/2);
            layoutParams.bottomMargin = (int) (interval/2);
        }
        binding.ivShowPos.setLayoutParams(layoutParams);
        if (imageRes != 0){
            binding.ivShowPos.setImageResource(imageRes);
        }
        binding.ivShowPos.setSelected(position == selectPosition);
    }

    @Override
    public int getItemCount() {
        return total;
    }

    public void setSelectPosition(int selectPosition) {
        this.selectPosition = selectPosition;
        notifyDataSetChanged();
    }

    protected class IndicatorViewHolder extends RecyclerView.ViewHolder{

        public IndicatorViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
