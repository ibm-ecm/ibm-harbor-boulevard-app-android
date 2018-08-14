package com.futureworkshops.android.harborboulevard.presentation.accounts.create.wizard.step.camera;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.futureworkshops.android.harborboulevard.R;
import com.futureworkshops.android.harborboulevard.presentation.Constants;
import com.futureworkshops.android.harborboulevard.presentation.accounts.create.wizard.WizardProvider;
import com.futureworkshops.datacap.common.camera.CameraParams;
import com.futureworkshops.datacap.common.model.Batch;
import com.futureworkshops.datacap.common.model.Page;
import com.ibm.datacap.sdk.model.IPageType;
import com.ibm.datacap.sdk.ui.camera.AbstractCaptureView;
import com.ibm.datacap.sdk.ui.camera.DatacapCameraView;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;


/**
 * This step will configure the camera view to detect specific document types
 */
public class CameraStep extends Fragment implements Step, DatacapCameraView.DatacapDetectionListener,
        CameraContract.View {

    public static final String FORM = "Form";
    public static final String CHEQUE = "Cheque";
    public static final String DL_FRONT = "DL_Front";
    public static final String DL_BACK = "DL_Back";


    @StringDef({
            FORM, CHEQUE, DL_FRONT, DL_BACK
    })
    public @interface PageType {
    }

    private static final String TAG = CameraStep.class.getSimpleName();

    private static final int CAMERA_REQUEST_CODE = 3001;

    private DatacapCameraView mCameraView;
    private ImageView mManualCaptureButton;

    @PageType
    private String mPageType;
    private WizardProvider mWizardProvider;
    private Page mPage;

    @Inject
    CameraPresenter mCameraPresenter;

    public static CameraStep newInstance(@PageType String pageType) {
        Bundle arguments = new Bundle(1);
        arguments.putString(Constants.ARGS_PAGE_TYPE, pageType);

        CameraStep step = new CameraStep();
        step.setArguments(arguments);

        return step;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        AndroidSupportInjection.inject(this);
        mWizardProvider = (WizardProvider) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCameraView = view.findViewById(R.id.core_camera_view);
        mManualCaptureButton = view.findViewById(R.id.take_picture_btn);

        mPageType = getArguments().getString(Constants.ARGS_PAGE_TYPE);

        TextView description = view.findViewById(R.id.task_description);
        description.setText(getDescriptionForCurrentStep());

        configureManualCapture();

        configureCamera();

        mManualCaptureButton.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mManualCaptureButton.getViewTreeObserver().removeOnPreDrawListener(this);
                mCameraView.setScanningIndicatorCoordinates(0, mManualCaptureButton.getBottom());

                return true;
            }
        });

        startPermissionFlow();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopCamera();
    }

    @Nullable
    @Override
    public VerificationError verifyStep() {
        if (mPage == null) {
            return new VerificationError("waiting");
        }

        final boolean hasPageImage = !TextUtils.isEmpty(mPage.getImagePath());

        if (hasPageImage) {
            return null;
        } else {
            // restart camera
            startCamera();
            return new VerificationError("Please make sure you scanned the document");
        }
    }

    @Override
    public void onSelected() {
        mPage = getPage(mPageType);

        // configure camera view based on page type
        mCameraPresenter.setPageType(mPage);

        // we want to progress as soon as we have the image
        mWizardProvider.wizard().setNextButtonEnabled(false);
        startCamera();
    }

    @Override
    public void setPageType(IPageType pageType) {
        mCameraView.setPageType(pageType);
    }

    @Override
    public void onError(@NonNull VerificationError error) {
        Toast.makeText(getContext(), error.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDocumentProcessingStarted() {

    }

    @Override
    public void onDocumentDetected(DatacapCameraView.DatacapDetectionResult datacapDetectionResult) {
        // stop the camera
        stopCamera();

        // save the image in the batch folder and update page property
        final Batch batch = mWizardProvider.getBatchWrapper().getBatch();
        mCameraPresenter.saveFile(mPage, batch, datacapDetectionResult);
    }

    @Override
    public void proceed() {
        mWizardProvider.wizard().proceed();
    }

    private Page getPage(String pageType) {
        switch (pageType) {
            case FORM:
                return mWizardProvider.getBatchWrapper().getFormPage();
            case DL_FRONT:
                return mWizardProvider.getBatchWrapper().getDlFrontPage();
            case DL_BACK:
                return mWizardProvider.getBatchWrapper().getDlBackPage();
            case CHEQUE:
                return mWizardProvider.getBatchWrapper().getChequePage();
            default:
                return null;
        }
    }

    private void startCamera() {
        // start auto-detection once the camera permission is available
        if (hasCameraPermission() && !mCameraView.isStarted() && !mCameraView.isDetecting()) {
            mCameraView.startCamera();
            new Handler().postDelayed(() -> {
                if (!mCameraView.isDetecting()) {
                    mCameraView.startDetection();
                }
            }, 1000);
        }
    }

    private void stopCamera() {
        // stop auto-detection when the screen is no longer in focus
        if (mCameraView.isStarted()) {
            mCameraView.stopDetection();
            mCameraView.stopCamera();
        }
    }

    private String getDescriptionForCurrentStep() {
        switch (mPageType) {
            case FORM:
                return getString(R.string.label_please_scan_the_form);
            case DL_FRONT:
                return getString(R.string.label_please_scan_the_front);
            case DL_BACK:
                return getString(R.string.label_please_scan_the_back);
            case CHEQUE:
                return getString(R.string.label_please_scan_the_cheque);
            default:
                return null;
        }
    }

    private void configureManualCapture() {
        mManualCaptureButton.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);

            mCameraView.stopDetection();
            final Batch batch = mWizardProvider.getBatchWrapper().getBatch();
            mCameraPresenter.takePhoto(mCameraView.takePhoto(),mPage,batch);
        });
    }


    private void configureCamera() {
        mCameraView.setCameraFacing(AbstractCaptureView.FACING_BACK);
        mCameraView.setFlashMode(AbstractCaptureView.FLASH_OFF);
        mCameraView.setSmallestAreaPercentage(CameraParams.SMALLEST_AREA_PERCENTAGE);
        mCameraView.setMinimumDetectionMargin(CameraParams.MINIMUM_DETECTION_MARGIN);
        mCameraView.setDocumentAspectRatio(CameraParams.DOCUMENT_ASPECT_RATIO);
        mCameraView.setAspectRatioTolerance(1.0f);
        mCameraView.setDatacapDetectionListener(this);
    }

    private void startPermissionFlow() {
        if (!hasCameraPermission()) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.CAMERA)) {
                showPermissionRational();
            } else {
                // No explanation needed, we can request the permission.
                requestCameraPermission();
            }
        }
    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void showPermissionRational() {
        Snackbar snackbar = Snackbar.make(getView(), R.string.permission_camera_rationale,
                Snackbar.LENGTH_LONG);
        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                requestCameraPermission();
            }
        });

        snackbar.show();
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.CAMERA},
                CAMERA_REQUEST_CODE);
    }

}
