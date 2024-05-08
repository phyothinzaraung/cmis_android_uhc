package com.koekoetech.clinic.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Ko Pyae on 3/7/2018.
 */

public class FeedbackModel {

    @Expose
    @SerializedName("Id")
    private int id;

    @Expose
    @SerializedName("Comment")
    private String comment;

    @Expose
    @SerializedName("FeedbackType")
    private String feedbackType;

    @Expose
    @SerializedName("SubmittedTime")
    private String submittedTime;

    @Expose
    @SerializedName("DoctorId")
    private int doctorId;

    @Expose
    @SerializedName("DoctorCode")
    private String doctorCode;

    @Expose
    @SerializedName("IsDeleted")
    private boolean isDeleted;

    @Expose
    @SerializedName("Accesstime")
    private String accessTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getFeedbackType() {
        return feedbackType;
    }

    public void setFeedbackType(String feedbackType) {
        this.feedbackType = feedbackType;
    }

    public String getSubmittedTime() {
        return submittedTime;
    }

    public void setSubmittedTime(String submittedTime) {
        this.submittedTime = submittedTime;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorCode() {
        return doctorCode;
    }

    public void setDoctorCode(String doctorCode) {
        this.doctorCode = doctorCode;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public String getAccessTime() {
        return accessTime;
    }

    public void setAccessTime(String accessTime) {
        this.accessTime = accessTime;
    }
}
