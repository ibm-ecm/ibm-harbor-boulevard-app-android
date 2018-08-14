package com.futureworkshops.datacap.common.model;

import android.support.annotation.NonNull;

import com.ibm.datacap.sdk.model.IFieldChar;

/**
 * Created by stelian on 09/03/2017.
 */

public class FieldChar implements IFieldChar {

    String confidence;
    String boundaries;
    String value;

    public FieldChar(@NonNull String value) {
        this.value = value;
    }


    @Override
    public String getConfidence() {
        return confidence;
    }

    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

    @Override
    public String getBoundaries() {
        return boundaries;
    }

    public void setBoundaries(String boundaries) {
        this.boundaries = boundaries;
    }

    @Override
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
