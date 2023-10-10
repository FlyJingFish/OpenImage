package com.flyjingfish.openimagecoillib;

import android.content.Context;
import android.net.Uri;

import java.io.InputStream;

final class PictureContentResolver {

    public static InputStream getContentResolverOpenInputStream(Context context, Uri uri) {
        try {
            return context.getContentResolver().openInputStream(uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}