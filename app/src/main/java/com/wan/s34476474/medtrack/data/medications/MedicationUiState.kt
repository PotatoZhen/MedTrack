package com.wan.s34476474.medtrack.data.medications

// Holds UI validation states
data class MedicationUiState(
    val medicationNameError: Boolean = false,
    val dosageError: Boolean = false,
    val dosageBlankError: Boolean = false,
    val freqError: Boolean = false,
    val timeError: Boolean = false,
    val typeError: Boolean = false,
    val success: Boolean = false,
    val generalError: String? = null
)
