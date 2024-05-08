package com.koekoetech.clinic.helper;

import android.os.Bundle;
import androidx.annotation.DrawableRes;
import com.koekoetech.clinic.R;
import com.koekoetech.clinic.model.PressNoteModel;

import java.util.ArrayList;

/**
 * Created by ZMN on 3/31/17.
 **/

public class PressNoteTypeHelper {

    private static PressNoteTypeHelper typeHelper;

    private Bundle prefixBundle;
    private Bundle typeBackgroundBundle;
    private ArrayList<PressNoteModel> pressNoteModelsList;

    private PressNoteTypeHelper() {
        prefixBundle = preparePrefixBundle();
        typeBackgroundBundle = prepareTypeBackgroundBundle();
        pressNoteModelsList = populatePressNotes();
    }

    public static PressNoteTypeHelper getTypeHelper() {
        if (typeHelper == null) {
            typeHelper = new PressNoteTypeHelper();
        }
        return typeHelper;
    }

    private Bundle preparePrefixBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(CMISConstant.PRESSNOTE_REASON, CMISConstant.PRESSNOTE_REASON_PREFIX);
        bundle.putString(CMISConstant.PRESSNOTE_COMPLAIN, CMISConstant.PRESSNOTE_COMPLAIN_PREFIX);
        bundle.putString(CMISConstant.PRESSNOTE_Diagnosis, CMISConstant.PRESSNOTE_DIAGNOSIS_PREFIX);
        bundle.putString(CMISConstant.PRESSNOTE_OBSERVATION, CMISConstant.PRESSNOTE_OBSERVATION_PREFIX);
        bundle.putString(CMISConstant.PRESSNOTE_INVESTIGATION, CMISConstant.PRESSNOTE_INVESTIGATION_PREFIX);
        bundle.putString(CMISConstant.PRESSNOTE_PROBLEM, CMISConstant.PRESSNOTE_PROBLEM_PREFIX);
        bundle.putString(CMISConstant.PRESSNOTE_REFERRAL, CMISConstant.PRESSNOTE_REFERRAL_PREFIX);
        bundle.putString(CMISConstant.PRESSNOTE_REQUEST, CMISConstant.PRESSNOTE_REQUESTS_PREFIX);
        bundle.putString(CMISConstant.PRESSNOTE_TREATMENT, CMISConstant.PRESSNOTE_TREATMENT_PREFIX);
        bundle.putString(CMISConstant.PRESSNOTE_SERVICE, CMISConstant.PRESSNOTE_SERVICE_PREFIX);
        bundle.putString(CMISConstant.PRESSNOTE_CARE, CMISConstant.PRESSNOTE_CARE_PREFIX);
        bundle.putString(CMISConstant.PRESSNOTE_INVESTIGATION, CMISConstant.PRESSNOTE_INVESTIGATION_PREFIX);
        return bundle;
    }

    private Bundle prepareTypeBackgroundBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt(CMISConstant.PRESSNOTE_REASON, R.drawable.progress_note_reason_bg);
        bundle.putInt(CMISConstant.PRESSNOTE_COMPLAIN, R.drawable.progress_note_compliant_bg);
        bundle.putInt(CMISConstant.PRESSNOTE_Diagnosis, R.drawable.progress_note_diagnosis_bg);
        bundle.putInt(CMISConstant.PRESSNOTE_OBSERVATION, R.drawable.progress_note_observation_bg);
        bundle.putInt(CMISConstant.PRESSNOTE_PROBLEM, R.drawable.progress_note_problem_bg);
        bundle.putInt(CMISConstant.PRESSNOTE_REFERRAL, R.drawable.progress_note_referral_bg);
        bundle.putInt(CMISConstant.PRESSNOTE_REQUEST, R.drawable.progress_note_requests_bg);
        bundle.putInt(CMISConstant.PRESSNOTE_TREATMENT, R.drawable.progress_note_treatment_bg);
        bundle.putInt(CMISConstant.PRESSNOTE_SERVICE, R.drawable.progress_note_service_bg);
        bundle.putInt(CMISConstant.PRESSNOTE_CARE, R.drawable.progress_note_treatment_bg);
        bundle.putInt(CMISConstant.PRESSNOTE_INVESTIGATION, R.drawable.progress_note_investigation_bg);
        return bundle;
    }

    public String getPrefixByType(String type) {
        return prefixBundle.getString(type);
    }

    @DrawableRes
    public int getTypeBackground(String type) {
        return typeBackgroundBundle.getInt(type);
    }

    public ArrayList<PressNoteModel> getPressNotesList() {
        return pressNoteModelsList;
    }

    public PressNoteModel getPressNoteModelByType(String type) {
        for (PressNoteModel pnm : pressNoteModelsList) {
            if (pnm.getTitle().equals(type)) {
                return pnm;
            }
        }
        return null;
    }

    public ArrayList<String> getPressNoteTypes() {
        ArrayList<String> pressNoteTypes = new ArrayList<>();
        for (PressNoteModel pnm : pressNoteModelsList) {
            pressNoteTypes.add(pnm.getTitle());
        }
        return pressNoteTypes;
    }

    private ArrayList<PressNoteModel> populatePressNotes() {
        ArrayList<PressNoteModel> pressNoteModels = new ArrayList<>();

        PressNoteModel reason = new PressNoteModel();
        reason.setBackground(R.drawable.progress_note_reason_bg);
        reason.setPreFix(CMISConstant.PRESSNOTE_REASON_PREFIX);
        reason.setTitle(CMISConstant.PRESSNOTE_REASON);
        reason.setPreFixTextColor("#A569BD");
        pressNoteModels.add(reason);

        PressNoteModel observation = new PressNoteModel();
        observation.setBackground(R.drawable.progress_note_observation_bg);
        observation.setPreFix(CMISConstant.PRESSNOTE_OBSERVATION_PREFIX);
        observation.setTitle(CMISConstant.PRESSNOTE_OBSERVATION);
        observation.setPreFixTextColor("#0AC92A");
        pressNoteModels.add(observation);

        PressNoteModel investigation = new PressNoteModel();
        investigation.setBackground(R.drawable.progress_note_investigation_bg);
        investigation.setPreFix(CMISConstant.PRESSNOTE_INVESTIGATION_PREFIX);
        investigation.setTitle(CMISConstant.PRESSNOTE_INVESTIGATION);
        investigation.setPreFixTextColor("#DC7633");
        pressNoteModels.add(investigation);

        PressNoteModel diagnosis = new PressNoteModel();
        diagnosis.setBackground(R.drawable.progress_note_diagnosis_bg);
        diagnosis.setPreFix(CMISConstant.PRESSNOTE_DIAGNOSIS_PREFIX);
        diagnosis.setTitle(CMISConstant.PRESSNOTE_Diagnosis);
        diagnosis.setPreFixTextColor("#d13976");
        pressNoteModels.add(diagnosis);

        PressNoteModel treatment = new PressNoteModel();
        treatment.setBackground(R.drawable.progress_note_treatment_bg);
        treatment.setPreFix(CMISConstant.PRESSNOTE_CARE_PREFIX);
        treatment.setTitle(CMISConstant.PRESSNOTE_CARE);
        treatment.setPreFixTextColor("#CACE08");
        pressNoteModels.add(treatment);

//        PressNoteModel referral = new PressNoteModel();
//        referral.setBackground(R.drawable.progress_note_referral_bg);
//        referral.setPreFix(CMISConstant.PRESSNOTE_REFERRAL_PREFIX);
//        referral.setTitle(CMISConstant.PRESSNOTE_REFERRAL);
//        referral.setPreFixTextColor("#FD04C7");
//        pressNoteModels.add(referral);

        return pressNoteModels;

    }

}
