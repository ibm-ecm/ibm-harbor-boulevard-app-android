package com.futureworkshops.datacap.common.model;

import com.futureworkshops.datacap.common.utils.CastUtils;
import com.ibm.datacap.sdk.model.IDocument;
import com.ibm.datacap.sdk.model.IPage;
import com.ibm.datacap.sdk.model.IProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stelian on 09/03/2017.
 */

public class Document implements IDocument {
    private static final String PROPERTY_TYPE = "TYPE";

    private String id;
    private List<Property> properties;
    private List<Page> pages;


    @Override
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public List<IProperty> getProperties() {
        return new ArrayList<>(properties);
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    @Override
    public List<IPage> getPages() {
        return CastUtils.castList(pages);
    }

    public void setPages(List<Page> pages) {
        this.pages = pages;
    }

    public String getType() {
        for (Property property : properties) {
            if (property.getName().equalsIgnoreCase(PROPERTY_TYPE)) {
                return property.getValue();
            }
        }

        return "";
    }

}
