package com.wan.s34476474.medtrack.data.clinicianDashboard

// Stores aggregated statistics displayed on the clinician dashboard
data class ClinicianStats(
    val totalPatients: Int,
    val avgMedicationsPerPatient: Float?,
    val mostCommonSymptom: String?,
    val avgSymptomSeverity: Float?
)
