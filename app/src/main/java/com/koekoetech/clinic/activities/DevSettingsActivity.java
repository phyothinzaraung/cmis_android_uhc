package com.koekoetech.clinic.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.koekoetech.clinic.BuildConfig;
import com.koekoetech.clinic.R;
import com.koekoetech.clinic.fragment.AppProgressDialog;
import com.koekoetech.clinic.helper.CMISConstant;
import com.koekoetech.clinic.helper.DateTimeHelper;
import com.koekoetech.clinic.helper.FileHelper;
import com.koekoetech.clinic.helper.LCAUiTask;
import com.koekoetech.clinic.helper.PatientDataExporter;
import com.koekoetech.clinic.helper.SharePreferenceHelper;
import com.koekoetech.clinic.helper.SuggestionsExporter;
import com.koekoetech.clinic.interfaces.DataImportExportCallBack;

import java.io.File;
import java.util.Date;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import io.realm.Realm;
import needle.Needle;

/**
 * Created by ZMN on 1/22/18.
 **/

public class DevSettingsActivity extends BaseActivity {

    private static final String TAG = DevSettingsActivity.class.getSimpleName();

    private CardView cvExportPatients;
    private CardView cvExportSuggestions;
    private CardView cvChangeBaseUrl;
    private CardView cvDefaultBaseUrl;
    private CardView cvFullBackup;

    private AppProgressDialog progressDialog;
    private SharePreferenceHelper sharePreferenceHelper;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_dev_settings;
    }

    @Override
    protected void setUpContents(Bundle savedInstanceState) {

        setupToolbar(true);
        setupToolbarText(getString(R.string.action_dev_settings));

        bindViews();

        sharePreferenceHelper = SharePreferenceHelper.getHelper(this);
        Log.d(TAG, "setUpContents: Base URL " + sharePreferenceHelper.getBaseUrl());

        cvExportPatients.setOnClickListener(v -> onClickExportPatient());

        cvExportSuggestions.setOnClickListener(v -> onClickExportSuggestions());

        cvChangeBaseUrl.setOnClickListener(v -> onClickChangeBaseUrl());

        cvDefaultBaseUrl.setOnClickListener(v -> onClickDefaultBaseUrl());

        cvFullBackup.setOnClickListener(v -> onClickFullBackUp());

    }

    private void bindViews() {
        cvExportPatients = findViewById(R.id.export_patients);
        cvExportSuggestions = findViewById(R.id.export_suggestions);
        cvChangeBaseUrl = findViewById(R.id.change_base_url);
        cvDefaultBaseUrl = findViewById(R.id.default_base_url);
        cvFullBackup = findViewById(R.id.full_backup);
    }

    void onClickExportPatient() {
        final PatientImportExportListener callBack = new PatientImportExportListener();
        PatientDataExporter exporter = new PatientDataExporter(callBack);
        exporter.init(this);
        callBack.onStart();
        exporter.export(this);
    }

    void onClickExportSuggestions() {
        final SuggestionsImportExportListener callBack = new SuggestionsImportExportListener();
        SuggestionsExporter exporter = new SuggestionsExporter(callBack);
        exporter.init(this);
        callBack.onStart();
        exporter.export(this);
    }

    void onClickChangeBaseUrl() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dev_setting_change_base_url);
        View view = View.inflate(this, R.layout.base_url_edit_dialog, null);
        builder.setView(view);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.dev_setting_action_change, null);
        builder.setNegativeButton(R.string.cancel, null);
        final AlertDialog dialog = builder.create();
        dialog.show();
        final TextInputLayout tilBaseUrl = view.findViewById(R.id.dialog_input_layout_base_url);
        final EditText edtPwd = view.findViewById(R.id.dialog_edt_base_url);
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(v -> {
            if (TextUtils.isEmpty(edtPwd.getText().toString().trim())) {
                tilBaseUrl.setError("Please enter base url");
                return;
            }

            String baseUrl = edtPwd.getText().toString().trim();
            sharePreferenceHelper.setBaseUrl(baseUrl);
            Toast.makeText(DevSettingsActivity.this, "Base Url Changed!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
    }

    void onClickDefaultBaseUrl() {
        sharePreferenceHelper.setBaseUrl(BuildConfig.DEFAULT_BASE_URL);
        Toast.makeText(this, "Base Url has been set to default.", Toast.LENGTH_SHORT).show();
    }

    void onClickFullBackUp() {
        getProgressDialog("Exporting Patients Data");
        progressDialog.display(getSupportFragmentManager());
        Needle.onBackgroundThread().execute(new LCAUiTask<Boolean>(this) {

            @Override
            protected Boolean doWork() {
                boolean isSuccess = false;
                final File externalFilesDir = getExternalFilesDir(null);
                if (externalFilesDir != null && externalFilesDir.exists()) {
                    String now = DateTimeHelper.formatDate(new Date(), DateTimeHelper.LOCAL_DATE_TIME_FILE_FORMAT);
                    String fileName = "CMIS_DB_BACK_UP_" + now + ".realm";
                    final File backUpFile = new File(externalFilesDir, fileName);
                    try (Realm realm = Realm.getDefaultInstance()) {
                        realm.writeCopyTo(backUpFile);
                        FileHelper.compressFile(backUpFile, CMISConstant.DATA_EXPORT_ZIP_PASSWORD, true);
                        isSuccess = true;
                    } catch (Exception e) {
                        Log.e(TAG, "onClickFullBackUp: Database Backup Error ", e);
                    }

                }
                return isSuccess;
            }

            @Override
            protected void thenDoUiRelatedWork(Boolean isSuccess) {
                hideProgress();
                final String message = (isSuccess != null && isSuccess) ? "Patients Data Exported Successfully" : "Patients Data Export Failed";
                Toast.makeText(DevSettingsActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getProgressDialog(String message) {
        hideProgress();
        progressDialog = AppProgressDialog.getProgressDialog(message);
    }

    private void hideProgress() {
        if (progressDialog != null && progressDialog.isDisplaying()) {
            progressDialog.safeDismiss();
        }
    }

    private class PatientImportExportListener implements DataImportExportCallBack {

        @Override
        public void onStart() {
            getProgressDialog("Exporting Patients Data");
            progressDialog.display(getSupportFragmentManager());
        }

        @Override
        public void onError() {
            hideProgress();
            Toast.makeText(getApplicationContext(), "Patients Data Export Failed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onComplete() {
            hideProgress();
            Toast.makeText(getApplicationContext(), "Patients Data Exported Successfully", Toast.LENGTH_SHORT).show();
        }
    }

    private class SuggestionsImportExportListener implements DataImportExportCallBack {

        @Override
        public void onStart() {
            getProgressDialog("Exporting Suggestions");
            progressDialog.display(getSupportFragmentManager());
        }

        @Override
        public void onError() {
            hideProgress();
            Toast.makeText(DevSettingsActivity.this, "Failed to export suggestions", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onComplete() {
            hideProgress();
            Toast.makeText(DevSettingsActivity.this, "Suggesions Exported Successfully", Toast.LENGTH_SHORT).show();
        }
    }


}
