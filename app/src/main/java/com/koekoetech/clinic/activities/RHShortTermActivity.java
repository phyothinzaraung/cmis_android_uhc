package com.koekoetech.clinic.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.koekoetech.clinic.R;
import com.koekoetech.clinic.app.GlideApp;
import com.koekoetech.clinic.fragment.AppProgressDialog;
import com.koekoetech.clinic.helper.DateTimeHelper;
import com.koekoetech.clinic.helper.ServiceHelper;
import com.koekoetech.clinic.helper.SharePreferenceHelper;
import com.koekoetech.clinic.helper.TextViewUtils;
import com.koekoetech.clinic.model.FormCompleteRecords;
import com.koekoetech.clinic.model.KeyValueText;
import com.koekoetech.clinic.model.RHShortTerm;
import com.koekoetech.clinic.model.UhcPatient;

import java.util.ArrayList;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RHShortTermActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = RHShortTermActivity.class.getSimpleName();

    private static final String EXTRA_RESULT_MODEL = "Result Model";
    private static final String EXTRA_FORM_TYPE = "Form Type";
    private static final String EXTRA_PROGRESS_CODE = "ProgressCode";

    private SwipeRefreshLayout srl_rh_short_term;

    private ImageView imgv_rh_short_term;

    private TextView tvPatientInfo;
    private TextView tvPatientCode;
    private TextView tvPatientProblem;
    private TextView tv_rh_client_type;
    private TextView tv_rh_method_type;
    private TextView tv_rh_date;

    private RadioButton rdo_new_patient;
    private RadioButton rdo_old_patient;
    private RadioButton rdo_inj;
    private RadioButton rdo_condom;
    private RadioButton rdo_EC;
    private RadioButton rdo_oc_pills;

    private LinearLayout btn_create;
    private LinearLayout linear_rh_old_info;
    private LinearLayout linear_rh_edit;
    private LinearLayout btn_cancel;

    private CardView cv_rh_item;
    private CardView cv_old_edit;

    private AppProgressDialog progressDialog;
    private ServiceHelper.ApiService service;
    private UhcPatient uhcPatient;
    private RHShortTerm rhOldModel = null;
    private String AgeGroup;
    private Realm realm;
    private String progressCode;

    public static Intent getNewIntent(UhcPatient patient, String formType, String progressCode, Context context) {
        Intent intent = new Intent(context, RHShortTermActivity.class);
        intent.putExtra(EXTRA_RESULT_MODEL, patient);
        intent.putExtra(EXTRA_FORM_TYPE, formType);
        intent.putExtra(EXTRA_PROGRESS_CODE, progressCode);
        return intent;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_rhshort_term;
    }

    @Override
    protected void setUpContents(Bundle savedInstanceState) {
        setupToolbar(true);
        setupToolbarText("RH Short Term");

        bindViews();

        uhcPatient = getIntent().getParcelableExtra(EXTRA_RESULT_MODEL);
        progressCode = getIntent().getStringExtra(EXTRA_PROGRESS_CODE);

        init();

        if (uhcPatient != null) {

            String photo;
            if (!TextUtils.isEmpty(uhcPatient.getLocal_filepath())) {
                photo = uhcPatient.getLocal_filepath();
                Log.d(TAG, "setUpContents: LocalFilePath : " + photo);
            } else {
                photo = uhcPatient.getPhotoUrl();
                Log.d(TAG, "setUpContents: PhotoUrl : " + photo);
            }

            GlideApp.with(this)
                    .load(photo)
                    .placeholder(R.mipmap.sample1)
                    .error(R.mipmap.sample1)
                    .into(imgv_rh_short_term);

            ArrayList<String> patientInfo = new ArrayList<>();

            if (!TextUtils.isEmpty(uhcPatient.getNameInEnglish())) {
                patientInfo.add(uhcPatient.getNameInEnglish());
            }

            patientInfo.add(String.valueOf(uhcPatient.getAge()));

            if (!TextUtils.isEmpty(uhcPatient.getGender())) {
                patientInfo.add(uhcPatient.getGender().substring(0, 1).toUpperCase());
            }

            tvPatientInfo.setText(TextUtils.join(" | ", patientInfo));

            KeyValueText patientCode = new KeyValueText("Code", uhcPatient.getPatientCode());
            TextViewUtils.populateKeyValueTv(tvPatientCode, patientCode);

            KeyValueText patientProblem = new KeyValueText("Problem", uhcPatient.getProblem());
            TextViewUtils.populateKeyValueTv(tvPatientProblem, patientProblem);

        }

        getRHShortTermByProgressCode();

        String uhcPatientAccessTime = uhcPatient.getAccesstime();
        if (!TextUtils.isEmpty(uhcPatientAccessTime)) {
            tv_rh_date.setText(DateTimeHelper.convertDateFormat(uhcPatientAccessTime, DateTimeHelper.SERVER_DATE_TIME_FORMAT, DateTimeHelper.LOCAL_DATE_DISPLAY_FORMAT));
        }


        linear_rh_edit.setOnClickListener(v -> {
            cv_rh_item.setVisibility(View.VISIBLE);
            getOldRecord(rhOldModel);
        });

        btn_cancel.setOnClickListener(v -> cv_rh_item.setVisibility(View.GONE));

        btn_create.setOnClickListener(v -> {

            RHShortTerm postModel = new RHShortTerm();
            if (rhOldModel != null) {
                postModel = rhOldModel;
            }

            String p = "New Client";
            if (rdo_new_patient.isChecked()) {
                p = "New Client";
            }
            if (rdo_old_patient.isChecked()) {
                p = "Old Client";
            }
            String m = "Inj";
            if (rdo_inj.isChecked()) {
                m = "Injectables";
            } else if (rdo_oc_pills.isChecked()) {
                m = "OC Pills";
            } else if (rdo_condom.isChecked()) {
                m = "Condoms";
            } else if (rdo_EC.isChecked()) {
                m = "EC";
            }
            if (uhcPatient.getAge() < 25) {
                AgeGroup = "<25";
            } else if (uhcPatient.getAge() >= 25) {
                AgeGroup = "25+";
            }
            postModel.setClientType(p);
            postModel.setMethod(m);
            postModel.setAgeGroup(AgeGroup);
            postModel.setPatientCount(1);

            postModel.setPatientCode(uhcPatient.getPatientCode());
            postModel.setName(uhcPatient.getNameInEnglish());
            postModel.setFacilityCode(uhcPatient.getFacilityCode());
            postModel.setFacilityTitle(uhcPatient.getFacilityName());
            postModel.setProgressCode(progressCode);
            postModel.setDoctorCode(SharePreferenceHelper.getHelper(this).getAuthenticationModel().getStaffCode());

            postRHShortTerm(postModel);

        });

    }

    private void bindViews() {
        srl_rh_short_term = findViewById(R.id.srl_rh_short_term);
        imgv_rh_short_term = findViewById(R.id.imgv_rh_short_term);
        tvPatientInfo = findViewById(R.id.rhShortTermTvPatientInfo);
        tvPatientCode = findViewById(R.id.rhShortTermTvPatientCode);
        tvPatientProblem = findViewById(R.id.rhShortTermTvPatientProblem);
        rdo_new_patient = findViewById(R.id.rdo_new_patient);
        rdo_old_patient = findViewById(R.id.rdo_old_patient);
        rdo_inj = findViewById(R.id.rdo_inj);
        rdo_condom = findViewById(R.id.rdo_condon);
        rdo_EC = findViewById(R.id.rdo_EC);
        rdo_oc_pills = findViewById(R.id.rdo_oc_pills);
        btn_create = findViewById(R.id.btn_create);
        tv_rh_client_type = findViewById(R.id.tv_rh_client_type);
        tv_rh_method_type = findViewById(R.id.tv_rh_method_type);
        linear_rh_old_info = findViewById(R.id.linear_rh_old_info);
        cv_rh_item = findViewById(R.id.cv_rh_item);
        linear_rh_edit = findViewById(R.id.linear_rh_edit);
        tv_rh_date = findViewById(R.id.tv_rh_date);
        cv_old_edit = findViewById(R.id.cv_old_edit);
        btn_cancel = findViewById(R.id.btn_cancel);
    }

    private void init() {
        progressDialog = AppProgressDialog.getProgressDialog("Loading....");

        service = ServiceHelper.getClient(this);
        srl_rh_short_term.setOnRefreshListener(this);
        srl_rh_short_term.setRefreshing(false);

        realm = Realm.getDefaultInstance();
    }

    private void postRHShortTerm(RHShortTerm model) {
        progressDialog.display(getSupportFragmentManager());

        Call<RHShortTerm> rhShortTerm = service.postRHShortTerm(model);
        rhShortTerm.enqueue(new Callback<RHShortTerm>() {
            @Override
            public void onResponse(@NonNull Call<RHShortTerm> call, @NonNull Response<RHShortTerm> response) {
                progressDialog.safeDismiss();
                if (response.isSuccessful()) {
                    setDone(true, () -> {
                        Toast.makeText(RHShortTermActivity.this, "Create Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    });

                } else {
                    Toast.makeText(RHShortTermActivity.this, "Connection Error. Please Try Again!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<RHShortTerm> call, @NonNull Throwable t) {
                progressDialog.safeDismiss();
                Toast.makeText(RHShortTermActivity.this, "Try Again!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getRHShortTermByProgressCode() {
        progressDialog.display(getSupportFragmentManager());
        Call<RHShortTerm> getRHByHHCode = service.getRHShortTermByProgressCode(progressCode);
        getRHByHHCode.enqueue(new Callback<RHShortTerm>() {
            @Override
            public void onResponse(@NonNull Call<RHShortTerm> call, @NonNull Response<RHShortTerm> response) {
                progressDialog.safeDismiss();
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.d(TAG, "onResponse: RH Short Term already exists");
                        rhOldModel = response.body();
                        cv_old_edit.setVisibility(View.VISIBLE);
                        linear_rh_old_info.setVisibility(View.VISIBLE);
                        linear_rh_edit.setVisibility(View.VISIBLE);
                        cv_rh_item.setVisibility(View.GONE);
                        tv_rh_client_type.setText(rhOldModel.getClientType());
                        tv_rh_method_type.setText(rhOldModel.getMethod());
                        setDone(true,null);
                    } else {
                        Log.d(TAG, "onResponse: RH Short Term does not exists");
                        cv_old_edit.setVisibility(View.GONE);
                        cv_rh_item.setVisibility(View.VISIBLE);
                        linear_rh_old_info.setVisibility(View.GONE);
                        setDone(false,null);
                    }
                } else {
                    showRetryDialog("Connection Error. Please Try Again!");
                }
            }

            @Override
            public void onFailure(@NonNull Call<RHShortTerm> call, @NonNull Throwable t) {
                progressDialog.safeDismiss();
                showRetryDialog("Connection Error. Please Try Again!");
            }
        });

    }

    private void setDone(boolean isDone, @Nullable Realm.Transaction.OnSuccess onSuccess) {
        realm.executeTransactionAsync(realm -> {
            final FormCompleteRecords formCompleteStatus = realm.where(FormCompleteRecords.class)
                    .equalTo("progressCode", progressCode)
                    .and()
                    .equalTo("formType", FormCompleteRecords.FORM_RH_SHORT_TERM)
                    .findFirst();
            if (formCompleteStatus == null) {
                if (isDone) {
                    FormCompleteRecords formCompleteStatus1 = new FormCompleteRecords();
                    formCompleteStatus1.setLocalId(UUID.randomUUID().toString());
                    formCompleteStatus1.setFormType(FormCompleteRecords.FORM_RH_SHORT_TERM);
                    formCompleteStatus1.setProgressCode(progressCode);
                    realm.copyToRealmOrUpdate(formCompleteStatus1);
                }
            } else {
                if (!isDone) {
                    formCompleteStatus.deleteFromRealm();
                }
            }
        }, onSuccess,null);

    }

    private void getOldRecord(RHShortTerm model) {
        if (model.getClientType().equals("New Client")) {
            rdo_new_patient.setChecked(true);
        } else if (model.getClientType().equals("Old Client")) {
            rdo_old_patient.setChecked(true);
        }

        switch (model.getMethod()) {
            case "Injectables":
                rdo_inj.setChecked(true);
                break;
            case "OC Pills":
                rdo_oc_pills.setChecked(true);
                break;
            case "Condoms":
                rdo_condom.setChecked(true);
                break;
            case "EC":
                rdo_EC.setChecked(true);
                break;
        }

    }

    @SuppressWarnings("SameParameterValue")
    private void showRetryDialog(String message) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Retry",
                        (dialog, whichButton) -> {

                            dialog.dismiss();
                            getRHShortTermByProgressCode();

                        })
                .setNegativeButton("Cancel",
                        (dialog, which) -> {
                            dialog.dismiss();
                            finish();
                        }).show();
    }

    @Override
    public void onRefresh() {
        srl_rh_short_term.setRefreshing(false);
        getRHShortTermByProgressCode();

    }
}
