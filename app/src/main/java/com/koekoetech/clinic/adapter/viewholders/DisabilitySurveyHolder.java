package com.koekoetech.clinic.adapter.viewholders;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.koekoetech.clinic.R;
import com.koekoetech.clinic.model.DisabilitySurvey;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by ZMN on 12/23/20.
 **/
public final class DisabilitySurveyHolder extends RecyclerView.ViewHolder {

    private final Context context;

    private final AppCompatTextView tvQuestion;
    private final RadioGroup rgOptions;

    private DisabilitySurveyHolder(@NonNull final View itemView) {
        super(itemView);
        this.context = itemView.getContext();

        // Bind Views
        this.tvQuestion = itemView.findViewById(R.id.itemDisabilitySurveyTvQuestion);
        this.rgOptions = itemView.findViewById(R.id.itemDisabilitySurveyRgOptions);
    }

    public static DisabilitySurveyHolder from(@NonNull final ViewGroup parent) {
        final Context context = parent.getContext();
        final LayoutInflater layoutInflater = LayoutInflater.from(context);
        final View itemView = layoutInflater.inflate(R.layout.item_patient_disability_survey, parent, false);
        return new DisabilitySurveyHolder(itemView);
    }

    public void bindSurvey(@NonNull final DisabilitySurvey survey) {

        tvQuestion.setText(survey.getQuestion());

        final @IdRes int selectedOptionRbId = getOptionRadioButtonIdFor(survey.getAnswer());
        if (selectedOptionRbId != 0) {
            rgOptions.check(selectedOptionRbId);
        } else {
            rgOptions.check(R.id.itemDisabilitySurveyRbNoDifficulty);
        }

        rgOptions.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            final RadioButton checkedRb = radioGroup.findViewById(checkedId);
            survey.setAnswer(checkedRb.getText().toString());
        });

    }

    @IdRes
    private int getOptionRadioButtonIdFor(@Nullable final String value) {
        if (!TextUtils.isEmpty(value)) {
            if (isEqualStrings(value, R.string.rb_lbl_no_difficulty)) {
                return R.id.itemDisabilitySurveyRbNoDifficulty;
            } else if (isEqualStrings(value, R.string.rb_lbl_some_difficulty)) {
                return R.id.itemDisabilitySurveyRbSomeDifficulty;
            } else if (isEqualStrings(value, R.string.rb_lbl_lots_of_difficulty)) {
                return R.id.itemDisabilitySurveyRbLotOfDifficulty;
            } else if (isEqualStrings(value, R.string.rb_lbl_cannot_do)) {
                return R.id.itemDisabilitySurveyRbCannotDo;
            }
        }

        return 0;
    }

    private boolean isEqualStrings(@NonNull String str, @StringRes int strRes) {
        return TextUtils.equals(context.getString(strRes), str);
    }
}
