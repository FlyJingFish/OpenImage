package com.flyjingfish.openimage.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.flyjingfish.openimage.R;
import com.flyjingfish.openimage.databinding.ActivityMemoryTestBinding;
import com.flyjingfish.openimage.fragment.TestFragment;
import com.flyjingfish.openimage.fragment.TestFragment2;

public class MemoryTestActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMemoryTestBinding binding = ActivityMemoryTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnFragmentX.setOnClickListener(v -> {
            TestFragment testFragment = new TestFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_container, testFragment)
                    .commitAllowingStateLoss();
        });
        binding.btnFragment.setOnClickListener(v -> {
            TestFragment2 testFragment = new TestFragment2();
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_container, testFragment)
                    .commitAllowingStateLoss();
        });

        binding.btnActivity.setOnClickListener(v -> {
            startActivity(new Intent(this, MemoryTestRecyclerViewActivity.class));
        });
    }
}
