package com.koekoetech.clinic.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.koekoetech.clinic.BuildConfig;
import com.koekoetech.clinic.R;
import com.koekoetech.clinic.adapter.UhcPatientsRecordAdapter;
import com.koekoetech.clinic.fragment.AppProgressDialog;
import com.koekoetech.clinic.helper.AppUtils;
import com.koekoetech.clinic.helper.BaseUrlProvider;
import com.koekoetech.clinic.helper.CmisGoogleAnalyticsConstants;
import com.koekoetech.clinic.helper.InMemoryAppStore;
import com.koekoetech.clinic.helper.RealmAccessHelper;
import com.koekoetech.clinic.interfaces.PatientRecordsCallBack;
import com.koekoetech.clinic.model.UhcPatient;
import com.koekoetech.clinic.model.UhcPatientProgress;

import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by ZMN on 8/27/17.
 **/

public class UhcPatientDetailActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, PatientRecordsCallBack {

    private static final String TAG = UhcPatientDetailActivity.class.getSimpleName();
    private static final String EXTRA_PATIENT = "patientCode";

    private CoordinatorLayout uhc_detail_container;

    private SwipeRefreshLayout uhc_patient_detail_swipe_refresh;

    private RecyclerView recycler_view_uhc_detail;

    private FloatingActionButton fab_uhc_detail;

    private AppProgressDialog progressDialog;
    private UhcPatientsRecordAdapter recordAdapter;
    private UhcPatient patient;
    private String patientCode;
    private Realm realm;

    public static Intent getNewIntent(String patientCode, Context context) {
        Intent intent = new Intent(context, UhcPatientDetailActivity.class);
        intent.putExtra(EXTRA_PATIENT, patientCode);
        return intent;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_uhcpatient_registration_detail;
    }

    @Override
    protected void setUpContents(Bundle savedInstanceState) {
        setupToolbar(true);
        setupToolbarText("Patient Detail");

        bindViews();

        patientCode = getIntent().getStringExtra(EXTRA_PATIENT);

        if (TextUtils.isEmpty(patientCode)) {
            finish();
            return;
        }

        realm = Realm.getDefaultInstance();
        patient = realm.where(UhcPatient.class).equalTo("patientCode", patientCode).findFirst();

        if (patient == null) {
            finish();
            return;
        }

        Log.d(TAG, "setUpContents: EXTRA_PATIENT : " + patient);
        Log.d(TAG, "setUpContents: ID : " + patient.getUicCode());

        // Configure RecyclerView
        configureRvAdapter();

        // Configure SwipeRefreshListener
        uhc_patient_detail_swipe_refresh.setOnRefreshListener(this);
        uhc_patient_detail_swipe_refresh.setRefreshing(false);

        fab_uhc_detail.setOnClickListener(v -> goToProgressCreateActivity());

        progressDialog = AppProgressDialog.getProgressDialog("Deleting Progress");

        if (InMemoryAppStore.getInstance().isDetailImplicitNCDLaunch()) {
            InMemoryAppStore.getInstance().setDetailImplicitNCDLaunch(false);
            String ncdUrl = BaseUrlProvider.getWebViewBaseUrl() + BuildConfig.NCD_URL + patientCode;
            startActivity(GenericWebViewActivity.getNewIntent(ncdUrl, "NCD", false, this));
        }

    }

    private void bindViews() {
        uhc_detail_container = findViewById(R.id.uhc_detail_container);
        uhc_patient_detail_swipe_refresh = findViewById(R.id.uhc_patient_detail_swipe_refresh);
        recycler_view_uhc_detail = findViewById(R.id.recycler_view_uhc_detail);
        fab_uhc_detail = findViewById(R.id.fab_uhc_detail);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume() called");
        super.onResume();
        loadRecords();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_patient_profile_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_update_profile) {
            startActivity(UhcPatientRegistrationActivity.getEditIntent(UhcPatientDetailActivity.this, patient.getPatientCode()));
            return false;
        } else if (item.getItemId() == R.id.menu_delete_profile) {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Patient")
                    .setMessage("Are you sure you want to delete patient permanently?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, (dialogInterface, i) -> {
                        progressDialog.setMessage("Deleting Patient");
                        progressDialog.display(getSupportFragmentManager());
                        realm.executeTransactionAsync(realm -> RealmAccessHelper.deletePatient(realm, patientCode, false), () -> {
                            sendActionAnalytics(CmisGoogleAnalyticsConstants.ACTION_DELETE_PATIENT);
                            progressDialog.safeDismiss();
                            Toast.makeText(UhcPatientDetailActivity.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(UhcPatientDetailActivity.this, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }, error -> {
                            Log.e(TAG, "onError: ", error);
                            progressDialog.safeDismiss();
                            Intent intent = new Intent(UhcPatientDetailActivity.this, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        });
                    }).create().show();
            return false;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRefresh() {
        uhc_patient_detail_swipe_refresh.setRefreshing(false);
        loadRecords();
    }

    @Override
    public void onDelete(final String progressCode) {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("Delete Confirm")
                .setMessage("Are you sure to delete this progress?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    progressDialog.display(getSupportFragmentManager());
                    realm.executeTransactionAsync(realm -> {
                        RealmAccessHelper.deleteByProgressCode(realm, progressCode, false);
                        RealmAccessHelper.markPatientChanged(realm,patientCode);
                    }, () -> {
                        loadRecords();
                        progressDialog.safeDismiss();
                        sendActionAnalytics(CmisGoogleAnalyticsConstants.ACTION_DELETE_PATIENT_PROGRESS);
                        Toast.makeText(UhcPatientDetailActivity.this, "Successfully Deleted", Toast.LENGTH_SHORT).show();
                    }, error -> {
                        progressDialog.safeDismiss();
                        Log.e(TAG, "onDelete: ", error);
                        Toast.makeText(UhcPatientDetailActivity.this, "Something went wrong while deleting progress!", Toast.LENGTH_SHORT).show();
                    });
                    dialog.dismiss();
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    @Override
    public void onEdit(String progressCode) {
        goToProgressEditActivity(progressCode);
    }

    private void configureRvAdapter() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recycler_view_uhc_detail.setLayoutManager(manager);
        recordAdapter = new UhcPatientsRecordAdapter(patient);
        recordAdapter.registerPatientRecordsCallBack(this);
        recycler_view_uhc_detail.setAdapter(recordAdapter);
    }

    private void goToProgressCreateActivity() {
        if (AppUtils.isTablet(this)) {
            startActivity(UhcProgressNoteTabletActivity.getCreateIntent(patient.getPatientCode(), this));
        } else {
            startActivity(UhcPatientProgressNotesFragmentActivity.getCreateIntent(patient.getPatientCode(), getApplicationContext()));
        }
    }

    private void goToProgressEditActivity(String progressCode) {
        if (AppUtils.isTablet(this)) {
            startActivity(UhcProgressNoteTabletActivity.getEditIntent(progressCode, this));
        } else {
            startActivity(UhcPatientProgressNotesFragmentActivity.getEditIntent(progressCode, this));
        }
    }

    private void loadRecords() {
        RealmResults<UhcPatientProgress> progresses = realm.where(UhcPatientProgress.class)
                .equalTo("patientCode", patient.getPatientCode())
                .equalTo("isDeleted", false)
                .findAll()
                .sort("createdTime", Sort.DESCENDING);
        populateAdapter(progresses);
    }

    private void populateAdapter(RealmResults<UhcPatientProgress> progresses) {
        recordAdapter.clear();
        recordAdapter.addHeader(patient);
        if (progresses.isEmpty()) {
            recordAdapter.showEmptyView(R.layout.item_empty_view_medical_record);
        } else {
            for (UhcPatientProgress progress : progresses) {
                recordAdapter.add(progress);
            }
        }
    }

}