package com.flyjingfish.openimage.fragment;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;

import com.flyjingfish.openimage.DataUtils;
import com.flyjingfish.openimage.R;
import com.flyjingfish.openimage.bean.MessageBean;
import com.flyjingfish.openimage.MyApplication;
import com.flyjingfish.openimage.adapter.MsgLvAdapter;
import com.flyjingfish.openimage.databinding.FragmentMsgLvBinding;
import com.flyjingfish.openimage.databinding.LayoutListviewBinding;
import com.flyjingfish.switchkeyboardlib.MenuModeView;
import com.flyjingfish.switchkeyboardlib.SwitchKeyboardUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MsgListViewViewFragment extends BaseFragment {
    private FragmentMsgLvBinding binding;
    private SwitchKeyboardUtil switchKeyboardUtil;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMsgLvBinding.inflate(inflater,container,false);
        binding.listView.setDividerHeight(0);
        loadData();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        switchKeyboardUtil = new SwitchKeyboardUtil(requireActivity());
        switchKeyboardUtil.setMenuViewHeightEqualKeyboard(false);
        switchKeyboardUtil.setUseSwitchAnim(true);
        switchKeyboardUtil.setUseMenuUpAnim(true);
        switchKeyboardUtil.setInputEditText(binding.layoutMsg.etContent);
        switchKeyboardUtil.setAudioBtn(binding.layoutMsg.tvAudio);
        switchKeyboardUtil.setAudioTouchView(binding.layoutMsg.tvAudioTouch);
        switchKeyboardUtil.setMenuViewContainer(binding.layoutMsg.llMenu);
        switchKeyboardUtil.setToggleMenuViews(new MenuModeView(binding.layoutMsg.tvMore,binding.layoutMsg.llMenuBtn),
                new MenuModeView(binding.layoutMsg.ivFace,binding.layoutMsg.llEmoji));
        switchKeyboardUtil.setOnKeyboardMenuListener(new SwitchKeyboardUtil.OnKeyboardMenuListener() {
            @Override
            public void onScrollToBottom() {
                scrollToBottom();
            }

            @Override
            public void onCallShowKeyboard() {

            }

            @Override
            public void onCallHideKeyboard() {
            }

            @Override
            public void onKeyboardHide(int keyboardHeight) {

            }

            @Override
            public void onKeyboardShow(int keyboardHeight) {
                binding.layoutMsg.tvAudio.setImageResource(R.drawable.ic_audio);
                binding.layoutMsg.ivFace.setImageResource(R.drawable.ic_face);
            }



            @Override
            public void onShowMenuLayout(View layoutView) {
                binding.layoutMsg.tvAudio.setImageResource(layoutView == binding.layoutMsg.tvAudioTouch?R.drawable.ic_keyboard:R.drawable.ic_audio);
                binding.layoutMsg.ivFace.setImageResource(layoutView == binding.layoutMsg.llEmoji?R.drawable.ic_keyboard:R.drawable.ic_face);
            }

            @Override
            public void onHideMenuViewContainer() {
                binding.layoutMsg.tvAudio.setImageResource(R.drawable.ic_audio);
                binding.layoutMsg.ivFace.setImageResource(R.drawable.ic_face);
            }
        });
        binding.listView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN){
                switchKeyboardUtil.hideMenuAndKeyboard();
            }
            return false;
        });
        switchKeyboardUtil.attachLifecycle(this);
        View.OnLayoutChangeListener onLayoutChangeListener = (v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> scrollToBottom();
        binding.listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState != AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
                    binding.listView.removeOnLayoutChangeListener(onLayoutChangeListener);
                }else {
                    binding.listView.addOnLayoutChangeListener(onLayoutChangeListener);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

    }
    private void scrollToBottom() {
        if (binding.listView.getAdapter() == null){
            return;
        }
        if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED){
            binding.listView.smoothScrollToPosition(binding.listView.getAdapter().getCount() - 1);
        }
    }

    @Override
    public boolean onKeyBackDown(int keyCode, KeyEvent event) {
        return switchKeyboardUtil.onKeyDown(keyCode, event);
    }

    private void loadData() {
        MyApplication.cThreadPool.submit(() -> {
            List<MessageBean> datas = new ArrayList<>();

            String response1 = DataUtils.getFromAssets(requireContext(), "message_data.json");
            try {
                JSONArray jsonArray = new JSONArray(response1);
                for (int i = 0; i < jsonArray.length(); i++) {
                    MessageBean itemData = new MessageBean();
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    int type = jsonObject.getInt("type");
                    itemData.type = type;
                    if (type == MessageBean.TEXT){
                        itemData.text = jsonObject.getString("text");
                    }else if (type == MessageBean.IMAGE){
                        itemData.imageUrl = jsonObject.getString("imageUrl");
                        itemData.coverUrl = jsonObject.getString("coverUrl");
                    }else if (type == MessageBean.VIDEO){
                        itemData.videoUrl = jsonObject.getString("videoUrl");
                        itemData.coverUrl = jsonObject.getString("coverUrl");
                    }
                    datas.add(itemData);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            setData(datas);
        });
    }

    private void setData(List<MessageBean> datas) {
        requireActivity().runOnUiThread(() -> {
            binding.listView.setAdapter(new MsgLvAdapter(datas,binding.listView));
            binding.listView.post(() -> binding.listView.smoothScrollToPosition(binding.listView.getAdapter().getCount()-1));
        });

    }

}
