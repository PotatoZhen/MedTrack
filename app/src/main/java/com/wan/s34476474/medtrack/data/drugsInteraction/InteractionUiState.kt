package com.wan.s34476474.medtrack.data.drugsInteraction

// Represents UI states for drug interaction feature
sealed interface InteractionUiState {


        object Initial: InteractionUiState
        object Loading: InteractionUiState
        data class Success(val outputText: String) : InteractionUiState
        data class Error(val errorMessage: String) : InteractionUiState

}