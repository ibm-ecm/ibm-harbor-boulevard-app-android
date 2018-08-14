package com.futureworkshops.android.harborboulevard.presentation.splash;

/**
 * Created by stelian on 24/11/2016.
 */

public class SplashscreenContract {
    public interface View {

        void showIsAuthenticating();

        void showDownloadingConfiguration();

        void onLoginSuccess();

        void onLoginFailed(String message);

        void onDownloadDcoFailed(String message);

        void onDownloadDcoSuccess();

    }

    public interface Presenter {
        void login();

        void getConfiguration();
    }
}
