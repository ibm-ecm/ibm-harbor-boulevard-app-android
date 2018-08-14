package com.futureworkshops.android.harborboulevard.presentation.accounts.create.wizard;

import com.futureworkshops.android.harborboulevard.model.BatchWrapper;
import com.stepstone.stepper.StepperLayout;

public interface WizardProvider {

    StepperLayout wizard();

    BatchWrapper getBatchWrapper();

    void refreshStepValidation();
}
