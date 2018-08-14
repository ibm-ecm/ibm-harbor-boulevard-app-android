package com.futureworkshops.android.harborboulevard.presentation.accounts.create.wizard.step.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Pair;

import com.futureworkshops.datacap.common.api.FileManager;
import com.futureworkshops.datacap.common.dagger.BatchDaggerHelper;
import com.futureworkshops.datacap.common.model.Batch;
import com.futureworkshops.datacap.common.model.Page;
import com.ibm.datacap.sdk.common.DatacapImageProcessor;
import com.ibm.datacap.sdk.model.IBatch;
import com.ibm.datacap.sdk.ui.camera.DatacapCameraView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class CameraPresenter implements CameraContract.Presenter {

    private static final String TAG = CameraPresenter.class.getSimpleName();

    private CameraContract.View mView;
    private final FileManager mFileManager;
    private final BatchDaggerHelper mBatchDaggerHelper;
    private DatacapImageProcessor mDatacapImageProcessor;

    @Inject
    public CameraPresenter(@Nonnull CameraContract.View mView,
                           @Nonnull FileManager fileManager,
                           @Nonnull BatchDaggerHelper batchDaggerHelper,
                           @NonNull DatacapImageProcessor datacapImageProcessor) {
        this.mView = mView;
        this.mFileManager = fileManager;
        this.mBatchDaggerHelper = batchDaggerHelper;
        this.mDatacapImageProcessor = datacapImageProcessor;
    }

    public void saveFile(Page page, IBatch batch, DatacapCameraView.DatacapDetectionResult datacapDetectionResult) {
        mFileManager.moveImageFile(batch.getId(), page.getId(), datacapDetectionResult.getDeskewedImagePath())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<File>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(File file) {
                        // save the image path
                        page.setImageFilePath(file.getAbsolutePath());
                        mView.proceed();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Error saving image: ", e);
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    @Override
    public void setPageType(Page page) {
        mView.setPageType(mBatchDaggerHelper.getBatchConfiguratorHelper().getPageTypeForPage(page));
    }

    @Override
    public void takePhoto(Single<Pair<byte[], Integer>> pictureSingle, Page page, Batch batch) {
        // create a page id so we can update the UI
        final String pageId = String.valueOf(System.currentTimeMillis());


        pictureSingle
                .subscribeOn(Schedulers.computation())
                .flatMap((Function<Pair<byte[], Integer>, SingleSource<Page>>) integerPair ->
                        saveManualCapturedImage(batch, page, integerPair.first, integerPair.second))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Page>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Page page) {
                        mView.proceed();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });

    }


    private Single<Page> saveManualCapturedImage(@NonNull Batch batch,
                                                 @NonNull final Page page,
                                                 @NonNull final byte[] data, final int cameraOrientation) {

        final String batchId = batch.getId();
        final List<Point> edgeCorners = new ArrayList<>();

        // save original image file async
        return mFileManager.saveImageFile(batchId, page.getId(), data, cameraOrientation)
                .flatMap((Function<File, Observable<File>>) file -> {
                    //  deskew image
                    return startImageCorrection(file.getPath())
                            .flatMap((Function<DatacapImageProcessor.DatacapImage, Observable<File>>) datacapImage ->
                                    saveProcessedImageWithFallback(datacapImage, edgeCorners, batchId, page, file));
                }).doOnNext(correctedImage ->
                        page.setImageFilePath(correctedImage.getPath()))
                .flatMap((Function<File, ObservableSource<Page>>) file -> Observable.just(page))
                .toList() // converts this to a Single
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
    private Observable<DatacapImageProcessor.DatacapImage> startImageCorrection(@NonNull final String originalImagePath) {
        return Observable.just(decodeBitmap(originalImagePath))
                .flatMap((Function<Bitmap, Observable<DatacapImageProcessor.DatacapImage>>) bitmap ->
                        mDatacapImageProcessor.applyAutomaticPerspectiveCorrection(bitmap))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());

    }

    private Bitmap decodeBitmap(String originalImagePath) {
        return BitmapFactory.decodeFile(originalImagePath);
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
