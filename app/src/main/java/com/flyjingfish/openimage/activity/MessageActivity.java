package com.flyjingfish.openimage.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.flyjingfish.openimage.fragment.MsgListViewViewFragment;
import com.flyjingfish.openimage.fragment.MsgRecyclerViewFragment;
import com.flyjingfish.openimage.databinding.ActivityMessageBinding;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageActivity extends AppCompatActivity {
    private ActivityMessageBinding binding;
    private MenuItem wechatEffect;
    private MenuItem autoScroll;
    public static boolean openWechatEffect;
    public static boolean openAutoScroll;
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        wechatEffect = menu.add("打开微信补位效果");
        autoScroll = menu.add("打开跟随滚动");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item == wechatEffect){
            CharSequence title = item.getTitle();
            boolean isOpen = TextUtils.equals(title,"关闭微信补位效果");
            openWechatEffect = !isOpen;
            if (openWechatEffect){
                Toast.makeText(this,"打开微信补位效果后，跟随滚动失效",Toast.LENGTH_SHORT).show();
            }
            wechatEffect.setTitle(isOpen?"打开微信补位效果":"关闭微信补位效果");
        }else if (item == autoScroll){
            CharSequence title = item.getTitle();
            boolean isAutoScroll = TextUtils.equals(title,"关闭跟随滚动");
            openAutoScroll = !isAutoScroll;
            autoScroll.setTitle(isAutoScroll?"打开跟随滚动":"关闭跟随滚动");
        }
        return super.onOptionsItemSelected(item);
    }
}
