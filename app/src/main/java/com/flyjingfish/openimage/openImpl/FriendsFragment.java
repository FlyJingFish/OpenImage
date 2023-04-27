package com.flyjingfish.openimage.openImpl;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.flyjingfish.openimage.bean.ImageItem;
import com.flyjingfish.openimage.databinding.LayoutFriendsBinding;
import com.flyjingfish.openimagelib.BaseInnerFragment;
import com.flyjingfish.openimagelib.OpenImageActivity;
import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.listener.OnSelectMediaListener;
import com.flyjingfish.openimagelib.utils.ScreenUtils;

public class FriendsFragment extends BaseInnerFragment {

    private LayoutFriendsBinding binding;
    private ImageItem imageItem;
    private AnimatorSet hideAnim;
    private ObjectAnimator hideTopAnim;
    private ObjectAnimator hideBottomAnim;
    private boolean isHide;
    private ObjectAnimator bgAnim;
    private final int bgColor = Color.parseColor("#2c2c2c");

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null){
            imageItem = (ImageItem) bundle.getSerializable("ImageItem");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutFriendsBinding.inflate(inflater,container,false);
        ViewGroup.LayoutParams layoutParams = binding.vStatus.getLayoutParams();
        layoutParams.height = ScreenUtils.getStatusBarHeight(requireContext());
        binding.vStatus.setLayoutParams(layoutParams);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Activity activity = requireActivity();
        if (activity instanceof OpenImageActivity){
            OpenImageActivity openImageActivity = (OpenImageActivity) activity;
            View bgView = openImageActivity.getBgView();
            bgView.setBackgroundColor(bgColor);
        }
        if (imageItem != null){
            binding.tvText.setText(imageItem.text);
        }
        binding.ivBack.setOnClickListener(v -> close());

        addOnItemClickListener((fragment, openImageUrl, position) -> {
            if (isHide){
                clickShowAnim();
            }else {
                clickHideAnim();
            }
            isHide= !isHide;
        });

        addOnSelectMediaListener(new OnSelectMediaListener() {
            @Override
            public void onSelect(OpenImageUrl openImageUrl, int position) {
                Log.e("addOnSelectMedia","---"+position);
                if (imageItem != null){
                    binding.tvText.setText(imageItem.text+"(显示位置="+position+"),openImageUrl="+openImageUrl);
                }
            }
        });
    }

    private void initClickAnim(boolean isHide){
        if (hideAnim == null){
            Activity activity = requireActivity();
            if (activity instanceof OpenImageActivity){
                OpenImageActivity openImageActivity = (OpenImageActivity) activity;
                View bgView = openImageActivity.getBgView();
                bgAnim = ObjectAnimator.ofInt(bgView,"backgroundColor", Color.BLACK,bgColor);
                bgAnim.setDuration(3000);
                bgAnim.setEvaluator(new android.animation.ArgbEvaluator());
            }
            hideTopAnim = ObjectAnimator.ofFloat(binding.rlTop,"translationY",0,-binding.rlTop.getHeight());
            hideBottomAnim = ObjectAnimator.ofFloat(binding.llBottom,"translationY",0,binding.llBottom.getHeight());
            hideBottomAnim = ObjectAnimator.ofFloat(binding.llBottom,"translationY",0,binding.llBottom.getHeight());
            hideAnim = new AnimatorSet();
            if (bgAnim != null){
                hideAnim.playTogether(hideTopAnim, hideBottomAnim,bgAnim);
            }else {
                hideAnim.playTogether(hideTopAnim, hideBottomAnim);
            }
            hideAnim.setDuration(240);
        }

        if (isHide){
            hideTopAnim.setFloatValues(0,-binding.rlTop.getHeight());
            hideBottomAnim.setFloatValues(0,binding.llBottom.getHeight());
            if (bgAnim != null){
                bgAnim.setIntValues(bgColor,Color.BLACK);
            }
        }else {
            hideTopAnim.setFloatValues(-binding.rlTop.getHeight(),0);
            hideBottomAnim.setFloatValues(binding.llBottom.getHeight(),0);
            if (bgAnim != null){
                bgAnim.setIntValues(Color.BLACK,bgColor);
            }
        }
    }

    private void clickHideAnim(){
        initClickAnim(true);
        hideAnim.start();
    }

    private void clickShowAnim(){
        initClickAnim(false);
        hideAnim.start();
    }

    @Override
    protected void onTouchScale(float scale) {
        super.onTouchScale(scale);
        if (!isHide){
            binding.rlTop.setTranslationY(-binding.rlTop.getHeight()*(1-scale)*4);
            binding.llBottom.setTranslationY(binding.llBottom.getHeight()*(1-scale)*4);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (hideAnim != null){
            hideAnim.cancel();
        }
    }
}
