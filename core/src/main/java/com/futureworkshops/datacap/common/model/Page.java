package com.futureworkshops.datacap.common.model;

import android.graphics.Point;

import com.futureworkshops.datacap.common.utils.CastUtils;
import com.ibm.datacap.sdk.model.IField;
import com.ibm.datacap.sdk.model.IPage;
import com.ibm.datacap.sdk.model.IProperty;

import java.util.List;

/**
 * Created by stelian on 02/03/2017.
 */

public class Page implements IPage {
    private static final String PROPERTY_IMAGE_FILE = "IMAGEFILE";
    private static final String PROPERTY_TYPE = "TYPE";
    private static final String PROPERTY_LABEL = "label";

    private String id;
    private List<Property> properties;
    private List<Field> fields;

    private String originalImagePath;
    private Point[] documentCorners;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public List<IProperty> getProperties() {
        return CastUtils.castList(properties);
    }

    @Override
    public List<IField> getFields() {
        return CastUtils.castList(fields);
    }

    public List<Field> getActualFields() {
        return fields;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    /**
     * @return the path of the deskewed and cropped image.
     */
    public String getImagePath() {
        for (Property property : properties) {
            if (property.getName().equalsIgnoreCase(PROPERTY_IMAGE_FILE)) {
                return property.getValue();
            }
        }

        return "";
    }

    /**
     * Return the label for the current page or {@link #getType()} if property does not exist.
     */
    public String getLabel() {
            for (Property property : properties) {
                if (property.getName().equalsIgnoreCase(PROPERTY_LABEL)) {
                    return property.getValue();
                }
            }

            return getType();
    }

    /**
     * @return the path of the deskewed and cropped image.
     */
    public String getType() {
        for (Property property : properties) {
            if (property.getName().equalsIgnoreCase(PROPERTY_TYPE)) {
                return property.getValue();
            }
        }

        return "";
    }

    /**
     * Set the path of the deskewed and cropped image.
     */
    public void setImageFilePath(String path) {
        for (Property property : properties) {
            if (property.getName().equals(PROPERTY_IMAGE_FILE)) {
                property.setValue(path);
            }
        }
    }

    /**
     * Get the path of the original(full & unprocessed) image.
     *
     * @return
     */
    public String getOriginalImagePath() {
        return originalImagePath;
    }

    /**
     * Set the path of the original(full & unprocessed) image.
     *
     * @return
     */
    public void setOriginalImagePath(String originalImagePath) {
        this.originalImagePath = originalImagePath;
    }

    public Point[] getDocumentCorners() {
        return documentCorners;
    }

    public void setDocumentCorners(Point[] documentCorners) {
        this.documentCorners = documentCorners;
    }

}
