package com.flyjingfish.openimage.activity;

import android.app.Instrumentation;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    protected String title;
    protected static final String TITLE = "title";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        title = getIntent().getStringExtra(TITLE);
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(isShowBack()); // 决定左上角图标的右侧是否有向左的小箭头, true
// 有小箭头，并且图标可以点击
            actionBar.setDisplayShowHomeEnabled(true);
            if (!TextUtils.isEmpty(title)){
                actionBar.setTitle(title);
            }
        }
    }

    public boolean isShowBack() {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:// 点击返回图标事件
                this.finish();
            default:
                return super.onOptionsItemSelected(item);

        }

    }
    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            finish();
        }else {
            super.onBackPressed();
        }
    }
}
