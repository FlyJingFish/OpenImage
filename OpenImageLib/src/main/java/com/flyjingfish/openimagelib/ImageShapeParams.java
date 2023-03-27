package com.flyjingfish.openimagelib;

import com.flyjingfish.openimagelib.beans.RectangleConnerRadius;
import com.flyjingfish.openimagelib.enums.ImageShapeType;

import java.io.Serializable;

class ImageShapeParams implements Serializable {
    public ImageShapeType shapeType;
    public RectangleConnerRadius rectangleConnerRadius;

    public ImageShapeParams(ImageShapeType shapeType, RectangleConnerRadius rectangleConnerRadius) {
        this.shapeType = shapeType;
        this.rectangleConnerRadius = rectangleConnerRadius;
    }
}
