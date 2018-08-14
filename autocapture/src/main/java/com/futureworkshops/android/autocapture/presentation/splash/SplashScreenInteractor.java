package com.futureworkshops.android.autocapture.presentation.splash;

import android.support.annotation.NonNull;

import com.ibm.datacap.sdk.api.DatacapApi;
import com.ibm.datacap.sdk.model.IBatchConfiguration;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Perform operations required by the {@link SplashscreenContract.Presenter}.
 */
public class SplashScreenInteractor {

    @Inject
    DatacapApi mDatacapApi;


    public SplashScreenInteractor(@NonNull DatacapApi datacapApi) {
        mDatacapApi = datacapApi;
    }

    public Completable login(@NonNull String application, @NonNull String username,
                             @NonNull String password, @NonNull String stationId) {
        return mDatacapApi.login(application, username, password, stationId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public Observable<IBatchConfiguration> getBatchConfiguration(@NonNull String appName,
                                                                 @NonNull String dcoName) {
        return mDatacapApi.getBatchConfiguration(appName, dcoName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
