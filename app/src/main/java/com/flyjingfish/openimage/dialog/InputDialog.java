package com.flyjingfish.openimage.dialog;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.flyjingfish.openimage.R;
import com.flyjingfish.openimage.databinding.DialogInputBinding;
import com.flyjingfish.switchkeyboardlib.MenuModeView;
import com.flyjingfish.switchkeyboardlib.SwitchKeyboardUtil;


public class InputDialog extends BaseInputDialog<DialogInputBinding> {

    private static final String CONTENT = "content";
    private String content;
    private boolean isShowMenu;

    public static InputDialog getDialog(String content){
        InputDialog infoInputDialog = new InputDialog();
        Bundle bundle = new Bundle();
        bundle.putString(CONTENT,content);
        infoInputDialog.setArguments(bundle);
        return infoInputDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        content = getArguments().getString(CONTENT);
    }

    @Override
    protected DialogInputBinding setViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return DialogInputBinding.inflate(inflater,container,false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SwitchKeyboardUtil switchKeyboardUtil = new SwitchKeyboardUtil(requireActivity());
        switchKeyboardUtil.setMenuViewHeightEqualKeyboard(true);
        switchKeyboardUtil.setUseSwitchAnim(true);
        switchKeyboardUtil.setUseMenuUpAnim(true);
        switchKeyboardUtil.attachLifecycle(this);
        switchKeyboardUtil.setInputEditText(binding.etContent);
        switchKeyboardUtil.setMenuViewContainer(binding.llMenu);
        switchKeyboardUtil.setAutoShowKeyboard(true);
        switchKeyboardUtil.setToggleMenuViews(
                new MenuModeView(binding.ivFace,binding.llEmoji));
        switchKeyboardUtil.setOnKeyboardMenuListener(new SwitchKeyboardUtil.OnKeyboardMenuListener() {
            @Override
            public void onScrollToBottom() {
            }

            @Override
            public void onCallShowKeyboard() {

            }

            @Override
            public void onCallHideKeyboard() {
            }

            @Override
            public void onKeyboardHide(int keyboardHeight) {
                Log.e("inputDialog","====2="+isShowMenu);
                if (!isShowMenu){
                    dismiss();
                }
            }

            @Override
            public void onKeyboardShow(int keyboardHeight) {
                binding.ivFace.setImageResource(R.drawable.ic_face);
                isShowMenu = false;
                Log.e("inputDialog","====3="+isShowMenu);
            }



            @Override
            public void onShowMenuLayout(View layoutView) {
                isShowMenu = true;
                Log.e("inputDialog","====4="+isShowMenu);
                binding.ivFace.setImageResource(layoutView == binding.llEmoji?R.drawable.ic_keyboard:R.drawable.ic_face);
            }

            @Override
            public void onHideMenuViewContainer() {
                binding.ivFace.setImageResource(R.drawable.ic_face);
            }
        });


        if (!TextUtils.isEmpty(content)){
            binding.etContent.setText(content);
            binding.etContent.setSelection(content.length());
        }
        binding.tvSend.setOnClickListener(v -> {
            if (onContentCallBack != null){
                onContentCallBack.onSendContent(binding.etContent.getText().toString());
                binding.etContent.setText("");
            }
        });
        binding.etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (onContentCallBack != null){
                    onContentCallBack.onContent(binding.etContent.getText().toString());
                }
            }
        });

    }
}
