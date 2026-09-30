package com.wan.s34476474.medtrack.data.patients

// Represents login screen state
data class LoginUiState(
    val success: Boolean = false,
    val firstTime: Boolean = false,
    val error: String? = null
)
