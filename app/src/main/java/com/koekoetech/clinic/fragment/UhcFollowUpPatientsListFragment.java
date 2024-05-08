package com.koekoetech.clinic.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.koekoetech.clinic.R;
import com.koekoetech.clinic.adapter.UhcPatientsListAdapter;
import com.koekoetech.clinic.helper.AppUtils;
import com.koekoetech.clinic.helper.CMISConstant;
import com.koekoetech.clinic.helper.DateTimeHelper;
import com.koekoetech.clinic.helper.SharePreferenceHelper;
import com.koekoetech.clinic.model.AuthenticationModel;
import com.koekoetech.clinic.model.UhcPatient;
import com.koekoetech.clinic.model.UhcPatientProgress;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by ZMN on 6/6/18.
 **/
public class UhcFollowUpPatientsListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, RealmChangeListener<RealmResults<UhcPatient>> {

    private static final String TAG = "UhcFollowUpPatientsList";

    private SwipeRefreshLayout swipeRefreshLayout;
    private Group content;
    private Group loadingView;
    private Group emptyView;
    private TextView tvPatientsCount;
    private RecyclerView rvPatientsList;

    private Realm realm;
    private RealmResults<UhcPatient> followupPatientsRealmResult;
    private UhcPatientsListAdapter adapter;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_follow_up_uhc_patients_list;
    }

    @Override
    protected void onViewReady(View view, @Nullable Bundle savedInstanceState) {
        bindViews(view);
        init();
    }

    @Override
    public void onDestroyView() {
        removePatientsChangeListener();
        super.onDestroyView();
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        loadPatients(getFollowupPatientsRealmResult());
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

    }

    private void removePatientsChangeListener() {
        if (followupPatientsRealmResult != null) {
            followupPatientsRealmResult.removeChangeListener(this);
        }
    }

    private void bindViews(View root) {
        swipeRefreshLayout = root.findViewById(R.id.uhcFollowUpPatientsListSwipeRefresh);

        rvPatientsList = root.findViewById(R.id.uhcFollowUpPatientsRvList);
        tvPatientsCount = root.findViewById(R.id.uhcFollowUpPatientsListTvCount);

        loadingView = root.findViewById(R.id.uhcFollowUpPatientsListGroupLoadingView);
        emptyView = root.findViewById(R.id.uhcFollowUpPatientsListGroupEmptyView);
        content = root.findViewById(R.id.uhcFollowUpPatientsListContent);
    }

    private void init() {

        showLoadingUI();
        realm = Realm.getDefaultInstance();

        boolean isUhcDoctor = false;
        final SharePreferenceHelper sharePreferenceHelper = SharePreferenceHelper.getHelper(requireContext());
        final AuthenticationModel authenticationModel = sharePreferenceHelper.getAuthenticationModel();
        if (authenticationModel != null) {
            isUhcDoctor = authenticationModel.isPermitted(CMISConstant.FORM_CARD_HOLDER_REG);
        }

        adapter = new UhcPatientsListAdapter(isUhcDoctor,sharePreferenceHelper.isDoctor());
        rvPatientsList.setHasFixedSize(true);
        rvPatientsList.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this);

        loadPatients(getFollowupPatientsRealmResult());

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

    private RealmResults<UhcPatient> getFollowupPatientsRealmResult() {
    final RealmResults<UhcPatientProgress> followUpProgresses = realm.where(UhcPatientProgress.class)
            .equalTo("isDeleted", false)
            .isNotNull("followUpDateTime")
            .isNotEmpty("followUpDateTime")
            .distinct("patientCode")
            .sort("followUpDateTime", Sort.ASCENDING)
            .findAll();

    final ArrayList<String> followupPatientCodes = new ArrayList<>();
    for (UhcPatientProgress followUpProgress : followUpProgresses) {
        boolean isAfterToday = DateTimeHelper.isAfterToday(followUpProgress.getFollowUpDateTime(), DateTimeHelper.SERVER_DATE_TIME_FORMAT);
        if (isAfterToday) {
            followupPatientCodes.add(followUpProgress.getPatientCode());
        }
    }

    final String[] followupPatientCodesArray = followupPatientCodes.toArray(new String[0]);
    return realm.where(UhcPatient.class).in("patientCode", followupPatientCodesArray).findAllAsync();
}

    private void loadPatients(@NonNull RealmResults<UhcPatient> patientRealmResultsToLoad) {
        removePatientsChangeListener();
        followupPatientsRealmResult = patientRealmResultsToLoad;
        followupPatientsRealmResult.addChangeListener(this);
        adapter.updateData(followupPatientsRealmResult);
        if (!followupPatientsRealmResult.isLoaded()) {
            showLoadingUI();
        } else {
            onChange(followupPatientsRealmResult);
        }
    }


}
