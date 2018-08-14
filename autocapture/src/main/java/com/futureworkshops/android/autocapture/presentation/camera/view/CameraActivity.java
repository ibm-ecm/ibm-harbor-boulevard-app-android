package com.futureworkshops.android.autocapture.presentation.camera.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.ImageButton;

import com.futureworkshops.android.autocapture.R;
import com.futureworkshops.android.autocapture.presentation.batch.view.BatchActivity;
import com.futureworkshops.android.autocapture.presentation.camera.CameraContract;
import com.futureworkshops.android.autocapture.presentation.camera.CameraPresenter;
import com.futureworkshops.android.autocapture.presentation.camera.view.model.PageItem;
import com.futureworkshops.android.autocapture.presentation.common.FlashToggleButton;
import com.futureworkshops.datacap.common.camera.BaseCameraActivity;
import com.futureworkshops.datacap.common.model.Page;
import com.futureworkshops.datacap.common.utils.SnackUtils;
import com.ibm.capture.sdk.ui.camera2.CameraView;
import com.ibm.datacap.sdk.ui.camera.AbstractCaptureView;
import com.ibm.datacap.sdk.ui.camera.DatacapCameraView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.AndroidInjection;


public class CameraActivity extends BaseCameraActivity implements CameraContract.View {
    
    public static final int SELECT_IMAGE_REQUEST_CODE = 10;
    public static final String INTENT_TYPE_IMAGE = "image/*";
    
    @Inject
    CameraPresenter mCameraPresenter;
    
    @BindView(R.id.core_camera_view)
    DatacapCameraView mCameraView;
    
    @BindView(R.id.import_btn)
    ImageButton mLaunchGalleryBtn;
    
    @BindView(R.id.recent_pictures_rv)
    RecyclerView mRecentPicturesRv;
    
    @BindDimen(R.dimen.camera_controls_layout_height)
    int mLayoutControlsHeight;
    
    private PageImageAdapter mPageImageAdapter;
    private boolean mHasCameraPermission = false;
    
    private boolean mCameraViewConfigured = false;
    private boolean mCanDetect = false;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_camera);
        
        ButterKnife.bind(this);
        
        // set the position of the scanning indicator
        View cameraControls = (View) findViewById(R.id.cameraControls);
        cameraControls.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                cameraControls.getViewTreeObserver().removeOnPreDrawListener(this);
                
                
                
                mCameraView.setScanningIndicatorCoordinates(0, cameraControls.getTop() -
                CameraActivity.this.getResources().getDimensionPixelSize(R.dimen.camera_controls_gallery_margin_top));

                return true;
            }
        });
        
        
        // init page recycler
        initImageRecycler();

        /*
            Check for camera permissions and request them if necessary.

            Results will be delivered in handleCameraPermissionGranted(), error messages will be
            displayed automatically (configured in BaseCameraActivity).
         */
        checkCameraPermissions();
        
        
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        
        // it's ok to load the images here because this activity is the only place
        // where the list of images can be modified
        loadBatchImages();
    }
    
    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();
        
        if (mHasCameraPermission && mCameraViewConfigured) {
            startCamera();
        }
    }
    
    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mCameraView.isStarted()) {
            mCameraView.stopCamera();
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECT_IMAGE_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    String selectedImagePath = data.getData().toString();
                    
                    if (!TextUtils.isEmpty(selectedImagePath)) {
                        mCameraPresenter.importImage(selectedImagePath);
                    }
                }
                break;
        }
    }
    
    @OnClick(R.id.use_flash)
    void onFlashClicked(FlashToggleButton flashBtn) {
        flashBtn.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
        
        mCameraPresenter.onFlashClicked(flashBtn.getState());
    }
    
    @OnClick(R.id.next_btn)
    void onNextButtonClicked(View v) {
        v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
        mCameraPresenter.showBatchActivity();
    }
    
    @OnClick(R.id.import_btn)
    void onImportBtnClick(View v) {
        v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
        
        mCameraView.stopDetection();
        
        // check permission before launching file chooser
        // onHandleStoragePermissionGranted will be called if we already have the permission
        // or after it has been granted
        checkStoragePermissions();
    }
    
    @OnClick(R.id.take_picture_btn)
    void onTakePhotoClicked(View v) {
        v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
        mCameraPresenter.takePhoto(mCameraView.takePhoto());
    }
    
    
    /**
     * Called after we receive the Camera permission.
     */
    @Override
    protected void handleCameraPermissionGranted() {
        mHasCameraPermission = true;
        mCameraPresenter.configureCameraView();
    }
    
    @Override
    protected void handleStoragePermissionGranted() {
        mCameraPresenter.startImageImport();
    }
    
    @Override
    public void onCameraViewConfigured() {
        mCameraViewConfigured = true;
        startCamera();
    }
    
    @Override
    public void setFlashAuto() {
        mCameraView.setFlashMode(AbstractCaptureView.FLASH_AUTO);
    }
    
    @Override
    public void setFlashOn() {
        mCameraView.setFlashMode(AbstractCaptureView.FLASH_ON);
    }
    
    @Override
    public void setFlashOff() {
        mCameraView.setFlashMode(AbstractCaptureView.FLASH_OFF);
    }
    
    @Override
    public void setCameraFacing(@DatacapCameraView.FacingMode int cameraFacing) {
        mCameraView.setCameraFacing(cameraFacing);
    }
    
    @Override
    public void setSmallestAreaPercentage(float smallestAreaPercentage) {
        mCameraView.setSmallestAreaPercentage(smallestAreaPercentage);
    }
    
    @Override
    public void setMinimumDetectionMargin(int minimumDetectionMargin) {
        mCameraView.setMinimumDetectionMargin(minimumDetectionMargin);
    }
    
    @Override
    public void setDocumentAspectRatio(float documentAspectRatio) {
        mCameraView.setDocumentAspectRatio(documentAspectRatio);
    }
    
    @Override
    public void stopDetection() {
        mCameraView.stopDetection();
    }
    
    @Override
    public void startDetection() {
        mCameraView.startDetection();
        
    }
    
    @Override
    public void setDocumentListener(DatacapCameraView.DatacapDetectionListener
                                        datacapDetectionListener) {
        mCameraView.setDatacapDetectionListener(datacapDetectionListener);
    }
    
    @Override
    public void setCameraStateCallback(CameraView.CameraStateCallback cameraStateCallback) {
        mCameraView.setCameraStateCallback(cameraStateCallback);
    }
    
    @Override
    public void onShowBatchActivityRequested() {
        final Intent documentIntent = new Intent(this, BatchActivity.class);
        startActivity(documentIntent);
    }
    
    @Override
    public void launchImageImport() {
        Intent intent = new Intent();
        intent.setType(INTENT_TYPE_IMAGE);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.title_import_image)),
            SELECT_IMAGE_REQUEST_CODE);
    }
    
    @Override
    public void onPagesLoaded(List<Page> pages) {
        // we have the pages currently added to the batch -> update the adapter
        if (!pages.isEmpty()) {
            mPageImageAdapter.setItems(pages);
        }
    }

    @Override
    public void onCameraOpened() {
        // startCamera detection if it's not already started
        if (!mCameraView.isDetecting()) {
            runOnUiThread(() -> {
                Log.e("CameraActivity", "onCameraOpened: " +"camera view is not detecting");
                mCameraView.startDetection();
            });
        }
    }

    @Override
    public void onCameraClosed() {
        //
    }

    @Override
    public void addLoadingItem(final String pageId) {
        // use a Handler to make sure the UI is updated
        new Handler().postDelayed(new Runnable() {
            
            @Override
            public void run() {
                // add a new item to the adapter
                // because we have no images yet we show a loading icon
                mPageImageAdapter.addItem(new PageItem(pageId));
                
                // scroll to the last added item
                mRecentPicturesRv.getLayoutManager().scrollToPosition(mPageImageAdapter.getItemCount() - 1);
            }
        }, 150);
    }
    
    @Override
    public void onPageSaveFailed(@NonNull String pageId, String message) {
        
        // remove the item used to identify the page
        mPageImageAdapter.removeItem(pageId);
        
        // show the error
        SnackUtils.showSimpleSnackbar(this, message);
    }
    
    @Override
    public void onPageSaved(@NonNull final Page page) {
        new Handler().postDelayed(new Runnable() {
            
            @Override
            public void run() {
                // update page item
                mPageImageAdapter.updateItem(page);
                
                // scroll to the last added item
                mRecentPicturesRv.getLayoutManager().scrollToPosition(mPageImageAdapter.getItemCount() - 1);
            }
        }, 150);
    }
    
    /**
     * Initialize the RecyclerView that will hold the image previews.
     */
    private void initImageRecycler() {
        if (mPageImageAdapter == null) {
            mPageImageAdapter = new PageImageAdapter(this);
        }
        
        mRecentPicturesRv.setLayoutManager(
            new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRecentPicturesRv.setAdapter(mPageImageAdapter);
        
    }
    
    private void startCamera() {
        if (!mCameraView.isStarted()) {
            mCameraView.startCamera();
        }
        
    }
    
    private void loadBatchImages() {
        // load batch images
        mCameraPresenter.getBatchPages();
    }
    
}
