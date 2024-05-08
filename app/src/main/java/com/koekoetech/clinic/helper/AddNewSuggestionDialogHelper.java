package com.koekoetech.clinic.helper;

import android.app.Activity;
import android.app.Dialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.koekoetech.clinic.R;
import com.koekoetech.clinic.model.AuthenticationModel;
import com.koekoetech.clinic.model.SuggestionWordModel;

import java.util.UUID;

import androidx.appcompat.app.AlertDialog;
import io.realm.Realm;

/**
 * Created by ZMN on 3/24/17.
 **/

public class AddNewSuggestionDialogHelper {

    private EditText editTextWord;
    private EditText editTextValue;
    private Button btnSave;
    private Activity currentActivity;
    private String suggestionType;
    private SuggestionCreatedListener suggestionCreatedListener;
    private AuthenticationModel authenticationModel;

    public AddNewSuggestionDialogHelper(Activity currentActivity) {
        this.currentActivity = currentActivity;
    }

    public void setSuggestionCreatedListener(SuggestionCreatedListener suggestionCreatedListener) {
        this.suggestionCreatedListener = suggestionCreatedListener;
    }

    public void setSuggestionType(String suggestionType) {
        this.suggestionType = suggestionType;
    }

    public void showAddNewSuggestionDialog(LayoutInflater inflater) {

        authenticationModel = SharePreferenceHelper.getHelper(currentActivity).getAuthenticationModel();

        View view = inflater.inflate(R.layout.dialog_fragment_add_new_word, null);

        editTextWord = view.findViewById(R.id.addNew_edt_word);
        editTextValue = view.findViewById(R.id.addNew_edt_value);

        EditTextValidityWatcher textWatcher = new EditTextValidityWatcher();

        editTextWord.addTextChangedListener(textWatcher);
        editTextValue.addTextChangedListener(textWatcher);


        AlertDialog.Builder builder =
                new AlertDialog.Builder(currentActivity);
        builder.setTitle("Add New Suggestion");
        builder.setPositiveButton("Save", (dialog, which) -> {

            SuggestionWordModel suggestionWordModel = new SuggestionWordModel();
            suggestionWordModel.setLocalID(UUID.randomUUID().toString());
            suggestionWordModel.setWord(editTextWord.getText().toString().trim());
            suggestionWordModel.setValue(editTextValue.getText().toString().trim());
            suggestionWordModel.setSource("ONLINE");
            suggestionWordModel.setIsReason(String.valueOf(suggestionType.equals(CMISConstant.PRESSNOTE_REASON)));
            suggestionWordModel.setIsObservation(String.valueOf(suggestionType.equals(CMISConstant.PRESSNOTE_OBSERVATION)));
            suggestionWordModel.setIsInvestigation(String.valueOf(suggestionType.equals(CMISConstant.PRESSNOTE_INVESTIGATION)));
            suggestionWordModel.setIsDiagnosis(String.valueOf(suggestionType.equals(CMISConstant.PRESSNOTE_Diagnosis)));
            suggestionWordModel.setIsCare(String.valueOf(suggestionType.equals(CMISConstant.PRESSNOTE_CARE)));
            suggestionWordModel.setIsReferral(String.valueOf(suggestionType.equals(CMISConstant.PRESSNOTE_REFERRAL)));
            suggestionWordModel.setStaffId(authenticationModel.getStaffId());
            suggestionWordModel.setHasSynced(false);
            suggestionWordModel.setIsDeleted(false);
            suggestionWordModel.setOnline(false);
            suggestionWordModel.setType("Specific");
            // Save suggestion model
            saveSuggestionOffline(suggestionWordModel);
        });
        builder.setNegativeButton("Cancel", null);
        builder.setView(view);

        Dialog dialog = builder.create();
        dialog.show();
        btnSave = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
        btnSave.setEnabled(false);
        editTextWord.setSingleLine();
        editTextValue.setSingleLine();
    }

    private void saveSuggestionOffline(final SuggestionWordModel suggestionWordModel) {
        Realm.getDefaultInstance().executeTransactionAsync(realm -> realm.copyToRealmOrUpdate(suggestionWordModel), () -> {
            if (suggestionCreatedListener != null) {
                suggestionCreatedListener.onSuggestionCreated();
            }
            Toast.makeText(currentActivity, "Saved Successfully", Toast.LENGTH_SHORT).show();
        }, error -> {
            Log.e("SuggestionWord", "saveSuggestionOffline: ", error);
            new AlertDialog.Builder(currentActivity)
                    .setTitle("Save Failure")
                    .setMessage("Something went wrong while saving suggestion! Retry ?")
                    .setPositiveButton(currentActivity.getResources().getString(R.string.retry), (dialog, which) -> saveSuggestionOffline(suggestionWordModel))
                    .setNegativeButton(currentActivity.getResources().getString(android.R.string.cancel), (dialog, which) -> dialog.dismiss())
                    .show();
        });

    }

    public interface SuggestionCreatedListener {
        void onSuggestionCreated();
    }

    private class EditTextValidityWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            boolean isValidTitle = !TextUtils.isEmpty(editTextWord.getText().toString().trim());
            boolean isValidDesc = !TextUtils.isEmpty(editTextValue.getText().toString().trim());

            btnSave.setEnabled(isValidTitle && isValidDesc);
        }
    }


}
