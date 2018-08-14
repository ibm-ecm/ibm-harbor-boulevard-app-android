package com.futureworkshops.android.harborboulevard.presentation.accounts.create.wizard.step.camera.dagger;

import com.futureworkshops.android.harborboulevard.presentation.accounts.create.wizard.step.camera.CameraContract;
import com.futureworkshops.android.harborboulevard.presentation.accounts.create.wizard.step.camera.CameraStep;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class CameraModule {

    @Binds
    abstract CameraContract.View bindView(CameraStep cameraStep);
}
