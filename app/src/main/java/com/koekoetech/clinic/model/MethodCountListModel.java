package com.koekoetech.clinic.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.koekoetech.clinic.helper.Pageable;

import java.io.Serializable;

/**
 * Created by Ko Pyae on 6/26/2017.
 */

public class MethodCountListModel implements Serializable,Pageable {
    @Expose
    @SerializedName("MethodName")
    private String MethodName;

    @Expose
    @SerializedName("NewClientCount")
    private int NewClientCount ;

    @Expose
    @SerializedName("OldClientCount")
    private int OldClientCount;

    public String getMethodName() {
        return MethodName;
    }

    public void setMethodName(String methodName) {
        MethodName = methodName;
    }

    public int getNewClientCount() {
        return NewClientCount;
    }

    public void setNewClientCount(int newClientCount) {
        NewClientCount = newClientCount;
    }

    public int getOldClientCount() {
        return OldClientCount;
    }

    public void setOldClientCount(int oldClientCount) {
        OldClientCount = oldClientCount;
    }
}
