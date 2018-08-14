package com.futureworkshops.android.autocapture.presentation.batch;

import android.content.Context;
import android.support.annotation.NonNull;

import com.futureworkshops.datacap.common.api.BatchFactory;
import com.futureworkshops.datacap.common.api.FileManager;
import com.futureworkshops.datacap.common.configuration.IDatacapConfiguration;
import com.futureworkshops.datacap.common.dagger.BatchDaggerHelper;
import com.futureworkshops.datacap.common.model.Batch;
import com.futureworkshops.datacap.common.model.Page;
import com.futureworkshops.datacap.common.utils.CastUtils;
import com.ibm.datacap.sdk.api.DatacapApi;
import com.ibm.datacap.sdk.model.IPage;

import java.util.List;

import io.reactivex.Completable;

/**
 * Created by stelian on 24/10/2017.
 */

public class BatchInteractor {

    private BatchDaggerHelper mBatchDaggerHelper;
    private DatacapApi mDatacapApi;
    private IDatacapConfiguration mConfiguration;
    private FileManager mFileManager;


    public BatchInteractor(BatchDaggerHelper batchDaggerHelper, DatacapApi datacapApi,
                           FileManager fileManager, IDatacapConfiguration configuration) {
        mBatchDaggerHelper = batchDaggerHelper;
        mDatacapApi = datacapApi;
        mFileManager = fileManager;
        mConfiguration = configuration;
    }

    /**
     * Return the list of pages added to the document present in the batch.
     *
     * @return
     */
    public List<Page> getPages() {
        return (List<Page>) mBatchDaggerHelper.getBatch().getDocuments().get(0).getPages();
    }

    /**
     * Upload a batch.
     * <p>
     * Parameters:
     * <li>{@code application name}</li>
     * <li>{@code job type name} -> normally you would choose a job type after you login;
     * you would normally download the workflows  DatacapApi#getWorkflows()
     * and depending on which workflow you want you call DatacapApi#getJobs()
     * to get the available job types</li>
     * <li> {@code batch}  -> the batch you want to upload</li>
     * <li> {@code batchFolder} -> the folder that contains the batch images, this needs to be a VALID folder
     * with WRITE permissions; this is sent separate from the batch because the
     * batch will be converted to IBatch which doesn't save the folder property.</li>
     */
    public Completable uploadBatch() {
        final Batch batch = mBatchDaggerHelper.getBatch();

        return mDatacapApi.uploadBatch(mConfiguration.getDatacapApplication(), // the application name
                "Mobile Only", // the job type
                batch, // the batch we want to upload
                batch.getLocalDir()); // batch local folder
    }

    public void clearBatchFolder(){
        mFileManager.deleteBatchFolder();
    }

    public void createNewBatch(@NonNull Context context) {
        Batch batch = BatchFactory.createAutocaptureBatch(context,
                mBatchDaggerHelper.getBatchConfiguratorHelper());

        mBatchDaggerHelper.setBatch(batch);
    }
}
