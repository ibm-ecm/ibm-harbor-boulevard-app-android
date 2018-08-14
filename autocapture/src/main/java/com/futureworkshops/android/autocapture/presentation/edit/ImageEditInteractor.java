package com.futureworkshops.android.autocapture.presentation.edit;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.annotation.NonNull;

import com.futureworkshops.datacap.common.api.FileManager;
import com.futureworkshops.datacap.common.dagger.BatchDaggerHelper;
import com.futureworkshops.datacap.common.model.Document;
import com.futureworkshops.datacap.common.model.Page;
import com.ibm.datacap.sdk.common.DatacapImageProcessor;
import com.ibm.datacap.sdk.model.IPage;

import io.reactivex.Completable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;

/**
 * Created by stelian on 24/10/2017.
 */

public class ImageEditInteractor {

    private FileManager mFileManager;
    private DatacapImageProcessor mDatacapImageProcessor;
    private BatchDaggerHelper mBatchDaggerHelper;

    public ImageEditInteractor(FileManager fileManager, DatacapImageProcessor datacapImageProcessor, BatchDaggerHelper batchDaggerHelper) {
        mFileManager = fileManager;
        mDatacapImageProcessor = datacapImageProcessor;
        mBatchDaggerHelper = batchDaggerHelper;
    }

    /**
     * Load a {@link Page} by it's ID.
     *
     * @param pageId
     * @return
     */
    public Page loadBageById(@NonNull String pageId) {
        final Document document = (Document) mBatchDaggerHelper.getBatch().getDocuments().get(0);

        for (IPage iPage : document.getPages()) {
            if (iPage.getId().equals(pageId)) {
                return (Page) iPage;
            }
        }

        return null;
    }


    /**
     * Return some default coordinates to show in the {@link com.ibm.datacap.sdk.ui.image.DocumentCornerPickerView}
     * if the page doesn't have identified corners.
     *
     * @return
     */
    public Point[] getDefaultCorners() {
        Point[] points = new Point[4];

        points[0] = new Point(200, 200);
        points[1] = new Point(800, 200);
        points[2] = new Point(800, 800);
        points[3] = new Point(200, 800);

        return points;
    }

    /**
     * Initialize the image processor if required.
     */
    public Single<Boolean> getProcessorInitializeObservable() {
        if (mDatacapImageProcessor.isInitialized()) {
            return Single.just(true);
        } else {
            return mDatacapImageProcessor.initialize();
        }

    }

    public ObservableSource<Bitmap> applyPerspectiveCorrection(Bitmap bitmap, Point[] corners) {
        return mDatacapImageProcessor.applyPerspectiveCorrection(bitmap, corners);
    }

    public Completable saveDeskewedImage(String imagePath, Bitmap deskewedBitmap) {
        return mFileManager.overwriteImageFile(imagePath, deskewedBitmap);
    }
}
