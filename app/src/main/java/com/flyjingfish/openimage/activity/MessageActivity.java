package com.flyjingfish.openimage.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.flyjingfish.openimage.R;
import com.flyjingfish.openimage.databinding.ActivityMessageBinding;
import com.flyjingfish.openimage.dialog.MessageMenuPop;
import com.flyjingfish.openimage.fragment.MsgListViewViewFragment;
import com.flyjingfish.openimage.fragment.MsgRecyclerViewFragment;
import com.flyjingfish.openimage.openImpl.MessageVpActivity;

public class MessageActivity extends BaseActivity {
    private ActivityMessageBinding binding;
    public boolean openWechatEffect;
    public boolean openAutoScroll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return position==0?new MsgRecyclerViewFragment():new MsgListViewViewFragment();
            }

            @Override
            public int getItemCount() {
                return 2;
            }
        });
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.btnRv.setSelected(position == 0);
                binding.btnLv.setSelected(position == 1);
            }
        });
        binding.btnRv.setOnClickListener(v -> binding.viewPager.setCurrentItem(0));
        binding.btnLv.setOnClickListener(v -> binding.viewPager.setCurrentItem(1));

        final MessageMenuPop messageMenuPop = new MessageMenuPop(this);
        messageMenuPop.setOnMenuClickListener(new MessageMenuPop.OnMenuClickListener() {
            @Override
            public void onWechatEffectClick(TextView textView) {
                CharSequence title = textView.getText();
                boolean isOpen = TextUtils.equals(title,"关闭微信补位效果");
                openWechatEffect = !isOpen;
                if (openWechatEffect){
                    Toast.makeText(MessageActivity.this,"打开微信补位效果后，跟随滚动失效",Toast.LENGTH_SHORT).show();
                }
                textView.setText(isOpen?"打开微信补位效果":"关闭微信补位效果");
                messageMenuPop.dismiss();
            }

            @Override
            public void onAutoScrollClick(TextView textView) {
                CharSequence title = textView.getText();
                boolean isAutoScroll = TextUtils.equals(title,"关闭跟随滚动");
                openAutoScroll = !isAutoScroll;
                textView.setText(isAutoScroll?"打开跟随滚动":"关闭跟随滚动");
                messageMenuPop.dismiss();
            }
        });
        titleBar.getRightImageView().setImageResource(R.drawable.ic_more_white);
        titleBar.getRightImageView().setOnClickListener(messageMenuPop::show);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MessageVpActivity.addCount = 0;
    }
}
