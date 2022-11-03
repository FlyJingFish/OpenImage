package com.flyjingfish.openimage.openImpl;

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
import com.flyjingfish.openimagelib.ViewPagerActivity;
import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.listener.OnSelectMediaListener;
import com.flyjingfish.openimagelib.utils.ScreenUtils;

public class FriendsFragment extends BaseInnerFragment {

    private LayoutFriendsBinding binding;
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
        ViewGroup.LayoutParams layoutParams = binding.vStatus.getLayoutParams();
        layoutParams.height = ScreenUtils.getStatusBarHeight(requireContext());
        binding.vStatus.setLayoutParams(layoutParams);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (imageItem != null){
            binding.tvText.setText(imageItem.text);
        }
        binding.ivBack.setOnClickListener(v -> ((ViewPagerActivity) requireActivity()).close(false));

        addOnItemClickListener((fragment, openImageUrl, position) -> {
            if (binding.rlBottom.getVisibility() == View.VISIBLE){
                binding.rlBottom.setVisibility(View.GONE);
                binding.rlTop.setVisibility(View.GONE);
            }else {
                binding.rlBottom.setVisibility(View.VISIBLE);
                binding.rlTop.setVisibility(View.VISIBLE);
            }
        });

        addOnSelectMediaListener(new OnSelectMediaListener() {
            @Override
            public void onSelect(OpenImageUrl openImageUrl, int position) {
                Log.e("addOnSelectMedia","---"+position);
            }
        });
    }
}
