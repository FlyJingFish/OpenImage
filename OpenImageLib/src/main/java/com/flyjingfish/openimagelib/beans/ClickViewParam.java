package com.flyjingfish.openimagelib.beans;

public class ClickViewParam {
    public int imgWidth;
    public int imgHeight;
    public int marginTop;
    public int marginLeft;
    public int browserWidth;

    /**
     * 以下参数都是在网页内部获取的
     *
     * @param imgWidth 网页内图片宽（单位：px）
     * @param imgHeight 网页内图片高（单位：px）
     * @param marginTop 图片在网页内距 WebView 顶部的长度（单位：px）
     * @param marginLeft 图片在网页内距 WebView 左侧的长度（单位：px）
     * @param browserWidth WebView内部网页的宽（单位：px）
     */
    public ClickViewParam(int imgWidth, int imgHeight, int marginTop, int marginLeft, int browserWidth) {
        this.imgWidth = imgWidth;
        this.imgHeight = imgHeight;
        this.marginTop = marginTop;
        this.marginLeft = marginLeft;
        this.browserWidth = browserWidth;
    }
}
