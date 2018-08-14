package com.futureworkshops.android.harborboulevard.presentation.accounts.create.wizard.step.process.dagger;

import com.futureworkshops.android.harborboulevard.presentation.accounts.create.wizard.step.process.ProcessingContract;
import com.futureworkshops.android.harborboulevard.presentation.accounts.create.wizard.step.process.ProcessingStep;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class ProcessingModule {

    @Binds
    abstract ProcessingContract.View bindsView(ProcessingStep processingStep);
}
