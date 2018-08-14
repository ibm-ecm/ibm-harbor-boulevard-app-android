package com.futureworkshops.datacap.common.dagger;

import com.futureworkshops.datacap.common.api.BatchConfiguratorHelper;
import com.futureworkshops.datacap.common.model.Batch;

/**
 * We create this wrapper for the {@link Batch} and {@link BatchConfiguratorHelper} in order to remove
 * some complexity that would otherwise be required to configure Dagger correctly.
 * <p>
 * The {@link BatchDaggerHelper} will be provided in the main dependency graph BUT objects need
 * to be initialised when sub-dependencies are available.
 */

public class BatchDaggerHelper {

    private BatchConfiguratorHelper mBatchConfiguratorHelper;
    private Batch mBatch;

    public BatchConfiguratorHelper getBatchConfiguratorHelper() {
        return mBatchConfiguratorHelper;
    }

    public void setBatchConfiguratorHelper(BatchConfiguratorHelper batchConfiguratorHelper) {
        mBatchConfiguratorHelper = batchConfiguratorHelper;
    }

    public Batch getBatch() {
        return mBatch;
    }

    public void setBatch(Batch batch) {
        mBatch = batch;
    }
}
