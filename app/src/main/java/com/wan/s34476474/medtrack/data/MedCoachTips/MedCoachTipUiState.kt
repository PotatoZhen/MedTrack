package com.wan.s34476474.medtrack.data.MedCoachTips

// Sealed interface representing UI states for MedCoach tip generation
sealed interface MedCoachTipUiState {

    object Initial: MedCoachTipUiState
    object Loading: MedCoachTipUiState
    data class Success(val outputText: String) : MedCoachTipUiState
    data class Error(val errorMessage: String) : MedCoachTipUiState
}