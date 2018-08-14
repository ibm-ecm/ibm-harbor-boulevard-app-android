package com.futureworkshops.android.harborboulevard.presentation.accounts.create.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.futureworkshops.android.harborboulevard.R;
import com.futureworkshops.datacap.common.api.BatchFactory;
import com.futureworkshops.datacap.common.model.Field;
import com.ibm.datacap.sdk.id.model.IdField;
import com.ibm.datacap.sdk.model.IField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Displays data extracted from <b>forms</b> and the <b>back of the driver's license</b>.
 */
public class PageFieldsAdapter extends RecyclerView.Adapter<PageFieldsAdapter.FieldViewHolder> {

    private List<Field> mFields;

    public PageFieldsAdapter() {
        mFields = new ArrayList<>();
    }

    public void setFields(List<Field> fields) {
        mFields.clear();
        mFields.addAll(fields);
        notifyDataSetChanged();
    }


    @Override
    public FieldViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scanned_content, parent, false);
        return new FieldViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FieldViewHolder holder, int position) {
        Field field = mFields.get(position);
        holder.key.setText(field.getLabel());
        holder.value.setText(field.getValue());
    }

    @Override
    public int getItemCount() {
        return mFields.size();
    }

    public void updateItems(List<IField> updatedFields) {
        for (Field field : mFields) {
            IField updField = getUpdatedField(updatedFields, field.getId());

            if (updField != null) {
                BatchFactory.updatePageField(field, updField.getValue());
            }
        }

        for (int i = 0; i < mFields.size(); i++) {


//            if (i <= updatedFields.size() - 1) {
//                mFields.get(i).setValue(updatedFields.get(i).getValue());
//            }
        }
        notifyDataSetChanged();
    }

    private IField getUpdatedField(List<IField> fields, String fieldId) {
        for (IField field : fields) {
            if (field.getId().equalsIgnoreCase(fieldId)) {
                return field;
            }
        }

        return null;
    }

    public void updateIdItems(HashMap<String, IdField> fields) {
        int i = 0;
        for (IdField field : fields.values()) {
            Log.e("Error", "field value is: " + field.getValue());
            if (i <= mFields.size() - 1) {
                mFields.get(i).setValue(field.getValue());
            } else {
                break;
            }
            ++i;
        }
        notifyDataSetChanged();
    }

    public static class FieldViewHolder extends RecyclerView.ViewHolder {

        private TextView key;
        private TextView value;

        public FieldViewHolder(View itemView) {
            super(itemView);
            key = itemView.findViewById(R.id.key);
            value = itemView.findViewById(R.id.value);
        }
    }
}
