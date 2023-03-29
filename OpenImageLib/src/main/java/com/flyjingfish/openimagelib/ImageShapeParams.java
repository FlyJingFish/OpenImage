package com.flyjingfish.openimagelib;

import android.os.Parcel;
import android.os.Parcelable;

import com.flyjingfish.openimagelib.beans.RectangleConnerRadius;
import com.flyjingfish.openimagelib.enums.ImageShapeType;

class ImageShapeParams implements Parcelable {
    public ImageShapeType shapeType;
    public RectangleConnerRadius rectangleConnerRadius;

    public ImageShapeParams(ImageShapeType shapeType, RectangleConnerRadius rectangleConnerRadius) {
        this.shapeType = shapeType;
        this.rectangleConnerRadius = rectangleConnerRadius;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.shapeType == null ? -1 : this.shapeType.ordinal());
        dest.writeParcelable(this.rectangleConnerRadius, flags);
    }

    public void readFromParcel(Parcel source) {
        int tmpShapeType = source.readInt();
        this.shapeType = tmpShapeType == -1 ? null : ImageShapeType.values()[tmpShapeType];
        this.rectangleConnerRadius = source.readParcelable(RectangleConnerRadius.class.getClassLoader());
    }

    protected ImageShapeParams(Parcel in) {
        int tmpShapeType = in.readInt();
        this.shapeType = tmpShapeType == -1 ? null : ImageShapeType.values()[tmpShapeType];
        this.rectangleConnerRadius = in.readParcelable(RectangleConnerRadius.class.getClassLoader());
    }

    public static final Parcelable.Creator<ImageShapeParams> CREATOR = new Parcelable.Creator<ImageShapeParams>() {
        @Override
        public ImageShapeParams createFromParcel(Parcel source) {
            return new ImageShapeParams(source);
        }

        @Override
        public ImageShapeParams[] newArray(int size) {
            return new ImageShapeParams[size];
        }
    };
}
