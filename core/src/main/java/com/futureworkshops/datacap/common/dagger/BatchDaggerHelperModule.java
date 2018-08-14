package com.futureworkshops.datacap.common.dagger;

import android.content.Context;

import com.futureworkshops.datacap.common.api.FileManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * This module is used to provide all the {@link BatchDaggerHelper}.
 * <p>
 * All objects defined in the {@link BatchDaggerHelper} need to be instantiated by the components
 * that have all required dependencies.
 */
@Module
public class BatchDaggerHelperModule {

    @Provides
    @Singleton
    BatchDaggerHelper providesBatchDaggerHelper() {
        return new BatchDaggerHelper();
    }

    @Provides
    @Singleton
    FileManager providesFileManager(Context context) {
        return new FileManager(context);
    }

}
