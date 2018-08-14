package com.futureworkshops.android.harborboulevard.presentation.accounts.create.wizard.step.camera;

import android.util.Pair;

import com.futureworkshops.datacap.common.model.Batch;
import com.futureworkshops.datacap.common.model.Page;
import com.ibm.datacap.sdk.model.IPageType;

import io.reactivex.Single;

public interface CameraContract {

    interface View {

        void proceed();

        void setPageType(IPageType pageType);
    }

    interface Presenter {

        void setPageType(Page page);

        void takePhoto(Single<Pair<byte[], Integer>> pictureSingle, Page page, Batch batch);
    }
}
