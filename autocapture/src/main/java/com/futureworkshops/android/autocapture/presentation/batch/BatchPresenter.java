package com.futureworkshops.android.autocapture.presentation.batch;

import android.content.Context;
import android.support.annotation.NonNull;

import com.futureworkshops.datacap.common.model.Page;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by stelian on 24/10/2017.
 */

public class BatchPresenter implements BatchContract.Presenter {

    private Context mContext;
    private BatchContract.View mView;
    private BatchInteractor mBatchInteractor;


    @Inject
    public BatchPresenter(Context context, BatchInteractor interactor, BatchContract.View view) {
        mContext = context;
        mView = view;
        mBatchInteractor = interactor;
    }

    @Override
    public void getPages() {
        final List<Page> pages = mBatchInteractor.getPages();

        if (pages.isEmpty()) {
            mView.onPagesLoadFailed("No pages found");
        } else {
            mView.onPagesLoaded(pages);
        }
    }

    @Override
    public void onPageClicked(@NonNull String pageId) {
        mView.launchEditForPage(pageId);
    }

    @Override
    public void uploadBatch() {
        if (mBatchInteractor.getPages().size() == 0) {
            mView.onNoPageError();
        } else {
            mBatchInteractor.uploadBatch()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new CompletableObserver() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onComplete() {
                            mBatchInteractor.clearBatchFolder();

                            mView.onBatchUploadSuccess();
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            mView.onBatchUploadFailed(e.getMessage());
                        }
                    });
        }
    }

    @Override
    public void createNewBatch() {
        mBatchInteractor.createNewBatch(mContext);
        mView.onBatchCreated();
    }
}
