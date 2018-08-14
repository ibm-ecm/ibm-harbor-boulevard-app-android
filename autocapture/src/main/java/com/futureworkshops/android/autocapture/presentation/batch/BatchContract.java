package com.futureworkshops.android.autocapture.presentation.batch;

import android.support.annotation.NonNull;

import com.futureworkshops.datacap.common.model.Page;

import java.util.List;

/**
 * Created by stelian on 24/10/2017.
 */

public class BatchContract {
    public interface View {

        void onPagesLoaded(List<Page> pages);

        void onPagesLoadFailed(String error);

        void launchEditForPage(@NonNull String pageId);

        void onNoPageError();

        void onBatchUploadSuccess();

        void onBatchUploadFailed(String error);

        void onBatchCreated();
    }

    public interface Presenter {
        void getPages();

        void onPageClicked(String pageId);

        void uploadBatch();

        void createNewBatch();
    }

}
