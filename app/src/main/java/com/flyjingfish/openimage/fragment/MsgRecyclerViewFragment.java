package com.flyjingfish.openimage.fragment;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.flyjingfish.openimage.DataUtils;
import com.flyjingfish.openimage.R;
import com.flyjingfish.openimage.bean.MessageBean;
import com.flyjingfish.openimage.MyApplication;
import com.flyjingfish.openimage.adapter.MsgRvAdapter;
import com.flyjingfish.openimage.databinding.FragmentMsgRvBinding;
import com.flyjingfish.openimage.databinding.LayoutRecyclerviewBinding;
import com.flyjingfish.openimagelib.utils.StatusBarHelper;
import com.flyjingfish.switchkeyboardlib.MenuModeView;
import com.flyjingfish.switchkeyboardlib.SwitchKeyboardUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MsgRecyclerViewFragment extends BaseFragment {
    private FragmentMsgRvBinding binding;
    private SwitchKeyboardUtil switchKeyboardUtil;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMsgRvBinding.inflate(inflater,container,false);
        binding.rv.setLayoutManager(new LinearLayoutManager(requireActivity()));
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
        binding.rv.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN){
                switchKeyboardUtil.hideMenuAndKeyboard();
            }
            return false;
        });
        switchKeyboardUtil.attachLifecycle(this);
        binding.rv.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> scrollToBottom());

    }
    private void scrollToBottom() {
        if (binding.rv.getAdapter() == null){
            return;
        }
        binding.rv.scrollToPosition(binding.rv.getAdapter().getItemCount() - 1);
    }
    private void loadData() {
        MyApplication.cThreadPool.submit(() -> {
            List<MessageBean> datas = new ArrayList<>();

            String response1 = DataUtils.getFromAssets(requireActivity(), "message_data.json");
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

    @Override
    public boolean onKeyBackDown(int keyCode, KeyEvent event) {
        return switchKeyboardUtil.onKeyDown(keyCode, event);
    }

    private void setData(List<MessageBean> datas) {
        requireActivity().runOnUiThread(() -> {
            binding.rv.setAdapter(new MsgRvAdapter(datas));
            binding.rv.post(() -> binding.rv.scrollToPosition(binding.rv.getAdapter().getItemCount()-1));

        });

    }

}
