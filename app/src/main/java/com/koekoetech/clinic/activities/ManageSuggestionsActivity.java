package com.koekoetech.clinic.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;
import com.koekoetech.clinic.R;
import com.koekoetech.clinic.adapter.ManageSuggestionsAdapter;
import com.koekoetech.clinic.helper.SharePreferenceHelper;
import com.koekoetech.clinic.interfaces.ManageSuggestionsCallback;
import com.koekoetech.clinic.model.SuggestionWordModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by ZMN on 12/15/17.
 **/

public class ManageSuggestionsActivity extends BaseActivity implements ManageSuggestionsCallback {

    private static final String TAG = "ManageSuggestions";

    private Realm realm;
    private int staffId;
    private ManageSuggestionsAdapter manageSuggestionsAdapter;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_manage_suggestions;
    }

    @Override
    protected void setUpContents(Bundle savedInstanceState) {

        // Bind Views
        RecyclerView rvSuggestions = findViewById(R.id.ManageSuggestions_rvLists);

        // Init
        realm = Realm.getDefaultInstance();
        staffId = SharePreferenceHelper.getHelper(getApplicationContext()).getAuthenticationModel().getStaffId();
        manageSuggestionsAdapter = new ManageSuggestionsAdapter(loadSuggestionList(), this);

        // Setup Views
        setupToolbar(true);
        setupToolbarText("Manage Suggestions");
        rvSuggestions.setAdapter(manageSuggestionsAdapter);
        rvSuggestions.setHasFixedSize(true);
    }

    private RealmResults<SuggestionWordModel> loadSuggestionList() {
        return realm.where(SuggestionWordModel.class)
                .equalTo("isDeleted", false)
                .equalTo("staffId", staffId)
                .equalTo("type", "Specific")
                .findAllAsync();
    }

    @Override
    public void onSuggestionEdit(final int position) {

        @Nullable final SuggestionWordModel suggestionWordModel = getDetachedAdapterItem(position);
        if (suggestionWordModel == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.manage_suggestions_edit_suggestion);
        View view = View.inflate(this, R.layout.dialog_edit_suggestion, null);
        builder.setView(view);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.manage_suggestions_save, null);
        builder.setNegativeButton(android.R.string.cancel, null);
        final AlertDialog dialog = builder.create();
        dialog.show();
        final TextInputLayout tipWord = view.findViewById(R.id.suggestion_edit_dialog_input_layout_word);
        final TextInputLayout tipWordValue = view.findViewById(R.id.suggestion_edit_dialog_input_layout_value);
        final EditText edtWord = view.findViewById(R.id.suggestion_edit_edt_word);
        final EditText edtValue = view.findViewById(R.id.suggestion_edit_edt_value);

        if (!TextUtils.isEmpty(suggestionWordModel.getWord())) {
            edtWord.setText(suggestionWordModel.getWord());
        }

        if (!TextUtils.isEmpty(suggestionWordModel.getValue())) {
            edtValue.setText(suggestionWordModel.getValue());
        }

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(v -> {
            String word = edtWord.getText().toString().trim();
            String value = edtValue.getText().toString().trim();

            if (TextUtils.isEmpty(word)) {
                tipWord.setError(getString(R.string.manage_suggestions_hint_enter_word));
                return;
            }

            if (TextUtils.isEmpty(value)) {
                tipWordValue.setError(getString(R.string.manage_suggestions_hint_enter_value));
                return;
            }

            updateSuggestion(word, value, suggestionWordModel);
            dialog.dismiss();

//            realm.executeTransactionAsync(realm -> {
//                suggestionWordModel.setWord(word);
//                suggestionWordModel.setValue(value);
//                suggestionWordModel.setHasSynced(false);
//                realm.copyToRealmOrUpdate(suggestionWordModel);
//            }, dialog::dismiss, error -> dialog.dismiss());

        });
    }

    @Override
    public void onSuggestionDelete(final int position) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.manage_suggestions_delete_confirm_title)
                .setMessage(R.string.manage_suggestions_delete_confirm)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    @Nullable final SuggestionWordModel suggestionWordModel = getDetachedAdapterItem(position);
                    if (suggestionWordModel != null) {
                        deleteSuggestion(suggestionWordModel);
                    }
//                    if (suggestionWordModel.isOnline()) {
//                        realm.executeTransactionAsync(realm -> {
//                                    suggestionWordModel.setOnline(false);
//                                    suggestionWordModel.setHasSynced(false);
//                                    suggestionWordModel.setDeleted(true);
//                                    realm.copyToRealmOrUpdate(suggestionWordModel);
//                                },
//                                dialog::dismiss, error -> dialog.dismiss());
//                    } else {
//                        try {
//                            realm.executeTransaction(realm -> {
//                                SuggestionWordModel deleteTarget = realm.where(SuggestionWordModel.class)
//                                        .equalTo("localID", suggestionWordModel.getLocalID())
//                                        .findFirst();
//                                if (deleteTarget != null) {
//                                    deleteTarget.deleteFromRealm();
//                                }
//                            });
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            Toast.makeText(ManageSuggestionsActivity.this, getString(R.string.manage_suggestions_delete_fail), Toast.LENGTH_SHORT).show();
//                        }
//                    }

                })
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void updateSuggestion(@Nullable final String word, @Nullable final String value, @NonNull final SuggestionWordModel targetSuggestionWordModel) {
        Log.d(TAG, "updateSuggestion() called with: word = [" + word + "], value = [" + value + "], targetSuggestionWordModel = [" + targetSuggestionWordModel + "]");

        realm.executeTransactionAsync(realm -> {
            targetSuggestionWordModel.setWord(word);
            targetSuggestionWordModel.setValue(value);
            targetSuggestionWordModel.setHasSynced(false);
            realm.copyToRealmOrUpdate(targetSuggestionWordModel);
        });
    }

    private void deleteSuggestion(@NonNull final SuggestionWordModel targetSuggestionWordModel) {
        realm.executeTransactionAsync(realm -> {
            if (targetSuggestionWordModel.isOnline()) {
                targetSuggestionWordModel.setOnline(false);
                targetSuggestionWordModel.setHasSynced(false);
                targetSuggestionWordModel.setDeleted(true);
                realm.copyToRealmOrUpdate(targetSuggestionWordModel);
            } else {
                final SuggestionWordModel deleteTarget = realm.where(SuggestionWordModel.class)
                        .equalTo("localID", targetSuggestionWordModel.getLocalID())
                        .findFirst();
                if (deleteTarget != null) {
                    deleteTarget.deleteFromRealm();
                }
            }
        });
    }

    @Nullable
    private SuggestionWordModel getDetachedAdapterItem(final int position) {
        @Nullable final SuggestionWordModel adapterItem = manageSuggestionsAdapter.getItem(position);
        if (adapterItem != null) {
            return realm.copyFromRealm(adapterItem);
        }
        return null;
    }
}
