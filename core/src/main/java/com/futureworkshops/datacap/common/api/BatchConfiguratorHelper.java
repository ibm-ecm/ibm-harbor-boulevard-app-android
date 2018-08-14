package com.futureworkshops.datacap.common.api;

import android.support.annotation.NonNull;

import com.futureworkshops.datacap.common.model.Page;
import com.ibm.datacap.sdk.model.IBatchConfiguration;
import com.ibm.datacap.sdk.model.IBatchType;
import com.ibm.datacap.sdk.model.IDocumentType;
import com.ibm.datacap.sdk.model.IDocumentTypeReference;
import com.ibm.datacap.sdk.model.IFieldType;
import com.ibm.datacap.sdk.model.IFieldTypeReference;
import com.ibm.datacap.sdk.model.IPageType;
import com.ibm.datacap.sdk.model.IPageTypeReference;
import com.ibm.datacap.sdk.model.IProperty;
import com.ibm.datacap.sdk.model.IRuleSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stelian on 30/11/2016.
 */

public class BatchConfiguratorHelper {

    private static final String PROPERTY_LABEL = "label";

    private IBatchConfiguration mBatchConfiguration;

    public BatchConfiguratorHelper(@NonNull IBatchConfiguration batchConfiguration) {
        mBatchConfiguration = batchConfiguration;
    }

    public List<IDocumentTypeReference> getBatchDocumentTypeReferences() {
        return mBatchConfiguration.getBatchType().getDocumentTypeReferences();
    }

    public List<IDocumentType> getAllBatchDocuments() {
        return mBatchConfiguration.getDocumentTypes();
    }

    /**
     * Get a list of allowed document types for the current batch.
     */
    public List<IDocumentType> getAllowedDocumentTypes() {
        final List<IDocumentTypeReference> types =
                mBatchConfiguration.getBatchType().getDocumentTypeReferences();
        final List<IDocumentType> validDocuments = new ArrayList<>();

        // check all documents and see witch one is valid for the current batch
        for (IDocumentType document : mBatchConfiguration.getDocumentTypes()) {

            for (IDocumentTypeReference type : types) {
                if (type.getType().equalsIgnoreCase(document.getType())) {
                    validDocuments.add(document);
                    break;
                }
            }
        }

        return validDocuments;
    }

    /**
     * Get a list of valid page types for the given document type;
     */
    public List<IPageType> getPageTypesForDocument(IDocumentType documentType) {
        final List<IPageType> validPageTypes = new ArrayList<>();
        List<IPageType> pageTypes = mBatchConfiguration.getPageTypes();

        for (IPageTypeReference pageTypeReference : documentType.getPageTypeReferences()) {
            for (IPageType pageType : pageTypes) {
                if (pageTypeReference.getType().equals(pageType.getType())) {
                    validPageTypes.add(pageType);
                }
            }
        }

        return validPageTypes;
    }

    /**
     * Batch type is equivalent with the application name.
     */
    public IBatchType getBatchType() {
        return mBatchConfiguration.getBatchType();
    }

    /**
     * Get a list of {@link IFieldType}s specific for the given {@link IPageType}.
     */
    public List<IFieldType> getPageFields(@NonNull IPageType page) {
        List<IFieldType> allowedFields = new ArrayList<>();

        if (mBatchConfiguration != null) {
            final List<IFieldType> fields = mBatchConfiguration.getFieldTypes();
            final List<IFieldTypeReference> fieldTypes = page.getFieldTypeReferences();

            for (IFieldType field : fields) {
                final String fieldType = field.getType();

                // check that the field is of the type allowed by the page
                for (IFieldTypeReference type : fieldTypes) {
                    if (fieldType.equalsIgnoreCase(type.getType())) {
                        allowedFields.add(field);
                        break;
                    }
                }

            }
        }

        return allowedFields;
    }

    /**
     * Return an {@link IDocumentType} by the value of it's {@code label} property.
     */
    public IDocumentType getDocumentTypeByLabel(@NonNull String label) {
        IDocumentType documentType = null;
        for (IDocumentType allowedDocType : getAllowedDocumentTypes()) {
            for (IProperty iProperty : allowedDocType.getProperties()) {
                if (iProperty.getName().equals(PROPERTY_LABEL) && iProperty.getValue().equals(label)) {
                    documentType = allowedDocType;
                    break;
                }
            }

            if (documentType == null && allowedDocType.getType().equals(label)) {
                documentType = allowedDocType;
                break;
            }
        }
        return documentType;
    }

    /**
     * Get a list of {@link IRuleSet} for the batch configuration.
     *
     * @return
     */
    public List<IRuleSet> getBatchRuleSet() {
        return mBatchConfiguration.getRuleSets();
    }

    public List<IPageType> getAllPageTypes() {
        return mBatchConfiguration.getPageTypes();
    }

    /**
     * Return the {@link IPageType} used to create the given Page.
     *
     * @param page
     * @return
     */
    public IPageType getPageTypeForPage(Page page) {
        // the type property of IPageType is saved as a page property
        final String pageType = page.getType();

        for (IPageType iPageType : mBatchConfiguration.getPageTypes()) {
            if (iPageType.getType().equalsIgnoreCase(pageType)) {
                return iPageType;
            }
        }

        return null;
    }
}
