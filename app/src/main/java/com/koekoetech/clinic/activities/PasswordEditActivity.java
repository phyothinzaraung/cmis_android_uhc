package com.koekoetech.clinic.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.koekoetech.clinic.R;
import com.koekoetech.clinic.fragment.AppProgressDialog;
import com.koekoetech.clinic.helper.ServiceHelper;
import com.koekoetech.clinic.helper.SharePreferenceHelper;
import com.koekoetech.clinic.model.StaffProfileModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ZMN on 5/17/17.
 **/

public class PasswordEditActivity extends BaseActivity {

    private TextInputLayout inputLayoutCurrentPassword;
    private TextInputLayout inputLayoutNewPassword;
    private TextInputLayout inputLayoutConfirmNewPassword;
    private EditText editTextCurrentPassword;
    private EditText editTextNewPassword;
    private EditText editTextConfirmNewPassword;

    private StaffProfileModel staffProfileModel;

    public static Matcher getMatcher(String regExPattern, String s) {
        Pattern pattern = Pattern.compile(regExPattern);
        return pattern.matcher(s);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_password_edit;
    }

    @Override
    protected void setUpContents(Bundle savedInstanceState) {
        setupToolbar(true);
        setupToolbarText("Password Edit");

        bindViews();
    }

    private void bindViews(){
        inputLayoutCurrentPassword = findViewById(R.id.passwordEdit_tilCurrentPassword);
        inputLayoutNewPassword = findViewById(R.id.passwordEdit_tilNewPassword);
        inputLayoutConfirmNewPassword = findViewById(R.id.passwordEdit_tilConfirmNewPassword);
        editTextCurrentPassword = findViewById(R.id.passwordEdit_edtCurrentPassword);
        editTextNewPassword = findViewById(R.id.passwordEdit_edtNewPassword);
        editTextConfirmNewPassword = findViewById(R.id.passwordEdit_edtConfirmNewPassword);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_patient_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_save_profile) {
            submitChanges();
            return false;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validateForm() {
        String regExSpecialChar = ".*[^A-Za-z0-9\\s].*";
        String regExNumber = ".*[0-9].*";
        String regExChar = ".*[A-Za-z].*";
        String regExNotStratWithWhiteSpace = "[^\\s].*";
        String regExNotEndWithWhiteSpace = ".*[^\\s]";

        String enteredNewPassword = editTextNewPassword.getText().toString();
        String enteredConfirmPassword = editTextConfirmNewPassword.getText().toString();

        if (TextUtils.isEmpty(editTextCurrentPassword.getText().toString().trim())) {
            inputLayoutCurrentPassword.setError("Please Enter Your Current Password!");
            return false;
        }
        if (TextUtils.isEmpty(enteredNewPassword.trim())) {
            inputLayoutNewPassword.setError("Please Enter Your New Password!");
            return false;
        }
        if (enteredNewPassword.length() < 6) {
            inputLayoutNewPassword.setError("Password should be at least 6 characters");
            return false;
        }

        Matcher matcherSpecialChar = getMatcher(regExSpecialChar, enteredNewPassword);
        if (!matcherSpecialChar.matches()) {
            inputLayoutNewPassword.setError("Password should include special character");
            return false;
        }

        Matcher matcherChar = getMatcher(regExChar, enteredNewPassword);
        if (!matcherChar.matches()) {
            inputLayoutNewPassword.setError("Password should include characters");
            return false;
        }

        Matcher matcherNum = getMatcher(regExNumber, enteredNewPassword);
        if (!matcherNum.matches()) {
            inputLayoutNewPassword.setError("Password should include numbers");
            return false;
        }

        Matcher matcherNotStartWithSpace = getMatcher(regExNotStratWithWhiteSpace, enteredNewPassword);
        if (!matcherNotStartWithSpace.matches()) {
            inputLayoutNewPassword.setError("Password can't start with space");
            return false;
        }

        Matcher matcherNotEndWithSpace = getMatcher(regExNotEndWithWhiteSpace, enteredNewPassword);
        if (!matcherNotEndWithSpace.matches()) {
            inputLayoutNewPassword.setError("Password can't end with space");
            return false;
        }

        if (TextUtils.isEmpty(enteredConfirmPassword.trim())) {
            inputLayoutConfirmNewPassword.setError("Please Retype Your New Password!");
            return false;
        }

        if (!enteredNewPassword.trim().equals(editTextConfirmNewPassword.getText().toString().trim())) {
            inputLayoutConfirmNewPassword.setError("New Password and Confirm New Password must be same!");
            return false;
        }

        if (enteredNewPassword.trim().equals(editTextCurrentPassword.getText().toString().trim())) {
            inputLayoutNewPassword.setError("New Password cannot be same as current password!");
            return false;
        }


        return true;
    }

    private AppProgressDialog prepareProgressDialog(String message) {
        return AppProgressDialog.getProgressDialog(message);
    }

    private void fetchProfile() {
        final AppProgressDialog progressDialog = prepareProgressDialog("Loading Profile");
        int staffId = SharePreferenceHelper.getHelper(this).getAuthenticationModel().getStaffId();
        Call<StaffProfileModel> callStaffProfileById = ServiceHelper.getClient(this).getStaffById(staffId);
        progressDialog.display(getSupportFragmentManager());
        callStaffProfileById.enqueue(new Callback<StaffProfileModel>() {
            @Override
            public void onResponse(@NonNull Call<StaffProfileModel> call, @NonNull Response<StaffProfileModel> response) {
                progressDialog.safeDismiss();
                if (response.isSuccessful()) {
                    staffProfileModel = response.body();
                    savePassword();
                } else {
                    handleFailure();
                }
            }

            @Override
            public void onFailure(@NonNull Call<StaffProfileModel> call, @NonNull Throwable t) {
                progressDialog.safeDismiss();
                handleFailure();
            }

            private void handleFailure() {
                new AlertDialog.Builder(PasswordEditActivity.this)
                        .setTitle("Current Password Check Error")
                        .setMessage("Something went wrong while checking your current password! Retry Checking Again?")
                        .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                            dialog.dismiss();
                            finish();
                        })
                        .setPositiveButton("Retry", (dialog, which) -> fetchProfile()).create().show();

            }

        });
    }

    private void uploadProfile(final StaffProfileModel staffProfileModel) {
        final AppProgressDialog progressDialog = prepareProgressDialog("Saving New Password");
        Call<StaffProfileModel> callUpdateStaffProfile = ServiceHelper.getClient(this).createUpdateStaffProfile(staffProfileModel);
        progressDialog.display(getSupportFragmentManager());
        callUpdateStaffProfile.enqueue(new Callback<StaffProfileModel>() {
            @Override
            public void onResponse(@NonNull Call<StaffProfileModel> call, @NonNull Response<StaffProfileModel> response) {
                progressDialog.safeDismiss();
                if (response.isSuccessful()) {
                    StaffProfileModel profileModel = response.body();
                    if (profileModel != null) {
                        Toast.makeText(PasswordEditActivity.this, "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        handleFailure();
                    }
                } else {
                    handleFailure();
                }
            }

            @Override
            public void onFailure(@NonNull Call<StaffProfileModel> call, @NonNull Throwable t) {
                progressDialog.safeDismiss();
                handleFailure();
            }

            private void handleFailure() {
                new AlertDialog.Builder(PasswordEditActivity.this)
                        .setTitle("Password Save Error")
                        .setMessage("Something went wrong while saving new password! Retry Saving Password?")
                        .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                            dialog.dismiss();
                            finish();
                        })
                        .setPositiveButton("Retry", (dialog, which) -> uploadProfile(staffProfileModel)).create().show();
            }

        });
    }

    private boolean isCurrentPasswordValid() {

        if (staffProfileModel == null) {
            return false;
        }

        if (!editTextCurrentPassword.getText().toString().trim().equals(staffProfileModel.getPassword())) {
            inputLayoutCurrentPassword.setError("Incorrect Current Password!");
            return false;
        }

        return true;

    }

    private void submitChanges() {
        if (validateForm()) {

            if (staffProfileModel == null) {
                fetchProfile();
            } else {
                savePassword();
            }

        }
    }

    private void savePassword() {

        if (isCurrentPasswordValid()) {
            if (editTextNewPassword.getText().toString().trim().equals(staffProfileModel.getPassword())) {
                inputLayoutNewPassword.setError("New Password is same as your current password");
            } else {
                staffProfileModel.setPassword(editTextNewPassword.getText().toString().trim());
                uploadProfile(staffProfileModel);
            }

        }

    }
}
