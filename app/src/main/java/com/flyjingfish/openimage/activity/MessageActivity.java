package com.flyjingfish.openimage.activity;

import android.os.Bundle;

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
    public static ExecutorService cThreadPool = Executors.newFixedThreadPool(5);;
    private ActivityMessageBinding binding;

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

}
