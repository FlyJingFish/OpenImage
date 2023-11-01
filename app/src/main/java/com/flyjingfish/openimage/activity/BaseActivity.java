package com.flyjingfish.openimage.activity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.flyjingfish.openimage.R;
import com.flyjingfish.openimagelib.utils.ScreenUtils;
import com.flyjingfish.titlebarlib.TitleBar;

public class BaseActivity extends AppCompatActivity {
    protected String title;
    protected static final String TITLE = "title";
    protected TitleBar titleBar;

    public boolean isShowTitleBar(){
        return true;
    }

    public String getTitleString(){
        return title;
    }

    public boolean titleAboveContent(){
        return true;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getIntent().getStringExtra(TITLE);
        titleBar = new TitleBar(this);
        titleBar.setShadow(4, getResources().getColor(R.color.shadow_color), TitleBar.ShadowType.GRADIENT);
        titleBar.setTitleGravity(TitleBar.TitleGravity.CENTER);
        titleBar.setOnBackViewClickListener(v -> finish());
        titleBar.setTitleBarBackgroundColorWithStatusBar(getResources().getColor(R.color.teal_200));
        if (isShowTitleBar()){
            titleBar.show();
        }else {
            titleBar.hide();
        }
        titleBar.getTitleView().setTypeface(null, Typeface.BOLD);
        titleBar.getTitleView().setPadding((int) ScreenUtils.dp2px(this,16),0, (int) ScreenUtils.dp2px(this,16),0);
        titleBar.getBackImageView().setImageResource(R.drawable.ic_back_white);
        titleBar.setTitleGravity(TitleBar.TitleGravity.START);
        titleBar.getTitleView().setTextColor(Color.WHITE);
        titleBar.getBackImageView().setVisibility(isShowBack()? View.VISIBLE:View.GONE);
        titleBar.setTitle(getTitleString());
        titleBar.setAboveContent(titleAboveContent());
        titleBar.attachToWindow();//这句只用在TitleBar未加入到页面上时使用
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
//    @Override
//    public void onBackPressed() {
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
//            finish();
//        }else {
//            super.onBackPressed();
//        }
//    }
}
