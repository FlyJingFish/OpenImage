package com.flyjingfish.openimage.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

import com.flyjingfish.openimage.databinding.ActivityWebviewBinding;
import com.flyjingfish.openimage.widget.WebViewHelper;

public class WebViewActivity extends BaseActivity{

    private com.flyjingfish.openimage.databinding.ActivityWebviewBinding binding;
    private WebView mWebView;
    private WebViewHelper mWebViewHelper;
    private WebChromeClient webChromeClient;
    private WebViewClient webViewClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWebviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mWebView = binding.webView;
        mWebView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mWebView.setVerticalScrollBarEnabled(true);
        mWebView.setHorizontalScrollBarEnabled(true);

        mWebViewHelper = new WebViewHelper(this, mWebView);

        webViewClient = new WebViewClient();
        webChromeClient = new WebChromeClient();

        mWebViewHelper.getWebConfig();
        mWebView.setWebViewClient(webViewClient);
        mWebView.setWebChromeClient(webChromeClient);
        mWebView.loadUrl("file:android_asset/image.html");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebViewHelper.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebViewHelper.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebViewHelper.onDestroy();
    }
}
