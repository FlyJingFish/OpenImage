package com.flyjingfish.openimagelib.enums;

public enum OpenImageOrientation {
    HORIZONTAL(0),
    VERTICAL(1);
    int orientation;

    OpenImageOrientation(int orientation) {
        this.orientation = orientation;
    }

    public int getOrientation() {
        return orientation;
    }

    public static OpenImageOrientation getOrientation(int orientation){
        if (orientation == 1){
            return VERTICAL;
        }else {
            return HORIZONTAL;
        }
    }

}
