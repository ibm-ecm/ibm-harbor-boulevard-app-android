package com.futureworkshops.android.harborboulevard.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.futureworkshops.datacap.common.api.BatchConfiguratorHelper;
import com.futureworkshops.datacap.common.api.BatchFactory;
import com.futureworkshops.datacap.common.model.Batch;
import com.futureworkshops.datacap.common.model.Page;
import com.ibm.datacap.sdk.model.IDocument;
import com.ibm.datacap.sdk.model.IPage;
import com.ibm.datacap.sdk.model.IProperty;

import java.util.List;

/**
 * This class provides helper methods to retrieve the required page types for the wizard.
 * <p>
 * Created by stelian on 05/10/2017.
 */

public class BatchWrapper {
    private static final String PROPERTY_TYPE = "TYPE";
    private static final String PROPERTY_PROCESS_CHEQUES = "ProcessChecks";

    private Batch mBatch;


    /**
     * Create a new batch.
     *
     * @param context
     */
    public BatchWrapper(@NonNull Context context, BatchConfiguratorHelper batchConfiguratorHelper) {
        mBatch = BatchFactory.createHarborBoulevardBatch(context, batchConfiguratorHelper);
    }

    /**
     * Get the batch.
     *
     * @return
     */
    public Batch getBatch() {
        return mBatch;
    }

    /**
     * Get the page associated with a form document.
     * <p/>
     * For this sample , we know that the {@code type} of the page will be <strong>Form</strong>.
     *
     * @return
     */
    public Page getFormPage() {
        // go through all documents and search for a page with type=="Form"
        for (IDocument iDocument : mBatch.getDocuments()) {
            for (IPage iPage : iDocument.getPages()) {
                final IProperty type = findProperty(PROPERTY_TYPE, iPage.getProperties());

                if (validProperty(type)) {
                    if (type.getValue().equalsIgnoreCase("Form")) {
                        return (Page) iPage;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Return the page associated with a cheque.
     * <p>
     * <p>A page that can process cheques needs to have {@code PROPERTY_PROCESS_CHEQUES} with a value
     * of {@code true}.</p>
     *
     * @return
     */
    public Page getChequePage() {
        // go through all documents and search for a page with PROPERTY_PROCESS_CHEQUES=="true"
        for (IDocument iDocument : mBatch.getDocuments()) {
            for (IPage iPage : iDocument.getPages()) {
                final IProperty processChequeProperty = findProperty(PROPERTY_PROCESS_CHEQUES, iPage.getProperties());


                if (validProperty(processChequeProperty)) {
                    if (Boolean.parseBoolean(processChequeProperty.getValue())) {
                        return (Page) iPage;
                    }

                }
            }
        }

        return null;
    }

    /**
     * Get the page associated with a Driver's License front side.
     * <p/>
     * For this sample , we know that the {@code type} of the page will be <strong>DL_Front</strong>.
     *
     * @return
     */
    public Page getDlFrontPage() {
        // go through all documents and search for a page with type=="DL_Front"
        for (IDocument iDocument : mBatch.getDocuments()) {
            for (IPage iPage : iDocument.getPages()) {
                final IProperty type = findProperty(PROPERTY_TYPE, iPage.getProperties());

                if (validProperty(type)) {
                    if (type.getValue().equalsIgnoreCase("DL_Front")) {
                        return (Page) iPage;
                    }
                }
            }
        }

        return null;
    }


    /**
     * Get the page associated with a Driver's License back side.
     * <p/>
     * For this sample , we know that the {@code type} of the page will be <strong>DL_Back</strong>.
     *
     * @return
     */
    public Page getDlBackPage() {
        // go through all documents and search for a page with type=="DL_Back"
        for (IDocument iDocument : mBatch.getDocuments()) {
            for (IPage iPage : iDocument.getPages()) {
                final IProperty type = findProperty(PROPERTY_TYPE, iPage.getProperties());

                if (validProperty(type)) {
                    if (type.getValue().equalsIgnoreCase("DL_Back")) {
                        return (Page) iPage;
                    }
                }
            }
        }

        return null;
    }

    private static IProperty findProperty(@NonNull String propertyName, List<? extends IProperty> properties) {
        for (IProperty property : properties) {
            if (property.getName().equalsIgnoreCase(propertyName)) {
                return property;
            }
        }
        return null;
    }

    private static boolean validProperty(IProperty typeProperty) {
        return typeProperty != null && !TextUtils.isEmpty(typeProperty.getValue());

    }

}
