package com.flyjingfish.openimage.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.viewbinding.ViewBinding;

import com.flyjingfish.openimage.R;
import com.flyjingfish.switchkeyboardlib.SystemKeyboardUtils;


public abstract class BaseInputDialog<VB extends ViewBinding> extends BaseDialogFragment<VB>{
    protected OnContentCallBack onContentCallBack;
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

        @Override
        protected void onStart() {
            super.onStart();
            Window window = getWindow();
            if (window != null) {
                window.setGravity(Gravity.BOTTOM);
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
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

    private void hideSoftInput(){
        View view = getView();
        if (view != null){
            SystemKeyboardUtils.hideSoftInput(view);
        }
    }

}