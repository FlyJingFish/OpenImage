package com.flyjingfish.openimage.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.flyjingfish.openimage.databinding.ActivityWebviewBinding;

public class WebViewActivity extends BaseActivity{

    private com.flyjingfish.openimage.databinding.ActivityWebviewBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWebviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnSingle.setOnClickListener(v -> {
            jump(v, WebViewActivity1.class);
        });
        binding.btnMuch.setOnClickListener(v -> {
            jump(v, WebViewActivity2.class);
        });
    }

    private void jump(View v, Class<?> cls){
        Intent intent = new Intent(this,cls);
        intent.putExtra(TITLE, ((TextView) v).getText().toString());
        startActivity(intent);
    }
}
