package com.wan.s34476474.medtrack.data.clinicianDashboard

// Represents the different UI states for clinician insight generation
sealed interface ClinicianUiState {
    object Idle : ClinicianUiState
    object Loading : ClinicianUiState
    data class Success(val insights: List<String>) : ClinicianUiState
    data class Error(val message: String) : ClinicianUiState
}