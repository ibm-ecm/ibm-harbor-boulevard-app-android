package com.futureworkshops.android.harborboulevard.domain.dagger;

import com.futureworkshops.android.harborboulevard.presentation.accounts.create.FundWithCheckWizardActivity;
import com.futureworkshops.android.harborboulevard.presentation.accounts.create.wizard.step.camera.CameraStep;
import com.futureworkshops.android.harborboulevard.presentation.accounts.create.wizard.step.camera.dagger.CameraModule;
import com.futureworkshops.android.harborboulevard.presentation.accounts.create.wizard.step.complete.LastStep;
import com.futureworkshops.android.harborboulevard.presentation.accounts.create.wizard.step.complete.dagger.LastModule;
import com.futureworkshops.android.harborboulevard.presentation.accounts.create.wizard.step.process.ProcessingStep;
import com.futureworkshops.android.harborboulevard.presentation.accounts.create.wizard.step.process.dagger.ProcessingModule;
import com.futureworkshops.android.harborboulevard.presentation.splash.SplashscreenActivity;
import com.futureworkshops.android.harborboulevard.presentation.splash.dagger.SplashscreenModule;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityComponentBindModule {

    @ContributesAndroidInjector(modules = SplashscreenModule.class)
    public abstract SplashscreenActivity bindSplashActivity();

    @ContributesAndroidInjector
    public abstract FundWithCheckWizardActivity bindCWizardActivity();

    @ContributesAndroidInjector(modules = CameraModule.class)
    public abstract CameraStep bindCameraStep();

    @ContributesAndroidInjector(modules = ProcessingModule.class)
    public abstract ProcessingStep bindProcessingStep();

    @ContributesAndroidInjector(modules = LastModule.class)
    public abstract LastStep bindFinishedStep();
}
