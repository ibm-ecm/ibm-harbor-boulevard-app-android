package com.futureworkshops.android.harborboulevard.presentation.accounts.create;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dd.CircularProgressButton;
import com.futureworkshops.android.harborboulevard.R;
import com.futureworkshops.android.harborboulevard.model.BatchWrapper;
import com.futureworkshops.android.harborboulevard.presentation.accounts.create.adapter.WizardAdapter;
import com.futureworkshops.android.harborboulevard.presentation.accounts.create.wizard.WizardProvider;
import com.futureworkshops.datacap.common.ScaleTransformation;
import com.futureworkshops.datacap.common.dagger.BatchDaggerHelper;
import com.squareup.picasso.Picasso;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

import static com.dd.CircularProgressButton.IDLE_STATE_PROGRESS;
import static com.dd.CircularProgressButton.INDETERMINATE_STATE_PROGRESS;

/**
 * This activity will guide the user into opening an account that requires information from:
 * <ul><li>a standardized form</li>
 * <li> Driver License front</li>
 * <li>Driver License back</li>
 * <li>a cheque</li>
 * </ul>
 * <p>
 * <p>The activity is structured as a step by step mWizard where the user captures the image then
 * moves forward to a step that will extract information from the image until all documents are
 * provided.</p>
 */
public class FundWithCheckWizardActivity extends AppCompatActivity implements WizardProvider, StepperLayout.StepperListener {

    @Inject
    BatchDaggerHelper mBatchDaggerHelper;

    /**
     * A wrapper around {@link com.futureworkshops.datacap.common.model.Batch} that provides helper
     * method to get required page types.
     */
    private BatchWrapper mBatchWrapper;

    private StepperLayout mStepperLayout;
    private LinearLayout mRequirementslayout;
    private CircularProgressButton mStartBtn;

    private ImageView mBanner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fund_with_check_wizard);

        mBanner = findViewById(R.id.banner);
        mRequirementslayout = findViewById(R.id.requirements_layout);

        mStepperLayout = findViewById(R.id.wizard);
        mStepperLayout.setListener(this);
        mStepperLayout.setOffscreenPageLimit(1);

        mBatchWrapper = new BatchWrapper(this, mBatchDaggerHelper.getBatchConfiguratorHelper());

        mStartBtn = findViewById(R.id.start_btn);
        mStartBtn.setIndeterminateProgressMode(true);
        mStartBtn.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            toggleLoading(true);
            v.postDelayed(this::createStepperAdapter, 500);
        });
    }

    private void toggleLoading(boolean isLoading) {
        mStartBtn.setProgress(isLoading ? INDETERMINATE_STATE_PROGRESS : IDLE_STATE_PROGRESS);
    }

    @Override
    protected void onStart() {
        super.onStart();

        int pixelSize = (int) (getResources().getDisplayMetrics().density * 300);

        Picasso.with(this)
                .load(R.drawable.third)
                .transform(ScaleTransformation.getScaleHeightTransformation(pixelSize))
                .placeholder(R.drawable.picasso_placeholder)
                .into(mBanner);
    }

    @Override
    public StepperLayout wizard() {
        return mStepperLayout;
    }

    @Override
    public BatchWrapper getBatchWrapper() {
        return mBatchWrapper;
    }

    @Override
    public void refreshStepValidation() {
        final int currentStepPosition = mStepperLayout.getCurrentStepPosition();
        final VerificationError verificationError = mStepperLayout.getAdapter().findStep(currentStepPosition).verifyStep();
        boolean enabled = verificationError == null;

        mStepperLayout.setNextButtonVerificationFailed(!enabled);
        mStepperLayout.setNextButtonEnabled(enabled);
        mStepperLayout.setCompleteButtonEnabled(enabled);
    }

    @Override
    public void onCompleted(View completeButton) {
        finish();
    }

    @Override
    public void onError(VerificationError verificationError) {

    }

    @Override
    public void onStepSelected(int newStepPosition) {

        // we want to disable the next button until the document is processed
        mStepperLayout.setNextButtonVerificationFailed(true);
        mStepperLayout.setNextButtonEnabled(false);
        mStepperLayout.setCompleteButtonEnabled(false);
    }

    @Override
    public void onReturn() {

    }

    /**
     * Create the adapter in a background thread because the fragments require some initialisation
     * that will otherwise affect the UI thread.
     */
    private void createStepperAdapter() {
        WizardAdapter adapter = new WizardAdapter(getSupportFragmentManager(), FundWithCheckWizardActivity.this);
        mStepperLayout.setAdapter(adapter);
        toggleLoading(false);

        mRequirementslayout.setVisibility(View.INVISIBLE);
        mStepperLayout.setVisibility(View.VISIBLE);
    }
}
