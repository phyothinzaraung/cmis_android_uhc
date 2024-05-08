package com.koekoetech.clinic.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.koekoetech.clinic.BuildConfig;
import com.koekoetech.clinic.R;
import com.koekoetech.clinic.activities.DataSyncActivity;
import com.koekoetech.clinic.activities.DisabilitySurveyActivity;
import com.koekoetech.clinic.activities.GenericWebViewActivity;
import com.koekoetech.clinic.activities.ImmunizationListActivity;
import com.koekoetech.clinic.activities.UhcFormsChoiceActivity;
import com.koekoetech.clinic.app.GlideApp;
import com.koekoetech.clinic.helper.BaseUrlProvider;
import com.koekoetech.clinic.helper.CMISConstant;
import com.koekoetech.clinic.helper.CmisGoogleAnalyticsConstants;
import com.koekoetech.clinic.helper.DateTimeHelper;
import com.koekoetech.clinic.helper.GAHelper;
import com.koekoetech.clinic.helper.InMemoryAppStore;
import com.koekoetech.clinic.helper.SharePreferenceHelper;
import com.koekoetech.clinic.helper.TextViewUtils;
import com.koekoetech.clinic.interfaces.PatientRecordsCallBack;
import com.koekoetech.clinic.model.AuthenticationModel;
import com.koekoetech.clinic.model.DisabilitySurvey;
import com.koekoetech.clinic.model.KeyValueText;
import com.koekoetech.clinic.model.StaffModel;
import com.koekoetech.clinic.model.UhcPatient;
import com.koekoetech.clinic.model.UhcPatientProgress;
import com.koekoetech.clinic.model.UhcPatientProgressNote;
import com.koekoetech.clinic.model.UhcPatientProgressNotePhoto;

import java.text.DecimalFormat;
import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by ZMN on 8/27/17.
 **/

public class UhcPatientsRecordAdapter extends BaseRecyclerViewAdapter {

    private static final String TAG = UhcPatientsRecordAdapter.class.getSimpleName();
    private Realm realm;
    private PatientRecordsCallBack patientRecordsCallBack;
    private UhcPatient uhcPatient;
    private boolean isUhcDoctor = false;

    public UhcPatientsRecordAdapter(UhcPatient patient) {
        this.realm = Realm.getDefaultInstance();
        this.uhcPatient = patient;
        AuthenticationModel authenticationModel = realm.where(AuthenticationModel.class).findFirst();
        if (authenticationModel != null) {
            this.isUhcDoctor = authenticationModel.isPermitted(CMISConstant.FORM_CARD_HOLDER_REG);
        }
    }

    public void registerPatientRecordsCallBack(PatientRecordsCallBack patientRecordsCallBack) {
        this.patientRecordsCallBack = patientRecordsCallBack;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateCustomViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_uhc_patient_record, parent, false);
        return new PatientProgressRecordHolder(view);
    }

    @Override
    protected void onBindCustomViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((PatientProgressRecordHolder) holder).bindPatientProgressRecord((UhcPatientProgress) getItemsList().get(position));
    }

    @Override
    protected RecyclerView.ViewHolder onCreateCustomHeaderViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header_patient_detail, parent, false);
        return new UhcPatientDetailHeaderHolder(view);
    }

    @Override
    protected void onBindCustomHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        RecyclerHeader recyclerHeader = (RecyclerHeader) getItemsList().get(position);
        ((UhcPatientDetailHeaderHolder) holder).bind((UhcPatient) recyclerHeader.getHeaderData());
    }

    class UhcPatientDetailHeaderHolder extends RecyclerView.ViewHolder {

        private TextView tvGeneralInfo;
        private TextView tvUicCode;
        private TextView tvBodyMeasurement;
        private TextView tvBmi;
        private TextView tvAllergies;
        private TextView tvRiskGroup;
        private TextView tvPregnancyStatus;
        private TextView tvContraceptiveStatus;
        private TextView tvImmunizationStatus;
        private TextView tvPastMedicalHistory;
        private TextView tvPastSurgicalHistory;
        private TextView tvRelevantFamilyHistory;

        private ImageView ivProfile;
        private ImageView ivSurveyCompleteIndicator;

        private LinearLayout immunizationStatusContainer;
        private LinearLayout modulesContainer;
        private LinearLayout llDisabilitySurveyContainer;

        private View dividerView;
        private View disabilitySurveyDivider;

        private Button btnNCD;

        UhcPatientDetailHeaderHolder(View itemView) {
            super(itemView);
            bindViews(itemView);
        }

        private void bindViews(View view){
            tvGeneralInfo = view.findViewById(R.id.headerPatientDetail_tvGeneralInfo);
            tvUicCode = view.findViewById(R.id.headerPatientDetail_tvUicCode);
            tvBodyMeasurement = view.findViewById(R.id.headerPatientDetail_tvBodyMeasurement);
            tvBmi = view.findViewById(R.id.headerPatientDetail_tvBmi);
            tvAllergies = view.findViewById(R.id.headerPatientDetail_tvAllergies);
            tvRiskGroup = view.findViewById(R.id.headerPatientDetail_tvRiskGroup);
            tvPregnancyStatus = view.findViewById(R.id.headerPatientDetail_tvPregnancyStatus);
            tvContraceptiveStatus = view.findViewById(R.id.headerPatientDetail_tvContraceptiveStatus);
            tvImmunizationStatus = view.findViewById(R.id.headerPatientDetail_tvImmunizationStatus);
            tvPastMedicalHistory = view.findViewById(R.id.headerPatientDetail_tvPastMedicalHistory);
            tvPastSurgicalHistory = view.findViewById(R.id.headerPatientDetail_tvPastSurgicalHistory);
            tvRelevantFamilyHistory = view.findViewById(R.id.headerPatientDetail_tvPastRelevantFamilyHistory);
            ivProfile = view.findViewById(R.id.headerPatientDetail_ivProfile);
            immunizationStatusContainer = view.findViewById(R.id.headerPatientDetail_immunizationStatusContainer);
            dividerView = view.findViewById(R.id.headerPatientDetail_divider);
            modulesContainer = view.findViewById(R.id.headerPatientDetail_modulesNavContainer);
            btnNCD = view.findViewById(R.id.headerPatientDetail_btnNCD);
            llDisabilitySurveyContainer = view.findViewById(R.id.headerPatientDetail_DisabilitySurveyContainer);
            disabilitySurveyDivider = view.findViewById(R.id.headerPatientDetail_DisabilitySurveyDivider);
            ivSurveyCompleteIndicator = view.findViewById(R.id.headerPatientDetail_ivSurveyComplete);
        }

        void bind(UhcPatient uhcPatient) {
            final Context context = itemView.getContext();
            String photo = !TextUtils.isEmpty(uhcPatient.getLocal_filepath()) ? uhcPatient.getLocal_filepath() : uhcPatient.getPhotoUrl();
            GlideApp.with(context)
                    .load(photo)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate()
                    .placeholder(R.mipmap.sample1)
                    .error(R.mipmap.sample1)
                    .into(ivProfile);

            String ageUnit = "";
            if (!TextUtils.isEmpty(uhcPatient.getAgeType())) {
                if (uhcPatient.getAgeType().equalsIgnoreCase("Year")) {
                    ageUnit = "yrs";
                } else if (uhcPatient.getAgeType().equalsIgnoreCase("Month")) {
                    ageUnit = "months";
                }
            }
            String age = uhcPatient.getAge() + " " + ageUnit;
            String[] generalInfo = {uhcPatient.getNameInEnglish(), age, uhcPatient.getGender()};
            TextViewUtils.populateMultiItemTv(tvGeneralInfo, generalInfo);

            KeyValueText uicCode = new KeyValueText(context.getString(R.string.patientDetailLbl_uicCode), uhcPatient.getUicCode());
            TextViewUtils.populateKeyValueTv(tvUicCode, uicCode);

            String txtWeight = "-";
            String txtHeight = "-";
            String txtBmi = "-";

            try {
                DecimalFormat df = new DecimalFormat("###.#");

                if (uhcPatient.getBodyWeight() > 0) {
                    double weight = uhcPatient.getBodyWeight();
                    if (weight > 0) {
                        txtWeight = df.format(weight) + " lbs ";
                    }
                } else {
                    Log.w(TAG, "bindHeader: Body Weight is NULL");
                }

                if (uhcPatient.getHeightIn_in() > 0) {
                    double height = uhcPatient.getHeightIn_in();
                    if (height > 0) {
                        Double height_feet = height / 12;
                        Double height_inches = height % 12;
                        txtHeight = height_feet.intValue() + " feet " + height_inches.intValue() + " inches ";
                    }
                } else {
                    Log.w(TAG, "bindHeader: Body Height is NULL");
                }

                if (uhcPatient.getBmi() > 0) {
                    txtBmi = df.format(uhcPatient.getBmi());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            KeyValueText weight = new KeyValueText(context.getString(R.string.patientDetailLbl_weight), txtWeight);
            KeyValueText height = new KeyValueText(context.getString(R.string.patientDetailLbl_height), txtHeight);
            TextViewUtils.populateKeyValueTv(tvBodyMeasurement, weight, height);

            KeyValueText bmi = new KeyValueText(context.getString(R.string.patientDetailLbl_bmi), txtBmi);
            TextViewUtils.populateKeyValueTv(tvBmi, bmi);

            KeyValueText allergies = new KeyValueText(context.getString(R.string.patientDetailLbl_allergies), uhcPatient.getKnownAllergies());
            TextViewUtils.populateKeyValueTv(tvAllergies, allergies);

            KeyValueText riskGroup = new KeyValueText(context.getString(R.string.patientDetailLbl_riskFactors), uhcPatient.getRiskGroup());
            TextViewUtils.populateKeyValueTv(tvRiskGroup, riskGroup);

            if (!TextUtils.isEmpty(uhcPatient.getPreviousMedicalHistory())) {
                tvPastMedicalHistory.setVisibility(View.VISIBLE);
                KeyValueText pastMedicalHistory = new KeyValueText(context.getString(R.string.patientDetailLbl_pastMedicalHistory), uhcPatient.getPreviousMedicalHistory());
                TextViewUtils.populateKeyValueTv(tvPastMedicalHistory, pastMedicalHistory);
            } else {
                tvPastMedicalHistory.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(uhcPatient.getPreviousSurgicalHistory())) {
                tvPastSurgicalHistory.setVisibility(View.VISIBLE);
                KeyValueText pastSurgicalHistory = new KeyValueText(context.getString(R.string.patientDetailLbl_pastSurgicalHistory), uhcPatient.getPreviousSurgicalHistory());
                TextViewUtils.populateKeyValueTv(tvPastSurgicalHistory, pastSurgicalHistory);
            } else {
                tvPastSurgicalHistory.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(uhcPatient.getRelevantFamilyHistory())) {
                tvRelevantFamilyHistory.setVisibility(View.VISIBLE);
                KeyValueText relevantFamilyHistory = new KeyValueText(context.getString(R.string.patientDetailLbl_relevantFamilyHistory), uhcPatient.getRelevantFamilyHistory());
                TextViewUtils.populateKeyValueTv(tvRelevantFamilyHistory, relevantFamilyHistory);
            } else {
                tvRelevantFamilyHistory.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(uhcPatient.getGender()) && uhcPatient.getGender().equalsIgnoreCase("Female")) {
                tvPregnancyStatus.setVisibility(View.VISIBLE);
                KeyValueText pregnancyStatus = new KeyValueText(context.getString(R.string.patientDetailLbl_pregnancyStatus), uhcPatient.isPregnancy() ? "pregnant" : "not pregnant");
                TextViewUtils.populateKeyValueTv(tvPregnancyStatus, pregnancyStatus);

                tvContraceptiveStatus.setVisibility(View.VISIBLE);
                KeyValueText contraceptiveStatus = new KeyValueText(context.getString(R.string.patientDetailLbl_contraceptiveStatus), uhcPatient.getCurrentContraceptionStatus());
                TextViewUtils.populateKeyValueTv(tvContraceptiveStatus, contraceptiveStatus);

            } else {
                tvPregnancyStatus.setVisibility(View.GONE);
                tvContraceptiveStatus.setVisibility(View.GONE);
            }

            String immunizationStatus = context.getString(R.string.patientDetailLbl_immunizationStatus);
            if (!TextUtils.isEmpty(uhcPatient.getImmunizationStatus())) {
                immunizationStatus += " (" + uhcPatient.getImmunizationStatus() + ")";
            }
            tvImmunizationStatus.setText(immunizationStatus);

            final String immunizations = uhcPatient.getImmunizations();

            boolean isAgeYearExceedsRange = uhcPatient.getAgeType().equalsIgnoreCase("Year") && uhcPatient.getAge() > 2;
            boolean isAgeMonthExceedsRange = uhcPatient.getAgeType().equalsIgnoreCase("Month") && uhcPatient.getAge() > 24;
            if (isAgeYearExceedsRange || isAgeMonthExceedsRange) {
                immunizationStatusContainer.setVisibility(View.GONE);
                dividerView.setVisibility(View.GONE);
            } else {
                immunizationStatusContainer.setVisibility(View.VISIBLE);
                dividerView.setVisibility(View.VISIBLE);
            }

            immunizationStatusContainer.setOnClickListener(v -> {
                if (!TextUtils.isEmpty(immunizations)) {
                    Intent viewIntent = ImmunizationListActivity.getViewIntent(context, immunizations);
                    context.startActivity(viewIntent);
                }
            });

            int visibility = isUhcDoctor ? View.VISIBLE : View.GONE;
            tvUicCode.setVisibility(visibility);
            tvBodyMeasurement.setVisibility(visibility);
            tvBmi.setVisibility(visibility);
            tvRiskGroup.setVisibility(visibility);

            llDisabilitySurveyContainer.setOnClickListener(v -> context.startActivity(DisabilitySurveyActivity.getSurveyIntent(context, uhcPatient.getPatientCode())));

            final long surveyCount = realm.where(DisabilitySurvey.class)
                    .equalTo("patientCode", uhcPatient.getPatientCode())
                    .count();

            ivSurveyCompleteIndicator.setVisibility(surveyCount > 0 ? View.VISIBLE : View.GONE);

            if (shouldShowNCDButton()) {
                modulesContainer.setVisibility(View.VISIBLE);
                btnNCD.setOnClickListener(v -> {
                    UhcPatient currentPatient = realm.where(UhcPatient.class).equalTo("patientCode", uhcPatient.getPatientCode()).findFirst();
                    if (currentPatient != null) {
                        if (currentPatient.isHasSynced()) {
                            GAHelper.sendEvent(context,CmisGoogleAnalyticsConstants.PATIENT_DETAIL, CmisGoogleAnalyticsConstants.ACTION_NCD_MODULE);
                            String ncdUrl = BaseUrlProvider.getWebViewBaseUrl() + BuildConfig.NCD_URL + currentPatient.getPatientCode();
                            context.startActivity(GenericWebViewActivity.getNewIntent(ncdUrl, "NCD", false, context));
//                            RealmResults<UhcPatientProgress> progresses = realm.where(UhcPatientProgress.class)
//                                    .equalTo("patientCode", currentPatient.getPatientCode())
//                                    .findAll()
//                                    .sort("createdTime", Sort.DESCENDING);
//                            if (progresses != null && !progresses.isEmpty()) {
//                                UhcPatientProgress latestProgress = progresses.get(0);
//                                if (latestProgress != null) {
//                                    GAHelper.sendEvent(CmisGoogleAnalyticsConstants.PATIENT_DETAIL,CmisGoogleAnalyticsConstants.ACTION_NCD_MODULE);
//                                    String latestProgressCode = latestProgress.getProgressCode();
//                                    Log.d(TAG, "bind: Latest Progress Code : " + latestProgressCode);
//                                    String dummyUrl = "https://guides.codepath.com/android";
//                                    context.startActivity(GenericWebViewActivity.getNewIntent(dummyUrl, "NCD", false, context));
//                                }
//                            }
                        } else {
                            new AlertDialog.Builder(context)
                                    .setTitle(R.string.dialog_title_report_patient_sync_req)
                                    .setMessage(R.string.dialog_msg_report_patient_sync_req)
                                    .setPositiveButton(R.string.dialog_action_sync, (dialog, which) -> {
                                        dialog.dismiss();
                                        InMemoryAppStore.getInstance().setDetailPatientCode(currentPatient.getPatientCode());
                                        InMemoryAppStore.getInstance().setDetailImplicitNCDLaunch(true);
                                        Intent dataSyncIntent = DataSyncActivity.getNewIntent(context, false);
                                        dataSyncIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        context.startActivity(dataSyncIntent);
                                    })
                                    .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss()).show();
                        }
                    }
                });
            } else {
                modulesContainer.setVisibility(View.GONE);
            }

        }

        private boolean shouldShowNCDButton() {

            long progressNotesCountWithHypertension = realm.where(UhcPatientProgressNote.class)
                    .equalTo("patientCode", uhcPatient.getPatientCode())
                    .equalTo("type", "Diagnosis")
                    .contains("note", "hypertension", Case.INSENSITIVE)
                    .equalTo("isDeleted", false).count();
            return progressNotesCountWithHypertension > 0;

        }
    }

    class PatientProgressRecordHolder extends RecyclerView.ViewHolder {

        private final String seeSummery;
        private final String seeNotes;

        private TextView tvPostedDate;
        private TextView tvReferLbl;
        private TextView tvReferToDoctorName;
        private TextView tvSummeryDetailToggle;
        private TextView tvDiagnosis;
        private TextView tvTreatment;
        private TextView tvFollowUpDate;
        private TextView tvCharges;

        private RecyclerView rvProgressNotes;
        private RecyclerView rvProgressNotePhotos;

        private LinearLayout summeryContainerLayout;

        private RelativeLayout deleteLayout;
        private RelativeLayout addEditLayout;
        private RelativeLayout doneLayout;

        private Context mContext;
        private UhcPatientProgressNoteAdapter progressNoteAdapter;
        private UhcPatientProgressNotePhotoAdapter photoAdapter;
        private AuthenticationModel authenticationModel;

        PatientProgressRecordHolder(View itemView) {
            super(itemView);
            bindViews(itemView);
            mContext = itemView.getContext();
            authenticationModel = SharePreferenceHelper.getHelper(mContext).getAuthenticationModel();
            seeSummery = mContext.getString(R.string.item_uhc_patient_record_lbl_see_summery);
            seeNotes = mContext.getString(R.string.item_uhc_patient_record_lbl_see_notes);
        }

        private void bindViews(View view){
            tvPostedDate = view.findViewById(R.id.item_uhcPatientRecord_tvPostedDate);
            tvReferLbl = view.findViewById(R.id.item_uhcPatientRecord_tvLblReferTo);
            tvReferToDoctorName = view.findViewById(R.id.item_uhcPatientRecord_tvReferToDoctorName);
            tvSummeryDetailToggle = view.findViewById(R.id.item_uhcPatientRecord_tvSummaryDetailToggle);
            tvDiagnosis = view.findViewById(R.id.item_uhcPatientRecord_tvSummaryDiagnosis);
            tvTreatment = view.findViewById(R.id.item_uhcPatientRecord_tvSummaryTreatment);
            tvFollowUpDate = view.findViewById(R.id.item_uhcPatientRecord_tvSummaryFollowUpDate);
            tvCharges = view.findViewById(R.id.item_uhcPatientRecord_tvSummaryCharges);
            rvProgressNotes = view.findViewById(R.id.item_uhcPatientRecord_rvProgressNotes);
            rvProgressNotePhotos = view.findViewById(R.id.item_uhcPatientRecord_rvProgressNotePhotos);
            summeryContainerLayout = view.findViewById(R.id.item_uhcPatientRecord_summeryContainer);
            deleteLayout = view.findViewById(R.id.item_uhcPatientRecord_rlDeleteContainer);
            addEditLayout = view.findViewById(R.id.item_uhcPatientRecord_rlAddEditContainer);
            doneLayout = view.findViewById(R.id.item_uhcPatientRecord_rlDoneContainer);
        }

        void bindPatientProgressRecord(UhcPatientProgress progress) {
            Log.d(TAG, "bindPatientRecord() called with: patient code = [" + progress.getPatientCode() + "]");
            Log.d(TAG, "bindPatientRecord() called with: progress created time = [" + progress.getCreatedTime() + "]");

            String displayCreatedDate = "";

            if (!TextUtils.isEmpty(progress.getVisitDate())) {
                displayCreatedDate = DateTimeHelper.convertDateFormat(progress.getVisitDate(), DateTimeHelper.SERVER_DATE_TIME_FORMAT, DateTimeHelper.LOCAL_DATE_TIME_DISPLAY_FORMAT);
            } else {
                if (!TextUtils.isEmpty(progress.getCreatedTime())) {
                    displayCreatedDate = DateTimeHelper.convertDateFormat(progress.getCreatedTime(), DateTimeHelper.SERVER_DATE_TIME_FORMAT, DateTimeHelper.LOCAL_DATE_TIME_DISPLAY_FORMAT);
                }
            }

            tvPostedDate.setText(displayCreatedDate);

            RealmResults<UhcPatientProgressNote> progressNotes = realm.where(UhcPatientProgressNote.class)
                    .equalTo("progressCode", progress.getProgressCode())
                    .and()
                    .equalTo("isDeleted", false)
                    .findAll().sort("type", Sort.DESCENDING);

            progressNoteAdapter = new UhcPatientProgressNoteAdapter(progressNotes);
            rvProgressNotes.setAdapter(progressNoteAdapter);

            if (progress.getCreatedBy() != authenticationModel.getStaffId()) {
                // Other Doctor's Patient
                StaffModel patientOwner = realm.where(StaffModel.class)
                        .equalTo("ID", progress.getCreatedBy())
                        .and()
                        .equalTo("IsDeleted", false).findFirst();
                if (patientOwner != null) {
                    tvReferLbl.setVisibility(View.VISIBLE);
                    tvReferToDoctorName.setVisibility(View.VISIBLE);
                    tvReferLbl.setText(mContext.getString(R.string.item_uhc_patient_record_lbl_refer_from));
                    tvReferToDoctorName.setText(patientOwner.getName());
                } else {
                    tvReferLbl.setVisibility(View.GONE);
                    tvReferToDoctorName.setVisibility(View.GONE);
                }

            } else {
                // Logged in doctor's Patient
                if (!TextUtils.isEmpty(progress.getReferToDoctorName())) {
                    tvReferLbl.setVisibility(View.VISIBLE);
                    tvReferToDoctorName.setVisibility(View.VISIBLE);
                    tvReferLbl.setText(mContext.getString(R.string.item_uhc_patient_record_lbl_refer_to));
                    tvReferToDoctorName.setText(progress.getReferToDoctorName());
                } else {
                    tvReferLbl.setVisibility(View.GONE);
                    tvReferToDoctorName.setVisibility(View.GONE);
                }
            }

//            if (!TextUtils.isEmpty(progress.getReferToDoctorName())) {
//                tvReferLbl.setVisibility(View.VISIBLE);
//                tvReferToDoctorName.setVisibility(View.VISIBLE);
//                tvReferToDoctorName.setText(progress.getReferToDoctorName());
//                String referToLabel;
//                if (authenticationModel.getStaffId() == progress.getReferToDoctor()) {
//                    referToLabel = mContext.getString(R.string.item_uhc_patient_record_lbl_refer_to);
//                } else {
//                    referToLabel = mContext.getString(R.string.item_uhc_patient_record_lbl_refer_from);
//                }
//                tvReferLbl.setText(referToLabel);
//            } else {
//                tvReferLbl.setVisibility(View.GONE);
//                tvReferToDoctorName.setVisibility(View.GONE);
//            }

            ArrayList<UhcPatientProgressNotePhoto> photos = new ArrayList<>();

            for (UhcPatientProgressNote progressNote : progressNotes) {
                RealmResults<UhcPatientProgressNotePhoto> progressNotePhotos = realm.where(UhcPatientProgressNotePhoto.class)
                        .equalTo("progressNoteCode", progressNote.getProgressNoteCode())
                        .and()
                        .equalTo("isDeleted", false)
                        .findAll();

                for (UhcPatientProgressNotePhoto p : progressNotePhotos) {
                    UhcPatientProgressNotePhoto photoCopy = new UhcPatientProgressNotePhoto(p);
                    photoCopy.setType(progressNote.getType());
                    photos.add(photoCopy);
                }

            }

            photoAdapter = new UhcPatientProgressNotePhotoAdapter(false, true);
            rvProgressNotePhotos.setNestedScrollingEnabled(false);
            rvProgressNotePhotos.setHasFixedSize(true);
            rvProgressNotePhotos.setAdapter(photoAdapter);
            photoAdapter.replacePhotos(photos);

            if (progress.getCharges() != null || !TextUtils.isEmpty(progress.getOutDiagnosis()) || !TextUtils.isEmpty(progress.getPlanOfCare()) || !TextUtils.isEmpty(progress.getFollowUpDateTime()) || !TextUtils.isEmpty(progress.getFollowUpReason())) {
                tvSummeryDetailToggle.setVisibility(View.VISIBLE);
                tvSummeryDetailToggle.setOnClickListener(v -> toggleSummeryDetail(tvSummeryDetailToggle.getText().toString(), progress));
                toggleSummeryDetail(seeSummery, progress);
            } else {
                tvSummeryDetailToggle.setVisibility(View.GONE);
                toggleSummeryDetail(seeNotes, progress);
            }

            deleteLayout.setOnClickListener(v -> {
                if (patientRecordsCallBack != null) {
                    patientRecordsCallBack.onDelete(progress.getProgressCode());
                }
            });

            addEditLayout.setOnClickListener(v -> {
                if (patientRecordsCallBack != null) {
                    patientRecordsCallBack.onEdit(progress.getProgressCode());
                }
            });

            doneLayout.setOnClickListener(v -> {
                Intent uhcFormChoiceIntent = UhcFormsChoiceActivity.newIntent(uhcPatient, "UhcFollowUp", progress.getProgressCode(), mContext);
                mContext.startActivity(uhcFormChoiceIntent);
            });
        }

        private void toggleSummeryDetail(String mode, UhcPatientProgress progress) {
            if (mode.equals(seeNotes)) {
                rvProgressNotes.setVisibility(View.VISIBLE);
                rvProgressNotePhotos.setVisibility(View.VISIBLE);
                summeryContainerLayout.setVisibility(View.GONE);
                tvSummeryDetailToggle.setText(seeSummery);
            } else if (mode.equals(seeSummery)) {
                rvProgressNotes.setVisibility(View.GONE);
                rvProgressNotePhotos.setVisibility(View.GONE);
                summeryContainerLayout.setVisibility(View.VISIBLE);
                tvSummeryDetailToggle.setText(seeNotes);

                KeyValueText diagnosisText = new KeyValueText(mContext.getString(R.string.item_uhc_patient_record_lbl_diagnosis), progress.getOutDiagnosis());
                TextViewUtils.populateKeyValueTv(tvDiagnosis, diagnosisText);

                KeyValueText treatmentText = new KeyValueText(mContext.getString(R.string.item_uhc_patient_record_lbl_treatment), progress.getPlanOfCare());
                TextViewUtils.populateKeyValueTv(tvTreatment, treatmentText);

                String followUpDateTime = DateTimeHelper.convertDateFormat(progress.getFollowUpDateTime(), DateTimeHelper.SERVER_DATE_TIME_FORMAT, DateTimeHelper.LOCAL_DATE_DISPLAY_FORMAT);
                KeyValueText followUpDateTimeText = new KeyValueText(mContext.getString(R.string.item_uhc_patient_record_lbl_follow_up_date), followUpDateTime);
                TextViewUtils.populateKeyValueTv(tvFollowUpDate, followUpDateTimeText);

                String charges = "";
                if (progress.getCharges() != null) {
                    charges = progress.getCharges().toString();
                }
                KeyValueText chargesText = new KeyValueText(mContext.getString(R.string.item_uhc_patient_record_lbl_charges), charges);
                TextViewUtils.populateKeyValueTv(tvCharges, chargesText);

            }
        }
    }

}
