package com.flyjingfish.openimageglidelib;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

class SingleMediaScanner implements MediaScannerConnection.MediaScannerConnectionClient {
   private MediaScannerConnection mMs;
   private final String path;
   private final ScanListener listener;

   public void onMediaScannerConnected() {
      this.mMs.scanFile(this.path, null);

   }

   public SingleMediaScanner(@Nullable Context context, @NonNull String path, @Nullable ScanListener listener) {
      super();
      this.path = path;
      this.listener = listener;
      this.mMs = new MediaScannerConnection(context, this);
      this.mMs.connect();
   }

   @Override
   public void onScanCompleted(String path, Uri uri) {
      this.mMs.disconnect();
      this.mMs = null;
      if (listener != null) {
         listener.onScanFinish();
      }
   }

   public interface ScanListener {
      void onScanFinish();
   }
}