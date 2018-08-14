package com.futureworkshops.datacap.common.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.futureworkshops.datacap.common.model.Batch;
import com.futureworkshops.datacap.common.model.Document;
import com.futureworkshops.datacap.common.model.Field;
import com.futureworkshops.datacap.common.model.FieldChar;
import com.futureworkshops.datacap.common.model.Page;
import com.futureworkshops.datacap.common.model.Property;
import com.ibm.datacap.sdk.model.IDocumentType;
import com.ibm.datacap.sdk.model.IFieldType;
import com.ibm.datacap.sdk.model.IPageType;
import com.ibm.datacap.sdk.model.IProperty;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This Factory will create the {@link com.ibm.datacap.sdk.model.IBatch} object and all other related
 * classes required for upload.
 */
public class BatchFactory {

    public static final String PROPERTY_TYPE = "TYPE";
    public static final String PROPERTY_STATUS = "STATUS";
    public static final String PROPERTY_IMAGE_FILE = "IMAGEFILE";
    public static final String PROPERTY_LABEL = "label";


    /**
     * Create a new batch  required for the Autocapture sample app.
     * <p>
     * <strong>Make sure to provide a folder with WRITE permission as the batch local folder !!</strong>
     *
     * @return
     */
    public static Batch createAutocaptureBatch(@NonNull Context context, @NonNull BatchConfiguratorHelper batchConfiguratorHelper) {
        Batch batch = new Batch();
        batch.setId(String.valueOf(System.currentTimeMillis()));
        String batchType = batchConfiguratorHelper.getBatchType().getType();

        List<Property> properties = new ArrayList<>();
        properties.add(new Property(PROPERTY_TYPE, batchType));
        properties.add(new Property(PROPERTY_STATUS, "0"));

        batch.setProperties(properties);

        //  create a single document
        List<IDocumentType> documentTypes = batchConfiguratorHelper.getAllowedDocumentTypes();
        List<Document> documents = new ArrayList<>();

        final Document document = createDocumentWithType(documentTypes.get(0));
        documents.add(document);

        batch.setDocuments(documents);

        // create local folder
        final File filesDir = new File(context.getFilesDir(), FileManager.BATCH_FOLDER);
        File batchDir = new File(filesDir.getAbsolutePath(), batch.getId());

        if (!batchDir.exists()) {
            batchDir.mkdirs();
        }


        batch.setLocalDir(batchDir.getAbsolutePath());

        return batch;
    }


    /**
     * Create a new batch  required for the HarborBoulevard sample app.
     * <p> This batch will contain a {@link Document} for every available {@link IDocumentType} and each
     * document will contain a {@link Page} for eveyr {@link IPageType} supported by the document</p>
     * <p>
     * <p>
     * <strong>Make sure to provide a folder with WRITE permission as the batch local folder !!</strong>
     *
     * @return
     */
    public static Batch createHarborBoulevardBatch(@NonNull Context context, @NonNull BatchConfiguratorHelper batchConfiguratorHelper) {
        Batch batch = new Batch();
        batch.setId(String.valueOf(System.currentTimeMillis()));
        String batchType = batchConfiguratorHelper.getBatchType().getType();

        List<Property> properties = new ArrayList<>();
        properties.add(new Property(PROPERTY_TYPE, batchType));
        properties.add(new Property(PROPERTY_STATUS, "0"));

        batch.setProperties(properties);
        batch.setDocuments(createDocuments(batchConfiguratorHelper));

        // create local folder
        FileManager fileManager = new FileManager(context);
        File root = fileManager.getBatchRootFolder();
        File batchDir = new File(root.getAbsolutePath(), batch.getId());

        if (!batchDir.exists()) {
            batchDir.mkdirs();
        }


        batch.setLocalDir(batchDir.getAbsolutePath());

        return batch;
    }

    /**
     * Create a new {@link Page} with the given {@link IPageType} and ID.
     *
     * @param batchConfiguratorHelper
     * @param pageType
     * @param pageId
     * @return
     */
    public static Page createPageWithTypeAndId(@NonNull BatchConfiguratorHelper batchConfiguratorHelper,
                                               @NonNull IPageType pageType,
                                               @NonNull String pageId) {
        List<IFieldType> pageFields = batchConfiguratorHelper.getPageFields(pageType);

        final Page page = new Page();
        page.setId(pageId);

        // add default properties
        List<Property> properties = new ArrayList<>();

        for (IProperty property : pageType.getProperties()) {
            if (property.getName().equals(PROPERTY_TYPE)) {
                properties.add(new Property(PROPERTY_TYPE, pageType.getType()));
            } else {
                properties.add(new Property(property.getName(), property.getValue()));
            }
        }

        page.setProperties(properties);

        List<Field> fields = new ArrayList<>();
        for (IFieldType fieldType : pageFields) {
            fields.add(createField(fieldType, null));
        }

        page.setFields(fields);
        return page;
    }

    /**
     * Update the value of a {@link Field} and also generate the required {@link FieldChar}.
     *
     * @param field
     * @param fieldvalue
     */
    public static void updatePageField(@NonNull Field field, String fieldvalue) {
        field.setValue(fieldvalue);
        field.setFieldChars(createFieldChars(fieldvalue));
    }

    private static List<Document> createDocuments(BatchConfiguratorHelper batchConfiguratorHelper) {
        List<Document> documents = new ArrayList<>();
        // add default properties
        List<IDocumentType> documentTypes = batchConfiguratorHelper.getAllowedDocumentTypes();
        for (IDocumentType documentType : documentTypes) {
            Document document = createDocumentWithType(documentType);
            document.setPages(createPages(documentType, batchConfiguratorHelper));

            documents.add(document);
        }

        return documents;
    }

    @NonNull
    private static Document createDocumentWithType(IDocumentType documentType) {
        Document document = new Document();
        document.setId(String.valueOf(System.currentTimeMillis()));

        List<Property> properties = new ArrayList<>();
        for (IProperty property : documentType.getProperties()) {

            if (property.getName().equals(PROPERTY_TYPE)) {
                properties.add(new Property(PROPERTY_TYPE, documentType.getType()));
            } else {
                properties.add(new Property(property.getName(), property.getValue()));
            }
        }

        document.setProperties(properties);
        return document;
    }

    private static List<Page> createPages(IDocumentType documentType, BatchConfiguratorHelper batchConfiguratorHelper) {
        List<Page> pages = new ArrayList<>();

        //  get page types
        List<IPageType> pageTypesForDocument = batchConfiguratorHelper
                .getPageTypesForDocument(documentType);

        for (IPageType pageType : pageTypesForDocument) {
            final Page page = createPageWithType(batchConfiguratorHelper, pageType);
            pages.add(page);
        }

        return pages;
    }

    @NonNull
    private static Page createPageWithType(BatchConfiguratorHelper batchConfiguratorHelper, IPageType pageType) {
        List<IFieldType> pageFields = batchConfiguratorHelper.getPageFields(pageType);

        final Page page = new Page();
        page.setId(String.valueOf(System.currentTimeMillis()));

        // add default properties
        List<Property> properties = new ArrayList<>();

        for (IProperty property : pageType.getProperties()) {
            if (property.getName().equals(PROPERTY_TYPE)) {
                properties.add(new Property(PROPERTY_TYPE, pageType.getType()));
            } else {
                properties.add(new Property(property.getName(), property.getValue()));
            }
        }

        page.setProperties(properties);

        List<Field> fields = new ArrayList<>();
        for (IFieldType fieldType : pageFields) {
            fields.add(createField(fieldType, null));
        }

        page.setFields(fields);
        return page;
    }

    private static Field createField(IFieldType fieldType, String fieldValue) {
        Field field = new Field();
        field.setId(String.valueOf(System.nanoTime()));

        // some fields don't have the 'label' property
        boolean hasLabel = false;
        List<Property> fieldProps = new ArrayList<>();
        for (IProperty property : fieldType.getProperties()) {
            if (property.getName().equalsIgnoreCase(PROPERTY_TYPE)) {
                fieldProps.add(new Property(PROPERTY_TYPE, fieldType.getType()));
            } else {
                hasLabel = property.getName().equalsIgnoreCase(PROPERTY_LABEL);

                fieldProps.add(new Property(property.getName(), property.getValue()));
            }
        }

        // if we don't have a label we need to use the field type
        if (!hasLabel) {
            fieldProps.add(
                    new Property(PROPERTY_LABEL, fieldType.getType()));
        }

        field.setProperties(fieldProps);

        if (!TextUtils.isEmpty(fieldValue)) {
            field.setValue(fieldValue);
            field.setFieldChars(createFieldChars(fieldValue));
        }

        return field;
    }

    private static List<FieldChar> createFieldChars(@NonNull String fieldvalue) {
        List<FieldChar> charList = new ArrayList<>();
        final char[] chars = fieldvalue.toCharArray();
        for (char c : chars) {
            charList.add(new FieldChar(getAsciiRepresentation(c)));
        }

        return charList;
    }

    private static String getAsciiRepresentation(char character) {
        int ascii = (int) character;
        return String.valueOf(ascii);
    }

}
