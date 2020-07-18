package com.daita.dermi.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.parceler.Parcel;

@Parcel
@Entity
public class Diagnosis {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "file_uri")
    public String fileUri;

    @ColumnInfo(name = "notes")
    public String notes;

    @ColumnInfo(name = "primary_diagnosis")
    public String primaryDiagnosis;

    @ColumnInfo(name = "primary_diagnosis_confidence")
    public float primaryDiagnosisConfidence;

    @ColumnInfo(name = "other_diagnoses")
    public String otherDiagnoses;
}
