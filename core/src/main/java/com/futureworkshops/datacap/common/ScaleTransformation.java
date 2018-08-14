package com.futureworkshops.datacap.common;

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

/**
 * Scale images to desired width or height while maintaining image aspect ratio.
 */
public class ScaleTransformation implements Transformation {
    private float mTargetHeight;
    private float mTargetWidth;


    private ScaleTransformation(float targetHeight, float targetWidth) {
        mTargetHeight = targetHeight;
        mTargetWidth = targetWidth;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        float mRatio;
        int height = source.getHeight();
        int width = source.getWidth();


        if (mTargetHeight != 0) {
            mRatio = height / mTargetHeight;
        } else {
            mRatio = width / mTargetWidth;
        }

        int ratioAdjustedWidth = (int) (width / mRatio);
        int ratioAdjustedHeight = (int) (height / mRatio);

        Bitmap result = Bitmap.createScaledBitmap(source, ratioAdjustedWidth, ratioAdjustedHeight, false);
        if (result != source) {
            source.recycle();
        }
        return result;
    }

    @Override
    public String key() {
        return String.valueOf(mTargetHeight);
    }

    public static ScaleTransformation getScaleWidthTransformation(float targetWidth) {
        return new ScaleTransformation(0, targetWidth);
    }

    public static ScaleTransformation getScaleHeightTransformation(float targetHeight) {
        return new ScaleTransformation(targetHeight, 0);
    }
}
