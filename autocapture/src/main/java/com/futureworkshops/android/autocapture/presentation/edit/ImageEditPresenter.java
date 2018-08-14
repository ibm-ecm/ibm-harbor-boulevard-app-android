package com.futureworkshops.android.autocapture.presentation.edit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.annotation.NonNull;

import com.futureworkshops.datacap.common.model.Page;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.CompletableObserver;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by stelian on 24/10/2017.
 */

public class ImageEditPresenter implements ImageEditContract.Presenter {

    private Context mContext;
    private ImageEditContract.View mView;
    private ImageEditInteractor mImageEditInteractor;
    private Page mPage;

    public ImageEditPresenter(Context context, ImageEditInteractor interactor, ImageEditContract.View view) {
        mContext = context;
        mImageEditInteractor = interactor;
        mView = view;
    }

    @Override
    public void loadPage(@NonNull String pageId) {
        mPage = mImageEditInteractor.loadBageById(pageId);

        if (mPage == null) {
            mView.onPageNotFound();
        } else {
            mView.onPageLoaded(mPage);

            // also send the corner coordinates
            mView.onPageCornersLoaded(getPageCorners());
        }
    }

    @Override
    public void saveDeskewResults(Bitmap deskewedBitmap) {
        mImageEditInteractor.saveDeskewedImage(mPage.getImagePath(), deskewedBitmap)
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {

                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {
                        mView.onPageSaved();

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.onPageSaveFailed(e.getMessage());
                    }
                });

    }

    /**
     * Deskew the image with the given coordinates.
     *
     * @param corners
     */
    @Override
    public void applyPerspectiveCorrection(final List<Point> corners) {
        // corners from Page have different order than the one required by the processor
        final Point[] orderedCorners = new Point[4];
        corners.toArray(orderedCorners);

        Observable.fromCallable(new Callable<Bitmap>() {
            @Override
            public Bitmap call() throws Exception {
                return BitmapFactory.decodeFile(mPage.getOriginalImagePath());
            }
        }).flatMap(new Function<Bitmap, ObservableSource<Bitmap>>() {
            @Override
            public ObservableSource<Bitmap> apply(final Bitmap bitmap) throws Exception {
                return mImageEditInteractor.getProcessorInitializeObservable()
                        .flatMapObservable(new Function<Boolean, ObservableSource<Bitmap>>() {
                            @Override
                            public ObservableSource<Bitmap> apply(Boolean aBoolean) throws Exception {
                                return mImageEditInteractor.applyPerspectiveCorrection(bitmap, orderedCorners);
                            }
                        });
            }
        }).subscribe(new Observer<Bitmap>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                mView.onImageDeskewFailed(e.getMessage());
            }

            @Override
            public void onNext(Bitmap bitmap) {
                // save corners to the page
                mPage.setDocumentCorners(orderedCorners);

                mView.onImageDeskewed(bitmap);
            }
        });
    }


    /**
     * Get the document corner coordinates or some default ones if the document hasn't been identified.
     *
     * @return
     */
    private Point[] getPageCorners() {
        final Point[] documentCorners = mPage.getDocumentCorners();
        if (validCorners(documentCorners)) {
            return documentCorners;
        } else {
            return mImageEditInteractor.getDefaultCorners();
        }
    }

    /**
     * Check that all four corners exist.
     *
     * @param documentCorners
     * @return
     */
    private boolean validCorners(Point[] documentCorners) {
        boolean valid = false;
        if (null != documentCorners) {
            int count = 0;

            for (Point point : documentCorners) {
                if (null == point) {
                    break;
                } else {
                    count++;
                }
            }

            valid = count == 4;
        }

        return valid;
    }

}
