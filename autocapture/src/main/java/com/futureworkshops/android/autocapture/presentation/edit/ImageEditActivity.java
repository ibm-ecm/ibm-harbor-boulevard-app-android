package com.futureworkshops.android.autocapture.presentation.edit;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.futureworkshops.android.autocapture.R;
import com.futureworkshops.datacap.common.model.Page;
import com.futureworkshops.datacap.common.utils.SnackUtils;
import com.ibm.datacap.sdk.ui.image.DocumentCornerPickerView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.AndroidInjection;

public class ImageEditActivity extends AppCompatActivity implements ImageEditContract.View {

    public static final String PAGE_ID = "pageID";

    @Inject
    ImageEditContract.Presenter mImageEditPresenter;

    @BindView(R.id.datacap_corner_picker)
    DocumentCornerPickerView mDocumentCornerPickerView;

    @BindView(R.id.bitmap_preview)
    ImageView mCorrectedImgView;

    @BindView(R.id.apply_btn)
    Button mApplyButton;

    @BindView(R.id.deskew_btn)
    Button mDeskewBtn;

    private String mPageId;
    private Bitmap mDeskewedBitmap;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_edit);

        ButterKnife.bind(this);

        mPageId = getIntent().getStringExtra(PAGE_ID);

        // disable apply btn until a deskew actually happens
        mApplyButton.setEnabled(false);
        mApplyButton.setTextColor(Color.DKGRAY);
    }

    @Override
    protected void onStart() {
        super.onStart();

        loadImage();
    }

    @OnClick(R.id.cancel_btn)
    void onCancelClicked(View v) {
        v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);

        finishWithCancel();
    }

    @OnClick(R.id.apply_btn)
    void onApplyClicked(View v) {
        v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);

        mImageEditPresenter.saveDeskewResults(mDeskewedBitmap);


    }

    @OnClick(R.id.deskew_btn)
    void onDeskewClicked(View v) {
        v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);

        mImageEditPresenter.applyPerspectiveCorrection(mDocumentCornerPickerView.getSelectedCorners());
    }

    @Override
    public void onPageLoaded(@NonNull Page page) {
        mDocumentCornerPickerView.setImagePath(page.getOriginalImagePath());
        updateDocumentPickerCorners(page);
    }

    @Override
    public void onPageNotFound() {
        showPageNotFoundDialog();
    }

    @Override
    public void onPageCornersLoaded(final Point[] corners) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mDocumentCornerPickerView.setInitialCornerPosition(corners);
                mDocumentCornerPickerView.invalidate();
            }
        }, 150);
    }

    @Override
    public void onImageDeskewed(Bitmap bitmap) {
        mDeskewedBitmap = bitmap;

        // hide deskew button and view
        mDocumentCornerPickerView.setVisibility(View.GONE);
        mDeskewBtn.setVisibility(View.GONE);

        // show deskew result
        mCorrectedImgView.setVisibility(View.VISIBLE);
        mCorrectedImgView.setImageBitmap(mDeskewedBitmap);

        // enable apply button
        mApplyButton.setEnabled(true);
        mApplyButton.setTextColor(getPrimaryColor());

    }

    @Override
    public void onImageDeskewFailed(String message) {
        SnackUtils.showSimpleSnackbar(this, message);
    }

    @Override
    public void onPageSaved() {
        // set the result and send back the page id
        final Intent intent = new Intent();
        intent.putExtra(PAGE_ID, mPageId);

        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onPageSaveFailed(String message) {
        showpageSaveFailedDialog(message);

//        // hide deskew result
//        mCorrectedImgView.setVisibility(View.GONE);
//
//        // show deskew button and view
//        mDocumentCornerPickerView.setVisibility(View.VISIBLE);
//        mDeskewBtn.setVisibility(View.VISIBLE);
//
//        // disable apply button
//        mApplyButton.setEnabled(false);
    }

    private void loadImage() {
        if (TextUtils.isEmpty(mPageId)) {
            showMissingPageIdDialog();
        } else {
            mImageEditPresenter.loadPage(mPageId);
        }

    }

    private void showMissingPageIdDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("Missing page with ID ")
                .setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishWithCancel();
                    }
                })
                .show();

    }

    private void showPageNotFoundDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("Could not find page with ID " + mPageId)
                .setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishWithCancel();
                    }
                })
                .show();
    }


    private void showpageSaveFailedDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("Could not save deskewed image \\n" + message)
                .setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishWithCancel();
                    }
                })
                .show();
    }

    private void finishWithCancel() {
        setResult(RESULT_CANCELED);
        finish();
    }

    private Point[] getDefaultCorners() {
        Point[] points = new Point[4];

        points[0] = new Point(200, 200);
        points[1] = new Point(800, 200);
        points[2] = new Point(800, 800);
        points[3] = new Point(200, 800);

        return points;
    }


    private void updateDocumentPickerCorners(Page page) {
        Point[] documentCorners = page.getDocumentCorners();

        if (documentCorners.length == 0) {
            documentCorners = getDefaultCorners();
        }

        mDocumentCornerPickerView.setInitialCornerPosition(documentCorners);
    }

    private int getPrimaryColor() {
        TypedValue typedValue = new TypedValue();

        TypedArray a = obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorPrimary});
        int color = a.getColor(0, 0);

        a.recycle();

        return color;
    }

}
