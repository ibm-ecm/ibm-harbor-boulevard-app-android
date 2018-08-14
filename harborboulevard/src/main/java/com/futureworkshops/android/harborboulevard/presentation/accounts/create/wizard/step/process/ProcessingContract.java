package com.futureworkshops.android.harborboulevard.presentation.accounts.create.wizard.step.process;

import com.futureworkshops.datacap.common.model.Page;
import com.ibm.datacap.sdk.id.model.IdField;
import com.ibm.datacap.sdk.model.IField;

import java.util.HashMap;
import java.util.List;

public interface ProcessingContract {

    interface View {

        void onTextExtracted(List<IField> iFields);

        void errorExtractingText(Throwable e);

        void onIdTextExtracted(HashMap<String, IdField> fields);
    }

    interface Presenter {

        void extractTextFromForm(Page page);

        void extractTextFromDLBack(Page page);

        void extractTextChequeInformation(Page page);
    }
}
