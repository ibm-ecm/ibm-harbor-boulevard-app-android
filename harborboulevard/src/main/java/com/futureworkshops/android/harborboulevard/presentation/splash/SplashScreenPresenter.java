package com.futureworkshops.android.harborboulevard.presentation.splash;

import android.content.Context;
import android.util.Log;

import com.futureworkshops.datacap.common.api.BatchConfiguratorHelper;
import com.futureworkshops.datacap.common.configuration.IDatacapConfiguration;
import com.futureworkshops.datacap.common.dagger.BatchDaggerHelper;
import com.ibm.datacap.sdk.model.IBatchConfiguration;

import io.reactivex.CompletableObserver;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by stelian on 24/11/2016.
 */

public class SplashScreenPresenter implements SplashscreenContract.Presenter {
    public static final String TAG = SplashScreenPresenter.class.getSimpleName();

    private Context mContext;
    private IDatacapConfiguration mConfiguration;
    private SplashscreenContract.View mView;
    private SplashScreenInteractor mSplashScreenInteractor;
    private BatchDaggerHelper mBatchDaggerHelper;


    public SplashScreenPresenter(Context context, SplashScreenInteractor interactor,
                                 SplashscreenContract.View view,
                                 IDatacapConfiguration datacapConfiguration,
                                 BatchDaggerHelper batchDaggerHelper) {
        mContext = context;
        mSplashScreenInteractor = interactor;
        mView = view;
        mConfiguration = datacapConfiguration;
        mBatchDaggerHelper = batchDaggerHelper;
    }


    /**
     * Authenticate the user and perform all required operations to configure the app for use.
     */
    @Override
    public void login() {
        mView.showIsAuthenticating();

        mSplashScreenInteractor.login(mConfiguration.getDatacapApplication(),
                mConfiguration.getUsername(), mConfiguration.getPassword(), mConfiguration.getStation())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {
                        mView.onLoginSuccess();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.getMessage());
                        mView.onLoginFailed(e.getMessage());
                    }
                });

    }


    /**
     * Download the DCO file to be used by this application (also referred to as Configuration or
     * BatchConfiguration)
     */
    public void getConfiguration() {
        mView.showDownloadingConfiguration();
        mSplashScreenInteractor.getBatchConfiguration(mConfiguration.getDatacapApplication(),
                mConfiguration.getDatacapApplication())
                .subscribe(new Observer<IBatchConfiguration>() {
                    @Override
                    public void onComplete() {
                    }


                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.getMessage());
                        mView.onDownloadDcoFailed(e.getMessage());
                    }

                    @Override
                    public void onNext(IBatchConfiguration batchConfig) {

                        // create batch configuration helper
                        mBatchDaggerHelper.setBatchConfiguratorHelper(new BatchConfiguratorHelper(batchConfig));

                        mView.onDownloadDcoSuccess();
                    }
                });
    }


}
