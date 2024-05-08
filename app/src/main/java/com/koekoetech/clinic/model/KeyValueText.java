package com.koekoetech.clinic.model;

import android.text.TextUtils;

/**
 * Created by ZMN on 4/2/18.
 **/
public class KeyValueText {

    private String key;
    private String value;
    private String defaultValue = "-";
    private String separator = ":";

    public KeyValueText() {
    }

    public KeyValueText(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public KeyValueText(String key, String value, String separator) {
        this.key = key;
        this.value = value;
        this.separator = separator;
    }

    public KeyValueText(String key, String value, String defaultValue, String separator) {
        this.key = key;
        this.value = value;
        this.defaultValue = defaultValue;
        this.separator = separator;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return TextUtils.isEmpty(value) ? defaultValue : value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    @Override
    public String toString() {
        return "KeyValueText{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", defaultValue='" + defaultValue + '\'' +
                ", separator='" + separator + '\'' +
                '}';
    }
}
