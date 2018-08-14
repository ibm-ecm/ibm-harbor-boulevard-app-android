package com.futureworkshops.android.harborboulevard.presentation.accounts.create.wizard.step.complete.dagger;

import com.futureworkshops.android.harborboulevard.presentation.accounts.create.wizard.step.complete.LastContract;
import com.futureworkshops.android.harborboulevard.presentation.accounts.create.wizard.step.complete.LastStep;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class LastModule {

    @Binds
    abstract LastContract.View bindView(LastStep lastStep);
}
