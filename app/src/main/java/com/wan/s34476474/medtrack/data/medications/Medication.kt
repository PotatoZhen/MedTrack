package com.wan.s34476474.medtrack.data.medications

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.wan.s34476474.medtrack.data.patients.Patient

// Room entity representing a Medication record linked to a Patient, with cascading delete
@Entity (
    tableName = "medications",
    foreignKeys = [
        ForeignKey(
            entity = Patient::class,
            parentColumns = ["patientID"],
            childColumns =  ["pID"],
            onDelete = ForeignKey.CASCADE

        )
    ]
)
data class Medication(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val pID: String,

    val name: String,
    val dosage: String,
    val frequency: String,
    val time: String,
    val type: String,
    val notes: String
)



