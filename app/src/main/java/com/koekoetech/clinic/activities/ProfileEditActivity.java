package com.koekoetech.clinic.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.koekoetech.clinic.R;
import com.koekoetech.clinic.app.GlideApp;
import com.koekoetech.clinic.fragment.AppProgressDialog;
import com.koekoetech.clinic.helper.Base64Helper;
import com.koekoetech.clinic.helper.CMISConstant;
import com.koekoetech.clinic.helper.DateTimeHelper;
import com.koekoetech.clinic.helper.FileHelper;
import com.koekoetech.clinic.helper.PermissionHelper;
import com.koekoetech.clinic.helper.PhotoUtils;
import com.koekoetech.clinic.helper.ServiceHelper;
import com.koekoetech.clinic.helper.SharePreferenceHelper;
import com.koekoetech.clinic.model.AuthenticationModel;
import com.koekoetech.clinic.model.LocationModel;
import com.koekoetech.clinic.model.StaffProfileModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ZMN on 5/16/17.
 **/

public class ProfileEditActivity extends BaseActivity {

    private static final String TAG = ProfileEditActivity.class.getSimpleName();
    private static final int IMAGE_CROPPER_REQUEST_CODE = 290;

    // [START] Text Input Layouts
    private TextInputLayout inputLayoutName;
    private TextInputLayout inputLayoutUserName;
    private TextInputLayout inputLayoutDateOfBirth;
    private TextInputLayout inputLayoutMobilePhoneNo;
    private TextInputLayout inputLayoutHomePhoneNo;
    private TextInputLayout inputLayoutEmail;
    private TextInputLayout inputLayoutAddress;
    private TextInputLayout inputLayoutNationality;
    private TextInputLayout inputLayoutQualification;
    private TextInputLayout inputLayoutExQualification;
    // [END] Text Input Layouts

    // [START] Edit Texts
    private EditText editTextName;
    private EditText editTextUserName;
    private EditText editTextDateOfBirth;
    private EditText editTextMobilePhoneNo;
    private EditText editTextHomePhoneNo;
    private EditText editTextEmail;
    private EditText editTextAddress;
    private EditText editTextNationality;
    private EditText editTextQualification;
    private EditText editTextExQualification;
    // [END] Edit Texts

    // [START] Spinners
    private Spinner spinnerState;
    private Spinner spinnerTownship;
    private Spinner spinnerEducation;
    private Spinner spinnerReligion;
    // [END] Spinners

    private ImageView imageViewProfilePhoto;
    private FrameLayout frameChangePhoto;

    private Realm realm;
    private ArrayAdapter<String> townshipSpinnerAdapter;
    private StaffProfileModel oldProfileModel;
    private DatePickerDialog datePickerDialog;
    private AppProgressDialog progressDialog;
    private String selectedEducation;
    private String selectedReligion;
    private String selectedTownship;
    private String selectedState;
    private String photoPath;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_profile_edit;
    }

    @Override
    protected void setUpContents(Bundle savedInstanceState) {
        bindViews();
        realm = Realm.getDefaultInstance();

        setupToolbar(true);
        setupToolbarText("Profile Edit");

        setUpSpinners();

        datePickerDialog = prepareDatePickerDialog();

        editTextDateOfBirth.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(editTextDateOfBirth.getWindowToken(), 0);
                }
                showDatePickerDialog();
            }
        });

        editTextDateOfBirth.setOnClickListener(v -> showDatePickerDialog());

        String profileLink = SharePreferenceHelper.getHelper(this).getAuthenticationModel().getPhotoUrl();
        if (!TextUtils.isEmpty(profileLink)) {
            GlideApp.with(this).load(profileLink).placeholder(R.mipmap.sample1).error(R.mipmap.sample1).into(imageViewProfilePhoto);
        }

        fetchProfile();

    }

    private void bindViews() {
        // [START] Text Input Layouts
        inputLayoutName = findViewById(R.id.profileEdit_tilName);
        inputLayoutUserName = findViewById(R.id.profileEdit_tilUserName);
        inputLayoutDateOfBirth = findViewById(R.id.profileEdit_tilDob);
        inputLayoutMobilePhoneNo = findViewById(R.id.profileEdit_tilPhoneMobile);
        inputLayoutHomePhoneNo = findViewById(R.id.profileEdit_tilPhoneHome);
        inputLayoutEmail = findViewById(R.id.profileEdit_tilEmail);
        inputLayoutAddress = findViewById(R.id.profileEdit_tilAddress);
        inputLayoutNationality = findViewById(R.id.profileEdit_tilNationality);
        inputLayoutQualification = findViewById(R.id.profileEdit_tilQualification);
        inputLayoutExQualification = findViewById(R.id.profileEdit_tilExQualification);
        // [END] Text Input Layouts

        // [START] Edit Texts
        editTextName = findViewById(R.id.profileEdit_edtName);
        editTextUserName = findViewById(R.id.profileEdit_edtUserName);
        editTextDateOfBirth = findViewById(R.id.profileEdit_edtDob);
        editTextMobilePhoneNo = findViewById(R.id.profileEdit_edtPhoneMobile);
        editTextHomePhoneNo = findViewById(R.id.profileEdit_edtPhoneHome);
        editTextEmail = findViewById(R.id.profileEdit_edtEmail);
        editTextAddress = findViewById(R.id.profileEdit_edtAddress);
        editTextNationality = findViewById(R.id.profileEdit_edtNationality);
        editTextQualification = findViewById(R.id.profileEdit_edtQualification);
        editTextExQualification = findViewById(R.id.profileEdit_edtExQualification);
        // [END] Edit Texts

        // [START] Spinners
        spinnerState = findViewById(R.id.profileEdit_SpinnerState);
        spinnerTownship = findViewById(R.id.profileEdit_SpinnerTownship);
        spinnerEducation = findViewById(R.id.profileEdit_SpinnerEducation);
        spinnerReligion = findViewById(R.id.profileEdit_SpinnerReligion);
        // [END] Spinners
        imageViewProfilePhoto = findViewById(R.id.profileEdit_ivPhoto);
        frameChangePhoto = findViewById(R.id.profileEdit_frameChangePhoto);

        frameChangePhoto.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= 23) {
                if (PermissionHelper.requiresPermissions(this, PermissionHelper.PERMISSIONS_TO_REQUEST)) {
                    PermissionHelper.handlePermissions(this);
                } else {
                    PhotoUtils.captureImage(this);
                }
            } else {
                PhotoUtils.captureImage(this);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_patient_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_save_profile) {
            submitProfile();
//                if (newProfileUri != null) {
//                    uploadPhoto(newProfileUri);
//                } else {
//                    submitProfile();
//                }
            return false;
        } else if (itemId == android.R.id.home) {
            onBackPressed();
            return false;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: UHCRegistrationModel Code " + resultCode);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PhotoUtils.IMAGE_REQUEST_CODE:
                    Uri resultUri = PhotoUtils.getPickImageResultUri(data, this);
                    Log.d(TAG, "onActivityResult: Picked Imaged Uri : " + resultUri.toString());
                    callCropper(resultUri);
                    break;
                case IMAGE_CROPPER_REQUEST_CODE:
                    if (data != null) {
                        final String filepath = data.getStringExtra(ImageCropperActivity.EXTRA_CROPPED_IMAGE_PATH);
                        Log.d(TAG, "onActivityResult: Chosen Profile Image Path : " + filepath);
                        if (!TextUtils.isEmpty(filepath)) {
                            photoPath = filepath;
                            displayProfilePhoto(photoPath);
                        }
                    }
                    break;
                case PermissionHelper.REQUEST_PERMISSION_SETTING:
                    if (PermissionHelper.requiresPermissions(this, PermissionHelper.PERMISSIONS_TO_REQUEST)) {
                        Toast.makeText(this, "All Required Permissions were not granted!", Toast.LENGTH_SHORT).show();
                    } else {
                        PhotoUtils.captureImage(this);
                    }
                    break;
                default:
                    super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionHelper.REQ_PERMISSIONS) {

            //check if all permissions are granted
            boolean isAllGranted = false;
            for (int r : grantResults) {
                if (r == PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = true;
                } else {
                    isAllGranted = false;
                    break;
                }
            }

            if (isAllGranted) {
                PhotoUtils.captureImage(this);
            } else if (PermissionHelper.shouldShowRationlaeDialog(this)) {
                PermissionHelper.getCameraStorageRationaleDialogBuilder(this).show();
            } else {
                PermissionHelper.getPermissionFailureDialogBuilder(this).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        StaffProfileModel updatedProfile = getFormData();
        boolean profileChanges = oldProfileModel != null && !updatedProfile.equals(oldProfileModel);
        boolean photoChange = !TextUtils.isEmpty(photoPath);
        if (profileChanges || photoChange) {
            new AlertDialog.Builder(this)
                    .setTitle("Unsaved Changes")
                    .setMessage("Exit without saving changes?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> ProfileEditActivity.super.onBackPressed())
                    .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss()).show();
        } else {
            super.onBackPressed();
        }
    }

    private StaffProfileModel getFormData() {

        StaffProfileModel staffProfileModel = new StaffProfileModel();

        if (oldProfileModel != null) {
            staffProfileModel = new StaffProfileModel(oldProfileModel);
        }

        String enteredName = editTextName.getText().toString().trim();
        if (!TextUtils.isEmpty(enteredName)) {
            staffProfileModel.setName(enteredName);
        }

        String enteredPhoneNumber = editTextMobilePhoneNo.getText().toString().trim();
        if (!TextUtils.isEmpty(enteredPhoneNumber)) {
            staffProfileModel.setMobilePhone(enteredPhoneNumber);
        }

        String enteredHomePhoneNumber = editTextHomePhoneNo.getText().toString().trim();
        if (!TextUtils.isEmpty(enteredHomePhoneNumber)) {
            staffProfileModel.setHomePhone(enteredHomePhoneNumber);
        }

        String enteredEmail = editTextEmail.getText().toString().trim();
        if (!TextUtils.isEmpty(enteredEmail)) {
            staffProfileModel.setEmail(enteredEmail);
        }

        String enteredAddress = editTextAddress.getText().toString().trim();
        if (!TextUtils.isEmpty(enteredAddress)) {
            staffProfileModel.setAddress(enteredAddress);
        }

        String enteredUserName = editTextUserName.getText().toString().trim();
        if (!TextUtils.isEmpty(enteredUserName)) {
            staffProfileModel.setUsername(enteredUserName);
        }

        String enteredDOB = editTextDateOfBirth.getText().toString().trim();
        if (!TextUtils.isEmpty(enteredDOB)) {
            String formattedDOB = DateTimeHelper.convertDateFormat(enteredDOB, DateTimeHelper.LOCAL_DATE_FORMAT, DateTimeHelper.SERVER_DATE_TIME_FORMAT);
            if (!TextUtils.isEmpty(formattedDOB)) {
                staffProfileModel.setdOB(formattedDOB);
            }
        }

        if (!TextUtils.isEmpty(selectedState)) {
            staffProfileModel.setDivisionState(selectedState);
        }

        if (!TextUtils.isEmpty(selectedTownship)) {
            staffProfileModel.setTownship(selectedTownship);
        }

        String enteredNationality = editTextNationality.getText().toString().trim();
        if (!TextUtils.isEmpty(enteredNationality)) {
            staffProfileModel.setNationality(enteredNationality);
        }

        if (!TextUtils.isEmpty(selectedReligion)) {
            staffProfileModel.setReligion(selectedReligion);
        }

        if (!TextUtils.isEmpty(selectedEducation)) {
            staffProfileModel.setEducation(selectedEducation);
        }

        String enteredQualification = editTextQualification.getText().toString().trim();
        if (!TextUtils.isEmpty(enteredQualification)) {
            staffProfileModel.setQualification(enteredQualification);
        }

        String enteredExQualification = editTextExQualification.getText().toString().trim();
        if (!TextUtils.isEmpty(enteredExQualification)) {
            staffProfileModel.setExQualification(enteredExQualification);
        }


        if (!TextUtils.isEmpty(photoPath)) {
            File photo = new File(photoPath);
            if (photo.exists()) {
                staffProfileModel.setPhotoId(Base64Helper.fileToBase64(photo));
            }
        }

        return staffProfileModel;
    }

    private DatePickerDialog prepareDatePickerDialog() {
        Calendar initCalendar = Calendar.getInstance();

        String enteredDOB = editTextDateOfBirth.getText().toString().trim();
        if (!TextUtils.isEmpty(enteredDOB)) {
            Date dob = DateTimeHelper.getDateFromString(enteredDOB, DateTimeHelper.LOCAL_DATE_FORMAT);
            if (dob != null) {
                initCalendar.setTime(dob);
            }
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
            Calendar newDate = Calendar.getInstance();
            newDate.set(year, monthOfYear, dayOfMonth);
            String selectedDate = DateTimeHelper.formatDate(newDate.getTime(), DateTimeHelper.LOCAL_DATE_FORMAT);
            editTextDateOfBirth.setText(selectedDate);
        }, initCalendar.get(Calendar.YEAR), initCalendar.get(Calendar.MONTH), initCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        return datePickerDialog;

    }

    private boolean validateForm() {

        if (TextUtils.isEmpty(editTextName.getText().toString().trim())) {
            inputLayoutName.setError("Please Enter Name");
            return false;
        }

        if (TextUtils.isEmpty(editTextUserName.getText().toString().trim())) {
            inputLayoutUserName.setError("Please Enter Username");
            return false;
        }

        return true;
    }

    private void setUpSpinners() {

        ArrayList<String> states = new ArrayList<>();
        states.add("-- Select State --");

        RealmResults<LocationModel> stateDivisionsList = realm
                .where(LocationModel.class)
                .distinct("StateDivision")
                .findAll()
                .sort("StateDivision", Sort.ASCENDING);

        for (LocationModel loc : stateDivisionsList) {
            states.add(loc.getStateDivision());
        }

        spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    selectedState = parent.getItemAtPosition(position).toString();
                    refreshTownshipByState(selectedState);
                    spinnerItemSelect(spinnerTownship, selectedTownship);
                } else {
                    selectedState = "";
                    selectedTownship = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> stateSpinnerAdapter = new ArrayAdapter<>(this, R.layout.item_spinner, states);
        spinnerState.setAdapter(stateSpinnerAdapter);

        townshipSpinnerAdapter = new ArrayAdapter<>(this, R.layout.item_spinner);
        spinnerTownship.setAdapter(townshipSpinnerAdapter);

        spinnerTownship.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    selectedTownship = parent.getItemAtPosition(position).toString();
                } else {
                    selectedTownship = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> eduSpinnerAdapter = new ArrayAdapter<>(this, R.layout.item_spinner, getResources().getStringArray(R.array.education));
        spinnerEducation.setAdapter(eduSpinnerAdapter);
        spinnerEducation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    selectedEducation = parent.getItemAtPosition(position).toString();
                } else {
                    selectedEducation = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> religionAdapter = new ArrayAdapter<>(this, R.layout.item_spinner, getResources().getStringArray(R.array.religions));
        spinnerReligion.setAdapter(religionAdapter);
        spinnerReligion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    selectedReligion = parent.getItemAtPosition(position).toString();
                } else {
                    selectedReligion = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerState.setSelection(0);
        spinnerTownship.setSelection(0);
        spinnerReligion.setSelection(0);
        spinnerEducation.setSelection(0);

    }

    private void refreshTownshipByState(String state) {
        townshipSpinnerAdapter.clear();
        ArrayList<String> townships = new ArrayList<>();
        townships.add("-- Select Township --");
        RealmResults<LocationModel> stateDivision = realm.where(LocationModel.class).equalTo("StateDivision", state, Case.INSENSITIVE).findAll();
        for (LocationModel locationModel : stateDivision) {
            townships.add(locationModel.getTownship());
        }
        townshipSpinnerAdapter.addAll(townships);
        townshipSpinnerAdapter.sort((s1, s2) -> {
            if (s1.equals("-- Select Township --")) {
                return -1;
            } else if (s2.equals("-- Select Township --")) {
                return 1;
            } else {
                return s1.compareTo(s2);
            }
        });
    }

    private void inflateProfileEditForm(StaffProfileModel staffProfileModel) {

        if (staffProfileModel == null) {
            return;
        }

        checkAndSetEditText(editTextName, staffProfileModel.getName());
        checkAndSetEditText(editTextUserName, staffProfileModel.getUsername());

        String dob = "";
        if (!TextUtils.isEmpty(staffProfileModel.getdOB())) {
            String oldDOB = DateTimeHelper.convertDateFormat(staffProfileModel.getdOB(), DateTimeHelper.SERVER_DATE_TIME_FORMAT, DateTimeHelper.LOCAL_DATE_FORMAT);
            if (!TextUtils.isEmpty(oldDOB)) {
                dob = oldDOB;
            }
        }

        checkAndSetEditText(editTextDateOfBirth, dob);
        checkAndSetEditText(editTextMobilePhoneNo, staffProfileModel.getMobilePhone());
        checkAndSetEditText(editTextHomePhoneNo, staffProfileModel.getHomePhone());
        checkAndSetEditText(editTextEmail, staffProfileModel.getEmail());
        checkAndSetEditText(editTextAddress, staffProfileModel.getAddress());
        checkAndSetEditText(editTextNationality, staffProfileModel.getNationality());
        checkAndSetEditText(editTextQualification, staffProfileModel.getQualification());
        checkAndSetEditText(editTextExQualification, staffProfileModel.getExQualification());

        if (!TextUtils.isEmpty(staffProfileModel.getDivisionState())) {
            selectedState = staffProfileModel.getDivisionState();

            if (!TextUtils.isEmpty(staffProfileModel.getTownship())) {
                selectedTownship = staffProfileModel.getTownship();
            }

            spinnerItemSelect(spinnerState, selectedState);
        }

        spinnerItemSelect(spinnerEducation, staffProfileModel.getEducation());
        spinnerItemSelect(spinnerReligion, staffProfileModel.getReligion());

        displayProfilePhoto(staffProfileModel.getPhotoUrl());

    }

    private void checkAndSetEditText(EditText editText, String text) {
        if (editText != null && !TextUtils.isEmpty(text)) {
            editText.setText(text.trim());
        }
    }

    private void spinnerItemSelect(Spinner spinner, Object selection) {

        if (selection == null) {
            return;
        }

        int selectionIndex = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).equals(selection)) {
                selectionIndex = i;
                break;
            }
        }

        spinner.setSelection(selectionIndex);
    }

    private void showDatePickerDialog() {
        if (datePickerDialog == null) {
            datePickerDialog = prepareDatePickerDialog();
        } else if (!datePickerDialog.isShowing()) {
            datePickerDialog.show();
        }
    }

    private void displayProfilePhoto(String photoUrl) {
        Log.d(TAG, "displayProfilePhoto: Profile URL : " + photoUrl);
        GlideApp.with(this)
                .load(photoUrl)
                .placeholder(R.mipmap.sample1)
                .error(R.mipmap.sample1)
                .fallback(R.mipmap.sample1)
                .dontAnimate()
                .into(imageViewProfilePhoto);
    }

    private void fetchProfile() {
        final AppProgressDialog progressDialog = AppProgressDialog.getProgressDialog("Loading Profile");
        int staffId = SharePreferenceHelper.getHelper(this).getAuthenticationModel().getStaffId();
        Log.v("StaffId", staffId + " ");
        Call<StaffProfileModel> callStaffProfileById = ServiceHelper.getClient(this).getStaffById(staffId);
        progressDialog.display(getSupportFragmentManager());
        callStaffProfileById.enqueue(new Callback<StaffProfileModel>() {
            @Override
            public void onResponse(@NonNull Call<StaffProfileModel> call, @NonNull Response<StaffProfileModel> response) {
                progressDialog.safeDismiss();
                if (response.isSuccessful()) {
                    oldProfileModel = response.body();

                    if (oldProfileModel == null) {
                        handleFailure();
                        return;
                    }

                    Log.d(TAG, "Profile Fetched : " + oldProfileModel.toString());

                    inflateProfileEditForm(oldProfileModel);

                } else {
                    handleFailure();
                }
            }

            @Override
            public void onFailure(@NonNull Call<StaffProfileModel> call, @NonNull Throwable t) {
                progressDialog.safeDismiss();
                handleFailure();
                Log.d(TAG, "onFailure: " + t.getLocalizedMessage());
            }

            private void handleFailure() {
                new AlertDialog.Builder(ProfileEditActivity.this)
                        .setTitle("Profile Load Error")
                        .setMessage("Something went wrong while loading profile! Retry Loading Profile?")
                        .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                            dialog.dismiss();
                            finish();
                        })
                        .setPositiveButton("Retry", (dialog, which) -> {
                            dialog.dismiss();
                            fetchProfile();
                        }).create().show();

            }

        });
    }

    private void uploadProfile(final StaffProfileModel staffProfileModel) {

//        if (progressDialog != null && progressDialog.isShowing()) {
//            progressDialog.setMessage("Applying Changes");
//            Button cancelButton = progressDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
//            if (cancelButton != null) {
//                cancelButton.setVisibility(View.GONE);
//            }
//        } else {
//            progressDialog = getProgressDialog(false);
//            progressDialog.show();
//        }

        progressDialog = AppProgressDialog.getProgressDialog("Applying Changes");
        progressDialog.display(getSupportFragmentManager());
        Call<StaffProfileModel> callUpdateStaffProfile = ServiceHelper.getClient(ProfileEditActivity.this).createUpdateStaffProfile(staffProfileModel);
        callUpdateStaffProfile.enqueue(new Callback<StaffProfileModel>() {

            @Override
            public void onResponse(@NonNull Call<StaffProfileModel> call, @NonNull Response<StaffProfileModel> response) {
                Log.d(TAG, "onResponse: " + response.message());
                if (response.isSuccessful()) {

                    if (progressDialog != null && progressDialog.isDisplaying()) {
                        progressDialog.safeDismiss();
                    }

                    StaffProfileModel spm = response.body();
                    Log.d(TAG, "onResponse: " + response.message());
                    if (spm != null) {
                        if (spm.getiD() == CMISConstant.ERR_CODE_USERNAME_ALREADY_EXISTS) {
                            handleFailure(getString(R.string.msg_username_already_taken));
                        } else {
                            updateLocalProfile(spm);

                        }
                    } else {
                        handleFailure("");
                    }
                } else {
                    handleFailure("");
                }
            }

            @Override
            public void onFailure(@NonNull Call<StaffProfileModel> call, @NonNull Throwable t) {
                if (progressDialog != null && progressDialog.isDisplaying()) {
                    progressDialog.safeDismiss();
                }
                handleFailure("");
                t.printStackTrace();
            }

            private void handleFailure(String message) {

                if (TextUtils.isEmpty(message)) {
                    message = "Something went wrong while saving profile! Retry Saving Profile?";
                }

                new AlertDialog.Builder(ProfileEditActivity.this)
                        .setTitle("Profile Save Error")
                        .setMessage(message)
                        .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss())
                        .setPositiveButton("Retry", (dialog, which) -> uploadProfile(staffProfileModel)).create().show();
            }

        });
    }

    private void submitProfile() {
        StaffProfileModel updatedModel = getFormData();
        Log.i(TAG, "submitProfile: " + updatedModel.toString());
        if (!updatedModel.equals(oldProfileModel)) {
            Log.d(TAG, "submitProfile: Profile has been Edited");
            if (validateForm()) {
                uploadProfile(updatedModel);
            }
        } else {
            Toast.makeText(this, "No Changes Found In Your Profile", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void callCropper(Uri sourceImageUri) {
        final Intent cropperIntent = ImageCropperActivity.getCropperIntent(
                getApplicationContext(), sourceImageUri, null);
        startActivityForResult(cropperIntent, IMAGE_CROPPER_REQUEST_CODE);
    }

    private void updateLocalProfile(StaffProfileModel profileModel) {

        SharePreferenceHelper sharePreferenceHelper = SharePreferenceHelper.getHelper(ProfileEditActivity.this);
        AuthenticationModel authenticationModel = sharePreferenceHelper.getAuthenticationModel();
        authenticationModel.setStaffName(profileModel.getName());
        authenticationModel.setPhotoUrl(profileModel.getPhotoUrl());
        sharePreferenceHelper.setAuthenticationModel(authenticationModel);

        realm.executeTransactionAsync(realm -> {
            AuthenticationModel authModelRealm = realm.where(AuthenticationModel.class).findFirst();
            if (authModelRealm != null) {
                authModelRealm.setStaffName(profileModel.getName());
                authModelRealm.setPhotoUrl(profileModel.getPhotoUrl());
            } else {
                Log.w(TAG, "updateLocalProfile: Authentication Model in Realm is NULL");
            }

            if (!TextUtils.isEmpty(photoPath)) {
                final File staffProfilePhoto = new File(photoPath);
                FileHelper.delete(staffProfilePhoto);
            }
        }, () -> {
            Toast.makeText(ProfileEditActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
            finish();
        }, error -> Log.e(TAG, "updateLocalProfile: ", error));

    }

}
