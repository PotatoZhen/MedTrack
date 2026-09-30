package com.wan.s34476474.medtrack.data.symptoms

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.wan.s34476474.medtrack.data.patients.Patient

// Defines Symptom as a Room database table with a foreign key relationship to Patient
@Entity (
    tableName = "symptoms",
    foreignKeys = [
        ForeignKey(
            entity = Patient::class,
            parentColumns = ["patientID"],
            childColumns =  ["pID"],
            onDelete = ForeignKey.CASCADE //Delete patient → symptoms auto deleted

        )
    ],

)
data class Symptom(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val pID: String,
    val category: String,
    val severity: String,
    val notes: String,
    val dateTime: String
)


