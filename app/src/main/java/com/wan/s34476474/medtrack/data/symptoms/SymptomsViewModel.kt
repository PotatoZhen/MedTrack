package com.wan.s34476474.medtrack.data.symptoms

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.wan.s34476474.medtrack.data.medications.Medication
import com.wan.s34476474.medtrack.data.medications.MedicationRepository
import com.wan.s34476474.medtrack.data.medications.MedicationsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SymptomsViewModel(context: Context) : ViewModel() {
    val repository = SymptomRepository(context = context)

    fun insertAll(symptoms: List<Symptom>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertAll(symptoms)
        }
    }

    fun getAllSymptomsById(patientID: String): Flow<List<Symptom?>> {
        return repository.getAllSymptomsById(patientID)
    }

    private val _uiState = MutableStateFlow(SymptomUiState())
    val uiState: StateFlow<SymptomUiState> = _uiState.asStateFlow()

    // Validates symptom input and saves symptom to database if valid
    fun validateAndSaveSymptom(
        patientId: String,
        category: String,
        dateTime: String,
        notes: String,
        severity: Int
    ) {
        val categoryError = category.isBlank()
        val dateTimeError = dateTime.isBlank()
        val notesError = notes.length > 200
        val severityError = severity <= 0

        val hasError = categoryError || dateTimeError || notesError || severityError

        if (hasError) {
            _uiState.value = SymptomUiState(
                categoryError = categoryError,
                dateTimeError = dateTimeError,
                notesError = notesError,
                severityError = severityError,
                success = false,
                generalError = "Please fix validation errors"
            )
            return
        }

        val symptom = Symptom(
            pID = patientId,
            category = category,
            severity = severity.toString(),
            notes = notes,
            dateTime = dateTime
        )

        viewModelScope.launch(Dispatchers.IO) {
            repository.insertSymptom(symptom)

            _uiState.value = SymptomUiState(
                success = true,
                generalError = null
            )
        }
    }

    fun getCurrentPatientId(context: Context): String {
        val prefs = context.getSharedPreferences("logged_in_patient_id", Context.MODE_PRIVATE)
        return prefs.getString("patient_id", "") ?: ""
    }

    //Factory
    class SymptomsViewModelFactory(context: Context) : ViewModelProvider.Factory {
        private val context = context.applicationContext
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            SymptomsViewModel(context) as T

    }


}