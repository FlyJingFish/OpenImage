package com.flyjingfish.openimage.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

import com.flyjingfish.openimage.databinding.ActivityWebview1Binding;
import com.flyjingfish.openimage.utils.WebOpenImageJS;

public class WebViewActivity2 extends BaseActivity{

    private ActivityWebview1Binding binding;
    private int type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWebview1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnSingle.setOnClickListener(v -> {
            setSelect(0);
        });
        binding.btnMuch.setOnClickListener(v -> {
            setSelect(1);
        });
        setSelect(0);
        setWebConfig();


    }

    private void setSelect(int pos) {
        type = pos;
        binding.btnSingle.setSelected(pos == 0);
        binding.btnMuch.setSelected(pos == 1);
        binding.webView.loadUrl("https://flyjingfish.github.io/demoweb/index.html");
    }

    public void setWebConfig() {
        binding.webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        binding.webView.setVerticalScrollBarEnabled(true);
        binding.webView.setHorizontalScrollBarEnabled(true);
        WebOpenImageJS webOpenImageJS = new WebOpenImageJS(this,binding.webView);
        WebViewClient webViewClient = new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                view.getSettings().setJavaScriptEnabled(true);
                if (type == 1){
                    webOpenImageJS.addMultiImageClickListener();
                }else {
                    webOpenImageJS.addSingleImageClickListener();
                }
                super.onPageFinished(view, url);
            }
        };
        WebChromeClient webChromeClient = new WebChromeClient();
        binding.webView.setWebViewClient(webViewClient);
        binding.webView.setWebChromeClient(webChromeClient);

        binding.webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        binding.webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        binding.webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        binding.webView.getSettings().setAllowFileAccessFromFileURLs(false);
        binding.webView.getSettings().setSavePassword(false);
        binding.webView.getSettings().setAllowFileAccess(false);
        binding.webView.getSettings().setDomStorageEnabled(true);
        binding.webView.getSettings().setSaveFormData(false);
        binding.webView.getSettings().setSupportZoom(false);
        binding.webView.getSettings().setBuiltInZoomControls(false);
        binding.webView.getSettings().setDisplayZoomControls(false);
        binding.webView.getSettings().setUseWideViewPort(true);
        binding.webView.getSettings().setLoadWithOverviewMode(true);

        binding.webView.getSettings().setJavaScriptEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        binding.webView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.webView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.webView.destroy();
    }
}
