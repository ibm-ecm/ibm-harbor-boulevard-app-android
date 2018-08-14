package com.futureworkshops.android.autocapture.domain.dagger;

import com.futureworkshops.android.autocapture.presentation.batch.dagger.BatchModule;
import com.futureworkshops.android.autocapture.presentation.batch.view.BatchActivity;
import com.futureworkshops.android.autocapture.presentation.camera.dagger.CameraModule;
import com.futureworkshops.android.autocapture.presentation.camera.view.CameraActivity;
import com.futureworkshops.android.autocapture.presentation.edit.ImageEditActivity;
import com.futureworkshops.android.autocapture.presentation.edit.dagger.ImageEditModule;
import com.futureworkshops.android.autocapture.presentation.splash.SplashscreenActivity;
import com.futureworkshops.android.autocapture.presentation.splash.dagger.SplashscreenModule;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * This Module's purpose, is to define which Activity depends on which Module.
 * By providing this Module to our {@link AppComponent}
 * we are allowing Dagger to generate SubComponents and inject our activities.
 * The benefit of this approach, is we don't have to define Dagger Components for our Modules, with
 * the exception of our top-level AppComponent.
 * Subcomponents are components that live below the AppComponent in our graph.
 */
@Module
public abstract class ActivityComponentBindModule {


    @ContributesAndroidInjector(modules = {SplashscreenModule.class})
    public abstract SplashscreenActivity bindSplashscreenActivity();

    @ContributesAndroidInjector(modules = {CameraModule.class})
    public abstract CameraActivity bindCameraActivity();

    @ContributesAndroidInjector(modules = {BatchModule.class})
    public abstract BatchActivity bindBatchActivity();

    @ContributesAndroidInjector(modules = {ImageEditModule.class})
    public abstract ImageEditActivity bindImageEditActivity();
}
