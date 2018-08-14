package com.futureworkshops.android.autocapture.presentation.splash.dagger;

import android.content.Context;

import com.futureworkshops.android.autocapture.presentation.splash.SplashScreenInteractor;
import com.futureworkshops.android.autocapture.presentation.splash.SplashScreenPresenter;
import com.futureworkshops.android.autocapture.presentation.splash.SplashscreenActivity;
import com.futureworkshops.android.autocapture.presentation.splash.SplashscreenContract;
import com.futureworkshops.datacap.common.configuration.IDatacapConfiguration;
import com.futureworkshops.datacap.common.dagger.BatchDaggerHelper;
import com.ibm.datacap.sdk.api.DatacapApi;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

/**
 * Dagger module used for specifying dependencies for the Splashscreen activity.
 * Created by stelian on 23/10/2017.
 */
@Module
public abstract class SplashscreenModule {

    /**
     * This method binds the SplashscreenActivity to SplashscreenContract.View.
     * When the SplashscreenContract.View is requested, the activity is returned.
     */
    @Binds
    abstract SplashscreenContract.View providesView(SplashscreenActivity activity);

    @Provides
    static SplashScreenInteractor providesSplashScreenInteractor(DatacapApi datacapApi) {
        return new SplashScreenInteractor(datacapApi);
    }

    @Provides
    static SplashscreenContract.Presenter providesSplashScreenPresenter(Context context,
                                                                        SplashScreenInteractor interactor,
                                                                        SplashscreenContract.View view,
                                                                        IDatacapConfiguration datacapConfiguration,
                                                                        BatchDaggerHelper batchDaggerHelper) {
        return new SplashScreenPresenter(context, interactor, view,
                datacapConfiguration, batchDaggerHelper);
    }
}
