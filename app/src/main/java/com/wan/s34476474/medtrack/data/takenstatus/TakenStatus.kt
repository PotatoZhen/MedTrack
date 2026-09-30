package com.wan.s34476474.medtrack.data.takenstatus

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import com.wan.s34476474.medtrack.data.medications.Medication

@Entity(
    tableName = "taken_status",
    primaryKeys = [ "patientId", "medicationId", "date"],
    foreignKeys = [ForeignKey(
        entity = Medication::class,
        parentColumns = ["id"],
        childColumns = ["medicationId"],
        onDelete = CASCADE
    )]
    )
data class TakenStatus(

    val patientId: String,
    val medicationId: Int,
    val date: String,
    val isTaken: Boolean
)
