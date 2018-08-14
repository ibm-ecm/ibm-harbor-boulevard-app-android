package com.futureworkshops.datacap.common.dagger;

import android.content.Context;

import com.futureworkshops.datacap.common.configuration.IDatacapConfiguration;
import com.ibm.datacap.sdk.api.DatacapApi;
import com.ibm.datacap.sdk.common.DatacapImageProcessor;
import com.ibm.datacap.sdk.id.DatacapIdProcessor;
import com.ibm.datacap.sdk.ocr.DatacapZoneProcessor;
import com.ibm.datacap.sdk.transaction.DatacapTransactionProcessor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * This module is used to provide all the Datacap dependencies that are otherwise expensive to create.
 */
@Module
public class DatacapApiModule {

    IDatacapConfiguration mConfiguration;

    public DatacapApiModule(IDatacapConfiguration configuration) {
        mConfiguration = configuration;
    }

    @Provides
    @Singleton
    IDatacapConfiguration providesConfiguration() {
        return mConfiguration;
    }

    @Provides
    @Singleton
    DatacapApi providesDatacapApi(Context context, IDatacapConfiguration configuration) {
        return new DatacapApi.Builder()
                .endpoint(configuration.getEndpoint())
                .serviceType(DatacapApi.DATACAP_SERVICE) // the type of service you want to use
                .withContext(context)
                .logLevel(DatacapApi.DEBUG) // debug
                .build();
    }

    @Provides
    @Singleton
    DatacapIdProcessor providesDatacapIdProcessor(Context context) {
        return new DatacapIdProcessor(context);
    }

    @Provides
    @Singleton
    DatacapZoneProcessor providesDatacapZonesProcessor(Context context) {
        return new DatacapZoneProcessor(context);
    }

    @Provides
    @Singleton
    DatacapTransactionProcessor providesDatacaTransactionProcessor(DatacapApi datacapApi,
                                                                   Context context) {
        return new DatacapTransactionProcessor(context, datacapApi);
    }

    @Provides
    @Singleton
    DatacapImageProcessor providesDatacapImageProcessor(Context context) {
        return new DatacapImageProcessor(context);
    }
}
