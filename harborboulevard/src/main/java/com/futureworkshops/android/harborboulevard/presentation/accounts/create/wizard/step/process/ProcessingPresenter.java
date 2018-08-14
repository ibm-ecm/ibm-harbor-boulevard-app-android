package com.futureworkshops.android.harborboulevard.presentation.accounts.create.wizard.step.process;

import android.util.Log;

import com.futureworkshops.datacap.common.model.Page;
import com.ibm.datacap.sdk.id.DatacapIdProcessor;
import com.ibm.datacap.sdk.id.model.IdData;
import com.ibm.datacap.sdk.model.IField;
import com.ibm.datacap.sdk.ocr.DatacapZoneProcessor;
import com.ibm.datacap.sdk.transaction.DatacapTransactionProcessor;

import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static com.ibm.datacap.sdk.id.DatacapId.TYPE_US_DRIVING_LICENSE;

public class ProcessingPresenter implements ProcessingContract.Presenter {

    private final ProcessingContract.View mView;
    /**
     * Extracts information from documents based on a predefined template.
     */
    private final DatacapZoneProcessor mDatacapZoneProcessor;
    /**
     * Extracts information from ID documents.
     */
    private final DatacapIdProcessor mDatacapIdProcessor;
    private final DatacapTransactionProcessor mDatacapTransactionProcessor;

    @Inject
    public ProcessingPresenter(@Nonnull ProcessingContract.View view,
                               @Nonnull DatacapZoneProcessor datacapZoneProcessor,
                               @Nonnull DatacapIdProcessor datacapIdProcessor,
                               @Nonnull DatacapTransactionProcessor datacapTransactionProcessor) {
        mView = view;
        mDatacapZoneProcessor = datacapZoneProcessor;
        mDatacapIdProcessor = datacapIdProcessor;
        mDatacapTransactionProcessor = datacapTransactionProcessor;
    }

    @Override
    public void extractTextFromForm(Page page) {
        if (page != null) {
            Observable<List<IField>> listObservable;
            if (mDatacapZoneProcessor.isProcessorReady()) {
                listObservable = mDatacapZoneProcessor.processPage(page);
            } else {
                listObservable = mDatacapZoneProcessor.initDefaultInstance()
                        .flatMap(aBoolean -> {
                            if (aBoolean) {
                                return mDatacapZoneProcessor.processPage(page)
                                        .subscribeOn(Schedulers.newThread())
                                        .observeOn(AndroidSchedulers.mainThread());
                            } else {
                                throw new RuntimeException("Could not initialise DatacapZoneProcessor");
                            }

                        });
            }

            listObservable.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableObserver<List<IField>>() {
                        @Override
                        public void onNext(List<IField> iFields) {
                            Log.e("Form processing", "Fields are here: " + iFields);
                            mView.onTextExtracted(iFields);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("Error", "Unable to extract fields ", e);
                            mView.errorExtractingText(e);
                            dispose();
                        }

                        @Override
                        public void onComplete() {
                            dispose();
                        }
                    });
        } else {
            mView.errorExtractingText(new NullPointerException("No page"));
        }
    }

    @Override
    public void extractTextFromDLBack(Page page) {
        if (page != null) {
            Observable<IdData> dataObservable;
            if (mDatacapIdProcessor.isInitialized()) {
                dataObservable = mDatacapIdProcessor.processIDPage(page, TYPE_US_DRIVING_LICENSE);
            } else {
                dataObservable = mDatacapIdProcessor.initialize(DatacapZoneProcessor.ENGLISH)
                        .flatMap(aBoolean -> {
                            if (aBoolean) {
                                return mDatacapIdProcessor.processIDPage(page, TYPE_US_DRIVING_LICENSE)
                                        .subscribeOn(Schedulers.newThread())
                                        .observeOn(AndroidSchedulers.mainThread());
                            } else {
                                throw new RuntimeException("Could not initialise DatacapIdProcessor");
                            }
                        });
            }

            dataObservable
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableObserver<IdData>() {
                        @Override
                        public void onNext(IdData idData) {
                            mView.onIdTextExtracted(idData.getFields());
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("Error", "Unable to extract id data", e);
                            mView.errorExtractingText(e);
                            dispose();
                        }

                        @Override
                        public void onComplete() {
                            dispose();
                        }
                    });
        } else {
            mView.errorExtractingText(new NullPointerException("No page"));
        }
    }

    @Override
    public void extractTextChequeInformation(Page page) {
        if (page != null) {
            mDatacapTransactionProcessor.processChequePage(page)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableObserver<List<IField>>() {
                        @Override
                        public void onNext(List<IField> iFields) {
                            mView.onTextExtracted(iFields);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("Error", "Unable to extract cheque fields ", e);
                            mView.errorExtractingText(e);
                            dispose();
                        }

                        @Override
                        public void onComplete() {
                            dispose();
                        }
                    });
        } else {
            mView.errorExtractingText(new NullPointerException("No page"));
        }
    }
}
