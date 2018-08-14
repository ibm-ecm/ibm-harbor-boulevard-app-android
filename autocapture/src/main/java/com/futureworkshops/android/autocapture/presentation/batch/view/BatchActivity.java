package com.futureworkshops.android.autocapture.presentation.batch.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.futureworkshops.android.autocapture.R;
import com.futureworkshops.android.autocapture.presentation.batch.BatchContract;
import com.futureworkshops.android.autocapture.presentation.batch.BatchPresenter;
import com.futureworkshops.android.autocapture.presentation.camera.view.CameraActivity;
import com.futureworkshops.android.autocapture.presentation.common.BaseActivity;
import com.futureworkshops.android.autocapture.presentation.edit.ImageEditActivity;
import com.futureworkshops.datacap.common.model.Page;
import com.futureworkshops.datacap.common.utils.SnackUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;

/**
 * Activity that displays all the Pages of the current Batch and allows the user to upload the batch.
 */
public class BatchActivity extends BaseActivity implements BatchContract.View, PageRecyclerAdapter.OnBatchItemClickListener {

    private static final int RC_EDIT_IMAGE = 53;

    @Inject
    BatchPresenter mBatchPresenter;

    @BindView(R.id.batch_recycler_view)
    RecyclerView mPageRecyclerView;

    private PageRecyclerAdapter mPageRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_batch);

        ButterKnife.bind(this);

        setupToolbar(true);

        // init recycler
        initImageRecycler();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mBatchPresenter.getPages();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_batch, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_upload:
                mBatchPresenter.uploadBatch();
                break;
            default:
                super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_EDIT_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    String pageId = data.getStringExtra(ImageEditActivity.PAGE_ID);

                    if (!TextUtils.isEmpty(pageId)) {
                        // this will tell the adapter to invalidate the cache for this page
                        // and reload the image
                        mPageRecyclerAdapter.updateItemImage(pageId);
                    }
                }
                break;
        }

    }

    @Override
    public void onPagesLoaded(final List<Page> pages) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mPageRecyclerAdapter.setItems(pages);
            }
        }, 150);
    }

    @Override
    public void onPagesLoadFailed(String error) {
        SnackUtils.showSimpleSnackbar(this, error);
    }

    @Override
    public void launchEditForPage(@NonNull String pageId) {
        Intent intent = new Intent(this, ImageEditActivity.class);
        intent.putExtra(ImageEditActivity.PAGE_ID, pageId);
        startActivityForResult(intent, RC_EDIT_IMAGE);
    }

    @Override
    public void onNoPageError() {
        SnackUtils.showSimpleSnackbar(this, "You need to take some pictures before uploading");
    }

    @Override
    public void onBatchUploadSuccess() {
        SnackUtils.showSnackbarWithAction(this, "Upload successful",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);

                        // create new batch
                        mBatchPresenter.createNewBatch();
                    }
                });
    }

    @Override
    public void onBatchUploadFailed(String error) {
        SnackUtils.showSimpleSnackbar(this, error);
    }

    @Override
    public void onBatchCreated() {
        // after we created the new batch we need to restart the Camera Activity
        Intent intent = new Intent(BatchActivity.this, CameraActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }

    @Override
    public void onPageClick(@NonNull String pageId) {
        mBatchPresenter.onPageClicked(pageId);
    }

    /**
     * Initialize the RecyclerView that will hold the image previews.
     */
    private void initImageRecycler() {
        if (mPageRecyclerAdapter == null) {
            mPageRecyclerAdapter = new PageRecyclerAdapter(this);

            // add click listener to be notified when user taps on a Page item
            mPageRecyclerAdapter.setOnBatchItemClickListener(this);
        }

        mPageRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mPageRecyclerView.setAdapter(mPageRecyclerAdapter);

    }


}
