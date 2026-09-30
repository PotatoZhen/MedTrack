package com.wan.s34476474.medtrack.data.clinicianDashboard

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.wan.s34476474.medtrack.data.MedCoachTips.MedCoachTipViewModel
import com.wan.s34476474.medtrack.data.drugs.MedCoachDrugViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.wan.s34476474.medtrack.BuildConfig
import kotlinx.coroutines.flow.SharingStarted


class ClinicianViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = ClinicianRepository(application)

    // Live dashboard statistics stream from Room database
    val stats = repository.getDashboardStats()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ClinicianStats(0, 0f, "", 0f)
        )


    // Gemini model used to generate clinician insights
    private val generativeModel = GenerativeModel(
        modelName = "gemini-3-flash-preview",
        apiKey = BuildConfig.GENAI_API_KEY
    )


    //  UI State for insights
    private val _uiState = MutableStateFlow<ClinicianUiState>(ClinicianUiState.Idle)
    val uiState: StateFlow<ClinicianUiState> = _uiState


    // Generates AI insights based on aggregated clinician statistics
    fun generateInsights() {

        val currentStats = stats.value

        val prompt = """
        You are a clinical analyst.
        
        Use the data provided below.

        - Total number of patients in the database: ${currentStats.totalPatients}
        - Average meds per patient: ${currentStats.avgMedicationsPerPatient}
        - Common symptom category across all patients: ${currentStats.mostCommonSymptom}
        - Average symptom severity across all patients: ${currentStats.avgSymptomSeverity}

        Return ONLY the 3 useful insights as bullet points.
    """.trimIndent()

        _uiState.value = ClinicianUiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(prompt)

                val output = response.text ?: ""

                val list = output
                    .split("\n")
                    .filter { it.isNotBlank() }
                    .take(3)

                _uiState.value = ClinicianUiState.Success(list)

            } catch (e: Exception) {
                _uiState.value = ClinicianUiState.Error(e.message ?: "Error")
            }
        }
    }


    // Factory
    class ClinicianViewModelFactory(
        private val application: Application
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ClinicianViewModel(application) as T

        }

    }
}