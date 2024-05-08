package com.koekoetech.clinic.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.koekoetech.clinic.R;
import com.koekoetech.clinic.activities.SearchActivity;
import com.koekoetech.clinic.adapter.UhcPatientsListAdapter;
import com.koekoetech.clinic.helper.AppUtils;
import com.koekoetech.clinic.helper.CMISConstant;
import com.koekoetech.clinic.helper.CmisGoogleAnalyticsConstants;
import com.koekoetech.clinic.helper.MyanmarZawgyiConverter;
import com.koekoetech.clinic.helper.Rabbit;
import com.koekoetech.clinic.helper.SharePreferenceHelper;
import com.koekoetech.clinic.model.AuthenticationModel;
import com.koekoetech.clinic.model.PatientsListFilter;
import com.koekoetech.clinic.model.UhcPatient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by ZMN on 6/6/18.
 **/
public class UhcPatientsListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, RealmChangeListener<RealmResults<UhcPatient>> {

    private static final String TAG = "UhcPatientsListFragment";
    private static final int REQ_ADVANCE_SEARCH = 286;

    private SwipeRefreshLayout swipeRefreshLayout;
    private Group content;
    private Group loadingView;
    private Group emptyView;
    private MaterialButton btnAdvanceSearch;
    private TextView tvPatientsCount;
    private TextView tvSearchClearMsg;
    private RecyclerView rvPatientsList;

    private Realm realm;
    private RealmResults<UhcPatient> patientRealmResults;
    private UhcPatientsListAdapter adapter;
    private boolean isSearch = false;
    private boolean isUhcDoctor = false;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_uhc_patients_list;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    protected void onViewReady(View view, @Nullable Bundle savedInstanceState) {
        bindViews(view);
        init();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_search_registration, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_registration_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(isUhcDoctor ? "Search by Name or UIC Code" : "Search by Name");
        searchView.setScrollBarSize(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit() called with: query = [" + query + "]");
                final String normalSearchQuery;
                if (MyanmarZawgyiConverter.isZawgyiEncoded(query)) {
                    normalSearchQuery = Rabbit.zg2uni(query);
                } else {
                    normalSearchQuery = query;
                }
                sendActionAnalytics(CmisGoogleAnalyticsConstants.ACTION_PATIENT_SEARCH);
                searchByNameOrUid(normalSearchQuery);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextChange() called with: newText = [" + newText + "]");

                if (TextUtils.isEmpty(newText)) {
                    if (isSearch) {
                        isSearch = false;
                        loadAllPatients();
                    }
                    return true;
                }
                return false;
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQ_ADVANCE_SEARCH && data != null) {

                @Nullable final PatientsListFilter patientsListFilter = data.getParcelableExtra(SearchActivity.RESULT_EXTRA_FILTER);
                if (patientsListFilter != null) {
                    Log.d(TAG, "onActivityResult: Patient List Filter : " + patientsListFilter);
                    isSearch = true;
                    final RealmQuery<UhcPatient> listFilterRealmQuery = patientsListFilter.getRealmQuery(realm);
                    loadPatients(listFilterRealmQuery.findAllAsync());
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        removePatientsChangeListener();
        super.onDestroyView();
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        if (isSearch) {
            isSearch = false;
            loadAllPatients();
        }
    }

    @Override
    public void onChange(@NonNull RealmResults<UhcPatient> patients) {
        Log.d(TAG, "UhcPatientsListFragment:onChange: called with " + patients.size());
        final int patientsCount = patients.size();
        final String patientsCountLabel;
        if (patientsCount > 0) {
            patientsCountLabel = patientsCount + (patientsCount > 1 ? " Patients" : " Patient");
            showPatientListUI();
        } else {
            showEmptyUI();
            patientsCountLabel = "0 Patient";
        }
        tvPatientsCount.setText(patientsCountLabel);

        AppUtils.applyVisibility(tvSearchClearMsg, isSearch ? View.VISIBLE : View.GONE);
    }

    private void bindViews(@NonNull View root) {
        swipeRefreshLayout = root.findViewById(R.id.uhcPatientsListSwipeRefresh);

        rvPatientsList = root.findViewById(R.id.uhcPatientsRvList);
        tvPatientsCount = root.findViewById(R.id.uhcPatientsListTvCount);
        tvSearchClearMsg = root.findViewById(R.id.uhcPatientsListTvClearSearchMessage);
        btnAdvanceSearch = root.findViewById(R.id.uhcPatientsListBtnAdvanceSearch);

        loadingView = root.findViewById(R.id.uhcPatientsListGroupLoadingView);
        emptyView = root.findViewById(R.id.uhcPatientsListGroupEmptyView);
        content = root.findViewById(R.id.uhcPatientsListContent);
    }

    private void init() {

        showLoadingUI();
        realm = Realm.getDefaultInstance();

        final SharePreferenceHelper sharePreferenceHelper = SharePreferenceHelper.getHelper(requireContext());
        final AuthenticationModel authenticationModel = sharePreferenceHelper.getAuthenticationModel();
        if (authenticationModel != null) {
            isUhcDoctor = authenticationModel.isPermitted(CMISConstant.FORM_CARD_HOLDER_REG);
        }

//        patientRealmResults = getPatientRealmResults();
//        patientRealmResults.addChangeListener(this);
        adapter = new UhcPatientsListAdapter(isUhcDoctor, sharePreferenceHelper.isDoctor());
        rvPatientsList.setHasFixedSize(true);
        rvPatientsList.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this);

        btnAdvanceSearch.setOnClickListener((View v) -> startActivityForResult(new Intent(getContext(), SearchActivity.class), REQ_ADVANCE_SEARCH));
        loadAllPatients();
    }

    private void removePatientsChangeListener() {
        if (patientRealmResults != null) {
            patientRealmResults.removeChangeListener(this);
        }
    }

    private void searchByNameOrUid(@Nullable final String normalSearchQuery) {
        Log.d(TAG, "searchByNameOrUid() called");
        sendActionAnalytics(CmisGoogleAnalyticsConstants.ACTION_SEARCH_BY_NAME);

        if (TextUtils.isEmpty(normalSearchQuery)) {
            isSearch = false;
            loadAllPatients();
        } else {
            isSearch = true;
            final RealmResults<UhcPatient> searchResults = realm.where(UhcPatient.class)
                    .equalTo("isDeleted", false)
                    .beginGroup()
                    .equalTo("uicCode", normalSearchQuery, Case.INSENSITIVE)
                    .or()
                    .contains("nameInEnglish", normalSearchQuery, Case.INSENSITIVE)
                    .or()
                    .contains("nameInMyanmar", normalSearchQuery, Case.INSENSITIVE)
                    .or()
                    .equalTo("hhCode", normalSearchQuery, Case.INSENSITIVE)
                    .endGroup()
                    .sort("createdTime", Sort.DESCENDING)
                    .findAllAsync();
            loadPatients(searchResults);
        }
    }

    private void showLoadingUI() {
        Log.d(TAG, "showLoadingUI() called");
        AppUtils.applyVisibility(content, View.GONE);
        AppUtils.applyVisibility(loadingView, View.VISIBLE);
        AppUtils.applyVisibility(emptyView, View.GONE);
    }

    private void showEmptyUI() {
        Log.d(TAG, "showEmptyUI() called");
        AppUtils.applyVisibility(content, View.GONE);
        AppUtils.applyVisibility(loadingView, View.GONE);
        AppUtils.applyVisibility(emptyView, View.VISIBLE);
    }

    private void showPatientListUI() {
        Log.d(TAG, "showPatientListUI() called");
        AppUtils.applyVisibility(content, View.VISIBLE);
        AppUtils.applyVisibility(loadingView, View.GONE);
        AppUtils.applyVisibility(emptyView, View.GONE);
    }

    private void loadPatients(@NonNull RealmResults<UhcPatient> patientRealmResultsToLoad) {
        removePatientsChangeListener();
        patientRealmResults = patientRealmResultsToLoad;
        patientRealmResults.addChangeListener(this);
        adapter.updateData(patientRealmResults);
        if (!patientRealmResults.isLoaded()) {
            showLoadingUI();
        } else {
            onChange(patientRealmResults);
        }
    }

    private void loadAllPatients() {
        loadPatients(getPatientRealmResults());
    }

    @NonNull
    private RealmResults<UhcPatient> getPatientRealmResults() {
        return realm.where(UhcPatient.class)
                .equalTo("isDeleted", false)
                .sort("createdTime", Sort.DESCENDING)
                .findAllAsync();
    }

}
