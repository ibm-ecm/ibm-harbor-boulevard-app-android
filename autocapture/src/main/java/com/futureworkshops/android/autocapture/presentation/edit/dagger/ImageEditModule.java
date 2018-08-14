package com.futureworkshops.android.autocapture.presentation.edit.dagger;

import android.content.Context;

import com.futureworkshops.android.autocapture.presentation.edit.ImageEditActivity;
import com.futureworkshops.android.autocapture.presentation.edit.ImageEditContract;
import com.futureworkshops.android.autocapture.presentation.edit.ImageEditInteractor;
import com.futureworkshops.android.autocapture.presentation.edit.ImageEditPresenter;
import com.futureworkshops.datacap.common.api.FileManager;
import com.futureworkshops.datacap.common.dagger.BatchDaggerHelper;
import com.ibm.datacap.sdk.common.DatacapImageProcessor;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module
public abstract class ImageEditModule {

    @Binds
    abstract ImageEditContract.View providesImageEditView(ImageEditActivity imageEditActivity);


    @Provides
    static ImageEditInteractor providesImageEditInteractor(FileManager fileManager,
                                                           DatacapImageProcessor datacapImageProcessor,
                                                           BatchDaggerHelper batchDaggerHelper) {
        return new ImageEditInteractor(fileManager, datacapImageProcessor,batchDaggerHelper);
    }

    @Provides
    static ImageEditContract.Presenter providesCameraPresenter(Context context,
                                                               ImageEditInteractor interactor,
                                                               ImageEditContract.View view) {
        return new ImageEditPresenter(context, interactor, view);
    }

}
