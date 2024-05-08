package com.koekoetech.clinic.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.koekoetech.clinic.R;
import com.koekoetech.clinic.helper.SharePreferenceHelper;

public class ChangePassCodeActivity extends BaseActivity {

    private TextInputLayout inputLayoutCurrentPassCode;
    private TextInputLayout inputLayoutNewPassCode;
    private TextInputLayout inputLayoutConfirmNewPassCode;
    private EditText editTextCurrentPassCode;
    private EditText editTextNewPassCode;
    private EditText editTextConfirmNewPassCode;

    private SharePreferenceHelper share;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_change_passcode;
    }

    @Override
    protected void setUpContents(Bundle savedInstanceState) {
        setupToolbar(true);
        setupToolbarText("Passcode Edit");

        bindViews();

        init();
    }

    private void bindViews(){
        inputLayoutCurrentPassCode = findViewById(R.id.ChangePassCode_tilCurrentPassCode);
        inputLayoutNewPassCode = findViewById(R.id.ChangePassCode_tilNewPassCode);
        inputLayoutConfirmNewPassCode = findViewById(R.id.ChangePassCode_tilConfirmNewPassCode);
        editTextCurrentPassCode = findViewById(R.id.ChangePassCode_edtCurrentPassCode);
        editTextNewPassCode = findViewById(R.id.ChangePassCode_edtNewPassCode);
        editTextConfirmNewPassCode = findViewById(R.id.ChangePassCode_edtConfirmNewPassCode);
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

    private void init() {
        share = SharePreferenceHelper.getHelper(ChangePassCodeActivity.this);
    }

    private void submitChanges() {
        if (validateForm()) {
            String passcode = editTextNewPassCode.getText().toString().trim();
            share.setPasscode(passcode);
            Toast.makeText(ChangePassCodeActivity.this, "Successfully Changed Passcode", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private boolean validateForm() {

        String enteredCurrentPassCode = editTextCurrentPassCode.getText().toString().trim();
        String enteredNewPassCode = editTextNewPassCode.getText().toString().trim();
        String enteredNewConfirmPassCode = editTextConfirmNewPassCode.getText().toString().trim();

        inputLayoutCurrentPassCode.setErrorEnabled(false);
        inputLayoutCurrentPassCode.setError("");
        inputLayoutNewPassCode.setErrorEnabled(false);
        inputLayoutNewPassCode.setError("");
        inputLayoutConfirmNewPassCode.setErrorEnabled(false);
        inputLayoutConfirmNewPassCode.setError("");

        if (TextUtils.isEmpty(enteredCurrentPassCode)) {
            inputLayoutCurrentPassCode.setErrorEnabled(true);
            inputLayoutCurrentPassCode.setError("Please enter your current passcode!");
            return false;
        }

        if(!TextUtils.equals(share.getPasscode(),enteredCurrentPassCode)){
            inputLayoutCurrentPassCode.setErrorEnabled(true);
            inputLayoutCurrentPassCode.setError("Incorrect current password!");
            return false;
        }

        if (TextUtils.isEmpty(enteredNewPassCode)) {
            inputLayoutNewPassCode.setErrorEnabled(true);
            inputLayoutNewPassCode.setError("Please enter your new passcode!");
            return false;
        }

        if(TextUtils.getTrimmedLength(enteredNewPassCode) != 4){
            inputLayoutNewPassCode.setErrorEnabled(true);
            inputLayoutNewPassCode.setError("Please enter 4 digits passcode!");
            return false;
        }

        if (TextUtils.isEmpty(enteredNewConfirmPassCode)) {
            inputLayoutConfirmNewPassCode.setErrorEnabled(true);
            inputLayoutConfirmNewPassCode.setError("Please retype your new passcode!");
            return false;
        }

        if (!TextUtils.equals(enteredNewPassCode,enteredNewConfirmPassCode)) {
            inputLayoutConfirmNewPassCode.setErrorEnabled(true);
            inputLayoutConfirmNewPassCode.setError("New passcode and confirm new passcode must be same!");
            return false;
        }

        if(TextUtils.equals(share.getPasscode(), enteredNewPassCode)){
            inputLayoutNewPassCode.setErrorEnabled(true);
            inputLayoutNewPassCode.setError("Current passcode and new passcode must be different!");
            return false;
        }

        return true;
    }

}
