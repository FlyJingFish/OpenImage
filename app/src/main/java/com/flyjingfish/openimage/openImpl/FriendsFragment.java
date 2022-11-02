package com.flyjingfish.openimage.openImpl;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.flyjingfish.openimage.bean.ImageItem;
import com.flyjingfish.openimage.databinding.LayoutFriendsBinding;
import com.flyjingfish.openimagelib.BaseUpperLayerFragment;
import com.flyjingfish.openimagelib.ViewPagerActivity;

public class FriendsFragment extends BaseUpperLayerFragment {

    private com.flyjingfish.openimage.databinding.LayoutFriendsBinding binding;
    private ImageItem imageItem;

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
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (imageItem != null){
            binding.tvText.setText(imageItem.text);
        }
        binding.ivBack.setOnClickListener(v -> ((ViewPagerActivity) requireActivity()).close(false));
        binding.vTouch.setOnClickListener(v -> {
            if (binding.rlBottom.getVisibility() == View.VISIBLE){
                binding.rlBottom.setVisibility(View.GONE);
                binding.rlTop.setVisibility(View.GONE);
            }else {
                binding.rlBottom.setVisibility(View.VISIBLE);
                binding.rlTop.setVisibility(View.VISIBLE);
            }
        });
    }
}
