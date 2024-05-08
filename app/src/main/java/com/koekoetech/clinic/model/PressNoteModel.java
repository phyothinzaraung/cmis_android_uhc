package com.koekoetech.clinic.model;

import androidx.annotation.DrawableRes;
import com.koekoetech.clinic.helper.Pageable;

/**
 * Created by hello on 2/17/17.
 **/

public class PressNoteModel implements Pageable {

    private String title;

    @DrawableRes
    private int background;
    private String preFix;
    private String preFixTextColor;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getBackground() {
        return background;
    }

    public void setBackground(@DrawableRes int background) {
        this.background = background;
    }

    public String getPreFix() {
        return preFix;
    }

    public void setPreFix(String preFix) {
        this.preFix = preFix;
    }

    public String getPreFixTextColor() {
        return preFixTextColor;
    }

    public void setPreFixTextColor(String preFixTextColor) {
        this.preFixTextColor = preFixTextColor;
    }
}
