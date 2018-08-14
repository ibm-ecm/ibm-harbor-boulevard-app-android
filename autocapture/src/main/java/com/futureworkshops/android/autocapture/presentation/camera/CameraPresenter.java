package com.futureworkshops.android.autocapture.presentation.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.util.Pair;

import com.futureworkshops.android.autocapture.presentation.common.FlashToggleButton;
import com.futureworkshops.datacap.common.camera.CameraParams;
import com.futureworkshops.datacap.common.model.Page;
import com.ibm.capture.sdk.ui.camera2.CameraView;
import com.ibm.datacap.sdk.ui.camera.DatacapCameraView;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

// Make sure you provide  DatacapDetectionListener to the DatacapCameraView.
// The first is responsible for handling auto-captured images .
public class CameraPresenter implements CameraContract.Presenter, DatacapCameraView.DatacapDetectionListener, CameraView.CameraStateCallback {

    public static final String IMAGE = "image.jpg";

    private final Context mContext;
    private CameraInteractor mCameraInteractor;
    private CameraContract.View mView;

    @Inject
    public CameraPresenter(@NonNull Context context, CameraInteractor interactor,
                           @NonNull CameraContract.View cameraView) {
        mContext = context;
        mCameraInteractor = interactor;
        mView = cameraView;
    }

    @Override
    public void configureCameraView() {
        // select physical device by orientation
        mView.setCameraFacing(DatacapCameraView.FACING_BACK);

        // use auto flash
        mView.setFlashAuto();

        // The minimum area percentage (relative to the screen area )that the detected document
        // must have in order to be considered valid.
        mView.setSmallestAreaPercentage(CameraParams.SMALLEST_AREA_PERCENTAGE);

        // The minimum margin required between the detection result and the screen boundaries.
        mView.setMinimumDetectionMargin(CameraParams.MINIMUM_DETECTION_MARGIN);

        // The aspect ratio required for the detection result.
        mView.setDocumentAspectRatio(CameraParams.DOCUMENT_ASPECT_RATIO);

        // callback that notifies when we have a detection result
        mView.setDocumentListener(this);

        // get notified when the camera opens/closes
        mView.setCameraStateCallback(this);

        mView.onCameraViewConfigured();
    }

    @Override
    public void onCameraOpened(CameraView cameraView) {
        mView.onCameraOpened();
    }

    @Override
    public void onCameraClosed(CameraView cameraView) {
        mView.onCameraClosed();
    }

    @Override
    public void getBatchPages() {
        List<Page> pages = mCameraInteractor.getPages();
        mView.onPagesLoaded(pages);
    }

    @Override
    public void onFlashClicked(int state) {
        switch (state) {
            case FlashToggleButton.FLASH_TOGGLE_AUTO:
                mView.setFlashAuto();
                break;
            case FlashToggleButton.FLASH_TOGGLE_ON:
                mView.setFlashOn();
                break;
            case FlashToggleButton.FLASH_TOGGLE_OFF:
                mView.setFlashOff();
                break;
        }
    }

    @Override
    public void importImage(@NonNull String selectedImagePath) {
        // we want to stop detection while processing this result
        mView.stopDetection();

        // create a page id so we can update the UI
        final String pageId = String.valueOf(System.currentTimeMillis());

        // notify UI
        mView.addLoadingItem(pageId);

        // create page with above id
        final Page newPage = mCameraInteractor.createPage(pageId);

        mCameraInteractor.saveImportedImagePage(newPage, selectedImagePath)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Page>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Page page) {
                        // this page contains the paths to the saved image files
                        // this is the page we want to add to the document
                        // and this is the page that we send back to update the image list
                        mView.onPageSaved(page);

                        // add the page to the document
                        mCameraInteractor.addPageToDocument(page);

                        // resume detection
                        mView.startDetection();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.onPageSaveFailed(pageId, e.getMessage());

                        // resume detection
                        mView.startDetection();
                    }
                });

    }

    /**
     * Take a picture, run edge detection and add a new Page containing the results.
     * <p>
     * <p> {@link DatacapCameraView} will return a {@link Pair} containing image data(byte[])
     * and the rotation of the camera sensor relative to the device orientation.</p>
     *
     * @param pictureSingle {@link Single} returned by {@link DatacapCameraView#takePhoto()}.
     */
    @Override
    public void takePhoto(Single<Pair<byte[], Integer>> pictureSingle) {
        // we want to stop detection while processing this result
        mView.stopDetection();

        // create a page id so we can update the UI
        final String pageId = String.valueOf(System.currentTimeMillis());

        // notify UI
        mView.addLoadingItem(pageId);

        // create page with above id
        final Page newPage = mCameraInteractor.createPage(pageId);

        pictureSingle
                .subscribeOn(Schedulers.computation())
                .flatMap(new Function<Pair<byte[], Integer>, SingleSource<Page>>() {
                    @Override
                    public SingleSource<Page> apply(Pair<byte[], Integer> integerPair) throws Exception {
                        return mCameraInteractor
                                .saveManualCapturePage(newPage, integerPair.first, integerPair.second);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Page>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Page page) {
                        // this page contains the paths to the saved image files
                        // this is the page we want to add to the document
                        // and this is the page that we send back to update the image list
                        mView.onPageSaved(page);

                        // add the page to the document
                        mCameraInteractor.addPageToDocument(page);

                        // resume detection
                        mView.startDetection();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.onPageSaveFailed(pageId, e.getMessage());

                        // resume detection
                        mView.startDetection();
                    }
                });

    }

    @Override
    public void showBatchActivity() {
        mView.onShowBatchActivityRequested();
    }

    @Override
    public void startImageImport() {
        mView.launchImageImport();
    }

    @Override
    public void onDocumentProcessingStarted() {
        // not needed here
    }

    /**
     * Called when document edges are automatically detected.
     * <p>
     * This method will create a Page stub so the UI can show some state until the actual image
     * files are saved.
     */
    @Override
    public void onDocumentDetected(DatacapCameraView.DatacapDetectionResult datacapDetectionResult) {
        // we want to stop detection while processing this result
        mView.stopDetection();

        // create a page id so we can update the UI
        final String pageId = String.valueOf(System.currentTimeMillis());

        // notify UI
        mView.addLoadingItem(pageId);

        // create page with above id
        final Page newPage = mCameraInteractor.createPage(pageId);

        //  save image files to the page
        mCameraInteractor.saveAutocaptureImagePage(newPage, datacapDetectionResult)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Page>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Page page) {
                        // this page contains the paths to the saved image files
                        // this is the page we want to add to the document
                        // and this is the page that we send back to update the image list
                        mView.onPageSaved(page);

                        // add the page to the document
                        mCameraInteractor.addPageToDocument(page);

                        // resume detection
                        mView.startDetection();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.onPageSaveFailed(pageId, e.getMessage());

                        // resume detection
                        mView.startDetection();
                    }
                });

    }

    /**
     * Create Bitmap from byte[] and apply required rotation
     *
     * @param integerPair
     * @return
     */
    private Bitmap fromByteArray(Pair<byte[], Integer> integerPair) {
        final Bitmap bitmap = BitmapFactory.decodeByteArray(integerPair.first,
                0, integerPair.first.length);
        final int mImageRotation = integerPair.second;

        // we only rotate image if it's required
        final Bitmap correctedFrame;
        if (mImageRotation == 0) {
            correctedFrame = bitmap;
        } else {
            correctedFrame = rotateBitmap(bitmap, mImageRotation);
            bitmap.recycle();
        }

        return correctedFrame;
    }

    private Bitmap rotateBitmap(@NonNull Bitmap source, int rotationAngle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotationAngle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }


}
