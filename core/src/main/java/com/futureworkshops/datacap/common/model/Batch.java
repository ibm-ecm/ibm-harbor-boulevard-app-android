package com.futureworkshops.datacap.common.model;

import com.futureworkshops.datacap.common.utils.CastUtils;
import com.ibm.datacap.sdk.model.IBatch;
import com.ibm.datacap.sdk.model.IDocument;
import com.ibm.datacap.sdk.model.IField;
import com.ibm.datacap.sdk.model.IProperty;

import java.util.List;

/**
 * Created by stelian on 09/03/2017.
 */

public class Batch implements IBatch {

    private String id;
    private List<Property> properties;
    private List<Document> documents;
    private  List<Field> fields;

    private String localDir;

    @Override
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public List<IProperty> getProperties() {
        return CastUtils.castList(properties);
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    @Override
    public List<IDocument> getDocuments() {
        return CastUtils.castList(documents);
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    @Override
    public List<IField> getFields() {
        return CastUtils.castList(fields);
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public String getLocalDir() {
        return localDir;
    }

    public void setLocalDir(String localDir) {
        this.localDir = localDir;
    }
}
