package com.flyjingfish.openimage.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class TestBean implements Parcelable , Serializable {
    public String test;

    public TestBean() {
    }

    protected TestBean(Parcel in) {
        test = in.readString();
    }

    public static final Creator<TestBean> CREATOR = new Creator<TestBean>() {
        @Override
        public TestBean createFromParcel(Parcel in) {
            return new TestBean(in);
        }

        @Override
        public TestBean[] newArray(int size) {
            return new TestBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(test);
    }
}
