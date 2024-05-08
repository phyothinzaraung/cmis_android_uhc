package com.koekoetech.clinic.adapter.viewholders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.koekoetech.clinic.R;
import com.koekoetech.clinic.helper.CMISConstant;
import com.koekoetech.clinic.helper.TextViewUtils;
import com.koekoetech.clinic.interfaces.ManageSuggestionsCallback;
import com.koekoetech.clinic.model.KeyValueText;
import com.koekoetech.clinic.model.SuggestionWordModel;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by ZMN on 12/23/20.
 **/
public final class SuggestionHolder extends RecyclerView.ViewHolder {

    private final TextView tvSuggestionWord;
    private final TextView tvSuggestionValue;
    private final TextView tvSuggestionType;
    private final MaterialButton btnEdit;
    private final MaterialButton btnDelete;

    private final ManageSuggestionsCallback manageSuggestionsCallback;
    private final Context mContext;

    private SuggestionHolder(@NonNull View itemView, final ManageSuggestionsCallback manageSuggestionsCallback) {
        super(itemView);
        this.manageSuggestionsCallback = manageSuggestionsCallback;
        this.mContext = itemView.getContext();

        // Bind Views
        tvSuggestionWord = itemView.findViewById(R.id.tv_suggestion_keyword);
        tvSuggestionValue = itemView.findViewById(R.id.tv_suggestion_value);
        tvSuggestionType = itemView.findViewById(R.id.tv_suggestion_type);
        btnEdit = itemView.findViewById(R.id.btn_suggestionEdit);
        btnDelete = itemView.findViewById(R.id.btn_suggestionDelete);
    }

    public static SuggestionHolder create(@NonNull final ViewGroup parent, @NonNull final ManageSuggestionsCallback manageSuggestionsCallback) {
        final Context context = parent.getContext();
        final LayoutInflater layoutInflater = LayoutInflater.from(context);
        final View itemView = layoutInflater.inflate(R.layout.item_manage_suggestions, parent, false);
        return new SuggestionHolder(itemView, manageSuggestionsCallback);
    }

    public void bind(@NonNull final SuggestionWordModel suggestionWordModel) {

        final KeyValueText textWord = new KeyValueText(mContext.getString(R.string.manage_suggestions_word_name), suggestionWordModel.getWord());
        TextViewUtils.populateKeyValueTv(tvSuggestionWord, textWord);

        final KeyValueText textValue = new KeyValueText(mContext.getString(R.string.manage_suggestions_word_value), suggestionWordModel.getValue());
        TextViewUtils.populateKeyValueTv(tvSuggestionValue, textValue);

        final KeyValueText textType = new KeyValueText(mContext.getString(R.string.manage_suggestions_type), getType(suggestionWordModel));
        TextViewUtils.populateKeyValueTv(tvSuggestionType, textType);

        btnEdit.setOnClickListener(v -> {
            if (manageSuggestionsCallback != null) {
                manageSuggestionsCallback.onSuggestionEdit(getAdapterPosition());
            }
        });

        btnDelete.setOnClickListener(v -> {
            if (manageSuggestionsCallback != null) {
                manageSuggestionsCallback.onSuggestionDelete(getAdapterPosition());
            }
        });
    }

    private String getType(@NonNull final SuggestionWordModel suggestionWordModel) {
        String type = "-";

        try {

            if (Boolean.parseBoolean(suggestionWordModel.getIsProblem())) {
                type = CMISConstant.PRESSNOTE_PROBLEM;
            } else if (Boolean.parseBoolean(suggestionWordModel.getIsCare())) {
                type = CMISConstant.PRESSNOTE_CARE;
            } else if (Boolean.parseBoolean(suggestionWordModel.getIsDiagnosis())) {
                type = CMISConstant.PRESSNOTE_Diagnosis;
            } else if (Boolean.parseBoolean(suggestionWordModel.getIsTreatment())) {
                type = CMISConstant.PRESSNOTE_TREATMENT;
            } else if (Boolean.parseBoolean(suggestionWordModel.getIsInvestigation())) {
                type = CMISConstant.PRESSNOTE_INVESTIGATION;
            } else if (Boolean.parseBoolean(suggestionWordModel.getIsReferral())) {
                type = CMISConstant.PRESSNOTE_REFERRAL;
            } else if (Boolean.parseBoolean(suggestionWordModel.getIsObservation())) {
                type = CMISConstant.PRESSNOTE_OBSERVATION;
            } else if (Boolean.parseBoolean(suggestionWordModel.getIsReason())) {
                type = CMISConstant.PRESSNOTE_REASON;
            } else if (Boolean.parseBoolean(suggestionWordModel.getIsRequest())) {
                type = CMISConstant.PRESSNOTE_REQUEST;
            } else if (Boolean.parseBoolean(suggestionWordModel.getIsService())) {
                type = CMISConstant.PRESSNOTE_SERVICE;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return type;
    }
}
