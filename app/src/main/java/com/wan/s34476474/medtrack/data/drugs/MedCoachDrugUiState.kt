package com.wan.s34476474.medtrack.data.drugs

// UI state holder for MedCoach drug information screen
data class MedCoachDrugUiState(
    val medicationNameError: Boolean = false,
    val purpose: String = "",
    val warnings: String = "",
    val dosage: String = "",
    val isLoading: Boolean = false,
    val error: String = "")
