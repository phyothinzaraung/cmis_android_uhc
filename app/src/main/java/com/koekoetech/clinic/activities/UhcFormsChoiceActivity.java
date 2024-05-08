package com.koekoetech.clinic.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.material.textfield.TextInputLayout;
import com.koekoetech.clinic.R;
import com.koekoetech.clinic.helper.CMISConstant;
import com.koekoetech.clinic.helper.CmisGoogleAnalyticsConstants;
import com.koekoetech.clinic.helper.DateTimeHelper;
import com.koekoetech.clinic.model.AuthenticationModel;
import com.koekoetech.clinic.model.FormCompleteRecords;
import com.koekoetech.clinic.model.StaffModel;
import com.koekoetech.clinic.model.UhcPatient;
import com.koekoetech.clinic.model.UhcPatientProgress;

import java.util.Calendar;
import java.util.Date;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import io.realm.Realm;

/**
 * Created by ZMN on 7/31/17.
 **/

public class UhcFormsChoiceActivity extends BaseActivity {

    private static final String TAG = UhcFormsChoiceActivity.class.getSimpleName();

    private static final int REQ_SELECT_DOCTOR = 283;
    private static final String EXTRA_RESULT_MODEL = "result_model";
    private static final String EXTRA_RESULT_FORM = "form";
    private static final String EXTRA_PROGRESS_CODE = "progress_code";


    private TextView tvReportsTitle;
    private EditText txt_charges;
    private TextView lbl_follow_up;
    private EditText txt_followup_date_time;
    private EditText txt_followup_reason;
    private LinearLayout linearLayout_save;
    private CardView cvReferContainer;
    private EditText txtCurrentDiagnosis;
    private EditText txtPlanOfCare;
    private RadioButton radioButton_SQH;
    private RadioButton radioButton_private_hospital;
    private RadioButton radioButton_public_hospital;
    private TextInputLayout layout_refer_SQH;
    private TextInputLayout layout_refer_PrivateHospital;
    private TextInputLayout layout_refer_PublicHospital;
    private EditText txt_refer_doctor;
    private EditText txt_refer_privateHospital;
    private EditText txt_refer_publicHospital;
    private LinearLayout layout_togglebtn_group;
    private ToggleButton togglebtnTB;
    private ToggleButton togglebtn_HIV;
    private ToggleButton togglebtn_RHLTM;
    private ToggleButton toggletbtn_CCP;
    private TextView txt_refer_reason;
    private LinearLayout linearLayout_referto;
    private CardView uhc_forms_card_rh;
    private ImageView ivRHShortTermDoneFlag;
    private TextInputLayout tilReferToReason;

    private void allFindViewbyId() {
        tvReportsTitle = findViewById(R.id.uhc_forms_tvReportsTitle);
        txt_charges = findViewById(R.id.txt_charges);
        lbl_follow_up = findViewById(R.id.lbl_follow_up);
        txt_followup_date_time = findViewById(R.id.txt_followup_date_time);
        txt_followup_reason = findViewById(R.id.txt_followup_reason);
        linearLayout_save = findViewById(R.id.linear_save);
        cvReferContainer = findViewById(R.id.cv_refer);
        txtCurrentDiagnosis = findViewById(R.id.edt_current_diagnosis);
        txtPlanOfCare = findViewById(R.id.edt_plan_of_care);
        radioButton_SQH = findViewById(R.id.radiobtn_SQH);
        radioButton_private_hospital = findViewById(R.id.radiobtn_private_hospital);
        radioButton_public_hospital = findViewById(R.id.radiobtn_public_hospital);
        layout_refer_SQH = findViewById(R.id.layout_refer_SQH);
        layout_refer_PrivateHospital = findViewById(R.id.layout_refer_PrivateHospital);
        layout_refer_PublicHospital = findViewById(R.id.layout_refer_PublicHospital);
        txt_refer_doctor = findViewById(R.id.txt_refer_doctor);
        txt_refer_privateHospital = findViewById(R.id.txt_refer_privateHospital);
        txt_refer_publicHospital = findViewById(R.id.txt_refer_publicHospital);
        layout_togglebtn_group = findViewById(R.id.layout_togglebtn_group);
        togglebtnTB = findViewById(R.id.togglebtn_TB);
        togglebtn_HIV = findViewById(R.id.togglebtn_HIV);
        togglebtn_RHLTM = findViewById(R.id.togglebtn_RHLTM);
        toggletbtn_CCP= findViewById(R.id.togglebtn_CCP);
        txt_refer_reason = findViewById(R.id.txt_refer_reason);
        linearLayout_referto = findViewById(R.id.linear_referto);
        uhc_forms_card_rh = findViewById(R.id.uhc_forms_card_rh);
        ivRHShortTermDoneFlag = findViewById(R.id.uhcForms_ivRHShortTermDone);
        tilReferToReason = findViewById(R.id.layout_refer_reason);
    }

    private String chosenFormType;
    private UhcPatient patient;
    private String patient_progressCode;
    private UhcPatientProgress uhcPatientProgress;
    private Realm realm;
    private int referToDoctorId;

    public static Intent newIntent(UhcPatient uhcPatient, String form, String progressCode, Context context) {
        Intent intent = new Intent(context, UhcFormsChoiceActivity.class);
        intent.putExtra(EXTRA_RESULT_MODEL, uhcPatient);
        intent.putExtra(EXTRA_RESULT_FORM, form);
        intent.putExtra(EXTRA_PROGRESS_CODE, progressCode);
        return intent;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_uhc_forms;
    }

    @Override
    protected void setUpContents(Bundle savedInstanceState) {

        allFindViewbyId();

        setupToolbar(true);
        setupToolbarText("Wrap Up");

        init();

        patient = getIntent().getParcelableExtra(EXTRA_RESULT_MODEL);
        String form = getIntent().getStringExtra(EXTRA_RESULT_FORM);
        patient_progressCode = getIntent().getStringExtra(EXTRA_PROGRESS_CODE);

        AuthenticationModel authenticationModel = realm.where(AuthenticationModel.class).findFirst();

        if (authenticationModel != null) {
            if (authenticationModel.isPermitted(CMISConstant.REPORT_RH) && patient.getGender().equalsIgnoreCase("Female")) {
                tvReportsTitle.setVisibility(View.VISIBLE);
                uhc_forms_card_rh.setVisibility(View.VISIBLE);
            } else {
                uhc_forms_card_rh.setVisibility(View.GONE);
                tvReportsTitle.setVisibility(View.GONE);
            }

        } else {
            uhc_forms_card_rh.setVisibility(View.GONE);
        }


        UhcPatientProgress progress = realm.where(UhcPatientProgress.class).equalTo("progressCode", patient_progressCode).findFirst();

        if (progress == null) {
            finish();
            return;
        }

        //REFER
        radioButton_SQH.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                sqhCheckedUI();
            }
        });

        radioButton_private_hospital.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                privateHospitalUI();
            }
        });

        radioButton_public_hospital.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                publicHospitalUI();
            }
        });

        uhcPatientProgress = new UhcPatientProgress(progress);

        if (!TextUtils.isEmpty(form)) {
            if (form.equals("UhcForm")) {
                cvReferContainer.setVisibility(View.GONE);
            } else if (form.equals("UhcFollowUp")) {
                cvReferContainer.setVisibility(View.VISIBLE);
            }
        }

        if (uhcPatientProgress != null) {

            if (uhcPatientProgress.getCharges() != null) {
                txt_charges.setText(String.valueOf(uhcPatientProgress.getCharges()));
            }

            if (!TextUtils.isEmpty(uhcPatientProgress.getFollowUpDateTime())) {
                txt_followup_date_time.setText(DateTimeHelper.convertDateFormat(uhcPatientProgress.getFollowUpDateTime(), DateTimeHelper.SERVER_DATE_TIME_FORMAT, DateTimeHelper.LOCAL_DATE_DISPLAY_FORMAT));
            }

            if (!TextUtils.isEmpty(uhcPatientProgress.getFollowUpReason())) {
                txt_followup_reason.setText(uhcPatientProgress.getFollowUpReason());
            }

            if (!TextUtils.isEmpty(uhcPatientProgress.getOutDiagnosis())) {
                txtCurrentDiagnosis.setText(uhcPatientProgress.getOutDiagnosis());
            }

            if (!TextUtils.isEmpty(uhcPatientProgress.getPlanOfCare())) {
                txtPlanOfCare.setText(uhcPatientProgress.getPlanOfCare());
            }

            int referType = uhcPatientProgress.getReferType();
            boolean hasRefer = false;
            switch (referType) {
                case CMISConstant.REFER_TYPE_SQH:
                    radioButton_SQH.setChecked(true);
                    hasRefer = true;
                    break;
                case CMISConstant.REFER_TYPE_PRIVATE_HOSPITAL:
                    radioButton_private_hospital.setChecked(true);
                    if (!TextUtils.isEmpty(uhcPatientProgress.getReferToFacility())) {
                        txt_refer_privateHospital.setText(uhcPatientProgress.getReferToFacility());
                    }
                    hasRefer = true;
                    break;
                case CMISConstant.REFER_TYPE_PUBLIC_HOSPITAL:
                    radioButton_public_hospital.setChecked(true);
                    if (!TextUtils.isEmpty(uhcPatientProgress.getReferToFacility())) {
                        txt_refer_publicHospital.setText(uhcPatientProgress.getReferToFacility());
                    }
                    hasRefer = true;
                    break;
                default:
                    radioButton_SQH.setChecked(true);
            }

            if (!TextUtils.isEmpty(uhcPatientProgress.getReferToDoctorName())) {
                txt_refer_doctor.setText(uhcPatientProgress.getReferToDoctorName());
                referToDoctorId = uhcPatientProgress.getReferToDoctor();
            }

            if (!TextUtils.isEmpty(uhcPatientProgress.getReferToReason())) {
                txt_refer_reason.setText(uhcPatientProgress.getReferToReason());
            }

            if (!TextUtils.isEmpty(uhcPatientProgress.getFollowUpDateTime())) {
                // Hide Refer
                cvReferContainer.setVisibility(View.GONE);
                lbl_follow_up.setVisibility(View.VISIBLE);
                txt_followup_date_time.setVisibility(View.VISIBLE);
                txt_followup_reason.setVisibility(View.VISIBLE);
            } else if (hasRefer) {
                // Hide Follow Up
                cvReferContainer.setVisibility(View.VISIBLE);
                lbl_follow_up.setVisibility(View.GONE);
                txt_followup_date_time.setVisibility(View.GONE);
                txt_followup_reason.setVisibility(View.GONE);
            }

        }

        txt_followup_date_time.setInputType(InputType.TYPE_NULL);
        txt_followup_date_time.setFocusable(false);
        txt_followup_date_time.setOnClickListener(v -> {

            Calendar c = Calendar.getInstance();
            Date pickerInitDate = DateTimeHelper.getDateFromString(txt_followup_date_time.getText().toString(), DateTimeHelper.LOCAL_DATE_DISPLAY_FORMAT);
            if (pickerInitDate != null) {
                c.setTime(pickerInitDate);
            }
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(UhcFormsChoiceActivity.this,
                    (view, year, monthOfYear, dayOfMonth) -> {

                        String strDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        txt_followup_date_time.setText(DateTimeHelper.convertDateFormat(strDate, DateTimeHelper.LOCAL_DATE_FORMAT, DateTimeHelper.LOCAL_DATE_DISPLAY_FORMAT));
                    }, mYear, mMonth, mDay);
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            datePickerDialog.show();

        });

        linearLayout_save.setOnClickListener(v -> {
            UhcPatientProgress patientProgress = getProgressData();
            if (areAllFollowUpFieldsEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UhcFormsChoiceActivity.this);
                builder.setMessage("You didn't fill all the fields in follow up. Are you sure to exit?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            } else {
                if (validateForm()) {
                    saveOffline(patientProgress);
                }
            }
        });

        linearLayout_referto.setOnClickListener(v -> {
            sendActionAnalytics(CmisGoogleAnalyticsConstants.ACTION_REFER_PATIENT);
            UhcPatientProgress progressToSave = new UhcPatientProgress(uhcPatientProgress);
            progressToSave.setReferToDateTime(DateTimeHelper.formatDate(new Date(), DateTimeHelper.SERVER_DATE_TIME_FORMAT));
            boolean shouldSave = true;

            if (radioButton_SQH.isChecked()) {
                if (TextUtils.isEmpty(txt_refer_doctor.getText().toString().trim())) {
                    shouldSave = false;
                    layout_refer_SQH.setErrorEnabled(true);
                    layout_refer_SQH.setError("Please select doctor to refer.");
                } else {
                    layout_refer_SQH.setError("");
                    layout_refer_SQH.setErrorEnabled(false);
                    progressToSave.setReferType(CMISConstant.REFER_TYPE_SQH);
                    progressToSave.setReferToDoctor(referToDoctorId);
                    progressToSave.setReferToDoctorName(txt_refer_doctor.getText().toString());
                }
            } else if (radioButton_private_hospital.isChecked()) {
                if (TextUtils.isEmpty(txt_refer_privateHospital.getText().toString().trim())) {
                    shouldSave = false;
                    layout_refer_PrivateHospital.setErrorEnabled(true);
                    layout_refer_PrivateHospital.setError("Please enter private hospital name.");
                } else {
                    layout_refer_PrivateHospital.setError("");
                    layout_refer_PrivateHospital.setErrorEnabled(true);
                    progressToSave.setReferType(CMISConstant.REFER_TYPE_PRIVATE_HOSPITAL);
                    progressToSave.setReferToDoctor(0);
                    progressToSave.setReferToDoctorName("");
                    progressToSave.setReferToFacility(txt_refer_privateHospital.getText().toString().trim());
                }
            } else if (radioButton_public_hospital.isChecked()) {
                if (TextUtils.isEmpty(txt_refer_publicHospital.getText().toString().trim())) {
                    shouldSave = false;
                    layout_refer_PublicHospital.setErrorEnabled(true);
                    layout_refer_PublicHospital.setError("Please enter public hospital name.");
                } else {
                    layout_refer_PublicHospital.setError("");
                    layout_refer_PublicHospital.setErrorEnabled(true);
                    progressToSave.setReferType(CMISConstant.REFER_TYPE_PUBLIC_HOSPITAL);
                    progressToSave.setReferToDoctor(0);
                    progressToSave.setReferToDoctorName("");
                    progressToSave.setReferToFacility(txt_refer_publicHospital.getText().toString().trim());
                }
            }

            if (TextUtils.isEmpty(txt_refer_reason.getText().toString().trim())) {
                shouldSave = false;
                tilReferToReason.setErrorEnabled(true);
                tilReferToReason.setError("Please enter refer reason.");
            } else {
                tilReferToReason.setError("");
                tilReferToReason.setErrorEnabled(true);
                progressToSave.setReferToReason(txt_refer_reason.getText().toString().trim());
            }

            if (shouldSave) {
                saveOffline(progressToSave);
            }
        });

        txt_refer_doctor.setOnClickListener(v -> {
            Intent sqhDoctorsListIntent = new Intent(UhcFormsChoiceActivity.this, SQHDoctorListActivity.class);
            startActivityForResult(sqhDoctorsListIntent, REQ_SELECT_DOCTOR);
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
        manageDoneFlags();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "] ");
        if (requestCode == REQ_SELECT_DOCTOR && resultCode == RESULT_OK) {
            StaffModel selectedDoctor = data.getParcelableExtra(SQHDoctorListActivity.EXTRA_SELECTED_DOCTOR);
            if (selectedDoctor != null) {
                Log.d(TAG, "onActivityResult: Got Selected Doctor");
                layout_refer_SQH.setError("");
                layout_refer_SQH.setErrorEnabled(false);
                txt_refer_doctor.setText(selectedDoctor.getName());
                referToDoctorId = selectedDoctor.getID();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void init() {
        realm = Realm.getDefaultInstance();

        uhc_forms_card_rh.setOnClickListener(v -> {
            chosenFormType = CMISConstant.UHC_FORM_RH;
            goToFormDetail();
        });
    }

    private boolean areAllFollowUpFieldsEmpty() {
        return TextUtils.isEmpty(txt_followup_date_time.getText().toString()) &&
                TextUtils.isEmpty(txt_followup_reason.getText().toString()) &&
                TextUtils.isEmpty(txtCurrentDiagnosis.getText().toString()) &&
                TextUtils.isEmpty(txtPlanOfCare.getText().toString()) &&
                TextUtils.isEmpty(txt_charges.getText().toString());
    }

    private boolean validateForm() {

        if (!TextUtils.isEmpty(txt_followup_reason.getText().toString())) {
            if (txt_followup_date_time.getText().toString().isEmpty()) {
                txt_followup_date_time.setError("Please enter follow up date time.");
                return false;
            }
        }
        return true;
    }

    private void saveOffline(final UhcPatientProgress patientProgress) {
        Realm.getDefaultInstance().executeTransactionAsync(realm -> {
            patientProgress.setHasChanges(true);
            realm.copyToRealmOrUpdate(patientProgress);
            UhcPatient uhcPatient = realm.where(UhcPatient.class).equalTo("patientCode", patientProgress.getPatientCode()).findFirst();
            if (uhcPatient != null) {
                uhcPatient.setHasSynced(false);
            }
        }, () -> {
            Toast.makeText(UhcFormsChoiceActivity.this, "Wrap up data saved successfully.", Toast.LENGTH_SHORT).show();
            finish();
        }, error -> Toast.makeText(getApplicationContext(), "Wrap up data saved fail", Toast.LENGTH_SHORT).show());
    }

    private void goToFormDetail() {
        startActivity(RHShortTermActivity.getNewIntent(patient, chosenFormType, patient_progressCode, this));
    }

    private UhcPatientProgress getProgressData() {
        Log.d(TAG, "getProgressData() called");
        UhcPatientProgress progress = new UhcPatientProgress(uhcPatientProgress);
        progress.setOutDiagnosis(txtCurrentDiagnosis.getText().toString().trim());
        if (!TextUtils.isEmpty(txt_charges.getText().toString())) {
            progress.setCharges(Double.parseDouble(txt_charges.getText().toString()));
        }
        if (!TextUtils.isEmpty(txt_followup_reason.getText().toString())) {
            progress.setFollowUpReason(txt_followup_reason.getText().toString());
        }

        String followUpDateTime = txt_followup_date_time.getText().toString();
        if (!TextUtils.isEmpty(followUpDateTime)) {
            String strTime = DateTimeHelper.formatDate(new Date(), DateTimeHelper.LOCAL_TIME_FORMAT);
            String dateTime = followUpDateTime + " " + strTime;
            Log.d(TAG, "getProgressData: FollowUp DateTime : " + dateTime);
            String followUpDateTimeStoreFormat = DateTimeHelper.convertDateFormat(dateTime, DateTimeHelper.LOCAL_DATE_TIME_DISPLAY_FORMAT, DateTimeHelper.SERVER_DATE_TIME_FORMAT);
            Log.d(TAG, "getProgressData: " + followUpDateTimeStoreFormat);
            progress.setFollowUpDateTime(followUpDateTimeStoreFormat);
        }
        progress.setPlanOfCare(txtPlanOfCare.getText().toString().trim());
//        if(!TextUtils.isEmpty(txt_refer_doctor.getText().toString().trim())){
//            progress.setReferToDoctorName(txt_refer_doctor.getText().toString());
//        }
//        progress.setReferToDoctor(uhcPatientProgress.getReferToDoctor());
//        progress.setReferToFacility(uhcPatientProgress.getReferToFacility());
//        if(!TextUtils.isEmpty(txt_refer_reason.getText().toString())){
//            progress.setReferToReason(txt_refer_reason.getText().toString());
//        }
//        progress.setReferToDateTime(uhcPatientProgress.getReferToDateTime());

        return progress;
    }

    private void manageDoneFlags() {
        boolean isRhShortTermDone = isFormSubmitted(FormCompleteRecords.FORM_RH_SHORT_TERM);
        ivRHShortTermDoneFlag.setVisibility(isRhShortTermDone ? View.VISIBLE : View.GONE);
    }

    private boolean isFormSubmitted(String formType) {
        return realm.where(FormCompleteRecords.class).equalTo("progressCode", patient_progressCode).equalTo("formType", formType).findAll().size() != 0;
    }

    private void sqhCheckedUI() {
        layout_refer_SQH.setVisibility(View.VISIBLE);
        layout_togglebtn_group.setVisibility(View.VISIBLE);
        layout_refer_PrivateHospital.setVisibility(View.GONE);
        layout_refer_PublicHospital.setVisibility(View.GONE);
    }

    private void privateHospitalUI() {
        layout_refer_PrivateHospital.setVisibility(View.VISIBLE);
        layout_refer_SQH.setVisibility(View.GONE);
        layout_refer_PublicHospital.setVisibility(View.GONE);
        layout_togglebtn_group.setVisibility(View.GONE);
    }

    private void publicHospitalUI() {
        layout_refer_PublicHospital.setVisibility(View.VISIBLE);
        layout_refer_SQH.setVisibility(View.GONE);
        layout_refer_PrivateHospital.setVisibility(View.GONE);
        layout_togglebtn_group.setVisibility(View.GONE);
    }

}
