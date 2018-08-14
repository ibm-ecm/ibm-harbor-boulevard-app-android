package com.futureworkshops.android.autocapture.presentation.camera;

import android.support.annotation.NonNull;
import android.util.Pair;

import com.futureworkshops.datacap.common.model.Page;
import com.ibm.capture.sdk.ui.camera2.CameraView;
import com.ibm.datacap.sdk.ui.camera.DatacapCameraView;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by stelian on 24/10/2017.
 */

public class CameraContract {

    public interface View {

        void onCameraViewConfigured();

        void setFlashAuto();

        void setFlashOn();

        void setFlashOff();

        void setCameraFacing(@DatacapCameraView.FacingMode int cameraFacing);

        void setSmallestAreaPercentage(float smallestAreaPercentage);

        void setMinimumDetectionMargin(int minimumDetectionMargin);

        void setDocumentAspectRatio(float documentAspectRatio);

        void stopDetection();

        void startDetection();

        void setDocumentListener(DatacapCameraView.DatacapDetectionListener datacapDetectionListener);

        void setCameraStateCallback(CameraView.CameraStateCallback cameraStateCallback);

        void onShowBatchActivityRequested();

        void launchImageImport();

        void onPagesLoaded(List<Page> pages);

        void onCameraOpened();

        void onCameraClosed();

        void addLoadingItem(@NonNull String pageId);

        void onPageSaveFailed(@NonNull String pageId, String message);

        void onPageSaved(@NonNull Page page);
    }

    public interface Presenter {
        void configureCameraView();

        void takePhoto(Single<Pair<byte[], Integer>> pictureSingle);

        void showBatchActivity();

        void startImageImport();

        void getBatchPages();

        void onFlashClicked(@DatacapCameraView.FlashMode int state);

        void importImage(@NonNull String selectedImagePath);
    }
}
