package com.futureworkshops.android.harborboulevard;

import android.support.v4.app.Fragment;

import com.futureworkshops.android.harborboulevard.domain.HarbourBoulevardDatacapConfiguration;
import com.futureworkshops.android.harborboulevard.domain.dagger.AppComponent;
import com.futureworkshops.android.harborboulevard.domain.dagger.DaggerAppComponent;
import com.futureworkshops.datacap.common.dagger.DatacapApiModule;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

/**
 * Extending {@link DaggerApplication} is the equivalent of implementing {@link dagger.android.HasActivityInjector} and
 * injecting a {@link dagger.android.DispatchingAndroidInjector}. What this means, is our Application will provide
 * {@link AndroidInjector} to our Activities.
 */
public class HarborBoulevardApplication extends DaggerApplication implements HasSupportFragmentInjector {

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingFragmentInjector;

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        final AppComponent appComponent = DaggerAppComponent.builder()
                .application(this)
                .datacapApi(new DatacapApiModule(new HarbourBoulevardDatacapConfiguration()))
                .build();

        appComponent.inject(this);

        return appComponent;
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingFragmentInjector;
    }

}
