package com.flyjingfish.openimage.utils;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.ImageView;

import androidx.annotation.Keep;

import com.flyjingfish.openimage.R;
import com.flyjingfish.openimagelib.OpenImage;
import com.flyjingfish.openimagelib.beans.ClickViewParam;
import com.flyjingfish.openimagelib.enums.MediaType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WebOpenImageJS {
    private final Activity activity;
    private final WebView webView;

    public WebOpenImageJS(Activity activity, WebView webView) {
        this.activity = activity;
        this.webView = webView;
        webView.addJavascriptInterface(this, nativeApiName);// 设置本地调用对象及其接口
    }

    private static final String nativeApiName = "openImageApi";

    private static final String inject_single_js =
            "function initClick(){" +
            "    var imgs = document.getElementsByTagName(\"img\");" +
            "    for (var i = 0;i < imgs.length;i++) {" +
            "        var obj = imgs[i];" +
            "        obj.onclick=function(){" +
            "            var marginTop = this.offsetTop-document.documentElement.scrollTop;" +
            "            var marginLeft = this.offsetLeft-document.documentElement.scrollLeft;" +
            "            var imgWidth = this.offsetWidth;" +
            "            var imgHeight = this.offsetHeight;" +
            "            window." + nativeApiName + ".openImageForSingleClick(this.src,imgWidth,imgHeight,marginTop,marginLeft,window.innerWidth)" +
            "        }" +
            "    }" +
            "}" +
            "initClick();";

    private static final String inject_multi_js =
            "function initClick(){" +
            "    var imgs = document.getElementsByTagName(\"img\");" +
            "    var jsonArray = [];" +
            "    for (var i = 0;i < imgs.length;i++) {" +
            "        var obj = imgs[i];" +
            "        let jsonObject = {" +
            "            \"url\" : obj.src," +
            "            \"imgWidth\":obj.offsetWidth," +
            "            \"imgHeight\":obj.offsetHeight," +
            "            \"marginTop\":obj.offsetTop-document.documentElement.scrollTop," +
            "            \"marginLeft\":obj.offsetLeft-document.documentElement.scrollLeft," +
            "            \"browserWidth\":window.innerWidth" +
            "        };" +
            "        jsonArray.push(jsonObject);" +
            "        obj.onclick=function(){" +
            "            window." + nativeApiName + ".openImageForMultiClick(JSON.stringify(jsonArray),this.src);" +
            "        }" +
            "    }" +
            "}" +
            "initClick();";

    public void addSingleImageClickListener() {
        webView.loadUrl("javascript: " + inject_single_js);
    }

    public void addMultiImageClickListener() {
        webView.loadUrl("javascript: " + inject_multi_js);
    }

    @Keep
    @JavascriptInterface
    public void openImageForSingleClick(String url, int imgWidth, int imgHeight, int marginTop, int marginLeft, int browserWidth) {
        activity.runOnUiThread(() -> {
            OpenImage.with(activity)
                    .setClickWebView(webView, new ClickViewParam(imgWidth, imgHeight, marginTop, marginLeft, browserWidth))
                    .setSrcImageViewScaleType(ImageView.ScaleType.CENTER_CROP, true)
                    .setImageUrl(url, MediaType.IMAGE)
                    .setAutoScrollScanPosition(true)
                    .setOpenImageStyle(R.style.DefaultPhotosTheme)
                    .setClickPosition(0).show();
        });
    }

    @Keep
    @JavascriptInterface
    public boolean openImageForMultiClick(String json, String clickUrl) {
        Log.e("openImages", json + ",clickUrl=" + clickUrl);
        try {
            JSONArray jsonArray = new JSONArray(json);
            final List<ClickViewParam> clickViewParams = new ArrayList<>();
            final List<String> list = new ArrayList<>();
            int clickPos = 0;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String url = jsonObject.getString("url");
                int imgWidth = jsonObject.getInt("imgWidth");
                int imgHeight = jsonObject.getInt("imgHeight");
                int marginTop = jsonObject.getInt("marginTop");
                int marginLeft = jsonObject.getInt("marginLeft");
                int browserWidth = jsonObject.getInt("browserWidth");
                ClickViewParam clickViewParam = new ClickViewParam(imgWidth, imgHeight, marginTop, marginLeft, browserWidth);
                clickViewParams.add(clickViewParam);
                list.add(url);
                if (TextUtils.equals(clickUrl, url)) {
                    clickPos = i;
                }
            }
            final int clickPosition = clickPos;
            activity.runOnUiThread(() -> {
                OpenImage.with(activity)
                        .setClickWebView(webView, clickViewParams)
                        .setSrcImageViewScaleType(ImageView.ScaleType.CENTER_CROP, true)
                        .setImageUrlList(list, MediaType.IMAGE)
                        .setAutoScrollScanPosition(true)
                        .setOpenImageStyle(R.style.DefaultPhotosTheme)
                        .setClickPosition(clickPosition).show();
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


        return true;
    }
}
