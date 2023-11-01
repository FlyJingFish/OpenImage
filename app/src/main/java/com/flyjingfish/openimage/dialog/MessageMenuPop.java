package com.flyjingfish.openimage.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.flyjingfish.openimage.databinding.PopMsgMenuBinding;

public class MessageMenuPop extends PopupWindow {

    private final com.flyjingfish.openimage.databinding.PopMsgMenuBinding binding;
    private OnMenuClickListener onMenuClickListener;
    public MessageMenuPop(Context context) {
        super(context);
        binding = PopMsgMenuBinding.inflate(LayoutInflater.from(context),null,false);
        setContentView(binding.getRoot());
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setFocusable(true);
        setOutsideTouchable(true);

        binding.tvWechatEffect.setOnClickListener(v -> {
            if (onMenuClickListener != null){
                onMenuClickListener.onWechatEffectClick((TextView) v);
            }
        });
        binding.tvAutoScroll.setOnClickListener(v -> {
            if (onMenuClickListener != null){
                onMenuClickListener.onAutoScrollClick((TextView) v);
            }
        });
    }

    public void show(View anchor){
        showAsDropDown(anchor,0,0);
    }

    public interface OnMenuClickListener{
        void onWechatEffectClick(TextView textView);
        void onAutoScrollClick(TextView textView);
    }

    public void setOnMenuClickListener(OnMenuClickListener onMenuClickListener) {
        this.onMenuClickListener = onMenuClickListener;
    }
}
