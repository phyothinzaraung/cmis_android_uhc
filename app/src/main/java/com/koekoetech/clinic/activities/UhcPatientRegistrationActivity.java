package com.koekoetech.clinic.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.koekoetech.clinic.R;
import com.koekoetech.clinic.app.GlideApp;
import com.koekoetech.clinic.fragment.AppProgressDialog;
import com.koekoetech.clinic.helper.CMISConstant;
import com.koekoetech.clinic.helper.CodeGen;
import com.koekoetech.clinic.helper.DateTimeHelper;
import com.koekoetech.clinic.helper.FileHelper;
import com.koekoetech.clinic.helper.MultiSelectSpinner;
import com.koekoetech.clinic.helper.MyanmarZawgyiConverter;
import com.koekoetech.clinic.helper.PermissionHelper;
import com.koekoetech.clinic.helper.PhotoUtils;
import com.koekoetech.clinic.helper.Rabbit;
import com.koekoetech.clinic.helper.SharePreferenceHelper;
import com.koekoetech.clinic.model.AuthenticationModel;
import com.koekoetech.clinic.model.LocationModel;
import com.koekoetech.clinic.model.UhcPatient;
import com.koekoetech.clinic.view.SegmentedGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

import static com.koekoetech.clinic.helper.CmisGoogleAnalyticsConstants.ACTION_CAPTURE_PATIENT_IMAGE;
import static com.koekoetech.clinic.helper.CmisGoogleAnalyticsConstants.ACTION_EDIT_PATIENT_INFO;
import static com.koekoetech.clinic.helper.CmisGoogleAnalyticsConstants.ACTION_READ_BAR_CODE;
import static com.koekoetech.clinic.helper.CmisGoogleAnalyticsConstants.ACTION_REGISTER_NEW_PATIENT;
import static com.koekoetech.clinic.helper.CmisGoogleAnalyticsConstants.ACTION_SELECT_IMMUNIZATION;

/**
 * Created by ZMN on 5/5/18.
 **/
public class UhcPatientRegistrationActivity extends BaseActivity {

    private static final String TAG = UhcPatientRegistrationActivity.class.getSimpleName();
    private static final String EXTRA_EDIT_PATIENT_CODE = "PatientCodeExtra";
    private static final String EXTRA_IS_EDITING = "IsEditingForm";
    private static final int IMAGE_CROPPER_REQUEST_CODE = 9872;
    private static final int IMMUNIZATION_REQUEST_CODE = 3847;
    private static final int REQ_BAR_CODE = 4930;

    private SegmentedGroup segmentedGroupRegistration;
    private RadioButton radioButtonRegularRegistration;
    private RadioButton radioButtonUHCRegistration;
    private FrameLayout frameLayoutPhoto;
    private ImageView imageViewPhoto;
    //UIC, HH, HH Member, Registration Date
    private TextInputLayout textInputLayoutUID;
    private EditText edtUID;
    private TextInputLayout textInputLayoutHHCode;
    private EditText edtHHCode;
    private TextInputLayout textInputLayoutHHMemberCode;
    private EditText edtHHMemberCode;
    private TextInputLayout textInputLayoutRegistrationDate;
    private EditText edtRegistrationDate;

    //Demographic

    private TextInputLayout textInputLayoutNameInEnglish;
    private EditText edtNameInEnglish;
    private TextInputLayout textInputLayoutNameInBurmese;
    private EditText edtNameInBurmese;
    private RadioButton radioButtonGenderMale;
    private RadioButton radioButtonGenderFemale;
    private TextView txtMaritalStatus;
    private FrameLayout frameLayoutMaritalStatus;
    private Spinner spinnerMaritalStatus;
    //Age, DOB,
    private RadioButton radioButtonAgeTypeYear;
    private RadioButton radioButtonAgeTypeMonth;
    private TextInputLayout textInputLayoutDOB;
    private EditText edtDOB;
    private TextInputLayout textInputLayoutPatientAge;
    private EditText edtPatientAge;
    //Person, Phone
    private TextInputLayout textInputLayoutPhone;
    private EditText edtPhone;
    private TextInputLayout textInputLayoutContactPerson;
    private EditText edtContactPerson;
    //Address
    private Spinner spinnerState;
    private Spinner spinnerTownship;
    private TextInputLayout textInputLayoutPatientAddress;
    private EditText edtPatientAddress;
    private TextInputLayout textInputLayoutOccupation;
    private EditText edtOccupation;

    // Relevant Medical Information:

    private TextInputLayout textInputLayoutBodyWeight;
    private EditText edtBodyWeight;
    private TextInputLayout textInputLayoutHeightInFt;
    private EditText edtHeightInFt;
    private TextInputLayout textInputLayoutHeightInInches;
    private EditText edtHeightInInches;
    private TextInputLayout textInputLayoutMuac;
    private EditText edtMuac;
    private TextInputLayout textInputLayoutImmunizationStatus;
    private EditText edtImmunizationStatus;
    //BP, RBS, Allergies
    private TextInputLayout textInputLayoutSymbolicBP;
    private EditText edtSystolicBP;
    private TextInputLayout textInputLayoutDiastolicBP;
    private EditText edtDiastolicBP;
    private TextInputLayout textInputLayoutRBS;
    private EditText edtRBS;
    private TextInputLayout textInputLayoutKnownAllergies;
    private EditText edtKnownAllergies;
    //Pregnancy || Contraceptive Status
    private TextView txtPregnancyStatus;
    private Spinner spinnerPregnancyStatus;
    private FrameLayout frameLayoutCurrentContraceptiveStatus;
    private TextView txtCurrentContraceptiveStatus;
    private Spinner spinnerCurrentContraceptiveStatus;
    // Risk Factor
    private TextView txtRiskFactor;
    private FrameLayout frameLayoutRiskFactor;
    private MultiSelectSpinner multiSelectionSpinnerRiskFactor;
    // Previous Medical, Surgical, Family History
    private TextInputLayout textInputLayoutPreviousMedicalHistory;
    private EditText edtPreviousMedicalHistory;
    private TextInputLayout textInputLayoutPreviousSurgicalHistory;
    private EditText edtPreviousSurgicalHistory;
    private TextInputLayout textInputLayoutRelevantFamilyHistory;
    private EditText edtRelevantFamilyHistory;

    private RelativeLayout layoutSave;

    private ArrayAdapter<String> townshipSpinnerAdapter;

    private Realm realm;

    private String selectedState;
    private String selectedTownship;
    private String selectedGender = "Male";
    private String selectedMaritalStatus;
    private String selectedPregnancyStatus;
    private String selectedContraceptiveStatus;
    private String selectedImmunizations;

    private String localPhotoPath;

    private boolean shouldIgnoreAgeTypeChange;
    private boolean shouldIgnoreAgeTextChange;
    private boolean isEditing;

    private UhcPatient patientEditSource;
    private AuthenticationModel authenticationModel;
    private AppProgressDialog progressDialog;


    public static Intent getCreateIntent(Context context) {
        Intent intent = new Intent(context, UhcPatientRegistrationActivity.class);
        intent.putExtra(EXTRA_IS_EDITING, false);
        return intent;
    }

    public static Intent getEditIntent(Context context, String patientCode) {
        Intent intent = new Intent(context, UhcPatientRegistrationActivity.class);
        intent.putExtra(EXTRA_IS_EDITING, true);
        intent.putExtra(EXTRA_EDIT_PATIENT_CODE, patientCode);
        return intent;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_registration;
    }

    @Override
    protected void setUpContents(Bundle savedInstanceState) {

        allFindViewbyId();

        setupToolbar(true);

        setupToolbarText("Patient Registration");

        init();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "]");
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PhotoUtils.IMAGE_REQUEST_CODE:
                    Uri resultUri = PhotoUtils.getPickImageResultUri(data, this);
                    Log.d(TAG, "onActivityResult: Picked Imaged Uri : " + resultUri.toString());
                    callCropper(resultUri);
                    break;
                case IMAGE_CROPPER_REQUEST_CODE:
                    if (data != null) {
//                        Uri newAddedIMGUri = data.getParcelableExtra("NewAddedIMGUri");
                        String filepath = data.getStringExtra(ImageCropperActivity.EXTRA_CROPPED_IMAGE_PATH);
                        if (!TextUtils.isEmpty(filepath)) {
                            sendActionAnalytics(ACTION_CAPTURE_PATIENT_IMAGE);
                            Log.v("filePath", filepath);
                            loadProfilePhoto(filepath);
                            localPhotoPath = filepath;
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
                case IMMUNIZATION_REQUEST_CODE:
                    if (data != null) {
                        selectedImmunizations = data.getStringExtra(ImmunizationListActivity.RESULT_SELECTED_IMMUNIZATIONS);
                        String immunizationStatus = data.getStringExtra(ImmunizationListActivity.RESULT_STATUS);
                        if (!TextUtils.isEmpty(immunizationStatus)) {
                            sendActionAnalytics(ACTION_SELECT_IMMUNIZATION);
                            edtImmunizationStatus.setText(immunizationStatus);
                        }
                    }
                    break;
                case REQ_BAR_CODE:
                    if (data != null) {
                        String barCode = data.getStringExtra("uic_code");
                        Log.d(TAG, "onActivityResult: UID Bar Code : " + barCode);
                        if (!TextUtils.isEmpty(barCode)) {
                            sendActionAnalytics(ACTION_READ_BAR_CODE);
                            if (barCode.length() > 12) {
                                String subBarCode = barCode.substring(0, 12);
                                Log.d(TAG, "onActivityResult: Sub BarCode UID : " + subBarCode);
                                edtUID.setText(subBarCode);
                            } else {
                                edtUID.setText(barCode);
                            }
                        }
                    }
                    break;
                default:
                    super.onActivityResult(requestCode, resultCode, data);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setTitle("Confirm Exit")
                .setMessage("Press Ok to exist from this form otherwise press Cancel to continue.")
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                })
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss()).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return false;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void init() {
        realm = Realm.getDefaultInstance();

        progressDialog = AppProgressDialog.getProgressDialog("Saving Patient Data");

        setUpSpinners();

        setUpEditTexts();

        manageAgeBasedFormFields(-5, "Year");

        manageGenderBasedFormFields(true);

        radioButtonRegularRegistration.setOnCheckedChangeListener((buttonView, isChecked) -> setUpRegTypeForm(isChecked));

        imageViewPhoto.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= 23) {
                if (PermissionHelper.requiresPermissions(UhcPatientRegistrationActivity.this, PermissionHelper.PERMISSIONS_TO_REQUEST)) {
                    PermissionHelper.handlePermissions(UhcPatientRegistrationActivity.this);
                } else {
                    PhotoUtils.captureImage(UhcPatientRegistrationActivity.this);
                }
            } else {
                PhotoUtils.captureImage(UhcPatientRegistrationActivity.this);
            }
        });

        layoutSave.setOnClickListener(v -> {
            Log.d(TAG, "init: Save Clicked");
            if (validateForm()) {
                savePatient(getFormData());
            }
        });

        CompoundButton.OnCheckedChangeListener ageTypeChangeListener = (buttonView, isChecked) -> {
            if (isChecked && !shouldIgnoreAgeTypeChange) {
                shouldIgnoreAgeTextChange = true;
                edtDOB.setText("");
                edtPatientAge.setText("");
                shouldIgnoreAgeTextChange = false;
            }
        };

        radioButtonAgeTypeYear.setOnCheckedChangeListener(ageTypeChangeListener);

        radioButtonAgeTypeMonth.setOnCheckedChangeListener(ageTypeChangeListener);

        radioButtonGenderMale.setOnCheckedChangeListener((buttonView, isChecked) -> {
            selectedGender = isChecked ? "Male" : "Female";
            manageGenderBasedFormFields(isChecked);
        });

        isEditing = getIntent().getBooleanExtra(EXTRA_IS_EDITING, false);

        authenticationModel = realm.where(AuthenticationModel.class).findFirst();

        if (authenticationModel == null) {
            new AlertDialog.Builder(this)
                    .setTitle("Unknown Error")
                    .setMessage("Cannot get logged in provider information. Please kindly inform app vendor about this error!")
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        dialog.dismiss();
                        finish();
                    })
                    .show();
            return;
        }

//        if (isEditing) {
//            String editPatientCode = getIntent().getStringExtra(EXTRA_EDIT_PATIENT_CODE);
//            if (!TextUtils.isEmpty(editPatientCode)) {
//                patientEditSource = realm.where(UhcPatient.class)
//                        .equalTo("patientCode", editPatientCode)
//                        .findFirst();
//                if (patientEditSource != null) {
//                    inflateForm();
//                } else {
//                    isEditing = false;
//                    setUpDefaultUI();
//                }
//            } else {
//                isEditing = false;
//                setUpDefaultUI();
//            }
//        } else {
//            setUpDefaultUI();
//        }

        if (isEditing) {
            final String editPatientCode = getIntent().getStringExtra(EXTRA_EDIT_PATIENT_CODE);
            if (!TextUtils.isEmpty(editPatientCode)) {
                patientEditSource = realm.where(UhcPatient.class)
                        .equalTo("patientCode", editPatientCode)
                        .findFirst();
            }
        }

        if (patientEditSource != null) {
            inflateForm(patientEditSource);
        } else {
            isEditing = false;
            setUpDefaultUI();
        }

    }

    private void setUpEditTexts() {

        // UID Edit Text
        edtUID.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= 23) {
                if (PermissionHelper.requiresPermissions(UhcPatientRegistrationActivity.this, PermissionHelper.PERMISSIONS_TO_REQUEST)) {
                    PermissionHelper.handlePermissions(UhcPatientRegistrationActivity.this);
                } else {
                    Intent intent = new Intent(UhcPatientRegistrationActivity.this, BarcodeScannerActivity.class);
                    startActivityForResult(intent, REQ_BAR_CODE);
                }
            } else {
                Intent intent = new Intent(UhcPatientRegistrationActivity.this, BarcodeScannerActivity.class);
                startActivityForResult(intent, REQ_BAR_CODE);
            }
        });
        edtUID.setInputType(InputType.TYPE_NULL);
        edtUID.setFocusableInTouchMode(false);

        // Registration Date
        edtRegistrationDate.setInputType(InputType.TYPE_NULL);
        edtRegistrationDate.setFocusableInTouchMode(false);
        edtRegistrationDate.setOnClickListener(v -> {
            String enteredRegDate = edtRegistrationDate.getText().toString().trim();
            Date initDate = new Date();
            if (!TextUtils.isEmpty(enteredRegDate) && DateTimeHelper.validateDateFormat(enteredRegDate, DateTimeHelper.LOCAL_DATE_FORMAT)) {
                Date parsedDated = DateTimeHelper.getDateFromString(enteredRegDate, DateTimeHelper.LOCAL_DATE_FORMAT);
                if (parsedDated != null) {
                    initDate = parsedDated;
                }
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(initDate);
            DatePickerDialog datePickerDialog = new DatePickerDialog(UhcPatientRegistrationActivity.this, DatePickerDialog.THEME_HOLO_LIGHT,
                    (view, year, monthOfYear, dayOfMonth) -> {

                        String strDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        edtRegistrationDate.setText(strDate);
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
            datePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackground(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackground(new ColorDrawable(Color.TRANSPARENT));
        });

        // DOB
        edtDOB.setInputType(InputType.TYPE_NULL);
        edtDOB.setFocusableInTouchMode(false);
        edtDOB.setOnClickListener(v -> {
            Date initDate = new Date();
            String enteredDob = edtDOB.getText().toString().trim();
            if (!TextUtils.isEmpty(enteredDob) && DateTimeHelper.validateDateFormat(enteredDob, DateTimeHelper.LOCAL_DATE_FORMAT)) {
                Date parsedDate = DateTimeHelper.getDateFromString(enteredDob, DateTimeHelper.LOCAL_DATE_FORMAT);
                if (parsedDate != null) {
                    initDate = parsedDate;
                }
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(initDate);
            DatePickerDialog datePickerDialog = new DatePickerDialog(UhcPatientRegistrationActivity.this, DatePickerDialog.THEME_HOLO_LIGHT,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        String strDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        edtDOB.setText(strDate);
                        if (radioButtonAgeTypeYear.isChecked()) {
                            onDobSelectedForAgeTypeYear(year, monthOfYear, dayOfMonth);
                        } else if (radioButtonAgeTypeMonth.isChecked()) {
                            onDobSelectedForAgeTypeMonth(year, monthOfYear, dayOfMonth);
                        }

                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
            datePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackground(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackground(new ColorDrawable(Color.TRANSPARENT));
        });

        // Patient Age
        edtPatientAge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!shouldIgnoreAgeTextChange) {
                    if (!TextUtils.isEmpty(s.toString().trim())) {
                        try {
                            int enteredAge = Integer.parseInt(s.toString().trim());
                            if (radioButtonAgeTypeYear.isChecked()) {
                                onAgeEnteredForAgeTypeYear(enteredAge);
                            } else if (radioButtonAgeTypeMonth.isChecked()) {
                                onAgeEnteredForAgeTypeMonth(enteredAge);
                            }
                        } catch (Exception e) {
                            textInputLayoutPatientAge.setErrorEnabled(true);
                            textInputLayoutPatientAge.setError("Invalid Age");
                        }
                    }
                }
            }
        });

        edtBodyWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString().trim())) {
                    try {
                        double enteredWeight = Double.parseDouble(s.toString().trim());
                        if (enteredWeight > 500 || enteredWeight < 0) {
                            textInputLayoutBodyWeight.setErrorEnabled(true);
                            textInputLayoutBodyWeight.setError("Invalid patient body weight!");
                        } else {
                            textInputLayoutBodyWeight.setErrorEnabled(false);
                            textInputLayoutBodyWeight.setError("");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        textInputLayoutBodyWeight.setErrorEnabled(true);
                        textInputLayoutBodyWeight.setError("Invalid patient body weight!");
                    }
                }
            }
        });

        edtHeightInFt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString().trim())) {
                    try {
                        double enteredHeightInFt = Double.parseDouble(s.toString().trim());
                        if (enteredHeightInFt > 8 || enteredHeightInFt < 0) {
                            textInputLayoutHeightInFt.setErrorEnabled(true);
                            textInputLayoutHeightInFt.setError("Invalid patient height!");
                        } else {
                            textInputLayoutHeightInFt.setErrorEnabled(false);
                            textInputLayoutHeightInFt.setError("");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        textInputLayoutHeightInFt.setErrorEnabled(true);
                        textInputLayoutHeightInFt.setError("Invalid patient height!");
                    }
                }
            }
        });

        edtHeightInInches.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString().trim())) {
                    try {
                        double enteredHeightInInches = Double.parseDouble(s.toString().trim());
                        if (enteredHeightInInches > 11.99 || enteredHeightInInches < 0) {
                            textInputLayoutHeightInInches.setErrorEnabled(true);
                            textInputLayoutHeightInInches.setError("Invalid patient height!");
                        } else {
                            textInputLayoutHeightInInches.setErrorEnabled(false);
                            textInputLayoutHeightInInches.setError("");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        textInputLayoutHeightInInches.setErrorEnabled(true);
                        textInputLayoutHeightInInches.setError("Invalid patient height!");
                    }
                }
            }
        });

        // Immunization List Edit Text
        edtImmunizationStatus.setOnClickListener(v -> {
            boolean shouldAllow = false;

            String inputAge = edtPatientAge.getText().toString().trim();
            if (!TextUtils.isEmpty(inputAge)) {
                try {
                    int enteredAge = Integer.parseInt(inputAge);
                    String ageType = getSelectedAgeType();
                    shouldAllow = enteredAge <= 2 && ageType.equals("Year") || enteredAge <= 24 && ageType.equals("Month");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (shouldAllow) {
                Intent intent;
                if (!TextUtils.isEmpty(selectedImmunizations)) {
                    intent = ImmunizationListActivity.getEditIntent(getApplicationContext(), selectedImmunizations);
                } else {
                    intent = ImmunizationListActivity.getNewIntent(getApplicationContext());
                }
                UhcPatientRegistrationActivity.this.startActivityForResult(intent, IMMUNIZATION_REQUEST_CODE);
            } else {
                Toast.makeText(UhcPatientRegistrationActivity.this, getString(R.string.immunizationNotAllowed), Toast.LENGTH_SHORT).show();
            }
        });
        edtImmunizationStatus.setInputType(InputType.TYPE_NULL);
        edtImmunizationStatus.setFocusableInTouchMode(false);

        edtKnownAllergies.setText(R.string.patient_reg_nkda);

    }

    @NonNull
    private String guessAgeByYear(int enteredAge) {
        Calendar cal = Calendar.getInstance();
        int bYear = cal.get(Calendar.YEAR) - enteredAge;
        return "15/6/" + bYear;
    }

    private String guessAgeByMonth(int enteredAge) {
        Calendar cal = Calendar.getInstance();
        int bYear = cal.get(Calendar.YEAR) - (enteredAge / 12);
        int bMonth = (cal.get(Calendar.MONTH) + 1) - (enteredAge % 12);

        if (bMonth < 1) {
            bYear -= 1;
            bMonth += 12;
        }

        return "15/" + bMonth + "/" + bYear;
    }

    private void setUpSpinners() {

        Log.d(TAG, "setUpSpinners() called");

        /*// Gender Spinner
        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Gender onItemSelected() called with: position = [" + position + "], id = [" + id + "]");
                if (position > 0) {
                    selectedGender = parent.getItemAtPosition(position).toString();
                    boolean isMale = !TextUtils.isEmpty(selectedGender) && selectedGender.equals("Male");
                    manageGenderBasedFormFields(isMale);
                } else {
                    selectedGender = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        // Martial Status
        spinnerMaritalStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Marital Status onItemSelected() called with: position = [" + position + "], id = [" + id + "]");
                if (position > 0) {
                    selectedMaritalStatus = parent.getItemAtPosition(position).toString();
                } else {
                    selectedMaritalStatus = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        RealmResults<LocationModel> locations = realm
                .where(LocationModel.class)
                .distinct("StateDivision")
                .findAll()
                .sort("StateDivision", Sort.ASCENDING);

        // State/Division Spinner
        List<String> statesList = new ArrayList<>();
        statesList.add("-- Select State --");
        for (LocationModel locationModel : locations) {
            statesList.add(locationModel.getStateDivision());
        }

        ArrayAdapter<String> stateSpinnerAdapter = new ArrayAdapter<>(this, R.layout.item_spinner, statesList);
        spinnerState.setAdapter(stateSpinnerAdapter);

        spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "State onItemSelected() called with: position = [" + position + "], id = [" + id + "]");
                if (position > 0) {
                    selectedState = parent.getItemAtPosition(position).toString();
                    getTownshipListByStateDivision(selectedState);
                    selectValue(spinnerTownship, selectedTownship);
                } else {
                    selectedState = "";
                    selectedTownship = "";
                    townshipSpinnerAdapter.clear();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Township Spinner
        townshipSpinnerAdapter = new ArrayAdapter<>(this, R.layout.item_spinner);
        spinnerTownship.setAdapter(townshipSpinnerAdapter);
        spinnerTownship.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Township onItemSelected() called with: position = [" + position + "], id = [" + id + "]");
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

        // Pregnancy Status
        spinnerPregnancyStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Pregnancy Status onItemSelected() called with: position = [" + position + "], id = [" + id + "]");
                if (position > 0) {
                    selectedPregnancyStatus = parent.getItemAtPosition(position).toString();
                } else {
                    selectedPregnancyStatus = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Current Contraception Status
        spinnerCurrentContraceptiveStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Current Contraceptive Status onItemSelected() called with: position = [" + position + "], id = [" + id + "]");
                selectedContraceptiveStatus = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Risk Factor
        String[] array = getResources().getStringArray(R.array.risk_factors);
        multiSelectionSpinnerRiskFactor.hasNoneOption(true);
        multiSelectionSpinnerRiskFactor.setItems(array);
        multiSelectionSpinnerRiskFactor.setSelection(new int[]{0});


    }

    private void setUpDefaultUI() {
        radioButtonRegularRegistration.setChecked(true);
        radioButtonAgeTypeYear.setChecked(true);
        radioButtonGenderMale.setChecked(true);
        setHint();

        if (!isEditing) {
            if (authenticationModel.isPermitted(CMISConstant.FORM_CARD_HOLDER_REG)) {
                segmentedGroupRegistration.setVisibility(View.VISIBLE);
            } else {
                segmentedGroupRegistration.setVisibility(View.GONE);
            }

            String providerDivisionState = authenticationModel.getDivisionState();
            if (!TextUtils.isEmpty(providerDivisionState)) {
                Log.d(TAG, "setUpDefaultUI: Provider's Division State" + providerDivisionState);
                selectValue(spinnerState, providerDivisionState);
            } else {
                Log.d(TAG, "setUpDefaultUI: Provider doesn't have default State/Division.");
                spinnerState.setSelection(0);
            }

            String providerTsp = authenticationModel.getTownship();
            if (!TextUtils.isEmpty(providerTsp)) {
                selectedTownship = providerTsp;
            }

        } else {
            segmentedGroupRegistration.setVisibility(View.VISIBLE);
        }

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        String selectedDate = mDay + "/" + (mMonth + 1) + "/" + mYear;
        edtRegistrationDate.setText(selectedDate);

    }

    private void setUpRegTypeForm(boolean isNormalPatient) {
        if (isNormalPatient) {
            setHint();
            textInputLayoutHHCode.setVisibility(View.GONE);
            textInputLayoutHHMemberCode.setVisibility(View.GONE);
            textInputLayoutUID.setVisibility(View.GONE);
            textInputLayoutNameInBurmese.setVisibility(View.GONE);
            txtMaritalStatus.setVisibility(View.GONE);
            frameLayoutMaritalStatus.setVisibility(View.GONE);
            textInputLayoutContactPerson.setVisibility(View.GONE);
            textInputLayoutOccupation.setVisibility(View.GONE);
            textInputLayoutBodyWeight.setVisibility(View.GONE);
            textInputLayoutHeightInFt.setVisibility(View.GONE);
            textInputLayoutHeightInInches.setVisibility(View.GONE);
            textInputLayoutMuac.setVisibility(View.GONE);
            textInputLayoutImmunizationStatus.setVisibility(View.GONE);
            textInputLayoutSymbolicBP.setVisibility(View.GONE);
            textInputLayoutDiastolicBP.setVisibility(View.GONE);
            textInputLayoutRBS.setVisibility(View.GONE);
            txtCurrentContraceptiveStatus.setVisibility(View.GONE);
            frameLayoutCurrentContraceptiveStatus.setVisibility(View.GONE);
            txtRiskFactor.setVisibility(View.GONE);
            frameLayoutRiskFactor.setVisibility(View.GONE);
            textInputLayoutPreviousMedicalHistory.setVisibility(View.GONE);
            textInputLayoutPreviousSurgicalHistory.setVisibility(View.GONE);
            textInputLayoutRelevantFamilyHistory.setVisibility(View.GONE);
        } else {
            setUHCHint();
            textInputLayoutHHCode.setVisibility(View.VISIBLE);
            textInputLayoutHHMemberCode.setVisibility(View.VISIBLE);
            textInputLayoutUID.setVisibility(View.VISIBLE);
            textInputLayoutNameInBurmese.setVisibility(View.VISIBLE);
            txtMaritalStatus.setVisibility(View.VISIBLE);
            frameLayoutMaritalStatus.setVisibility(View.VISIBLE);
            textInputLayoutContactPerson.setVisibility(View.VISIBLE);
            textInputLayoutOccupation.setVisibility(View.VISIBLE);
            textInputLayoutBodyWeight.setVisibility(View.VISIBLE);
            textInputLayoutHeightInFt.setVisibility(View.VISIBLE);
            textInputLayoutHeightInInches.setVisibility(View.VISIBLE);
            textInputLayoutMuac.setVisibility(View.VISIBLE);
            textInputLayoutImmunizationStatus.setVisibility(View.VISIBLE);
            textInputLayoutSymbolicBP.setVisibility(View.VISIBLE);
            textInputLayoutDiastolicBP.setVisibility(View.VISIBLE);
            textInputLayoutRBS.setVisibility(View.VISIBLE);
            txtCurrentContraceptiveStatus.setVisibility(View.VISIBLE);
            frameLayoutCurrentContraceptiveStatus.setVisibility(View.VISIBLE);
            txtRiskFactor.setVisibility(View.VISIBLE);
            frameLayoutRiskFactor.setVisibility(View.VISIBLE);
            textInputLayoutPreviousMedicalHistory.setVisibility(View.VISIBLE);
            textInputLayoutPreviousSurgicalHistory.setVisibility(View.VISIBLE);
            textInputLayoutRelevantFamilyHistory.setVisibility(View.VISIBLE);
        }
    }

    private void getTownshipListByStateDivision(String state) {
        Log.d(TAG, "getTownshipListByStateDivision() called with: state = [" + state + "]");
        townshipSpinnerAdapter.clear();

        Set<String> townshipNames = new HashSet<>();
        townshipNames.add("-- Select Township --");

        if (!TextUtils.isEmpty(state)) {
            ArrayList<LocationModel> locations = new ArrayList<>(realm.where(LocationModel.class).equalTo("StateDivision", state, Case.INSENSITIVE).findAll());
            for (LocationModel townshipList : locations) {
                townshipNames.add(townshipList.getTownship());
            }
        }

        townshipSpinnerAdapter.addAll(townshipNames);
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

    private void setHint() {
        textInputLayoutHHCode.setHint("HH Code");
        textInputLayoutHHMemberCode.setHint("HH Member Code");
        textInputLayoutRegistrationDate.setHint("Registration Date");
        textInputLayoutNameInEnglish.setHint("Name*");
        textInputLayoutNameInBurmese.setHint("Name In Burmese");
        textInputLayoutPatientAddress.setHint("Street Number/Name");
        textInputLayoutPhone.setHint("Phone");
        textInputLayoutContactPerson.setHint("Contact Person");
        textInputLayoutBodyWeight.setHint("Weight in lb");
        textInputLayoutHeightInFt.setHint("Height in feet");
        textInputLayoutHeightInInches.setHint("Height in inches");
        textInputLayoutMuac.setHint("MUAC (under 5 years)");
        textInputLayoutImmunizationStatus.setHint("Immunization Status (under 2 years)");
        textInputLayoutSymbolicBP.setHint("Systolic BP (over 30 years)");
        textInputLayoutDiastolicBP.setHint("Diastolic BP (over 30 years)");
        textInputLayoutKnownAllergies.setHint("Known Allergies");
        txtPregnancyStatus.setText(R.string.patient_reg_pregnancy_status);
        txtRiskFactor.setText(R.string.patient_reg_risk_factor);
    }

    private void setUHCHint() {
        textInputLayoutHHCode.setHint("HH Code*");
        textInputLayoutHHMemberCode.setHint("HH Member Code*");
        textInputLayoutRegistrationDate.setHint("Registration Date");
        textInputLayoutNameInEnglish.setHint("Name In English*");
        textInputLayoutNameInBurmese.setHint("Name In Burmese");
        textInputLayoutPatientAddress.setHint("Street Number/Name*");
        textInputLayoutContactPerson.setHint("Contact Person*");
        textInputLayoutPhone.setHint("Phone");
        textInputLayoutBodyWeight.setHint("Weight in lb*");
        textInputLayoutHeightInFt.setHint("Height in feet*");
        textInputLayoutHeightInInches.setHint("Height in inches*");
        textInputLayoutMuac.setHint("MUAC*(under 5 years)");
        textInputLayoutImmunizationStatus.setHint("Immunization Status*(under 2 years)");
        textInputLayoutSymbolicBP.setHint("Systolic BP*(over 30 years)");
        textInputLayoutDiastolicBP.setHint("Diastolic BP*(over 30 years)");
        textInputLayoutKnownAllergies.setHint("Known Allergies*");
        txtPregnancyStatus.setText(R.string.patient_reg_pregnancy_status_req);
        txtRiskFactor.setText(R.string.patient_reg_risk_factor_req);
    }

    private void inflateForm(@NonNull final UhcPatient patient) {

        boolean isUhc = patient.isUHC();
        radioButtonUHCRegistration.setChecked(isUhc);
        radioButtonRegularRegistration.setChecked(!isUhc);
        setUpRegTypeForm(!isUhc);

        String photo;
        if (!TextUtils.isEmpty(patient.getLocal_filepath())) {
            photo = patient.getLocal_filepath();
        } else {
            photo = patient.getPhotoUrl();
        }

        loadProfilePhoto(photo);

        edtUID.setText(handleNull(patient.getUicCode()));
        edtUID.setOnClickListener(null);

        edtHHCode.setText(handleNull(patient.getHhCode()));
        if (!TextUtils.isEmpty(patient.getHhCode())) {
            edtHHCode.setInputType(InputType.TYPE_NULL);
        }

        edtHHMemberCode.setText(handleNull(patient.gethHMemberCode()));
        if (!TextUtils.isEmpty(patient.gethHMemberCode())) {
            edtHHMemberCode.setInputType(InputType.TYPE_NULL);
        }

        if (!TextUtils.isEmpty(patient.getRegistrationDate())) {
            if (DateTimeHelper.validateDateFormat(patient.getRegistrationDate(), DateTimeHelper.SERVER_DATE_TIME_FORMAT)) {
                edtRegistrationDate.setText(handleNull(DateTimeHelper.convertDateFormat(patient.getRegistrationDate(), DateTimeHelper.SERVER_DATE_TIME_FORMAT, DateTimeHelper.LOCAL_DATE_FORMAT)));
            } else if (DateTimeHelper.validateDateFormat(patient.getRegistrationDate(), DateTimeHelper.LOCAL_DATE_FORMAT)) {
                edtRegistrationDate.setText(handleNull(patient.getRegistrationDate()));
            } else {
                edtRegistrationDate.setText(handleNull(DateTimeHelper.formatDate(new Date(), DateTimeHelper.LOCAL_DATE_FORMAT)));
            }
        }

        edtNameInEnglish.setText(handleNull(patient.getNameInEnglish()));

        String deviceLanguage = SharePreferenceHelper.getHelper(this).getDeviceLanguage();
        String nameInMyanmar = patient.getNameInMyanmar();
        Log.d(TAG, "inflateForm: Name In Myanmar : " + nameInMyanmar);

        if (TextUtils.equals(deviceLanguage, CMISConstant.DEVICE_LANG_ZAWGYI)) {
            nameInMyanmar = Rabbit.uni2zg(nameInMyanmar);
            Log.d(TAG, "inflateForm: Name In Myanmar ZawGyi : " + nameInMyanmar);
        }

        edtNameInBurmese.setText(nameInMyanmar);

        if (!TextUtils.isEmpty(patient.getGender())) {
            selectedGender = patient.getGender();
            radioButtonGenderMale.setChecked(selectedGender.equals("Male"));
            radioButtonGenderFemale.setChecked(selectedGender.equals("Female"));
        }

        if (!TextUtils.isEmpty(patient.getMaritalStatus())) {
            selectedMaritalStatus = patient.getMaritalStatus();
            selectValue(spinnerMaritalStatus, patient.getMaritalStatus());
        }

        if (!TextUtils.isEmpty(patient.getAgeType())) {
            if (patient.getAgeType().equalsIgnoreCase("Year")) {
                radioButtonAgeTypeYear.setChecked(true);
            } else if (patient.getAgeType().equalsIgnoreCase("Month")) {
                radioButtonAgeTypeMonth.setChecked(true);
            }
        }

        if (!TextUtils.isEmpty(patient.getDateOfBirth())) {
            if (DateTimeHelper.validateDateFormat(patient.getDateOfBirth(), DateTimeHelper.SERVER_DATE_TIME_12_HR_FORMAT)) {
                edtDOB.setText(handleNull(DateTimeHelper.convertDateFormat(patient.getDateOfBirth(), DateTimeHelper.SERVER_DATE_TIME_12_HR_FORMAT, DateTimeHelper.LOCAL_DATE_FORMAT)));
            } else if (DateTimeHelper.validateDateFormat(patient.getDateOfBirth(), DateTimeHelper.SERVER_DATE_TIME_FORMAT)) {
                edtDOB.setText(handleNull(DateTimeHelper.convertDateFormat(patient.getDateOfBirth(), DateTimeHelper.SERVER_DATE_TIME_FORMAT, DateTimeHelper.LOCAL_DATE_FORMAT)));
            } else if (DateTimeHelper.validateDateFormat(patient.getDateOfBirth(), DateTimeHelper.LOCAL_DATE_FORMAT)) {
                edtDOB.setText(handleNull(patient.getDateOfBirth()));
            }
        }

        shouldIgnoreAgeTextChange = true;
        String sourceAgeRaw = handleNull(String.valueOf(patient.getAge()));
        edtPatientAge.setText(sourceAgeRaw);
        shouldIgnoreAgeTextChange = false;

        try {
            int sourceAge = Integer.parseInt(sourceAgeRaw);
            if (getSelectedAgeType().equalsIgnoreCase("Year")) {
                manageAgeBasedFormFields(sourceAge, "Year");
            } else if (getSelectedAgeType().equalsIgnoreCase("Month")) {
                manageAgeBasedFormFields(sourceAge, "Month");
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        edtPhone.setText(handleNull(patient.getPhone()));
        edtContactPerson.setText(handleNull(patient.getContactPerson()));

        if (!TextUtils.isEmpty(patient.getStateDivision())) {
            selectValue(spinnerState, patient.getStateDivision());
            selectedState = patient.getStateDivision();
            getTownshipListByStateDivision(patient.getStateDivision());
        }

        if (!TextUtils.isEmpty(patient.getTownship())) {
            selectedTownship = patient.getTownship();
            selectValue(spinnerTownship, patient.getTownship());
        }

        edtPatientAddress.setText(handleNull(patient.getAddress()));

        edtOccupation.setText(handleNull(patient.getOccupation()));

        if (patient.getBodyWeight() == 0) {
            edtBodyWeight.setText("0.0");
        } else {
            edtBodyWeight.setText(String.valueOf(patient.getBodyWeight()));
        }

        getHeightFtAndInches(patient.getHeightIn_in());

        edtMuac.setText(handleNull(patient.getMUAC()));

        edtImmunizationStatus.setText(handleNull(patient.getImmunizationStatus()));
        selectedImmunizations = patient.getImmunizations();

        edtSystolicBP.setText(handleNull(patient.getSystolicBP()));

        edtDiastolicBP.setText(handleNull(patient.getDiastolicBP()));

        if (patient.getRbs() == null) {
            edtRBS.setText("0.0");
        } else {
            edtRBS.setText(String.valueOf(patient.getRbs()));
        }

        edtKnownAllergies.setText(handleNull(patient.getKnownAllergies()));

        if (patient.isPregnancy()) {
            selectValue(spinnerPregnancyStatus, "Pregnant");
        } else {
            selectValue(spinnerPregnancyStatus, "Not Pregnant");
        }

        if (!TextUtils.isEmpty(patient.getCurrentContraceptionStatus())) {
            selectValue(spinnerCurrentContraceptiveStatus, patient.getCurrentContraceptionStatus());
        }

        if (!TextUtils.isEmpty(patient.getRiskGroup())) {
            ArrayList<String> rfs = new ArrayList<>();
            for (String rf : patient.getRiskGroup().split(",")) {
                rfs.add(rf.trim());
            }
            multiSelectionSpinnerRiskFactor.setSelection(rfs);
        }

        edtPreviousMedicalHistory.setText(handleNull(patient.getPreviousMedicalHistory()));
        edtPreviousSurgicalHistory.setText(handleNull(patient.getPreviousSurgicalHistory()));
        edtRelevantFamilyHistory.setText(handleNull(patient.getRelevantFamilyHistory()));

    }

    private void loadProfilePhoto(final String url) {
        GlideApp.with(this)
                .load(url)
                .placeholder(R.mipmap.sample1)
                .dontAnimate()
                .error(R.mipmap.sample1)
                .into(imageViewPhoto);
    }

    private void savePatient(@NonNull final UhcPatient uhcPatient) {
        progressDialog.display(getSupportFragmentManager());
        realm.executeTransactionAsync(realm -> {
            final boolean isUIThread = Looper.myLooper() == Looper.getMainLooper();
            Log.d(TAG, "savePatient: is UI Thread : " + isUIThread);
            try {
                final String profilePhotoPath = uhcPatient.getLocal_filepath();
                if (!TextUtils.isEmpty(profilePhotoPath) && !TextUtils.isEmpty(localPhotoPath)) {
                    final File profilePhoto = new File(profilePhotoPath);
                    if (profilePhoto.exists()) {
                        final File storageDir = FileHelper.getStorageDir(getApplicationContext());
                        final File photoDir = new File(storageDir, uhcPatient.getPatientCode());
                        FileHelper.createDir(photoDir);
                        final File patientProfilePhoto = new File(photoDir, profilePhoto.getName());
                        final boolean isRenamed = profilePhoto.renameTo(patientProfilePhoto);
                        if (isRenamed) {
                            uhcPatient.setLocal_filepath(patientProfilePhoto.getAbsolutePath());
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            realm.copyToRealmOrUpdate(uhcPatient);
        }, () -> {
            progressDialog.safeDismiss();
            sendActionAnalytics(isEditing ? ACTION_EDIT_PATIENT_INFO : ACTION_REGISTER_NEW_PATIENT);
            Toast.makeText(UhcPatientRegistrationActivity.this, "Patient saved successfully", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(UhcPatientRegistrationActivity.this, HomeActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
            finish();
        }, error -> {
            progressDialog.safeDismiss();
            Toast.makeText(UhcPatientRegistrationActivity.this, "Failed to save patient!", Toast.LENGTH_SHORT).show();
        });

    }

    private void getHeightFtAndInches(double inches) {
        if (inches > 0) {
            int inFeet = (int) Math.round(inches / 12);
            int inInches = (int) Math.round(inches % 12);

            Log.d(TAG, "getHeightFtAndInches: Feet = " + inFeet + "inches = " + inInches);

            edtHeightInFt.setText(String.valueOf(inFeet));
            edtHeightInInches.setText(String.valueOf(inInches));
        } else {
            edtHeightInFt.setText("0");
            edtHeightInInches.setText("0");
        }
    }

    private void selectValue(Spinner spinner, String value) {
        Log.d(TAG, "selectValue() called with: value = [" + value + "]");

        if (TextUtils.isEmpty(value)) {
            spinner.setSelection(0);
        }

        int selectionIndex = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(value)) {
                selectionIndex = i;
                break;
            }
        }

        spinner.setSelection(selectionIndex);
    }

    private void callCropper(Uri sourceImageUri) {
        final Intent cropperIntent = ImageCropperActivity.getCropperIntent(
                getApplicationContext(), sourceImageUri, FileHelper.PROFILE_DIR);
        startActivityForResult(cropperIntent, IMAGE_CROPPER_REQUEST_CODE);
    }

    private void manageAgeBasedFormFields(int age, String ageType) {
        if (!TextUtils.isEmpty(ageType)) {
            if (ageType.equalsIgnoreCase("Year")) {
                edtMuac.setEnabled(age > 0 && age < 5);
                edtSystolicBP.setEnabled(age >= 30);
                edtDiastolicBP.setEnabled(age >= 30);
            } else if (ageType.equalsIgnoreCase("Month")) {
                edtMuac.setEnabled(age > 0 && age < (5 * 12));
                edtSystolicBP.setEnabled(age >= (30 * 12));
                edtDiastolicBP.setEnabled(age >= (30 * 12));
            }
            textInputLayoutMuac.setErrorEnabled(false);
            textInputLayoutSymbolicBP.setErrorEnabled(false);
            textInputLayoutDiastolicBP.setErrorEnabled(false);
        }
    }

    private void manageGenderBasedFormFields(boolean isMale) {
        spinnerPregnancyStatus.setClickable(!isMale);
        spinnerPregnancyStatus.setEnabled(!isMale);
        spinnerCurrentContraceptiveStatus.setClickable(!isMale);
        spinnerCurrentContraceptiveStatus.setEnabled(!isMale);
    }

    private void onDobSelectedForAgeTypeYear(int year, int monthOfYear, int dayOfMonth) {
        Log.d(TAG, "onDobSelectedForAgeTypeYear() called with: year = [" + year + "], monthOfYear = [" + monthOfYear + "], dayOfMonth = [" + dayOfMonth + "]");
        shouldIgnoreAgeTextChange = true;
        int ageInYears = getAgeInYears(year, monthOfYear, dayOfMonth);

        if (ageInYears < 1) {
            Log.d(TAG, "onDobSelectedForAgeTypeYear: Selected Birthday has age less than 1 year");
            shouldIgnoreAgeTypeChange = true;
            radioButtonAgeTypeYear.setChecked(false);
            radioButtonAgeTypeMonth.setChecked(true);
            shouldIgnoreAgeTypeChange = false;
            onDobSelectedForAgeTypeMonth(year, monthOfYear, dayOfMonth);
            return;
        }

        edtPatientAge.setText(String.valueOf(ageInYears));
        shouldIgnoreAgeTextChange = false;
        manageAgeBasedFormFields(ageInYears, "Year");
    }

    private void onDobSelectedForAgeTypeMonth(int year, int monthOfYear, int dayOfMonth) {
        Log.d(TAG, "onDobSelectedForAgeTypeMonth() called with: year = [" + year + "], monthOfYear = [" + monthOfYear + "], dayOfMonth = [" + dayOfMonth + "]");
        shouldIgnoreAgeTextChange = true;
        int ageInMonth = getAgeInMonth(year, monthOfYear, dayOfMonth);

        if (ageInMonth > 24) {
            Log.d(TAG, "onDobSelectedForAgeTypeMonth: Selected Birthday has age more than 24 months");
            shouldIgnoreAgeTypeChange = true;
            radioButtonAgeTypeMonth.setChecked(false);
            radioButtonAgeTypeYear.setChecked(true);
            shouldIgnoreAgeTypeChange = false;
            onDobSelectedForAgeTypeYear(year, monthOfYear, dayOfMonth);
            return;
        }

        edtPatientAge.setText(String.valueOf(ageInMonth));
        shouldIgnoreAgeTextChange = false;
        manageAgeBasedFormFields(ageInMonth, "Month");
    }

    private void onAgeEnteredForAgeTypeYear(int enteredAge) {
        shouldIgnoreAgeTypeChange = true;
        boolean shouldUpdateAgeField = !radioButtonAgeTypeYear.isChecked();
        radioButtonAgeTypeYear.setChecked(true);
        radioButtonAgeTypeMonth.setChecked(false);
        shouldIgnoreAgeTypeChange = false;
        if (enteredAge > 120) {
            textInputLayoutPatientAge.setErrorEnabled(true);
            textInputLayoutPatientAge.setError("The age is too old.");
        } else if (enteredAge < 0) {
            textInputLayoutPatientAge.setErrorEnabled(true);
            textInputLayoutPatientAge.setError("The age is invalid.");
        } else if (enteredAge == 0) {
            onAgeEnteredForAgeTypeMonth(enteredAge);
        } else {
            textInputLayoutPatientAge.setErrorEnabled(false);
            textInputLayoutPatientAge.setError("");
            String estimatedDOB = guessAgeByYear(enteredAge);
            edtDOB.setText(estimatedDOB);
            if (shouldUpdateAgeField) {
                edtPatientAge.setText(String.valueOf(enteredAge));
            }
            manageAgeBasedFormFields(enteredAge, "Year");
        }
    }

    private void onAgeEnteredForAgeTypeMonth(int enteredAge) {
        shouldIgnoreAgeTypeChange = true;
        boolean shouldUpdateAgeField = !radioButtonAgeTypeMonth.isChecked();
        radioButtonAgeTypeYear.setChecked(false);
        radioButtonAgeTypeMonth.setChecked(true);
        shouldIgnoreAgeTypeChange = false;
        if (enteredAge > 24) {
            onAgeEnteredForAgeTypeYear(enteredAge / 12);
        } else {
            textInputLayoutPatientAge.setErrorEnabled(false);
            textInputLayoutPatientAge.setError("");
            String estimatedDOB = guessAgeByMonth(enteredAge);
            edtDOB.setText(estimatedDOB);
            if (shouldUpdateAgeField) {
                edtPatientAge.setText(String.valueOf(enteredAge));
            }
            manageAgeBasedFormFields(enteredAge, "Month");
        }
    }

    private int getDivisionCodeByStateDivision(String state) {
        ArrayList<LocationModel> divisionList = new ArrayList<>(realm.where(LocationModel.class).equalTo("StateDivision", state, Case.INSENSITIVE).findAll());
        int divisionCode = 0;
        for (LocationModel division : divisionList) {
            divisionCode = division.getDivisionCode();
        }
        return divisionCode;
    }

    private int getAgeInYears(int year, int month, int day) {
        Log.d(TAG, "getAgeInYears() called with: year = [" + year + "], month = [" + month + "], day = [" + day + "]");
        Calendar cal = Calendar.getInstance();
        int thisYear, thisMonth, thisDay, age;

        thisYear = cal.get(Calendar.YEAR);
        thisMonth = cal.get(Calendar.MONTH);
        thisDay = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(year, month, day);

        age = thisYear - cal.get(Calendar.YEAR);

        if (thisMonth < cal.get(Calendar.MONTH)
                || ((thisMonth == cal.get(Calendar.MONTH)) && (thisDay < cal.get(Calendar.DAY_OF_MONTH)))) {
            --age;
        }
        Log.d(TAG, "getAgeInYears() returned: " + age);
        return age;
    }

    private int getAgeInMonth(int year, int month, int day) {
        Log.d(TAG, "getAgeInMonth() called with: year = [" + year + "], month = [" + month + "], day = [" + day + "]");
        Calendar dob = Calendar.getInstance();
        dob.set(year, month, day);

        Calendar today = Calendar.getInstance();
        int monthsBetween = 0;
        int dateDiff = today.get(Calendar.DAY_OF_MONTH) - dob.get(Calendar.DAY_OF_MONTH);

        if (dateDiff < 0) {
//            int borrow = today.getActualMaximum(Calendar.DAY_OF_MONTH);
//            dateDiff = (today.get(Calendar.DAY_OF_MONTH) + borrow) - dob.get(Calendar.DAY_OF_MONTH);
            monthsBetween--;

//            if (dateDiff > 0) {
//                monthsBetween++;
//            }
        }
//        else {
//            monthsBetween++;
//        }
        monthsBetween += today.get(Calendar.MONTH) - dob.get(Calendar.MONTH);
        monthsBetween += (today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)) * 12;

        Log.d(TAG, "getAgeInMonth() returned: " + monthsBetween);
        return monthsBetween;
    }

    private int getTownshipCodeByTownship(String township) {
        LocationModel locationModel = realm.where(LocationModel.class).equalTo("Township", township, Case.INSENSITIVE).findFirst();
        int townshipCode = 0;
        if (locationModel != null) {
            townshipCode = locationModel.getTownshipCode();
        }
        return townshipCode;
    }

    private boolean validateForm() {

        // Common Validations

        if (TextUtils.isEmpty(edtNameInEnglish.getText().toString().trim())) {
            textInputLayoutNameInEnglish.setErrorEnabled(true);
            textInputLayoutNameInEnglish.setError("Please Enter Name In English");
            return false;
        } else {
            textInputLayoutNameInEnglish.setErrorEnabled(false);
            textInputLayoutNameInEnglish.setError("");
        }

        String ageInput = edtPatientAge.getText().toString().trim();
        if (TextUtils.isEmpty(ageInput)) {
            textInputLayoutPatientAge.setErrorEnabled(true);
            textInputLayoutPatientAge.setError("Please enter Age or select Date Of Birth");
            return false;
        } else {
            if (!isValidInt(ageInput)) {
                textInputLayoutPatientAge.setErrorEnabled(true);
                textInputLayoutPatientAge.setError("Please enter valid age");
                return false;
            } else {
                try {
                    int enteredAge = Integer.parseInt(ageInput);
                    if (enteredAge <= 0 || enteredAge > 120) {
                        textInputLayoutPatientAge.setErrorEnabled(true);
                        textInputLayoutPatientAge.setError("Please enter valid age");
                        return false;
                    } else {
                        textInputLayoutPatientAge.setErrorEnabled(false);
                        textInputLayoutPatientAge.setError("");
                    }
                } catch (NumberFormatException e) {
                    textInputLayoutPatientAge.setErrorEnabled(true);
                    textInputLayoutPatientAge.setError("Please enter valid age");
                    return false;
                }
            }
        }

        if (TextUtils.isEmpty(selectedState)) {
            Toast.makeText(getApplicationContext(), "Please select state", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(selectedTownship)) {
            Toast.makeText(getApplicationContext(), "Please select township", Toast.LENGTH_SHORT).show();
            return false;
        }

        // UHC Card Holder Specific Validations
        if (radioButtonUHCRegistration.isChecked()) {

            if (!isEditing) {
                String enteredUID = edtUID.getText().toString().trim();
                String uidToCheck = TextUtils.isEmpty(enteredUID) ? get12DigitUIDCode() : enteredUID;
                UhcPatient patient = realm.where(UhcPatient.class).equalTo("uicCode", uidToCheck).findFirst();
                if (patient != null) {
                    Toast.makeText(getApplicationContext(), "UIC Code is duplicated.", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }

            String enteredHHCode = edtHHCode.getText().toString().trim();
            if (TextUtils.isEmpty(enteredHHCode)) {
                textInputLayoutHHCode.setErrorEnabled(true);
                textInputLayoutHHCode.setError("Please enter HH Code");
                return false;
            } else {
                textInputLayoutHHCode.setErrorEnabled(false);
                textInputLayoutHHCode.setError("");
            }

            if (TextUtils.isEmpty(enteredHHCode) || enteredHHCode.length() > 6) {
                textInputLayoutHHCode.setErrorEnabled(true);
                textInputLayoutHHCode.setError("HHCode must have up to 6 digits");
                return false;
            } else {
                textInputLayoutHHCode.setErrorEnabled(false);
                textInputLayoutHHCode.setError("");
            }

            String enteredHHMemberCode = edtHHMemberCode.getText().toString().trim();
            if (TextUtils.isEmpty(enteredHHMemberCode)) {
                textInputLayoutHHMemberCode.setErrorEnabled(true);
                textInputLayoutHHMemberCode.setError("Please enter HH Member Code");
                return false;
            } else {
                textInputLayoutHHMemberCode.setErrorEnabled(false);
                textInputLayoutHHMemberCode.setError("");
            }

            if (TextUtils.isEmpty(enteredHHMemberCode) || enteredHHMemberCode.length() > 2) {
                textInputLayoutHHMemberCode.setErrorEnabled(true);
                textInputLayoutHHMemberCode.setError("HH Member must have up to 2 digits");
                return false;
            } else {
                textInputLayoutHHMemberCode.setErrorEnabled(false);
                textInputLayoutHHMemberCode.setError("");
            }

            if (!isEditing) {
                int hhDuplicatePatient = (int) realm.where(UhcPatient.class).equalTo("hhCode", enteredHHCode).equalTo("hHMemberCode", enteredHHMemberCode).count();
                if (hhDuplicatePatient > 0) {
                    Toast.makeText(getApplicationContext(), "Duplicate HHCode and HHMemberCode.", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }

            if (TextUtils.isEmpty(edtContactPerson.getText().toString().trim())) {
                textInputLayoutContactPerson.setErrorEnabled(true);
                textInputLayoutContactPerson.setError("Please enter contact person");
                return false;
            } else {
                textInputLayoutContactPerson.setErrorEnabled(false);
                textInputLayoutContactPerson.setError("");
            }

            if (TextUtils.isEmpty(edtPatientAddress.getText().toString())) {
                textInputLayoutPatientAddress.setErrorEnabled(true);
                textInputLayoutPatientAddress.setError("Please enter patient address.");
                return false;
            } else {
                textInputLayoutPatientAddress.setErrorEnabled(false);
                textInputLayoutPatientAddress.setError("");
            }

            int enteredAge = Integer.parseInt(ageInput);

            if (radioButtonAgeTypeMonth.isChecked()) {
                enteredAge = enteredAge / 12;
            }

            if (enteredAge >= 5) {

                String bodyWeightInput = edtBodyWeight.getText().toString().trim();
                if (TextUtils.isEmpty(bodyWeightInput)) {
                    textInputLayoutBodyWeight.setErrorEnabled(true);
                    textInputLayoutBodyWeight.setError("Please enter weight");
                    return false;
                } else {
                    if (isInValidDouble(bodyWeightInput)) {
                        textInputLayoutBodyWeight.setErrorEnabled(true);
                        textInputLayoutBodyWeight.setError("Please enter valid weight");
                        return false;
                    } else {
                        try {
                            double enteredBodyWeight = Double.parseDouble(bodyWeightInput);
                            if (enteredBodyWeight <= 0 || enteredBodyWeight >= 500) {
                                textInputLayoutBodyWeight.setErrorEnabled(true);
                                textInputLayoutBodyWeight.setError("Please enter valid weight");
                                return false;
                            } else {
                                textInputLayoutBodyWeight.setErrorEnabled(false);
                                textInputLayoutBodyWeight.setError("");
                            }
                        } catch (Exception e) {
                            textInputLayoutBodyWeight.setErrorEnabled(true);
                            textInputLayoutBodyWeight.setError("Please enter valid weight");
                            return false;
                        }
                    }
                }

                String heightFtInput = edtHeightInFt.getText().toString().trim();
                if (TextUtils.isEmpty(heightFtInput)) {
                    textInputLayoutHeightInFt.setErrorEnabled(true);
                    textInputLayoutHeightInFt.setError("Please enter height in feet");
                    return false;
                } else {
                    if (isInValidDouble(heightFtInput)) {
                        textInputLayoutHeightInFt.setErrorEnabled(true);
                        textInputLayoutHeightInFt.setError("Please enter valid height in feet");
                        return false;
                    } else {
                        try {
                            double enteredHeightInFt = Double.parseDouble(heightFtInput);
                            if (enteredHeightInFt <= 0 || enteredHeightInFt > 8) {
                                textInputLayoutHeightInFt.setErrorEnabled(true);
                                textInputLayoutHeightInFt.setError("Please enter valid height in feet");
                                return false;
                            } else {
                                textInputLayoutHeightInFt.setErrorEnabled(false);
                                textInputLayoutHeightInFt.setError("");
                            }
                        } catch (NumberFormatException e) {
                            textInputLayoutHeightInFt.setErrorEnabled(true);
                            textInputLayoutHeightInFt.setError("Please enter valid height in feet");
                            return false;
                        }
                    }
                }

                String heightInInchesInput = edtHeightInInches.getText().toString().trim();
                if (TextUtils.isEmpty(heightInInchesInput)) {
                    textInputLayoutHeightInInches.setErrorEnabled(true);
                    textInputLayoutHeightInInches.setError("Please enter height in inches");
                    return false;
                } else {
                    if (isInValidDouble(heightInInchesInput)) {
                        textInputLayoutHeightInInches.setErrorEnabled(true);
                        textInputLayoutHeightInInches.setError("Please enter valid height in inches");
                        return false;
                    } else {
                        try {
                            double enteredHeightInInches = Double.parseDouble(heightInInchesInput);
                            if (enteredHeightInInches < 0 || enteredHeightInInches > 11.99) {
                                textInputLayoutHeightInInches.setErrorEnabled(true);
                                textInputLayoutHeightInInches.setError("Please enter valid height in inches");
                                return false;
                            } else {
                                textInputLayoutHeightInInches.setErrorEnabled(false);
                                textInputLayoutHeightInInches.setError("");
                            }
                        } catch (NumberFormatException e) {
                            textInputLayoutHeightInInches.setErrorEnabled(true);
                            textInputLayoutHeightInInches.setError("Please enter valid height in inches");
                            return false;
                        }

                    }
                }

            }

            if (enteredAge > 30) {
                if (TextUtils.isEmpty(edtSystolicBP.getText().toString().trim())) {
                    textInputLayoutSymbolicBP.setErrorEnabled(true);
                    textInputLayoutSymbolicBP.setError("Please enter systolic BP");
                    return false;
                } else {
                    textInputLayoutSymbolicBP.setErrorEnabled(false);
                    textInputLayoutSymbolicBP.setError("");
                }

                if (TextUtils.isEmpty(edtDiastolicBP.getText().toString().trim())) {
                    textInputLayoutDiastolicBP.setErrorEnabled(true);
                    textInputLayoutDiastolicBP.setError("Please enter diastolic BP");
                    return false;
                } else {
                    textInputLayoutDiastolicBP.setErrorEnabled(false);
                    textInputLayoutDiastolicBP.setError("");
                }

            }


            if (enteredAge < 2) {
                if (TextUtils.isEmpty(edtImmunizationStatus.getText().toString().trim())) {
                    textInputLayoutImmunizationStatus.setErrorEnabled(true);
                    textInputLayoutImmunizationStatus.setError("Please select Immunizations");
                    return false;
                } else {
                    textInputLayoutImmunizationStatus.setErrorEnabled(false);
                    textInputLayoutImmunizationStatus.setError("");
                }
            }

            if (enteredAge < 5) {
                if (TextUtils.isEmpty(edtMuac.getText().toString().trim())) {
                    textInputLayoutMuac.setErrorEnabled(true);
                    textInputLayoutMuac.setError("Please enter MUAC");
                    return false;
                } else {
                    textInputLayoutMuac.setErrorEnabled(false);
                    textInputLayoutMuac.setError("");
                }
            }

        }

        return true;
    }

    private boolean isValidInt(String integerValue) {
        try {
            Integer.parseInt(integerValue);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isInValidDouble(String doubleValue) {
        try {
            Double.parseDouble(doubleValue);
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    private UhcPatient getFormData() {
        UhcPatient uhcPatient;

        if (isEditing && patientEditSource != null) {
            uhcPatient = new UhcPatient(patientEditSource);
        } else {
            uhcPatient = new UhcPatient();
            uhcPatient.setLocalId(UUID.randomUUID().toString());
            uhcPatient.setPatientCode(getPatientCode());
            uhcPatient.setDeleted(false);
            uhcPatient.setCreatedBy(authenticationModel.getStaffId());
            uhcPatient.setFacilityCode(handleNull(authenticationModel.getFacilityCode()));
            uhcPatient.setFacilityName(handleNull(authenticationModel.getFacilityName()));
            uhcPatient.setFacilityType(handleNull(authenticationModel.getFacilityType()));
            uhcPatient.setCreatedTime(DateTimeHelper.formatDate(new Date(), DateTimeHelper.SERVER_DATE_TIME_FORMAT));
        }

        boolean isUhcPermitted = authenticationModel.isPermitted(CMISConstant.FORM_CARD_HOLDER_REG);
        if (isUhcPermitted || isEditing) {
            uhcPatient.setUHC(radioButtonUHCRegistration.isChecked());
        } else {
            uhcPatient.setUHC(false);
        }

        if (!TextUtils.isEmpty(localPhotoPath)) {
            uhcPatient.setLocal_filepath(localPhotoPath);
        }

        if (radioButtonUHCRegistration.isChecked()) {
            if (!TextUtils.isEmpty(edtUID.getText().toString())) {
                uhcPatient.setUicCode(edtUID.getText().toString());
            } else {
                uhcPatient.setUicCode(get12DigitUIDCode());
            }
        } else {
            uhcPatient.setUicCode(null);
        }

        uhcPatient.setHhCode(handleNull(edtHHCode.getText().toString().trim()));
        uhcPatient.sethHMemberCode(handleNull(edtHHMemberCode.getText().toString().trim()));
        String convertedRegDate = DateTimeHelper.convertDateFormat(edtRegistrationDate.getText().toString().trim(), DateTimeHelper.LOCAL_DATE_FORMAT, DateTimeHelper.SERVER_DATE_TIME_FORMAT);
        Log.d(TAG, "getFormData: Converted Registration Date : " + convertedRegDate);
        uhcPatient.setRegistrationDate(handleNull(convertedRegDate));
        uhcPatient.setNameInEnglish(handleNull(edtNameInEnglish.getText().toString().trim()));

        String patientNameInMyanmar = handleNull(edtNameInBurmese.getText().toString().trim());

        if (!TextUtils.isEmpty(patientNameInMyanmar) && MyanmarZawgyiConverter.isZawgyiEncoded(patientNameInMyanmar)) {
            // ZawGyi
            patientNameInMyanmar = Rabbit.zg2uni(patientNameInMyanmar);
            Log.d(TAG, "getFormData: Patient Name In Myanmar is Converted to Unicode From ZawGyi : " + patientNameInMyanmar);
        }

        uhcPatient.setNameInMyanmar(patientNameInMyanmar);
        uhcPatient.setGender(handleNull(selectedGender));
        uhcPatient.setMaritalStatus(handleNull(selectedMaritalStatus));
        uhcPatient.setAgeType(getSelectedAgeType());

        try {
            int enteredAge = Integer.parseInt(edtPatientAge.getText().toString().trim());
            uhcPatient.setAge(enteredAge);
            String enteredDOB = handleNull(edtDOB.getText().toString().trim());
            String checkedDOB = TextUtils.isEmpty(enteredDOB) ? guessAgeByYear(enteredAge) : enteredDOB;
            String formattedDOB = DateTimeHelper.convertDateFormat(checkedDOB, DateTimeHelper.LOCAL_DATE_FORMAT, DateTimeHelper.SERVER_DATE_TIME_FORMAT);
            uhcPatient.setDateOfBirth(handleNull(formattedDOB));
        } catch (Exception e) {
            e.printStackTrace();
        }

        uhcPatient.setPhone(handleNull(edtPhone.getText().toString().trim()));
        uhcPatient.setContactPerson(handleNull(edtContactPerson.getText().toString().trim()));
        uhcPatient.setStateDivision(handleNull(selectedState));
        uhcPatient.setTownship(handleNull(selectedTownship));
        uhcPatient.setStateCode(String.valueOf(getDivisionCodeByStateDivision(selectedState)));
        uhcPatient.setAddress(handleNull(edtPatientAddress.getText().toString().trim()));
        uhcPatient.setOccupation(handleNull(edtOccupation.getText().toString().trim()));

        try {
            uhcPatient.setBodyWeight(Double.parseDouble(edtBodyWeight.getText().toString().trim()));
            uhcPatient.setHeightIn_in(getPatientHeightInInches());
            uhcPatient.setBmi(getBmiValue());
        } catch (Exception e) {
            e.printStackTrace();
        }

        uhcPatient.setMUAC(handleNull(edtMuac.getText().toString().trim()));
        uhcPatient.setImmunizations(selectedImmunizations);
        uhcPatient.setImmunizationStatus(handleNull(edtImmunizationStatus.getText().toString().trim()));
        uhcPatient.setSystolicBP(handleNull(edtSystolicBP.getText().toString().trim()));
        uhcPatient.setDiastolicBP(handleNull(edtDiastolicBP.getText().toString().trim()));

        try {
            uhcPatient.setRbs(Double.parseDouble(edtRBS.getText().toString().trim()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        uhcPatient.setKnownAllergies(handleNull(edtKnownAllergies.getText().toString().trim()));

        if (!TextUtils.isEmpty(selectedPregnancyStatus)) {
            if (selectedPregnancyStatus.equalsIgnoreCase("Pregnant")) {
                uhcPatient.setPregnancy(true);
            } else if (selectedPregnancyStatus.equalsIgnoreCase("Not Pregnant")) {
                uhcPatient.setPregnancy(false);
            }
        }

        uhcPatient.setCurrentContraceptionStatus(selectedContraceptiveStatus);
        uhcPatient.setRiskGroup(handleNull(multiSelectionSpinnerRiskFactor.getSelectedItemsAsString()));
        uhcPatient.setPreviousMedicalHistory(handleNull(edtPreviousMedicalHistory.getText().toString().trim()));
        uhcPatient.setPreviousSurgicalHistory(handleNull(edtPreviousSurgicalHistory.getText().toString().trim()));
        uhcPatient.setRelevantFamilyHistory(handleNull(edtRelevantFamilyHistory.getText().toString().trim()));

        uhcPatient.setHasSynced(false);

        return uhcPatient;
    }

    private String getSelectedAgeType() {
        String ageType = "";
        if (radioButtonAgeTypeYear.isChecked()) {
            ageType = "Year";
        } else if (radioButtonAgeTypeMonth.isChecked()) {
            ageType = "Month";
        }
        return ageType;
    }

    private String handleNull(String target) {
        return TextUtils.isEmpty(target) ? "" : target.trim();
    }

    private String get12DigitUIDCode() {
        String stateCode, townshipCode;
        int divisionCodeByStateDivision = getDivisionCodeByStateDivision(selectedState);
        int townshipCodeByTownship = getTownshipCodeByTownship(selectedTownship);

        if (String.valueOf(divisionCodeByStateDivision).length() == 1) {
            stateCode = "0" + divisionCodeByStateDivision;
        } else {
            stateCode = String.valueOf(divisionCodeByStateDivision);
        }

        if (String.valueOf(townshipCodeByTownship).length() == 1) {
            townshipCode = "0" + townshipCodeByTownship;
        } else {
            townshipCode = String.valueOf(townshipCodeByTownship);
        }


        String enteredHhCode = edtHHCode.getText().toString().trim();
        String enteredHhMemberCode = edtHHMemberCode.getText().toString();

        if (enteredHhCode.length() < 6) {
            Integer[] zeros = new Integer[6 - enteredHhCode.length()];
            Arrays.fill(zeros, 0);
            enteredHhCode = TextUtils.join("", zeros) + enteredHhCode;
        }

        if (enteredHhMemberCode.length() == 1) {
            enteredHhMemberCode = "0" + enteredHhMemberCode;
        }

        return CodeGen.generate12DigitUIDCode(stateCode, townshipCode, enteredHhCode, enteredHhMemberCode);
    }

    private String getPatientCode() {
        long codeIndex = realm.where(UhcPatient.class).count();
        String generatePatientCode = CodeGen.generatePatientCode(getDivisionCodeByStateDivision(selectedState), getTownshipCodeByTownship(selectedTownship), authenticationModel.getStaffId(), (int) codeIndex);
        Log.d(TAG, "getPatientCode() returned: " + generatePatientCode);
        return generatePatientCode;
    }

    private Double getPatientHeightInInches() {
        double heightInInches = 0.0;

        try {
            double heightFt, heightInches;
            if (TextUtils.isEmpty(edtHeightInFt.getText().toString().trim())) {
                heightFt = 0.0;
            } else {
                heightFt = Double.parseDouble(edtHeightInFt.getText().toString());
            }
            if (TextUtils.isEmpty(edtHeightInInches.getText().toString().trim())) {
                heightInches = 0.0;
            } else {
                heightInches = Double.parseDouble(edtHeightInInches.getText().toString());
            }

            heightInInches = (heightFt * 12) + heightInches;
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getPatientHeightInInches() returned: " + heightInInches);
        return heightInInches;
    }

    private double getBmiValue() {
        double bmi = 0.0;
        String enteredHeightInches = edtHeightInInches.getText().toString().trim();
        String enteredHeightFeet = edtHeightInFt.getText().toString().trim();
        String enteredBodyWeight = edtBodyWeight.getText().toString().trim();
        if ((!TextUtils.isEmpty(enteredHeightInches) && !TextUtils.isEmpty(enteredHeightFeet)) && !TextUtils.isEmpty(enteredBodyWeight)) {

            try {
                Double heightFt = Double.parseDouble(enteredHeightFeet) * 12;
                Double heightInches = Double.parseDouble(enteredHeightInches);
                double height = heightFt + heightInches;
                double weight = Double.parseDouble(enteredBodyWeight);

                if (height > 0 && weight > 0) {
                    double multipleHeight = height * height;

                    double multipleWeight = weight * 703;
                    bmi = Math.round((multipleWeight / multipleHeight) * 100.0) / 100.0;

                    Log.d(TAG, "getBmiValue: bmi :" + bmi);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        Log.d(TAG, "getBmiValue() returned: " + bmi);
        return bmi;
    }

    private void allFindViewbyId() {
        //Registration, Photo
        segmentedGroupRegistration = findViewById(R.id.uhcRegistration_radioGroupRegistration);
        radioButtonRegularRegistration = findViewById(R.id.uhcRegistration_radioRegularRegistration);
        radioButtonUHCRegistration = findViewById(R.id.uhcRegistration_radioUHCRegistration);
        frameLayoutPhoto = findViewById(R.id.uhcRegistration_photo);
        imageViewPhoto = findViewById(R.id.uhcRegistration_ivPhoto);
        // UIC, HH, HH Member, Registration Date
        textInputLayoutUID = findViewById(R.id.uhcRegistration_tilUID);
        edtUID = findViewById(R.id.uhcRegistration_edtUID);
        textInputLayoutHHCode = findViewById(R.id.uhcRegistration_tilHHCode);
        edtHHCode = findViewById(R.id.uhcRegistration_edtHHCode);
        textInputLayoutHHMemberCode = findViewById(R.id.uhcRegistration_tilHHMemberCode);
        edtHHMemberCode = findViewById(R.id.uhcRegistration_edtHHMemberCode);
        textInputLayoutRegistrationDate = findViewById(R.id.uhcRegistration_tilRegistrationDate);
        edtRegistrationDate = findViewById(R.id.uhcRegistration_edtRegistrationDate);

        //Demographic

        textInputLayoutNameInEnglish = findViewById(R.id.uhcRegistration_tilNameInEnglish);
        edtNameInEnglish = findViewById(R.id.uhcRegistration_edtNameInEnglish);
        textInputLayoutNameInBurmese = findViewById(R.id.uhcRegistration_tilNameInBurmese);
        edtNameInBurmese = findViewById(R.id.uhcRegistration_edtNameInBurmese);
        radioButtonGenderMale = findViewById(R.id.uhcRegistration_radioGenderMale);
        radioButtonGenderFemale = findViewById(R.id.uhcRegistration_radioGenderFemale);
        txtMaritalStatus = findViewById(R.id.uhcRegistration_txtMaritalStatus);
        frameLayoutMaritalStatus = findViewById(R.id.uhcRegistration_frameLayoutMaritalStatus);
        spinnerMaritalStatus = findViewById(R.id.uhcRegistration_spinnerMaritalStatus);
        //Age, DOB,
        radioButtonAgeTypeYear = findViewById(R.id.uhcRegistration_radioAgeYear);
        radioButtonAgeTypeMonth = findViewById(R.id.uhcRegistration_radioAgeMonth);
        textInputLayoutDOB = findViewById(R.id.uhcRegistration_tilDOB);
        edtDOB = findViewById(R.id.uhcRegistration_edtDOB);
        textInputLayoutPatientAge = findViewById(R.id.uhcRegistration_tilPatientAge);
        edtPatientAge = findViewById(R.id.uhcRegistration_edtPatientAge);
        //Person, Phone
        textInputLayoutPhone = findViewById(R.id.uhcRegistration_tilPhone);
        edtPhone = findViewById(R.id.uhcRegistration_edtPhone);
        textInputLayoutContactPerson = findViewById(R.id.uhcRegistration_tilContactPerson);
        edtContactPerson = findViewById(R.id.uhcRegistration_edtContactPerson);
        //Address
        spinnerState = findViewById(R.id.uhcRegistration_spinnerState);
        spinnerTownship = findViewById(R.id.uhcRegistration_spinnerTownship);
        textInputLayoutPatientAddress = findViewById(R.id.uhcRegistration_tilPatientAddress);
        edtPatientAddress = findViewById(R.id.uhcRegistration_edtPatientAddress);
        textInputLayoutOccupation = findViewById(R.id.uhcRegistration_tilOccupation);
        edtOccupation = findViewById(R.id.uhcRegistration_edtOccupation);

        // Relevant Medical Information:

        textInputLayoutBodyWeight = findViewById(R.id.uhcRegistration_tilBodyWeight);
        edtBodyWeight = findViewById(R.id.uhcRegistration_edtBodyWeight);
        textInputLayoutHeightInFt = findViewById(R.id.uhcRegistration_tilHeightInFt);
        edtHeightInFt = findViewById(R.id.uhcRegistration_edtHeightInFt);
        textInputLayoutHeightInInches = findViewById(R.id.uhcRegistration_tilHeightInInches);
        edtHeightInInches = findViewById(R.id.uhcRegistration_edtHeightInInches);
        textInputLayoutMuac = findViewById(R.id.uhcRegistration_tilMuac);
        edtMuac = findViewById(R.id.uhcRegistration_edtMuac);
        textInputLayoutImmunizationStatus = findViewById(R.id.uhcRegistration_tilImmunizationStatus);
        edtImmunizationStatus = findViewById(R.id.uhcRegistration_edtImmunizationStatus);
        //BP, RBS, Allergies
        textInputLayoutSymbolicBP = findViewById(R.id.uhcRegistration_tilSymbolicBP);
        edtSystolicBP = findViewById(R.id.uhcRegistration_edtSystolicBP);
        textInputLayoutDiastolicBP = findViewById(R.id.uhcRegistration_tilDiastolicBP);
        edtDiastolicBP = findViewById(R.id.uhcRegistration_edtDiastolicBP);
        textInputLayoutRBS = findViewById(R.id.uhcRegistration_tilRBS);
        edtRBS = findViewById(R.id.uhcRegistration_edtRBS);
        textInputLayoutKnownAllergies = findViewById(R.id.uhcRegistration_tilKnownAllergies);
        edtKnownAllergies = findViewById(R.id.uhcRegistration_edtKnownAllergies);
        //Pregnancy || Contraceptive Status
        txtPregnancyStatus = findViewById(R.id.uhcRegistration_txtPregnancyStatus);
        spinnerPregnancyStatus = findViewById(R.id.uhcRegistration_spinnerPregnancyStatus);
        frameLayoutCurrentContraceptiveStatus = findViewById(R.id.uhcRegistration_frameLayoutCurrentContraceptiveStatus);
        txtCurrentContraceptiveStatus = findViewById(R.id.uhcRegistration_txtCurrentContraceptiveStatus);
        spinnerCurrentContraceptiveStatus = findViewById(R.id.uhcRegistration_spinnerCurrentContraceptiveStatus);
        // Risk Factor
        txtRiskFactor = findViewById(R.id.uhcRegistration_txtRiskFactor);
        frameLayoutRiskFactor = findViewById(R.id.uhcRegistration_frameLayoutRiskFactor);
        multiSelectionSpinnerRiskFactor = findViewById(R.id.uhcRegistration_multiSelectRiskFactor);
        // Previous Medical, Surgical, Family History
        textInputLayoutPreviousMedicalHistory = findViewById(R.id.uhcRegistration_tilPreviousMedicalHistory);
        edtPreviousMedicalHistory = findViewById(R.id.uhcRegistration_edtPreviousMedicalHistory);
        textInputLayoutPreviousSurgicalHistory = findViewById(R.id.uhcRegistration_tilPreviousSurgicalHistory);
        edtPreviousSurgicalHistory = findViewById(R.id.uhcRegistration_edtPreviousSurgicalHistory);
        textInputLayoutRelevantFamilyHistory = findViewById(R.id.uhcRegistration_tilRelevantFamilyHistory);
        edtRelevantFamilyHistory = findViewById(R.id.uhcRegistration_edtRelevantFamilyHistory);

        layoutSave = findViewById(R.id.uhcRegistration_layoutSave);
    }
}
