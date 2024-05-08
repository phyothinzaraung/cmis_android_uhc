package com.koekoetech.clinic.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.koekoetech.clinic.R;
import com.koekoetech.clinic.adapter.DisabilitySurveyAdapter;
import com.koekoetech.clinic.helper.CmisGoogleAnalyticsConstants;
import com.koekoetech.clinic.model.DisabilitySurvey;
import com.koekoetech.clinic.model.UhcPatient;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by ZMN on 2019-08-06.
 **/
public class DisabilitySurveyActivity extends BaseActivity {

    private static final String TAG = "DisabilitySurveyScreen";
    private static final String EXTRA_PATIENT_CODE = "extPatientCode";

    private final Realm realm = Realm.getDefaultInstance();

    private RecyclerView rvSurveyQuestions;
    private MaterialButton btnSubmit;

    private UhcPatient patient;
    private DisabilitySurveyAdapter surveyAdapter;

    public static Intent getSurveyIntent(@NonNull Context context, @NonNull String patientCode) {
        final Intent intent = new Intent(context, DisabilitySurveyActivity.class);
        intent.putExtra(EXTRA_PATIENT_CODE, patientCode);
        return intent;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_disability_survey;
    }

    @Override
    protected void setUpContents(Bundle savedInstanceState) {

        bindViews();

        final Intent intent = getIntent();
        if (intent != null) {
            final String patientCode = intent.getStringExtra(EXTRA_PATIENT_CODE);
            if (!TextUtils.isEmpty(patientCode)) {
                patient = realm.where(UhcPatient.class).equalTo("patientCode", patientCode).findFirst();
            }
        }

        if (patient == null) {
            finish();
            return;
        }

        patient = realm.copyFromRealm(patient);

        setupToolbar(true);
        setupToolbarText(getString(R.string.lbl_disability_survey));

        surveyAdapter = new DisabilitySurveyAdapter(this, getDisabilitySurveysList());
        rvSurveyQuestions.setHasFixedSize(true);
        rvSurveyQuestions.setAdapter(surveyAdapter);

        btnSubmit.setOnClickListener(v -> onSubmit());

    }

    private void bindViews(){
        rvSurveyQuestions = findViewById(R.id.disabilitySurvey_rvQuestions);
        btnSubmit = findViewById(R.id.disabilitySurvey_btnSubmit);
    }

    void onSubmit() {
        sendActionAnalytics(CmisGoogleAnalyticsConstants.ACTION_ANSWER_DISABILITY_SURVEY);

        final List<DisabilitySurvey> surveyList = surveyAdapter.getSurveyList();

        for (DisabilitySurvey survey : surveyList) {
            survey.setPatientCode(patient.getPatientCode());
        }

        Log.d(TAG, "onSubmit: " + TextUtils.join("\n", surveyList));

        realm.executeTransactionAsync(realm -> {
            // Save Survey List
            final RealmList<DisabilitySurvey> surveyRealmList = new RealmList<>();
            surveyRealmList.addAll(surveyList);
            realm.copyToRealmOrUpdate(surveyRealmList);

            // Mark Patient Change
            patient.setHasSynced(false);
            realm.copyToRealmOrUpdate(patient);

        }, () -> {
            Toast.makeText(this, "Survey Submitted", Toast.LENGTH_SHORT).show();
            finish();
        }, (error -> {
            Log.e(TAG, "onSubmit: Failed to save survey for patient " + patient.getPatientCode(), error);
            Toast.makeText(this, "Failed to submit survey", Toast.LENGTH_SHORT).show();
        }));

    }

    private List<DisabilitySurvey> getDisabilitySurveysList() {
        final List<DisabilitySurvey> surveyList = new ArrayList<>();

        if (this.patient != null) {

            final RealmResults<DisabilitySurvey> surveysResult = realm.where(DisabilitySurvey.class)
                    .equalTo("patientCode", patient.getPatientCode())
                    .findAll();

            if (surveysResult != null && !surveysResult.isEmpty()) {
                surveyList.addAll(realm.copyFromRealm(surveysResult));
            }

        }


        return surveyList;
    }
}
