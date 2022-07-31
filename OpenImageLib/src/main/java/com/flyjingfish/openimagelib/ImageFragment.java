package com.flyjingfish.openimagelib;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.flyjingfish.openimagelib.databinding.FragmentImageBinding;
import com.flyjingfish.openimagelib.widget.LoadingView;

public class ImageFragment extends BaseImageFragment<LoadingView> {

    private FragmentImageBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentImageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public View getExitImageView() {
        return super.getExitImageView();
    }

    @Override
    protected ImageView getSmallCoverImageView() {
        return binding.ivCoverFg;
    }

    @Override
    protected ImageView getPhotoView() {
        return binding.photoView;
    }

    @Override
    protected LoadingView getLoadingView() {
        return binding.loadingView;
    }
}
