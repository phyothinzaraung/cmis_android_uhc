package com.koekoetech.clinic.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.koekoetech.clinic.R;
import com.koekoetech.clinic.fragment.AppProgressDialog;
import com.koekoetech.clinic.helper.CmisGoogleAnalyticsConstants;
import com.koekoetech.clinic.helper.DevSettingEntryPoint;
import com.koekoetech.clinic.helper.MultiClickListener;
import com.koekoetech.clinic.helper.ServiceHelper;
import com.koekoetech.clinic.helper.SharePreferenceHelper;
import com.koekoetech.clinic.helper.UhcPatientDownloadHelper;
import com.koekoetech.clinic.interfaces.UhcPatientDownloadCallbacks;
import com.koekoetech.clinic.model.AuthenticationModel;
import com.koekoetech.clinic.model.LoginModel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LifecycleOwner;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends BaseActivity {

    private static final String TAG = "LoginActivity";

    private EditText edt_username;
    private EditText edt_password;

    private LinearLayout layout_login;
    private LinearLayout layout_app_info;

    private SharePreferenceHelper sharePreferenceHelper;
    private ServiceHelper.ApiService service;

    private AppProgressDialog appProgressDialog;
    private UhcPatientDownloadHelper patientDownloadHelper;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_login;
    }

    @Override
    protected void setUpContents(Bundle savedInstanceState) {

        setupToolbar(false);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        bindViews();

        init();
        String passcode = "1234";
        sharePreferenceHelper.setPasscode(passcode);

        layout_login.setEnabled(false);

        edt_password.setTransformationMethod(new PasswordTransformationMethod());

        edt_username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                layout_login.setEnabled(edt_password.getText().toString().trim().length() > 0 && s.toString().trim().length() > 0);
            }
        });

        edt_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                layout_login.setEnabled(edt_username.getText().toString().trim().length() > 0 && s.toString().trim().length() > 0);
            }
        });
        layout_login.setOnClickListener(v -> {
            sendActionAnalytics(CmisGoogleAnalyticsConstants.ACTION_LOGIN);
            LoginModel model = new LoginModel();
            model.setUsername(edt_username.getText().toString().trim());
            model.setPassword(edt_password.getText().toString().trim());
            Login(model);
        });

        layout_app_info.setOnClickListener(new MultiClickListener(7) {
            @Override
            protected void onTargetReached(View v) {
                DevSettingEntryPoint.goToDevSetting(LoginActivity.this);
            }

            @Override
            protected void onClickEachTime(int clickedCount) {

            }
        });
    }

    private void bindViews() {
        edt_username = findViewById(R.id.edt_username);
        edt_password = findViewById(R.id.edt_password);
        layout_login = findViewById(R.id.layout_login);
        layout_app_info = findViewById(R.id.login_app_info);
    }

    private void init() {
        service = ServiceHelper.getClient(this);
        sharePreferenceHelper = SharePreferenceHelper.getHelper(this);
        appProgressDialog = new AppProgressDialog();

        patientDownloadHelper = new UhcPatientDownloadHelper(new UhcPatientDownloadCallbacks() {
            @Override
            public Context getContext() {
                return getApplicationContext();
            }

            @NonNull
            @Override
            public LifecycleOwner lifecycleOwner() {
                return LoginActivity.this;
            }

            @Override
            public void patientDataRequestOnStart() {
                getAppProgressDialog().setTitle("Requesting Clinic Data");
                getAppProgressDialog().setMessage("Waiting for clinic data");
            }

            @Override
            public void patientDataRequestOnFail() {
                getAppProgressDialog().safeDismiss();
                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle(getResources().getString(R.string.dialog_title_clinic_data_req_failed))
                        .setMessage(getResources().getString(R.string.dialog_msg_clinic_data_req_failed))
                        .setPositiveButton(getResources().getString(R.string.txt_btn_ok), null)
                        .show();
            }

            @Override
            public void dataDownloadOnPrepare() {
                if (!getAppProgressDialog().isDisplaying()) {
                    getAppProgressDialog().display(getSupportFragmentManager());
                    getAppProgressDialog().setTitle("Preparing Download");
                    getAppProgressDialog().setMessage("Preparing to initDownloadProcess clinic data");
                }
            }

            @Override
            public void dataDownloadOnStart() {
                getAppProgressDialog().setTitle("Downloading Clinic Data");
            }

            @Override
            public void dataDownloadProgressUpdate(String progressMessage) {
                getAppProgressDialog().setMessage(progressMessage);
            }

            @Override
            public void dataDownloadOnFailed(String failedUrl) {
                getAppProgressDialog().safeDismiss();
                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle(getResources().getString(R.string.dialog_title_clinic_data_download_failed))
                        .setMessage(getResources().getString(R.string.dialog_msg_clinic_data_download_failed))
                        .setPositiveButton(getResources().getString(R.string.txt_btn_ok), (dialog, which) -> patientDownloadHelper.downloadClinicData(failedUrl))
                        .setNegativeButton(getString(R.string.cancel), ((dialog, which) -> finish()))
                        .show();
            }

            @Override
            public void onStartDataImport() {
                getAppProgressDialog().setTitle("Importing");
                getAppProgressDialog().setMessage("Importing Clinic Data");
            }

            @Override
            public void onCompleteDataImport() {
                getAppProgressDialog().safeDismiss();
                Toast.makeText(LoginActivity.this, "Data Import Completed", Toast.LENGTH_SHORT).show();
                goToHomeActivity();
            }

            @Override
            public void onErrorDataImport(String errMsg) {
                getAppProgressDialog().safeDismiss();

                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle(getResources().getString(R.string.dialog_title_clinic_data_import_failed))
                        .setMessage(errMsg)
                        .setPositiveButton(getResources().getString(R.string.txt_btn_ok), (dialog, which) -> patientDownloadHelper.initDownloadProcess(sharePreferenceHelper.getAuthenticationModel().getStaffId()))
                        .show();
            }

            @Override
            public void onDataImportProgress(String message) {
                getAppProgressDialog().setMessage(message);
            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager != null) {
            if (getCurrentFocus() != null) {
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }
        return true;
    }

    private AppProgressDialog getAppProgressDialog() {
        if (appProgressDialog == null) {
            appProgressDialog = new AppProgressDialog();
        }
        return appProgressDialog;
    }

    private void Login(final LoginModel loginmodel) {
        getAppProgressDialog().display(getSupportFragmentManager());
        new Handler(Looper.getMainLooper()).postDelayed(() -> runOnUiThread(() -> {
            getAppProgressDialog().setTitle("Logging In");
            getAppProgressDialog().setMessage("Authenticating account");
        }), 100);

        Call<AuthenticationModel> callPostStaffLogin = service.postStaffLogin(loginmodel);
        callPostStaffLogin.enqueue(new Callback<AuthenticationModel>() {
            @Override
            public void onResponse(@NonNull Call<AuthenticationModel> call, @NonNull Response<AuthenticationModel> response) {
                if (response.isSuccessful()) {
                    final AuthenticationModel model = response.body();
//                    Log.d(TAG, model.getStaffName());
                    if (model == null) {
                        getAppProgressDialog().safeDismiss();
                        new AlertDialog.Builder(LoginActivity.this)
                                .setTitle(getResources().getString(R.string.txt_title_login_fail))
                                .setMessage(getResources().getString(R.string.txt_login_fail))
                                .setPositiveButton(getResources().getString(R.string.txt_btn_ok), null)
                                .show();
                    } else {
                        Realm.getDefaultInstance().executeTransactionAsync(realm -> realm.copyToRealm(model),
                                () -> {
                                    sharePreferenceHelper.setAuthenticationModel(model);
                                    sharePreferenceHelper.setLogIn(true);
                                    final FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());
                                    firebaseAnalytics.setUserProperty(CmisGoogleAnalyticsConstants.UP_STAFF_ID, String.valueOf(model.getStaffId()));
                                    firebaseAnalytics.setUserProperty(CmisGoogleAnalyticsConstants.UP_STAFF_USER_NAME, loginmodel.getUsername());
                                    patientDownloadHelper.initDownloadProcess(model.getStaffId());
                                }, error -> {
                                    Log.e(TAG, "onError: ",error );
                                    handleError();
                                });
                    }


                } else {

                    handleError();
                    Log.d(TAG, "not success : " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthenticationModel> call, @NonNull Throwable t) {
                handleError();
                Log.d(TAG, "failure : " + t.getLocalizedMessage());
            }

            private void handleError() {
                getAppProgressDialog().safeDismiss();
                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle(getResources().getString(R.string.txt_title_connection_err))
                        .setMessage(getResources().getString(R.string.txt_try))
                        .setPositiveButton(getResources().getString(R.string.txt_btn_ok), null)
                        .show();
            }
        });
    }

    private void goToHomeActivity() {
        if (getAppProgressDialog().isDisplaying()) {
            getAppProgressDialog().safeDismiss();
        }
        Intent intent = new Intent(LoginActivity.this, CustomPassActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
    }

}
