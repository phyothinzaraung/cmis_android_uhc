package com.koekoetech.clinic.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.koekoetech.clinic.R;
import com.koekoetech.clinic.adapter.SQHDoctorListAdapter;
import com.koekoetech.clinic.helper.Pageable;
import com.koekoetech.clinic.interfaces.SqhDoctorSelectCallback;
import com.koekoetech.clinic.model.StaffModel;

import java.util.ArrayList;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;

public class SQHDoctorListActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, SqhDoctorSelectCallback {

    public static final String EXTRA_SELECTED_DOCTOR = "SelectedDoctor";

    private static final String TAG = SQHDoctorListActivity.class.getSimpleName();

    private RecyclerView recyclerView_doctor_list;

    private SwipeRefreshLayout swipeRefreshLayout_doctorList;

    private SQHDoctorListAdapter sqhDoctorListAdapter;
    private Realm realm;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_sqhdoctor_list;
    }

    @Override
    protected void setUpContents(Bundle savedInstanceState) {

        setupToolbar(true);
        setupToolbarText("SQH Doctor List");

        bindViews();

        init();

        loadAllDoctors();
    }

    private void bindViews(){
        recyclerView_doctor_list = findViewById(R.id.recycler_view_doctor_list);
        swipeRefreshLayout_doctorList = findViewById(R.id.swipe_refresh_layout_doctorList);
    }

    @Override
    public void onDoctorSelected(StaffModel staffModel) {
        Intent resultData = new Intent();
        resultData.putExtra(EXTRA_SELECTED_DOCTOR, staffModel);
        setResult(RESULT_OK, resultData);
        finish();
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout_doctorList.setRefreshing(false);
        loadAllDoctors();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.menu_search_registration, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (searchManager != null) {
            SearchView searchView = (SearchView) menu.findItem(R.id.menu_registration_search).getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setQueryHint("Search by doctor name");
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    Log.d(TAG, "onQueryTextSubmit() called with: s = [" + s + "]");
                    if (!TextUtils.isEmpty(s)) {
                        searchDoctorByName(s);
                    } else {
                        loadAllDoctors();
                    }
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (TextUtils.isEmpty(newText)) {
                        loadAllDoctors();
                        return true;
                    }
                    return false;
                }
            });
        }

        return true;
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

    private void loadAllDoctors() {
        sqhDoctorListAdapter.clear();
        RealmResults<StaffModel> allPatients = realm.where(StaffModel.class).equalTo("IsDeleted", false).findAll();
        ArrayList<Pageable> patientsList = new ArrayList<>(allPatients);
        sqhDoctorListAdapter.add(patientsList);
    }

    private void searchDoctorByName(String query) {
        if (TextUtils.isEmpty(query)) {
            return;
        }
        RealmResults<StaffModel> patients = realm.where(StaffModel.class)
                .equalTo("IsDeleted", false)
                .contains("Name", query, Case.INSENSITIVE)
                .findAll();
        sqhDoctorListAdapter.clear();
        ArrayList<Pageable> patientsList = new ArrayList<>(patients);
        sqhDoctorListAdapter.add(patientsList);

    }

    private void init() {
        realm = Realm.getDefaultInstance();
        sqhDoctorListAdapter = new SQHDoctorListAdapter(this);
        recyclerView_doctor_list.setAdapter(sqhDoctorListAdapter);
        swipeRefreshLayout_doctorList.setOnRefreshListener(this);
        swipeRefreshLayout_doctorList.setRefreshing(false);
    }
}
