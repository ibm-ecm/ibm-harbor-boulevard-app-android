package com.futureworkshops.android.autocapture.presentation.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.util.Log;

import com.futureworkshops.datacap.common.api.BatchConfiguratorHelper;
import com.futureworkshops.datacap.common.api.BatchFactory;
import com.futureworkshops.datacap.common.api.FileManager;
import com.futureworkshops.datacap.common.dagger.BatchDaggerHelper;
import com.futureworkshops.datacap.common.model.Batch;
import com.futureworkshops.datacap.common.model.Document;
import com.futureworkshops.datacap.common.model.Page;
import com.futureworkshops.datacap.common.utils.CastUtils;
import com.ibm.datacap.sdk.common.DatacapImageProcessor;
import com.ibm.datacap.sdk.model.IDocumentType;
import com.ibm.datacap.sdk.model.IPageType;
import com.ibm.datacap.sdk.ui.camera.DatacapCameraView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by stelian on 24/10/2017.
 */

public class CameraInteractor {

    private BatchDaggerHelper mBatchDaggerHelper;
    private FileManager mFileManager;
    private DatacapImageProcessor mDatacapImageProcessor;

    public CameraInteractor(BatchDaggerHelper batchDaggerHelper, FileManager fileManager, DatacapImageProcessor datacapImageProcessor) {
        mBatchDaggerHelper = batchDaggerHelper;
        mFileManager = fileManager;
        mDatacapImageProcessor = datacapImageProcessor;
    }

    /**
     * Get the pages defined for the current batch.
     *
     * @return
     */
    public List<Page> getPages() {
        // The Batch for this sample has only 1 document
        return (List<Page>) mBatchDaggerHelper.getBatch().getDocuments().get(0).getPages();
    }

    /**
     * Create a new page for the current batch and the only document.
     * <p>
     * The page type will be chosen automatically and will be the first available page type
     * for the current document type.
     *
     * @param pageId the ID to use for the new page
     * @return
     */
    public Page createPage(@NonNull String pageId) {
        final Batch batch = mBatchDaggerHelper.getBatch();
        final BatchConfiguratorHelper configuratorHelper = mBatchDaggerHelper.getBatchConfiguratorHelper();

        // get the document we are creating the page for
        final Document document = (Document) batch.getDocuments().get(0);

        // find out the type of the document
        final IDocumentType documentType = configuratorHelper.getDocumentTypeByLabel(document.getType());

        // get a list of valid page types for the document
        final List<IPageType> pageTypes = configuratorHelper.getPageTypesForDocument(documentType);

        // default to choose the first page type found - you can replace this with your custom logic
        final IPageType selectedPageType = pageTypes.get(0);

        return BatchFactory.createPageWithTypeAndId(configuratorHelper, selectedPageType, pageId);
    }


    /**
     * Add a {@link Page} to the current {@link Document}.
     *
     * @param page
     */
    public void addPageToDocument(@NonNull Page page) {
        final Document document = (Document) mBatchDaggerHelper.getBatch().getDocuments().get(0);
        List<Page> pages = CastUtils.castList(document.getPages());

        // create the list if it doesn't exist
        if (pages == null) {
            pages = new ArrayList<>();
        }

        // add the page to the document
        pages.add(page);
        document.setPages(pages);
    }

    /**
     * Add a new {@link Page} using images from a
     * {@link com.ibm.datacap.sdk.ui.camera.DatacapCameraView.DatacapDetectionResult}.
     *
     * @param data
     * @return
     */
    public Single<Page> saveAutocaptureImagePage(@NonNull final Page page,
                                                 final DatacapCameraView.DatacapDetectionResult data) {

        //  use image paths instead of bitmaps!!!!!
        final String batchId = mBatchDaggerHelper.getBatch().getId();
        return mFileManager.moveImageFile(batchId, page.getId(), data.getOriginalImagePath())
                .doOnNext(originalImage -> page.setOriginalImagePath(originalImage.getPath()))
                .flatMap((Function<File, Observable<File>>) file ->
                        mFileManager.moveImageFile(batchId, page.getId().concat("_processed"), data.getDeskewedImagePath()))
                .doOnNext(correctedImage -> page.setImageFilePath(correctedImage.getPath()))
                .flatMap((Function<File, ObservableSource<Page>>) file -> Observable.just(page))
                .flatMap((Function<Page, Observable<Page>>) page1 -> {
                    final PointF[] corners = data.getCorners();
                    Point[] intPoints = new Point[corners.length];

                    for (int i = 0; i < corners.length; i++) {
                        intPoints[i] = new Point((int) corners[i].x, (int) corners[i].y);
                    }

                    page1.setDocumentCorners(intPoints);

                    return Observable.just(page1);
                }).toList() // converts this to a Single
                .map(pages -> pages.get(0))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Add a new {@link Page} using a picture taken from the camera and process the other image.
     *
     * @param data
     * @return
     */
    public Single<Page> saveManualCapturePage(@NonNull final Page page,
                                              @NonNull final byte[] data, final int cameraOrientation) {

        final String batchId = mBatchDaggerHelper.getBatch().getId();
        final List<Point> edgeCorners = new ArrayList<>();

        // save original image file async
        return mFileManager.saveImageFile(batchId, page.getId(), data, cameraOrientation)
                .doOnNext(file -> {
                    // save original image path
                    page.setOriginalImagePath(file.getPath());
                })
                .flatMap((Function<File, Observable<File>>) file -> {
                    //  deskew image
                    return startImageCorrection(file.getPath())
                            .flatMap((Function<DatacapImageProcessor.DatacapImage, Observable<File>>) datacapImage ->
                                    saveProcessedImageWithFallback(datacapImage, edgeCorners, batchId, page, file));
                }).doOnNext(correctedImage ->
                        page.setImageFilePath(correctedImage.getPath()))
                .flatMap((Function<File, ObservableSource<Page>>) file -> Observable.just(page))
                .flatMap((Function<Page, Observable<Page>>) page1 -> {
                    if (!edgeCorners.isEmpty()) {
                        Point[] intPoints = new Point[edgeCorners.size()];

                        for (int i = 0; i < edgeCorners.size(); i++) {
                            intPoints[i] = edgeCorners.get(i);
                        }

                        page1.setDocumentCorners(intPoints);
                    }

                    return Observable.just(page1);
                })
                .toList() // converts this to a Single
                .map(pages -> pages.get(0))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

    }

    @NonNull
    private Observable<File> saveProcessedImageWithFallback(final DatacapImageProcessor.DatacapImage datacapImage,
                                                            final List<Point> edgeCorners,
                                                            final String batchId,
                                                            @NonNull final Page realmPage,
                                                            final File file) {
        return Observable.fromCallable(() -> {
            File processed;

            try {
                edgeCorners.clear();
                edgeCorners.addAll(datacapImage.getCorners());

                processed = mFileManager.saveImage(batchId,
                        realmPage.getId().concat("_processed"),
                        datacapImage.getProcessedImage());
            } catch (Exception e) {
                Log.w("CameraInteractor", "process image: ", e);
                processed = saveOriginalAsProcessed(file, realmPage.getId());
            }

            return processed;
        });
    }

    /**
     * Add a new {@link Page} using an imported image and process the other image.
     */
    public Single<Page> saveImportedImagePage(@NonNull final Page page,
                                              @NonNull final String imagePath) {

        final String batchId = mBatchDaggerHelper.getBatch().getId();
        final List<Point> edgeCorners = new ArrayList<>();

        // save original image file async
        return mFileManager.importImageFile(batchId, page.getId(), imagePath)
                .doOnNext(file -> {
                    // save original image path
                    page.setOriginalImagePath(file.getPath());
                })
                .flatMap((Function<File, Observable<File>>) file -> {
                    //  deskew image
                    return startImageCorrection(file.getPath())
                            .flatMap((Function<DatacapImageProcessor.DatacapImage, Observable<File>>) datacapImage ->
                                    saveProcessedImageWithFallback(datacapImage, edgeCorners, batchId, page, file));
                }).doOnNext(correctedImage -> page.setImageFilePath(correctedImage.getPath()))
                .flatMap((Function<File, ObservableSource<Page>>) file -> Observable.just(page))
                .flatMap((Function<Page, Observable<Page>>) page1 -> {
                    if (!edgeCorners.isEmpty()) {
                        Point[] intPoints = new Point[edgeCorners.size()];

                        for (int i = 0; i < edgeCorners.size(); i++) {
                            intPoints[i] = edgeCorners.get(i);
                        }

                        page1.setDocumentCorners(intPoints);
                    }

                    return Observable.just(page1);
                }).toList() // converts this to a Single
                .map(pages -> pages.get(0))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Return an Observable that will run image processing in a background thread .
     *
     * @param originalImagePath
     * @return
     */
    public Observable<DatacapImageProcessor.DatacapImage> startImageCorrection(@NonNull final String originalImagePath) {
        return Observable.just(decodeBitmap(originalImagePath))
                .flatMap((Function<Bitmap, Observable<DatacapImageProcessor.DatacapImage>>) bitmap ->
                        mDatacapImageProcessor.applyAutomaticPerspectiveCorrection(bitmap))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());

    }

    private Bitmap decodeBitmap(String originalImagePath) {
        return BitmapFactory.decodeFile(originalImagePath);
    }

    private File saveOriginalAsProcessed(File originalImage, String realmPageId) {
        // save original image as processed
        final String processedFileName = originalImage.getPath()
                .replace(realmPageId,
                        realmPageId + "_processed");

        try {
            final File processed = new File(processedFileName);
            mFileManager.copyFile(originalImage, processed);
            return processed;
        } catch (IOException e) {
            Log.e("CameraInteractor", "Save processed file: ", e);
        }

        return null;
    }


}
