package com.futureworkshops.datacap.common.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.annotation.NonNull;

/**
 * Created by stelian on 24/10/2017.
 */

public class BitmapUtils {

    public static Bitmap rotateBitmap(@NonNull Bitmap source, int rotationAngle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotationAngle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}
