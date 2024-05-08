package com.koekoetech.clinic.helper;

import androidx.annotation.NonNull;
import io.realm.DynamicRealm;
import io.realm.RealmMigration;

/**
 * Created by ZMN on 3/9/17.
 **/

public class RealmMigrator implements RealmMigration {

    @Override
    public void migrate(@NonNull DynamicRealm realm, long oldVersion, long newVersion) {

        // Access the Realm schema in order to create, modify or delete classes and their fields.
//        RealmSchema schema = realm.getSchema();
        // DO MIGRATION PROCESSES HERE

        // REF :
        // https://github.com/realm/realm-java/blob/master/examples/migrationExample/src/main/java/io/realm/examples/realmmigrationexample/model/Migration.java

        // Example
//        if(!schema.get("ClassName").hasField("NewAddedField")){
//            schema.get("ClassName").addField("NewAddedField",String.class);
//        }
//
//        if(!schema.get("ClassName").hasField("NewAddedIntField")){
//            schema.get("ClassName").addField("NewAddedIntField",String.class, FieldAttribute.REQUIRED);
//        }

//        RealmObjectSchema uhcPatientProgressSchema = schema.get("UhcPatientProgress");
//        if (uhcPatientProgressSchema != null) {
//
//            if (!uhcPatientProgressSchema.hasField("createdTimeStamp")) {
//                uhcPatientProgressSchema.addField("createdTimeStamp", Long.class, FieldAttribute.REQUIRED);
//            }
//
//            if (!uhcPatientProgressSchema.hasField("OutDiagnosis")) {
//                uhcPatientProgressSchema.addField("OutDiagnosis", String.class, FieldAttribute.REQUIRED);
//            }
//
//            if (!uhcPatientProgressSchema.hasField("Charges")) {
//                uhcPatientProgressSchema.addField("Charges", Double.class, FieldAttribute.REQUIRED);
//            }
//
//            if (!uhcPatientProgressSchema.hasField("Followup_Reason")) {
//                uhcPatientProgressSchema.addField("Followup_Reason", String.class, FieldAttribute.REQUIRED);
//            }
//
//            if (!uhcPatientProgressSchema.hasField("Medication")) {
//                uhcPatientProgressSchema.addField("Medication", String.class, FieldAttribute.REQUIRED);
//            }
//
//            if (!uhcPatientProgressSchema.hasField("PlanOfCare")) {
//                uhcPatientProgressSchema.addField("PlanOfCare", String.class, FieldAttribute.REQUIRED);
//            }
//
//            if (!uhcPatientProgressSchema.hasField("ReferToDoctor")) {
//                uhcPatientProgressSchema.addField("ReferToDoctor", int.class, FieldAttribute.REQUIRED);
//            }
//
//            if (!uhcPatientProgressSchema.hasField("ReferToDoctorName")) {
//                uhcPatientProgressSchema.addField("ReferToDoctorName", String.class, FieldAttribute.REQUIRED);
//            }
//
//            if (!uhcPatientProgressSchema.hasField("ReferToFacility")) {
//                uhcPatientProgressSchema.addField("ReferToFacility", String.class, FieldAttribute.REQUIRED);
//            }
//
//            if (!uhcPatientProgressSchema.hasField("ReferToReason")) {
//                uhcPatientProgressSchema.addField("ReferToReason", String.class, FieldAttribute.REQUIRED);
//            }
//
//            if (!uhcPatientProgressSchema.hasField("ReferToDateTime")) {
//                uhcPatientProgressSchema.addField("ReferToDateTime", String.class, FieldAttribute.REQUIRED);
//            }
//
//            if (!uhcPatientProgressSchema.hasField("Followup_DateTime")) {
//                uhcPatientProgressSchema.addField("Followup_DateTime", String.class, FieldAttribute.REQUIRED);
//            }
//
//        }
//
//        RealmObjectSchema uhcPatientProgressNoteSchema = schema.get("UhcPatientProgressNote");
//        if (uhcPatientProgressNoteSchema != null) {
//            if (!uhcPatientProgressNoteSchema.hasField("freeNote")) {
//                uhcPatientProgressNoteSchema.addField("freeNote", String.class);
//            }
//        }
//
//
//        oldVersion++;

    }
}
