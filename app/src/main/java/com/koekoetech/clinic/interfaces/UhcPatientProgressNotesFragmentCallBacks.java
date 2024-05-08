package com.koekoetech.clinic.interfaces;

import com.koekoetech.clinic.model.UhcPatientProgressNoteViewModel;

/**
 * Created by ZMN on 11/29/17.
 **/

public interface UhcPatientProgressNotesFragmentCallBacks {

    void onToolbarTextChange(String title);

    void onProgressNoteDeleted();

    void onProgressNoteEdit(UhcPatientProgressNoteViewModel editTarget);

    void onProgressCreatedTimeEdited();
}
