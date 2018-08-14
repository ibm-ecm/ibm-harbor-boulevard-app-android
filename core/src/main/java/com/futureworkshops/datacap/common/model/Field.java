package com.futureworkshops.datacap.common.model;

import com.futureworkshops.datacap.common.utils.CastUtils;
import com.ibm.datacap.sdk.model.IField;
import com.ibm.datacap.sdk.model.IFieldChar;
import com.ibm.datacap.sdk.model.IProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stelian on 03/03/2017.
 */

public class Field implements IField {

    public static final String PROPERTY_TYPE = "TYPE";
    public static final String PROPERTY_STATUS = "STATUS";
    public static final String PROPERTY_IMAGE_FILE = "IMAGEFILE";
    public static final String PROPERTY_LABEL = "label";

    private String id;
    private String value;
    private List<Property> properties;
    private List<FieldChar> fieldChars;

    public Field() {
        properties = new ArrayList<>();
        fieldChars = new ArrayList<>();
    }

    public Field(List<IProperty> properties, String id, String value) {
        this.id = id;
        this.value = value;
        this.properties = new ArrayList<>();

        for (IProperty property : properties) {
            this.properties.add((Property) property);
        }
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public List<IProperty> getProperties() {
        return CastUtils.castList(properties);
    }

    @Override
    public List<IFieldChar> getCharacters() {
        return CastUtils.castList(fieldChars);
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setProperties(List<Property> properties) {
        this.properties.clear();
        this.properties.addAll(properties);
    }

    public void setFieldChars(List<FieldChar> fieldChars) {
        this.fieldChars.clear();
        this.fieldChars.addAll(fieldChars);
    }

    public String getLabel() {
        String label = "";
        for (IProperty iProperty : properties) {
            if (iProperty.getName().equalsIgnoreCase(PROPERTY_LABEL)) {
                label = iProperty.getValue();
                break;
            }
        }

        return label;
    }

    public String getType() {
        String label = "";
        for (IProperty iProperty : properties) {
            if (iProperty.getName().equalsIgnoreCase(PROPERTY_TYPE)) {
                label = iProperty.getValue();
                break;
            }
        }

        return label;
    }
}
