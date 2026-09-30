package com.wan.s34476474.medtrack.data.drugsInteraction

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.wan.s34476474.medtrack.BuildConfig
import com.wan.s34476474.medtrack.data.MedCoachTips.MedCoachTip
import com.wan.s34476474.medtrack.data.MedCoachTips.MedCoachTipRepository
import com.wan.s34476474.medtrack.data.MedCoachTips.MedCoachTipUiState
import com.wan.s34476474.medtrack.data.MedCoachTips.MedCoachTipViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InteractionViewModel(application: Application
) : AndroidViewModel(application) {
    val repository = InteractionsRepository(application)


    private val _uiState: MutableStateFlow<InteractionUiState> =
        MutableStateFlow(InteractionUiState.Initial)

    val uiState: StateFlow<InteractionUiState> =
        _uiState.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-3-flash-preview",
        apiKey = BuildConfig.GENAI_API_KEY
    )

    // Validates inputs, builds AI prompt, and requests GenAI drug interaction analysis
    fun sendPrompt(
        med1: String,
        med2: String
    ) {

        // Show error when there is no internet connection
        if (!repository.isNetworkAvailable()) {
            _uiState.value =
                InteractionUiState.Error("No internet connection")
            return
        }

        _uiState.value = InteractionUiState.Loading

        // Ensures both medication inputs are provided before proceeding
        if (med1.isBlank() || med2.isBlank()) {
            _uiState.value = InteractionUiState.Error("Please select both medications")
            return
        }

        // Prevents comparing the same medication with itself
        if (med1 == med2) {
            _uiState.value = InteractionUiState.Error("Please select two different medications")
            return
        }

        val prompt = """
            You are a medical information assistant.
            
            Check for potential drug interactions between the following two medications:
            
            Medication 1: $med1
            Medication 2: $med2
            
            Please provide response in clear bullet-point format includes:
            - CAN or CANNOT be taken together (or "Use with caution" if uncertain)
            - What happens when taken together
            - Main risks or side effects
            - Simple explanation for patients

            If there is any potential danger, clearly highlight it as a WARNING
           """.trimIndent()

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(
                    content {
                        text(prompt)
                    }
                )

                val output = response.text ?: ""
                // Updates UI state with successful AI-generated interaction result
                _uiState.value = InteractionUiState.Success(output)



            } catch (e: Exception) {
                // Updates UI state when network or API error occurs
                _uiState.value = InteractionUiState.Error(e.localizedMessage ?: "")
            }
        }
    }


    // Factory
    class InteractionViewModelFactory(
        private val application: Application
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            InteractionViewModel(application) as T

    }
}