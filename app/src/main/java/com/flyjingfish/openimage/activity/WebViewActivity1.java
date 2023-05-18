package com.flyjingfish.openimage.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;

import com.flyjingfish.openimage.R;
import com.flyjingfish.openimage.databinding.ActivityWebview1Binding;
import com.flyjingfish.openimagelib.OpenImage;
import com.flyjingfish.openimagelib.beans.ClickViewParam;
import com.flyjingfish.openimagelib.enums.MediaType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WebViewActivity1 extends BaseActivity{

    private ActivityWebview1Binding binding;

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
        binding.btnSingle.setSelected(pos == 0);
        binding.btnMuch.setSelected(pos == 1);
        if (pos == 1){
            binding.webView.loadUrl("file:android_asset/image_multi.html");
        }else {
            binding.webView.loadUrl("file:android_asset/image_single.html");
        }
    }

    public void setWebConfig() {
        binding.webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        binding.webView.setVerticalScrollBarEnabled(true);
        binding.webView.setHorizontalScrollBarEnabled(true);
        WebViewClient webViewClient = new WebViewClient();
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
        binding.webView.addJavascriptInterface(this, "openImageApi");// 设置本地调用对象及其接口

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

    }

    @Keep
    @JavascriptInterface
    public void openSingleImage(String url,int imgWidth,int imgHeight,int marginTop,int marginLeft,int browserWidth) {
        runOnUiThread(() -> {
            OpenImage.with(WebViewActivity1.this)
                    .setClickWebView(binding.webView,new ClickViewParam(imgWidth,imgHeight,marginTop,marginLeft,browserWidth))
                    .setSrcImageViewScaleType(ImageView.ScaleType.FIT_XY,true)
                    .setImageUrl(url, MediaType.IMAGE)
                    .setAutoScrollScanPosition(true)
                    .setOpenImageStyle(R.style.DefaultPhotosTheme)
                    .setClickPosition(0).show();
        });
    }

    @Keep
    @JavascriptInterface
    public void openImages(String json,int clickPos) {
        try {
            JSONArray jsonArray = new JSONArray(json);
            final List<ClickViewParam> clickViewParams = new ArrayList<>();
            final List<String> list = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String url = jsonObject.getString("url");
                int imgWidth = jsonObject.getInt("imgWidth");
                int imgHeight = jsonObject.getInt("imgHeight");
                int marginTop = jsonObject.getInt("marginTop");
                int marginLeft = jsonObject.getInt("marginLeft");
                int browserWidth = jsonObject.getInt("browserWidth");
                ClickViewParam clickViewParam = new ClickViewParam(imgWidth,imgHeight,marginTop,marginLeft,browserWidth);
                clickViewParams.add(clickViewParam);
                list.add(url);
            }
            runOnUiThread(() -> {
                OpenImage.with(WebViewActivity1.this)
                        .setClickWebView(binding.webView,clickViewParams)
                        .setSrcImageViewScaleType(ImageView.ScaleType.CENTER_CROP,true)
                        .setImageUrlList(list, MediaType.IMAGE)
                        .setAutoScrollScanPosition(true)
                        .setOpenImageStyle(R.style.DefaultPhotosTheme)
                        .setClickPosition(clickPos).show();
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
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
