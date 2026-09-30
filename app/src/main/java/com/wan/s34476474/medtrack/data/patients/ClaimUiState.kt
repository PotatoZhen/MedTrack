package com.wan.s34476474.medtrack.data.patients

// Holds UI state for account claiming process
data class ClaimUiState(
    val patientIdError: Boolean = false,
    val phoneError: Boolean = false,
    val alreadyClaimedError: Boolean = false,
    val success: Boolean = false,
    val generalError: String? = null
)
