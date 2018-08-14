package com.futureworkshops.datacap.common.model;

import android.support.annotation.NonNull;

import com.ibm.datacap.sdk.model.IProperty;

/**
 * Created by stelian on 03/03/2017.
 */

public class Property implements IProperty {

    private String name;
    private String value;

    public Property(@NonNull String name, @NonNull String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
