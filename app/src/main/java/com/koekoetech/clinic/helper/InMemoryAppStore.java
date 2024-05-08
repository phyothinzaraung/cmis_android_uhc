package com.koekoetech.clinic.helper;

/**
 * Created by ZMN on 10/8/18.
 **/
public class InMemoryAppStore {

    private static final InMemoryAppStore ourInstance = new InMemoryAppStore();


    /* Patient code to indicate to open patient detail on Home Screen Launch*/
    private String detailPatientCode;

    /*Flag to indicate that automatically open up NCD Web Url on launch of UhcPatientDetailActivity*/
    private boolean detailImplicitNCDLaunch;

    private InMemoryAppStore() {
    }

    public static InMemoryAppStore getInstance() {
        return ourInstance;
    }

    public String getDetailPatientCode() {
        return detailPatientCode;
    }

    public void setDetailPatientCode(String detailPatientCode) {
        this.detailPatientCode = detailPatientCode;
    }

    public boolean isDetailImplicitNCDLaunch() {
        return detailImplicitNCDLaunch;
    }

    public void setDetailImplicitNCDLaunch(boolean detailImplicitNCDLaunch) {
        this.detailImplicitNCDLaunch = detailImplicitNCDLaunch;
    }
}
