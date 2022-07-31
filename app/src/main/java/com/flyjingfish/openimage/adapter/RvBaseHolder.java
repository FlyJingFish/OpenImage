package com.flyjingfish.openimage.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RvBaseHolder extends RecyclerView.ViewHolder {
    public RvBaseHolder(@NonNull View itemView) {
        super(itemView);
    }
    public  <T extends View> T getView(int id) {
        return (T) itemView.findViewById(id);
    }
}
