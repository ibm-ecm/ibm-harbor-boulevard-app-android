package com.futureworkshops.android.autocapture.presentation.batch.dagger;

import android.content.Context;

import com.futureworkshops.android.autocapture.presentation.batch.BatchContract;
import com.futureworkshops.android.autocapture.presentation.batch.BatchInteractor;
import com.futureworkshops.android.autocapture.presentation.batch.BatchPresenter;
import com.futureworkshops.android.autocapture.presentation.batch.view.BatchActivity;
import com.futureworkshops.datacap.common.api.FileManager;
import com.futureworkshops.datacap.common.configuration.IDatacapConfiguration;
import com.futureworkshops.datacap.common.dagger.BatchDaggerHelper;
import com.ibm.datacap.sdk.api.DatacapApi;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module
public abstract class BatchModule {

    @Binds
    abstract BatchContract.View providesBatchView(BatchActivity mainActivity);


    @Provides
    static BatchInteractor providesBatchInteractor(BatchDaggerHelper batchDaggerHelper,
                                                   DatacapApi datacapApi,
                                                   FileManager fileManager,
                                                   IDatacapConfiguration configuration) {
        return new BatchInteractor(batchDaggerHelper, datacapApi,fileManager,configuration);
    }

    @Provides
    static BatchContract.Presenter providesBatchPresenter(Context context,
                                                          BatchInteractor interactor,
                                                          BatchContract.View view) {
        return new BatchPresenter(context, interactor, view);
    }

}
