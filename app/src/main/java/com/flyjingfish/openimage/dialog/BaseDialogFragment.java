package com.flyjingfish.openimage.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewbinding.ViewBinding;

import com.flyjingfish.openimage.R;


public abstract class BaseDialogFragment<VB extends ViewBinding> extends DialogFragment {

    protected VB binding;

    protected abstract VB setViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container);

    protected View rootView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.transparentDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = setViewBinding(inflater, container);
        if (binding != null) {
            return rootView = binding.getRoot();
        } else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        try {
            manager.beginTransaction().remove(this).commit();
            super.show(manager, tag);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void dismiss() {
        dismissAllowingStateLoss();
    }

}
