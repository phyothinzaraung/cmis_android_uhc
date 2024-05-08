package com.koekoetech.clinic.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.koekoetech.clinic.R;
import com.koekoetech.clinic.adapter.viewholders.DisabilitySurveyHolder;
import com.koekoetech.clinic.helper.AppUtils;
import com.koekoetech.clinic.model.DisabilitySurvey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by ZMN on 2019-08-06.
 **/
public class DisabilitySurveyAdapter extends RecyclerView.Adapter<DisabilitySurveyHolder> {

    @NonNull
    private final List<DisabilitySurvey> surveyList;

    @NonNull
    private final Context context;

    public DisabilitySurveyAdapter(@NonNull Context context, @Nullable List<DisabilitySurvey> surveyList) {
        this.context = context;
        this.surveyList = surveyList == null || surveyList.isEmpty() ? getNewDisabilitySurveyQuestions() : surveyList;
        Collections.sort(this.surveyList, (survey1, survey2) -> AppUtils.emptyOnNull(survey1.getQuestion()).compareTo(AppUtils.emptyOnNull(survey2.getQuestion())));
    }

    @NonNull
    @Override
    public DisabilitySurveyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return DisabilitySurveyHolder.from(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull DisabilitySurveyHolder holder, int position) {
        holder.bindSurvey(surveyList.get(position));
    }

    @Override
    public int getItemCount() {
        return surveyList.size();
    }

    @NonNull
    public List<DisabilitySurvey> getSurveyList() {
        final String defaultAnswer = context.getString(R.string.rb_lbl_no_difficulty);
        for (DisabilitySurvey survey : surveyList) {
            if (TextUtils.isEmpty(survey.getAnswer())) {
                survey.setAnswer(defaultAnswer);
            }
        }
        return surveyList;
    }

    private List<DisabilitySurvey> getNewDisabilitySurveyQuestions() {
        List<DisabilitySurvey> defaultSurveyList = new ArrayList<>();

        final String[] defaultAllQuestions = context.getResources().getStringArray(R.array.disability_survey_questions);
        final String defaultAnswer = context.getString(R.string.rb_lbl_no_difficulty);
        for (String question : defaultAllQuestions) {
            defaultSurveyList.add(new DisabilitySurvey(question, defaultAnswer));
        }

        return defaultSurveyList;
    }

}
