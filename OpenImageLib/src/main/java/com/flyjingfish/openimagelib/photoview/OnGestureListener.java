package com.flyjingfish.openimagelib.photoview;

interface OnGestureListener {

    void onDrag(float dx, float dy);

    void onFling(float startX, float startY, float velocityX,
                 float velocityY);

    void onScale(boolean doubleFinger,float scaleFactor, float focusX, float focusY);

}