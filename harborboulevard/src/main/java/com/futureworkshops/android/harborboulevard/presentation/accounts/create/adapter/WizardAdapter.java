package com.futureworkshops.android.harborboulevard.presentation.accounts.create.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

import com.futureworkshops.android.harborboulevard.presentation.accounts.create.wizard.step.camera.CameraStep;
import com.futureworkshops.android.harborboulevard.presentation.accounts.create.wizard.step.complete.LastStep;
import com.futureworkshops.android.harborboulevard.presentation.accounts.create.wizard.step.process.ProcessingStep;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;

/**
 * Displays the steps required for setting up and account:
 * 1. Capture the form
 * 2. Show extracted content
 * 3. Capture the front of a driver's license
 * 4. Shows previous picture
 * 5. Capture the back of a driver's license
 * 6. Show extracted content
 * 7. Capture a cheque
 * 8. Show extracted content
 * 9. Shows this step if all the above were completed successfully
 */
public class WizardAdapter extends AbstractFragmentStepAdapter {

    public WizardAdapter(@NonNull FragmentManager fragmentManager,
                         @NonNull Context context) {
        super(fragmentManager, context);
    }

    @Override
    public int getCount() {
        return 9;
    }

    @Override
    public Step createStep(int position) {
        switch (position) {
            case 0:
                return CameraStep.newInstance(CameraStep.FORM);
            case 1:
                return ProcessingStep.newInstance(CameraStep.FORM);
            case 2:
                return CameraStep.newInstance(CameraStep.DL_FRONT);
            case 3:
                return ProcessingStep.newInstance(CameraStep.DL_FRONT);
            case 4:
                return CameraStep.newInstance(CameraStep.DL_BACK);
            case 5:
                return ProcessingStep.newInstance(CameraStep.DL_BACK);
            case 6:
                return CameraStep.newInstance(CameraStep.CHEQUE);
            case 7:
                return ProcessingStep.newInstance(CameraStep.CHEQUE);
            case 8:
                return LastStep.newInstance();
            default:
                return null;
        }
    }
}
