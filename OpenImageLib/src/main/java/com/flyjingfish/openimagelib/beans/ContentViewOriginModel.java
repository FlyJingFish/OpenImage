package com.flyjingfish.openimagelib.beans;

import android.os.Parcel;
import android.os.Parcelable;

public class ContentViewOriginModel implements Parcelable {
    public int left;
    public int top;
    public int width;
    public int height;
    public int dataPosition;
    public boolean transitioned;

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getDataPosition() {
        return dataPosition;
    }

    public void setDataPosition(int dataPosition) {
        this.dataPosition = dataPosition;
    }

    public boolean isTransitioned() {
        return transitioned;
    }

    public void setTransitioned(boolean transitioned) {
        this.transitioned = transitioned;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.left);
        dest.writeInt(this.top);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeInt(this.dataPosition);
        dest.writeInt(this.transitioned ? 1 : 0);
    }

    public ContentViewOriginModel() {
    }

    protected ContentViewOriginModel(Parcel in) {
        this.left = in.readInt();
        this.top = in.readInt();
        this.width = in.readInt();
        this.height = in.readInt();
        this.dataPosition = in.readInt();
        this.transitioned = in.readInt() == 1;
    }

    public static final Creator<ContentViewOriginModel> CREATOR = new Creator<ContentViewOriginModel>() {
        @Override
        public ContentViewOriginModel createFromParcel(Parcel source) {
            return new ContentViewOriginModel(source);
        }

        @Override
        public ContentViewOriginModel[] newArray(int size) {
            return new ContentViewOriginModel[size];
        }
    };
}
