package com.flyjingfish.openimagelib.beans;

import java.io.Serializable;

public class RectangleConnerRadius implements Serializable {
    public float leftTopRadius;
    public float rightTopRadius;
    public float rightBottomRadius;
    public float leftBottomRadius;

    /**
     * 分别设置圆角四个角度值 ，单位 px
     * @param leftTopRadius 左上角角度，单位 px
     * @param rightTopRadius 右上角角度，单位 px
     * @param rightBottomRadius 右下角角度，单位 px
     * @param leftBottomRadius 左下角角度，单位 px
     */
    public RectangleConnerRadius(float leftTopRadius, float rightTopRadius, float rightBottomRadius, float leftBottomRadius) {
        this.leftTopRadius = leftTopRadius;
        this.rightTopRadius = rightTopRadius;
        this.rightBottomRadius = rightBottomRadius;
        this.leftBottomRadius = leftBottomRadius;
    }

    /**
     * 设置圆角四个角度值 ，单位 px
     * @param radius 四个角角度，单位 px
     */
    public RectangleConnerRadius(float radius) {
        this.leftTopRadius = radius;
        this.rightTopRadius = radius;
        this.rightBottomRadius = radius;
        this.leftBottomRadius = radius;
    }
}
