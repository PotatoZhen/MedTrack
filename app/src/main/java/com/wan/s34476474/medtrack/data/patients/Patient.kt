package com.wan.s34476474.medtrack.data.patients

import androidx.room.Entity
import androidx.room.PrimaryKey

// Room entity representing a Patient record stored in the patients table
@Entity (tableName = "patients")
data class Patient(

    @PrimaryKey
    val patientID: String,

    val phoneNumber: String,

    val name: String,

    val password: String
)
