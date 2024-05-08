package com.koekoetech.clinic.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.koekoetech.clinic.R;
import com.koekoetech.clinic.activities.HomeActivity;
import com.koekoetech.clinic.helper.ServiceHelper;
import com.koekoetech.clinic.helper.SharePreferenceHelper;
import com.koekoetech.clinic.model.FeedbackModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FeedbackFragment extends BaseFragment {
    private static final String TAG = FeedbackFragment.class.getSimpleName();

    private EditText edtFeedback;

    private RadioButton rdoGeneral;
    private RadioButton rdoBugReport;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_feedback;
    }

    @Override
    protected void onViewReady(View view, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        bindViews(view);
        rdoGeneral.setChecked(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_feedback, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_submit) {
            SharePreferenceHelper share = SharePreferenceHelper.getHelper(getContext());
            if (TextUtils.isEmpty(edtFeedback.getText())) {
                Toast.makeText(getContext(), "Please Type Feedback", Toast.LENGTH_SHORT).show();
            } else {
                FeedbackModel model = new FeedbackModel();
                if (rdoGeneral.isChecked()) {
                    model.setFeedbackType("General Feedback");
                } else if (rdoBugReport.isChecked()) {
                    model.setFeedbackType("Bug Reports Feedback");
                }
                model.setComment(edtFeedback.getText().toString().trim());

                model.setDoctorId(share.getAuthenticationModel().getStaffId());

                if (share.getAuthenticationModel().getStaffCode() != null) {
                    model.setDoctorCode(share.getAuthenticationModel().getStaffCode());
                }
                callFeedback(model);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void bindViews(View view){
        edtFeedback = view.findViewById(R.id.feedbackList_edtFeedback);
        rdoGeneral = view.findViewById(R.id.feedbackList_rdoGeneral);
        rdoBugReport = view.findViewById(R.id.feedbackList_rdoBugReport);
    }

    private void callFeedback(final FeedbackModel model) {
        final AppProgressDialog dialog = AppProgressDialog.getProgressDialog("Submitting Feedback");
        dialog.display(requireActivity().getSupportFragmentManager());
        Log.d(TAG, "callFeedback: Model : " + "Type : " + model.getFeedbackType());
        Log.d(TAG, "callFeedback: Model : " + "Feedback : " + model.getComment());
        Log.d(TAG, "callFeedback: Model : " + "Id : " + model.getDoctorId() + " Code : " + model.getDoctorCode());
        ServiceHelper.ApiService service = ServiceHelper.getClient(getContext());
        Call<FeedbackModel> callCreate = service.postFeedback(model);
        callCreate.enqueue(new Callback<FeedbackModel>() {


            @Override
            public void onResponse(@NonNull Call<FeedbackModel> call, @NonNull Response<FeedbackModel> response) {
                dialog.safeDismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Feedback submitted successfully", Toast.LENGTH_SHORT).show();
                    Intent homeIntent = new Intent(requireContext(), HomeActivity.class);
                    homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(homeIntent);
                } else {
                    Log.d(TAG, "onResponse: " + response.message());
                    handleFailure();

                }
            }

            @Override
            public void onFailure(@NonNull Call<FeedbackModel> call, @NonNull Throwable t) {
                dialog.safeDismiss();
                Log.d(TAG, "onFailure: " + t.getLocalizedMessage());
                handleFailure();
            }

            private void handleFailure() {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Connection Problem")
                        .setMessage("Connection to the server has been lost! Try Again")
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.ok, (dialog12, which) -> callFeedback(model))
                        .setNegativeButton(android.R.string.cancel, (dialog1, which) -> dialog1.dismiss()).show();
            }
        });

    }

}
