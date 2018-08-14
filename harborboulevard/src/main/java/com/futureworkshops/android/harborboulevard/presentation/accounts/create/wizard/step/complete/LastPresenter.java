package com.futureworkshops.android.harborboulevard.presentation.accounts.create.wizard.step.complete;

import com.futureworkshops.datacap.common.model.Batch;
import com.ibm.datacap.sdk.api.DatacapApi;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;

class LastPresenter implements LastContract.Presenter {

    private final DatacapApi mDatacapApi;
    private final LastContract.View mView;

    @Inject
    public LastPresenter(@Nonnull DatacapApi datacapApi,
                         @Nonnull LastContract.View view) {
        mDatacapApi = datacapApi;
        mView = view;
    }

    @Override
    public void uploadBatch(Batch batch) {
        mDatacapApi.uploadBatch("AccountOpening", "Mobile Only",
                batch, batch.getLocalDir())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        mView.onUploadSuccess();
                        dispose();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.onUploadError(e);
                        dispose();
                    }
                });

    }
}
