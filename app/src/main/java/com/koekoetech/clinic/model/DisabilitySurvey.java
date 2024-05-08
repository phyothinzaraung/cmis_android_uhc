package com.koekoetech.clinic.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import java.util.UUID;

/**
 * Created by ZMN on 2019-08-06.
 **/
public class DisabilitySurvey extends RealmObject {

    @PrimaryKey
    private String localId;

    @Expose
    @SerializedName("DisabilitySurveyID")
    private String surveyId;

    @Expose
    @SerializedName("question")
    private String question;

    @Expose
    @SerializedName("answer")
    private String answer;

    @Expose
    @SerializedName("PatientCode")
    private String patientCode;


    public DisabilitySurvey() {
    }

    public DisabilitySurvey(String question, String answer) {
        this.localId = UUID.randomUUID().toString();
        this.surveyId = new UUID(0L,0L).toString();
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getPatientCode() {
        return patientCode;
    }

    public void setPatientCode(String patientCode) {
        this.patientCode = patientCode;
    }

    public String getLocalId() {
        return localId;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }

    public String getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(String surveyId) {
        this.surveyId = surveyId;
    }

    @Override
    public String toString() {
        return "DisabilitySurvey{" +
                "localId='" + localId + '\'' +
                ", surveyId='" + surveyId + '\'' +
                ", question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                ", patientCode='" + patientCode + '\'' +
                '}';
    }
}
