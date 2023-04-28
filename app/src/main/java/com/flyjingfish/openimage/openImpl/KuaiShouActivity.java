package com.flyjingfish.openimage.openImpl;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.flyjingfish.openimage.databinding.MyActivityKuaishouBinding;
import com.flyjingfish.openimage.dialog.BaseInputDialog;
import com.flyjingfish.openimage.dialog.InputDialog;
import com.flyjingfish.openimagelib.OpenImageActivity;
import com.flyjingfish.openimagelib.utils.ScreenUtils;
import com.flyjingfish.openimagelib.widget.TouchCloseLayout;
import com.flyjingfish.switchkeyboardlib.SwitchKeyboardUtil;

public class KuaiShouActivity extends OpenImageActivity {

    private MyActivityKuaishouBinding rootBinding;
    public static final String BUNDLE_DATA_KEY = "bundle_data";
    public static final String MY_DATA_KEY = "my_data";
    private ValueAnimator showCommentAnim;

    @Override
    public View getContentView() {
        rootBinding = MyActivityKuaishouBinding.inflate(getLayoutInflater());
        return rootBinding.getRoot();
    }

    @Override
    public View getBgView() {
        return rootBinding.vBg;
    }


    @Override
    public FrameLayout getViewPager2Container() {
        return rootBinding.flTouchView;
    }

    @Override
    public ViewPager2 getViewPager2() {
        return rootBinding.viewPager;
    }

    @Override
    public TouchCloseLayout getTouchCloseLayout() {
        return rootBinding.touchCloseLayout;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rootBinding.ivLike.setOnClickListener(v -> {
            if (isShowComment()){
                setShowComment(false);
                showCommentAnim.start();
            }else {
                setShowComment(true);
                showCommentAnim.start();
            }
        });
        rootBinding.tvCommentClose.setOnClickListener(v -> {
            if (isShowComment()){
                setShowComment(false);
                showCommentAnim.start();
            }
        });

        addOnItemClickListener((fragment, openImageUrl, position) -> {
            if (isShowComment()){
                setShowComment(false);
                showCommentAnim.start();
            }
        });

        rootBinding.llMu.setOnClickListener(v -> {
            InputDialog inputDialog = InputDialog.getDialog(rootBinding.tvMu.getText().toString());
            inputDialog.setOnContentCallBack(new BaseInputDialog.OnContentCallBack() {
                @Override
                public void onSendContent(String content) {

                }

                @Override
                public void onContent(String content) {
                    rootBinding.tvMu.setText(content);
                }
            });
            inputDialog.show(getSupportFragmentManager(),"inputDialog");
        });

        rootBinding.llCommentBottom.setOnClickListener(v -> {
            InputDialog inputDialog = InputDialog.getDialog(rootBinding.tvComment.getText().toString());
            inputDialog.setOnContentCallBack(new BaseInputDialog.OnContentCallBack() {
                @Override
                public void onSendContent(String content) {

                }

                @Override
                public void onContent(String content) {
                    rootBinding.tvComment.setText(content);
                }
            });
            inputDialog.show(getSupportFragmentManager(),"inputDialog");
        });
    }

    private void setShowComment(boolean isShow){
        if (showCommentAnim != null){
            showCommentAnim.cancel();
            showCommentAnim.removeAllUpdateListeners();
        }
        final int height = rootBinding.getRoot().getMeasuredHeight()/3*2;
        ShowCommentTypeEvaluator showCommentTypeEvaluator = new ShowCommentTypeEvaluator();
        showCommentAnim = ValueAnimator.ofObject(showCommentTypeEvaluator,0,0);
        showCommentAnim.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            setViewHeight(rootBinding.rlComment,value);
            if (animation.getAnimatedFraction() == 1){
                if (rootBinding.rlComment.getHeight() < height/2){
                    rootBinding.llMu.setVisibility(View.VISIBLE);
                }else {
                    rootBinding.llMu.setVisibility(View.GONE);
                }
            }
        });

        if (isShow){
            showCommentAnim.setIntValues(rootBinding.rlComment.getHeight(),height);
        }else {
            showCommentAnim.setIntValues(rootBinding.rlComment.getHeight(),((int) ScreenUtils.dp2px(KuaiShouActivity.this, 55)));
        }
    }
    private static class ShowCommentTypeEvaluator implements TypeEvaluator<Integer>{

        @Override
        public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
            return startValue + (int) ((endValue - startValue) * fraction);
        }
    }


    private boolean isShowComment(){
        return rootBinding.rlComment.getHeight() > ((int) ScreenUtils.dp2px(KuaiShouActivity.this, 55));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (showCommentAnim != null){
            showCommentAnim.removeAllUpdateListeners();
            showCommentAnim.cancel();
        }
    }

    public void setViewHeight(View view ,int height){
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = height;
        view.setLayoutParams(layoutParams);
        rootBinding.llMu.setVisibility(View.GONE);
    }

}
