package com.futureworkshops.android.autocapture.presentation.camera.dagger;

import android.content.Context;

import com.futureworkshops.android.autocapture.presentation.camera.view.CameraActivity;
import com.futureworkshops.android.autocapture.presentation.camera.CameraContract;
import com.futureworkshops.android.autocapture.presentation.camera.CameraInteractor;
import com.futureworkshops.android.autocapture.presentation.camera.CameraPresenter;
import com.futureworkshops.datacap.common.api.FileManager;
import com.futureworkshops.datacap.common.dagger.BatchDaggerHelper;
import com.ibm.datacap.sdk.common.DatacapImageProcessor;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module
public abstract class CameraModule {

    @Binds
    abstract CameraContract.View providesBaseCameraView(CameraActivity mainActivity);


    @Provides
    static CameraInteractor providesCameraInteractor(BatchDaggerHelper batchDaggerHelper,
                                                     FileManager fileManager,
                                                     DatacapImageProcessor datacapImageProcessor) {
        return new CameraInteractor(batchDaggerHelper, fileManager, datacapImageProcessor);
    }

    @Provides
    static CameraContract.Presenter providesCameraPresenter(Context context,
                                                                        CameraInteractor interactor,
                                                                        CameraContract.View view) {
        return new CameraPresenter(context, interactor, view);
    }

}
