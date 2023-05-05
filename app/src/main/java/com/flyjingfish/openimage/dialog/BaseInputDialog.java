package com.flyjingfish.openimage.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.viewbinding.ViewBinding;

import com.flyjingfish.openimage.R;
import com.flyjingfish.switchkeyboardlib.SystemKeyboardUtils;


public abstract class BaseInputDialog<VB extends ViewBinding> extends BaseDialogFragment<VB> implements DialogInterface.OnKeyListener {
    protected OnContentCallBack onContentCallBack;
//    private SystemKeyboardUtils keyboardUtils;
    protected abstract EditText getContentEditText();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, backgroundDimEnabled()? R.style.DimEnabledInputDialog :R.style.BaseInputDialog);
    }

    protected boolean backgroundDimEnabled(){
        return true;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new MyDialog(requireContext(), getTheme());
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().setOnKeyListener(this);
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
            int flag = window.getDecorView().getSystemUiVisibility();
            window.getDecorView().setSystemUiVisibility(flag | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED|WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getContentEditText().requestFocus();
    }


    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            dismiss();
            return true;
        }else {
            //这里注意当不是返回键时需将事件扩散，否则无法处理其他点击事件
            return false;
        }
    }

    public void setOnContentCallBack(OnContentCallBack onContentCallBack) {
        this.onContentCallBack = onContentCallBack;
    }

    public interface OnContentCallBack{
        void onSendContent(String content);
        void onContent(String content);
    }

    private class MyDialog extends Dialog{

        public MyDialog(@NonNull Context context, int themeResId) {
            super(context, themeResId);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (isShowing() && shouldCloseOnTouch(getContext(),event)){
                hideSoftInput();
            }
            return super.onTouchEvent(event);
        }
        public boolean shouldCloseOnTouch(Context context, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN
                    && isOutOfBounds(context, event) && getWindow().peekDecorView() != null) {
                return true;
            }
            return false;
        }
        private boolean isOutOfBounds(Context context, MotionEvent event) {
            final int x = (int) event.getX();
            final int y = (int) event.getY();
            final int slop = ViewConfiguration.get(context).getScaledWindowTouchSlop();
            final View decorView = getWindow().getDecorView();
            return (x < -slop) || (y < -slop)
                    || (x  > (decorView.getWidth()+slop))
                    || (y  > (decorView.getHeight()+slop));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        if (keyboardUtils != null){
//            keyboardUtils.setOnKeyBoardListener(null);
//        }
    }

    @Override
    public void dismiss() {
        hideSoftInput();
        super.dismiss();
    }

    private void hideSoftInput(){
        View view = getView();
        if (view != null){
            SystemKeyboardUtils.hideSoftInput(view);
        }
    }

}