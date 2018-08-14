package com.futureworkshops.android.harborboulevard.presentation.accounts.create.wizard.step.complete;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.futureworkshops.android.harborboulevard.R;
import com.futureworkshops.android.harborboulevard.presentation.accounts.create.wizard.WizardProvider;
import com.futureworkshops.datacap.common.model.Batch;
import com.ibm.datacap.sdk.api.DatacapApi;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;


public class LastStep extends Fragment implements BlockingStep, LastContract.View {

    /**
     * Allows communication with Datacap server.
     */
    @Inject
    DatacapApi datacapApi;

    private WizardProvider mWizardProvider;

    private boolean mUploaded = false;

    public static LastStep newInstance() {
        return new LastStep();
    }

    @Inject
    LastPresenter mLastPresenter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        AndroidSupportInjection.inject(this);
        mWizardProvider = (WizardProvider) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_finished, container, false);
    }

    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {
        callback.goToNextStep();
    }

    @Override
    public void onCompleteClicked(final StepperLayout.OnCompleteClickedCallback callback) {
        callback.complete();
    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {

    }

    @Nullable
    @Override
    public VerificationError verifyStep() {
        return mUploaded ? null : new VerificationError("waiting to upload");
    }

    @Override
    public void onSelected() {
        // upload the batch
        mWizardProvider.wizard().showProgress("Uploading documents");
        final Batch batch = mWizardProvider.getBatchWrapper().getBatch();
        mLastPresenter.uploadBatch(batch);
    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }

    @Override
    public void onUploadSuccess() {
        mUploaded = true;
        mWizardProvider.wizard().hideProgress();
        mWizardProvider.wizard().setNextButtonEnabled(true);
        mWizardProvider.wizard().setCompleteButtonEnabled(true);
        Log.e("Error", "Complete");
    }

    @Override
    public void onUploadError(Throwable throwable) {
        mUploaded = false;
        mWizardProvider.wizard().hideProgress();
        Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
        Log.e("Error", "Error uploading batch", throwable);
    }
}
