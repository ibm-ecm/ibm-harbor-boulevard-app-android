package com.futureworkshops.android.harborboulevard.presentation.accounts.create.wizard.step.complete;

import com.futureworkshops.datacap.common.model.Batch;

public interface LastContract {

    interface View {

        void onUploadSuccess();

        void onUploadError(Throwable throwable);
    }

    interface Presenter {

        void uploadBatch(Batch batch);
    }
}
