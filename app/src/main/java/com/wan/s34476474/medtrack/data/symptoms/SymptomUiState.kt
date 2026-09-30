package com.wan.s34476474.medtrack.data.symptoms

// Holds symptom screen UI states
data class SymptomUiState(

    val categoryError: Boolean = false,
    val dateTimeError: Boolean = false,
    val notesError: Boolean = false,
    val severityError: Boolean = false,
    val generalError: String? = null,
    val success: Boolean = false
)
