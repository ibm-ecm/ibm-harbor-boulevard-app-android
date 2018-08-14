package com.futureworkshops.android.harborboulevard.presentation.accounts.create.wizard.step.process;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.futureworkshops.android.harborboulevard.R;
import com.futureworkshops.android.harborboulevard.presentation.Constants;
import com.futureworkshops.android.harborboulevard.presentation.accounts.create.adapter.PageFieldsAdapter;
import com.futureworkshops.android.harborboulevard.presentation.accounts.create.wizard.WizardProvider;
import com.futureworkshops.android.harborboulevard.presentation.accounts.create.wizard.step.camera.CameraStep;
import com.futureworkshops.datacap.common.ScaleTransformation;
import com.futureworkshops.datacap.common.model.Field;
import com.futureworkshops.datacap.common.model.Page;
import com.ibm.datacap.sdk.id.model.IdField;
import com.ibm.datacap.sdk.model.IField;
import com.squareup.picasso.Picasso;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

import static com.futureworkshops.android.harborboulevard.presentation.accounts.create.wizard.step.camera.CameraStep.CHEQUE;
import static com.futureworkshops.android.harborboulevard.presentation.accounts.create.wizard.step.camera.CameraStep.DL_BACK;
import static com.futureworkshops.android.harborboulevard.presentation.accounts.create.wizard.step.camera.CameraStep.DL_FRONT;
import static com.futureworkshops.android.harborboulevard.presentation.accounts.create.wizard.step.camera.CameraStep.FORM;


public class ProcessingStep extends Fragment implements BlockingStep, ProcessingContract.View {

    private RecyclerView mRecyclerView;
    private WizardProvider mWizardProvider;

    @CameraStep.PageType
    private String mPageType;
    private Page mPage;

    private PageFieldsAdapter mAdapter;

    @Inject
    ProcessingPresenter mProcessingPresenter;

    private boolean mProcessed = false;

    public static ProcessingStep newInstance(@CameraStep.PageType String pageType) {
        Bundle arguments = new Bundle(1);
        arguments.putString(Constants.ARGS_PAGE_TYPE, pageType);

        ProcessingStep processingStep = new ProcessingStep();
        processingStep.setArguments(arguments);

        return processingStep;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        AndroidSupportInjection.inject(this);
        mWizardProvider = (WizardProvider) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageType = getArguments().getString(Constants.ARGS_PAGE_TYPE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scanned_content, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new PageFieldsAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    @Nullable
    @Override
    public VerificationError verifyStep() {
        return mProcessed ? null : new VerificationError("waiting to process");
    }

    // this method is called every time one of these steps is selected
    @Override
    public void onSelected() {
        mPage = getPage(mPageType);
        mProcessed = false;

        // sort out the page fields
        final List<Field> fields = mPage.getActualFields();
        if (fields == null || fields.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mAdapter.setFields(fields);
        }

        showImage();
        extractDocumentInformation();
    }

    @Override
    public void onError(@NonNull VerificationError error) {
        Toast.makeText(getContext(), error.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNextClicked(final StepperLayout.OnNextClickedCallback callback) {
        callback.goToNextStep();
    }

    @Override
    public void onCompleteClicked(final StepperLayout.OnCompleteClickedCallback callback) {
        callback.complete();
    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
        callback.goToPrevStep();
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

    private void showImage() {
        ImageView previewImageView = getView().findViewById(R.id.image_preview);

        final float targetHeight = getContext().getResources().getDimension(R.dimen.fragment_processing_step_image_height);

        if (mPage.getImagePath() != null) {
            File file = new File(mPage.getImagePath());
            if (file.exists()) {
                Picasso.with(getContext())
                        .load(file)
                        .transform(ScaleTransformation.getScaleHeightTransformation(targetHeight))
                        .placeholder(R.drawable.picasso_placeholder)
                        .into(previewImageView);
            }
        }

    }

    private void extractDocumentInformation() {
        // after displaying the image we pass it to the appropriate processor to extract data from it
        switch (mPageType) {
            case FORM:
                extractTextFromForm();
                break;
            case DL_FRONT:
                //  we know there is no information so proceed automatically
                mProcessed = true;
                mWizardProvider.refreshStepValidation();
                break;
            case DL_BACK:
                extractTextFromDLBack();
                break;
            case CHEQUE:
                extractChequeInformation();
                break;
        }
    }

    private void extractTextFromForm() {
        mWizardProvider.wizard().showProgress("Extracting information");
        mProcessingPresenter.extractTextFromForm(mPage);
    }

    @Override
    public void onTextExtracted(List<IField> iFields) {
        mAdapter.updateItems(iFields);
        hideProgress();
    }


    @Override
    public void errorExtractingText(Throwable e) {
        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        hideProgress();
    }

    private void extractTextFromDLBack() {
        mWizardProvider.wizard().showProgress("Extracting information");
        mProcessingPresenter.extractTextFromDLBack(mPage);
    }

    @Override
    public void onIdTextExtracted(HashMap<String, IdField> fields) {
        mAdapter.updateIdItems(fields);
        hideProgress();
    }

    private void extractChequeInformation() {
        mWizardProvider.wizard().showProgress("Extracting information");
        mProcessingPresenter.extractTextChequeInformation(mPage);
    }

    /**
     * Called when processing is completed.
     */
    private void hideProgress() {
        mProcessed = true;
        mWizardProvider.wizard().hideProgress();
        mWizardProvider.refreshStepValidation();
    }
}
