package com.futureworkshops.android.autocapture.presentation.edit;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.annotation.NonNull;

import com.futureworkshops.datacap.common.model.Page;

import java.util.List;

/**
 * Created by stelian on 24/10/2017.
 */

public class ImageEditContract {

    public interface View {

        void onPageLoaded(@NonNull Page page);

        void onPageNotFound();

        void onPageCornersLoaded(Point[] corners);

        void onImageDeskewed(Bitmap correctedImage);

        void onImageDeskewFailed(String message);

        void onPageSaved();

        void onPageSaveFailed(String message);
    }

    public interface Presenter {
        void applyPerspectiveCorrection(final List<Point> corners);

        void loadPage(String pageId);

        void saveDeskewResults(Bitmap deskewedBitmap);
    }
}
