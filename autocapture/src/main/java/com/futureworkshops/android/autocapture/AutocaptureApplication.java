package com.futureworkshops.android.autocapture;

import com.futureworkshops.android.autocapture.domain.AutocaptureDatacapConfiguration;
import com.futureworkshops.android.autocapture.domain.dagger.AppComponent;
import com.futureworkshops.android.autocapture.domain.dagger.DaggerAppComponent;
import com.futureworkshops.datacap.common.dagger.DatacapApiModule;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;

/**
 * Extending {@link DaggerApplication} is the equivalent of implementing {@link dagger.android.HasActivityInjector} and
 * injecting a {@link dagger.android.DispatchingAndroidInjector}. What this means, is our Application will provide
 * {@link AndroidInjector} to our Activities.
 */
public class AutocaptureApplication extends DaggerApplication {


    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        final AppComponent appComponent = DaggerAppComponent.builder()
                .application(this)
                .datacapApi(new DatacapApiModule(new AutocaptureDatacapConfiguration()))
                .build();

        appComponent.inject(this);

        return appComponent;
    }


}
