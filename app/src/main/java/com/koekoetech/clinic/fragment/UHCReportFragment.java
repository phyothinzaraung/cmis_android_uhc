package com.koekoetech.clinic.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.koekoetech.clinic.BuildConfig;
import com.koekoetech.clinic.R;
import com.koekoetech.clinic.activities.GenericWebViewActivity;
import com.koekoetech.clinic.helper.BaseUrlProvider;
import com.koekoetech.clinic.helper.CMISConstant;
import com.koekoetech.clinic.model.AuthenticationModel;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import io.realm.Realm;

public class UHCReportFragment extends BaseFragment {

    private static final String TAG = UHCReportFragment.class.getSimpleName();

    private CardView cvSummaryReport;
    private CardView cvVisitsCount;

    private AuthenticationModel authenticationModel;


    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_uhcreport;
    }

    @Override
    protected void onViewReady(View view, @Nullable Bundle savedInstanceState) {

        bindViews(view);

        authenticationModel = Realm.getDefaultInstance().where(AuthenticationModel.class).findFirst();

        // Inflate the layout for this fragment

        if (authenticationModel != null) {
            final String webViewBaseUrl = BaseUrlProvider.getWebViewBaseUrl();
            cvSummaryReport.setOnClickListener(v -> {
                final String path = authenticationModel.isPermitted(CMISConstant.FORM_CARD_HOLDER_REG) ? BuildConfig.UHC_PROVIDER_REPORT_WEB_VIEW_URL : BuildConfig.PROVIDER_REPORT_WEB_VIEW_URL;
                String url = webViewBaseUrl + path + authenticationModel.getStaffId();
                Log.d(TAG, "onViewCreated: Provider Report Web View URL " + url);
                startActivity(GenericWebViewActivity.getNewIntent(url, getString(R.string.title_report_summary), true, requireContext()));
            });

            cvVisitsCount.setOnClickListener(v -> {
                String url = webViewBaseUrl + BuildConfig.VISITS_COUNT_WEB_VIEW_URL + authenticationModel.getStaffId();
                Log.d(TAG, "onViewCreated: Visits Count Web View URL " + url);
                startActivity(GenericWebViewActivity.getNewIntent(url, getString(R.string.title_report_visits_count), true, requireContext()));
            });
        }
    }

    private void bindViews(View view){
        cvSummaryReport = view.findViewById(R.id.uhcReport_cvSummaryReport);
        cvVisitsCount = view.findViewById(R.id.uhcReport_cvVisitsCount);
    }
}
