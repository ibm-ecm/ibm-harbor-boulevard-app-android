package com.futureworkshops.datacap.common.camera;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.futureworkshops.core.R;
import com.futureworkshops.datacap.common.utils.SnackUtils;

/**
 * This base class is used to handle the permission required for working with the camera.
 */
public abstract class BaseCameraActivity extends AppCompatActivity {

    protected static final int RC_HANDLE_CAMERA_PERM = 2;
    protected static final int RC_HANDLE_STORAGE_PERM = 3;

    private static final String TAG = BaseCameraActivity.class.getSimpleName();

    protected abstract void handleCameraPermissionGranted();

    protected abstract void handleStoragePermissionGranted();

    /**
     * Call this method to check that the activity has camera permissions.
     */
    protected void checkCameraPermissions() {
        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            handleCameraPermissionGranted();
        } else {
            requestCameraPermission();
        }
    }

    protected void checkStoragePermissions() {
        // Check for the read external storage permission before trying to import files.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            handleStoragePermissionGranted();
        } else {
            requestStoragePermission();
        }
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // handle only the permissions we request
        if (requestCode != RC_HANDLE_CAMERA_PERM && requestCode != RC_HANDLE_STORAGE_PERM) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        final boolean permissionGranted = grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;

        if (requestCode == RC_HANDLE_CAMERA_PERM) {
            if (permissionGranted) {
                // we have permission, so create the camera source
                handleCameraPermissionGranted();
            } else {
                handleCameraPermissionNotGranted(grantResults);
            }
        } else {
            // this is the storage permission
            if (permissionGranted) {
                // we have permission, so create the camerasource
                handleStoragePermissionGranted();
            } else {
                handleStoragePermissionNotGranted(grantResults);
            }
        }


    }

    private void handleCameraPermissionNotGranted(@NonNull int[] grantResults) {
        Log.d(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.action_ok, listener)
                .show();
    }

    private void handleStoragePermissionNotGranted(@NonNull int[] grantResults) {
        Log.d(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning")
                .setMessage(R.string.no_storage_permission)
                .setPositiveButton(R.string.action_ok, listener)
                .show();
    }


    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions, RC_HANDLE_CAMERA_PERM);
            }
        };

        SnackUtils.showSnackbarWithAction(this,
                getString(R.string.permission_camera_rationale),
                listener);
    }

    /**
     * Handles the requesting of the read external storage permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestStoragePermission() {
        Log.w(TAG, "Storage permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_STORAGE_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions, RC_HANDLE_STORAGE_PERM);
            }
        };

        SnackUtils.showSnackbarWithAction(this,
                getString(R.string.permission_storage_rationale),
                listener);

    }

}
