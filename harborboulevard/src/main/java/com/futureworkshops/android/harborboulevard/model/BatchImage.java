package com.futureworkshops.android.harborboulevard.model;

import com.futureworkshops.android.harborboulevard.presentation.accounts.create.wizard.step.camera.CameraStep;

import java.io.File;

public class BatchImage {

    @CameraStep.PageType
    private final String type;

    private final File imageFile;

    public BatchImage(String type, File imageFile) {
        this.type = type;
        this.imageFile = imageFile;
    }

    public String getType() {
        return type;
    }

    public File getImageFile() {
        return imageFile;
    }

    public String getPath() {
        return imageFile.getPath();
    }
}
