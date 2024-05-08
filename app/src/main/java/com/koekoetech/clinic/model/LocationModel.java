package com.koekoetech.clinic.model;

import com.koekoetech.clinic.helper.Pageable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by minthit on 6/22/2015.
 */
public class LocationModel extends RealmObject implements Pageable {

    private String DialingCode;
    private String Country;
    private String StateDivision;
    private int DivisionCode;
    private String Township;
    private int TownshipCode;

    @PrimaryKey
    private int ID;
    private int Createdby;
    private String Accesstime;
    private String Abbreviation;

    public String getDialingCode() {
        return DialingCode;
    }

    public void setDialingCode(String dialingCode) {
        DialingCode = dialingCode;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getStateDivision() {
        return StateDivision;
    }

    public void setStateDivision(String stateDivision) {
        StateDivision = stateDivision;
    }

    public int getDivisionCode() {
        return DivisionCode;
    }

    public void setDivisionCode(int divisionCode) {
        DivisionCode = divisionCode;
    }

    public String getTownship() {
        return Township;
    }

    public void setTownship(String township) {
        Township = township;
    }

    public int getTownshipCode() {
        return TownshipCode;
    }

    public void setTownshipCode(int townshipCode) {
        TownshipCode = townshipCode;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getCreatedby() {
        return Createdby;
    }

    public void setCreatedby(int createdby) {
        Createdby = createdby;
    }

    public String getAccesstime() {
        return Accesstime;
    }

    public void setAccesstime(String accesstime) {
        Accesstime = accesstime;
    }

    public String getAbbreviation() {
        return Abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        Abbreviation = abbreviation;
    }
}
