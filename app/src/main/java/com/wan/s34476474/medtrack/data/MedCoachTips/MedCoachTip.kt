package com.wan.s34476474.medtrack.data.MedCoachTips

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.wan.s34476474.medtrack.data.patients.Patient

/*
 Room entity representing a saved GenAI MedCoach tip linked to a specific patient,
 with automatic deletion when the patient is removed.
 */
@Entity(
    tableName = "med_coach_tips",
    foreignKeys = [
        ForeignKey(
            entity = Patient::class,
            parentColumns = ["patientID"],
            childColumns =  ["patientId"],
            onDelete = ForeignKey.CASCADE //Delete patient → medications auto deleted

        )
    ],
)
data class MedCoachTip(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val patientId: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)
