package com.yalantis.ucrop1.callback;

import android.graphics.Bitmap;
import android.net.Uri;

import com.yalantis.ucrop1.model.ExifInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface BitmapLoadCallback {

    void onBitmapLoaded(@NonNull Bitmap bitmap, @NonNull ExifInfo exifInfo, @NonNull Uri imageInputUri, @Nullable Uri imageOutputUri);

    void onFailure(@NonNull Exception bitmapWorkerException);

}