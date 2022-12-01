package com.flyjingfish.openimagelib;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.flyjingfish.openimagelib.databinding.OpenImageFragmentImageBinding;
import com.flyjingfish.openimagelib.photoview.PhotoView;
import com.flyjingfish.openimagelib.widget.LoadingView;

public class ImageFragment extends BaseImageFragment<LoadingView> {

    private OpenImageFragmentImageBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = OpenImageFragmentImageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    protected PhotoView getSmallCoverImageView() {
        return binding.ivCoverFg;
    }

    @Override
    protected PhotoView getPhotoView() {
        return binding.photoView;
    }

    @Override
    protected View getItemClickableView() {
        return binding.photoView;
    }

    @Override
    protected LoadingView getLoadingView() {
        return binding.loadingView;
    }

    @Override
    protected void loadImageFinish(boolean isLoadImageSuccess) {

    }
}
