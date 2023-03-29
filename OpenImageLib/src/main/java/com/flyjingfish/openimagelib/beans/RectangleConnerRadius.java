package com.flyjingfish.openimagelib.beans;

import android.os.Parcel;
import android.os.Parcelable;

public class RectangleConnerRadius implements Parcelable {
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.leftTopRadius);
        dest.writeFloat(this.rightTopRadius);
        dest.writeFloat(this.rightBottomRadius);
        dest.writeFloat(this.leftBottomRadius);
    }

    public void readFromParcel(Parcel source) {
        this.leftTopRadius = source.readFloat();
        this.rightTopRadius = source.readFloat();
        this.rightBottomRadius = source.readFloat();
        this.leftBottomRadius = source.readFloat();
    }

    protected RectangleConnerRadius(Parcel in) {
        this.leftTopRadius = in.readFloat();
        this.rightTopRadius = in.readFloat();
        this.rightBottomRadius = in.readFloat();
        this.leftBottomRadius = in.readFloat();
    }

    public static final Parcelable.Creator<RectangleConnerRadius> CREATOR = new Parcelable.Creator<RectangleConnerRadius>() {
        @Override
        public RectangleConnerRadius createFromParcel(Parcel source) {
            return new RectangleConnerRadius(source);
        }

        @Override
        public RectangleConnerRadius[] newArray(int size) {
            return new RectangleConnerRadius[size];
        }
    };
}
