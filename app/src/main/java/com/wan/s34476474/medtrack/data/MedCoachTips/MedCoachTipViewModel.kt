package com.wan.s34476474.medtrack.data.MedCoachTips

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.wan.s34476474.medtrack.BuildConfig
import com.wan.s34476474.medtrack.data.medications.MedicationsViewModel
import kotlinx.coroutines.flow.Flow

class MedCoachTipViewModel(
    application: Application
) : AndroidViewModel(application) {

    val repository = MedCoachTipRepository(application)


    // Internal state holder for tracking MedCoach tip generation status
    private val _uiState: MutableStateFlow<MedCoachTipUiState> =
        MutableStateFlow(MedCoachTipUiState.Initial)

    val uiState: StateFlow<MedCoachTipUiState> =
        _uiState.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-3-flash-preview",
        apiKey = BuildConfig.GENAI_API_KEY
    )

    // Generates a personalised MedCoach message using GenAI, updates UI state, and saves result to database
    fun sendPrompt(
        patientId: String,
        meds: String
    ) {

        val prompt = """ 
        Patient medications:
        $meds

        Generate a short personalised reminder and encouragement message.
    """

        if (!repository.isNetworkAvailable()) {
            _uiState.value =
                MedCoachTipUiState.Error("No internet connection")
            return
        }

        _uiState.value = MedCoachTipUiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(
                    content {
                        text(prompt)
                    }
                )

                val output = response.text ?: ""

                _uiState.value = MedCoachTipUiState.Success(output)

                repository.saveTip(
                    patientId = patientId,
                    message = output
                )

            } catch (e: Exception) {
                _uiState.value = MedCoachTipUiState.Error(e.localizedMessage ?: "")
            }
        }
    }

    // Retrieves saved MedCoach tips for a specific patient
    fun getTipsById(patientId: String): Flow<List<MedCoachTip>> {
        return repository.getTipsById(patientId)
    }

    // Factory
    class MedCoachTipViewModelFactory(
        private val application: Application
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            MedCoachTipViewModel(application) as T

    }
}