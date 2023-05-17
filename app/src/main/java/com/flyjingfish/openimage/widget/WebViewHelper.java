package com.flyjingfish.openimage.widget;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import androidx.annotation.Keep;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import com.flyjingfish.openimage.R;
import com.flyjingfish.openimagelib.OpenImage;
import com.flyjingfish.openimagelib.beans.ClickViewParam;
import com.flyjingfish.openimagelib.enums.MediaType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class WebViewHelper {

    private final FragmentActivity activity;
    private WebView mWebView;

    public WebViewHelper(FragmentActivity context, WebView webView) {
        this.activity = context;
        this.mWebView = webView;
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void getWebConfig() {
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setAllowFileAccessFromFileURLs(false);
        mWebView.getSettings().setSavePassword(false);
        mWebView.getSettings().setAllowFileAccess(false);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setSaveFormData(false);
        mWebView.getSettings().setSupportZoom(false);
        mWebView.getSettings().setBuiltInZoomControls(false);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(this, "openImageApi");// 设置本地调用对象及其接口

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

    }

    @Keep
    @JavascriptInterface
    public boolean openSingleImage(String url,int imgWidth,int imgHeight,int marginTop,int marginLeft,int browserWidth) {
        Log.e("openImages","url:"+url+",imgWidth:"+imgWidth+",imgHeight:"+imgHeight
                +",marginTop:"+marginTop+",marginLeft:"+marginLeft
                +",browserWidth:"+browserWidth);
        activity.runOnUiThread(() -> {
            String url1 = "http://img1.baidu.com/it/u=2872324617,2662678801&fm=253&app=120&f=JPEG?w=640&h=960";
            String url2 = "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fp0.itc.cn%2Fq_70%2Fimages03%2F20210227%2F6687c969b58d486fa2f23d8488b96ae4.jpeg&refer=http%3A%2F%2Fp0.itc.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1661701773&t=19043990158a1d11c2a334146020e2ce";
            List<String> list = new ArrayList<>();
            list.add(url1);
            list.add(url2);

            OpenImage.with(activity)
                    .setClickWebView(mWebView,new ClickViewParam(imgWidth,imgHeight,marginTop,marginLeft,browserWidth))
                    .setSrcImageViewScaleType(ImageView.ScaleType.CENTER_CROP,true)
                    .setImageUrlList(list, MediaType.IMAGE)
                    .setAutoScrollScanPosition(true)
                    .setOpenImageStyle(R.style.DefaultPhotosTheme)
                    .setClickPosition(0).show();
        });
        return true;
    }

    @Keep
    @JavascriptInterface
    public boolean openImages(String json,int clickPos) {
        Log.e("openImages",json+",clickPos="+clickPos);
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
                Log.e("openImages","url:"+url+",imgWidth:"+imgWidth+",imgHeight:"+imgHeight
                        +",marginTop:"+marginTop+",marginLeft:"+marginLeft
                        +",browserWidth:"+browserWidth);
                ClickViewParam clickViewParam = new ClickViewParam(imgWidth,imgHeight,marginTop,marginLeft,browserWidth);
                clickViewParams.add(clickViewParam);
                list.add(url);
            }
            activity.runOnUiThread(() -> {
                OpenImage.with(activity)
                        .setClickWebView(mWebView,clickViewParams)
                        .setSrcImageViewScaleType(ImageView.ScaleType.CENTER_CROP,true)
                        .setImageUrlList(list, MediaType.IMAGE)
                        .setAutoScrollScanPosition(true)
                        .setOpenImageStyle(R.style.DefaultPhotosTheme)
                        .setClickPosition(clickPos).show();
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


        return true;
    }

    public void onDestroy() {
        if (mWebView != null) {
            mWebView.clearHistory();
            try {
                ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            } catch (Exception ignored) {
            }
            mWebView.loadUrl("about:blank");
            mWebView.stopLoading();
            mWebView.clearFormData();
            mWebView.clearSslPreferences();
            mWebView.setWebChromeClient(null);
            mWebView.setWebViewClient(null);
            mWebView.removeAllViews();
            mWebView.destroy();
            mWebView = null;
        }
    }

    public void onPause() {
        if (mWebView != null) {
            mWebView.onPause();
        }
    }

    public void onResume() {
        if (mWebView != null) {
            mWebView.onResume();
        }
    }

}
