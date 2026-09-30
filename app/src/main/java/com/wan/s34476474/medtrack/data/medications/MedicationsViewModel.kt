package com.wan.s34476474.medtrack.data.medications

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.wan.s34476474.medtrack.data.patients.Patient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MedicationsViewModel(context: Context) : ViewModel() {
    val repository = MedicationRepository(context = context)


    fun insertAll(medications: List<Medication>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertAll(medications)
        }
    }


    fun getAllMedicationById(patientID: String): Flow<List<Medication?>> {
        return repository.getAllMedicationsById(patientID)
    }

    private val _uiState = MutableStateFlow(MedicationUiState())
    val uiState: StateFlow<MedicationUiState> = _uiState.asStateFlow()

    // Validates medication dosage format using regex (e.g., mg, ml, g)
    private fun isValidDosage(input: String): Boolean {
        val dosageRegex = Regex("^\\d+(\\.\\d+)?(mg|ml|g)$")
        return dosageRegex.matches(input.trim())
    }

    // Validates medication input fields, creates Medication object, and saves it to the database
    fun validateAndSaveMedication( patientId: String,
                                   medicationName: String,
                                   dosage: String,
                                   frequency: String,
                                   time: String,
                                   type: String,
                                   notes: String) {

        val state = MedicationUiState(
            medicationNameError = medicationName.isBlank(),
            dosageError = !isValidDosage(dosage),
            dosageBlankError = dosage.isBlank(),
            freqError = frequency.isBlank(),
            timeError = time.isBlank(),
            typeError = type.isBlank()
        )

        val hasError =
            state.medicationNameError ||
                    state.dosageError ||
                    state.dosageBlankError ||
                    state.freqError ||
                    state.timeError ||
                    state.typeError

        if (hasError) {
            _uiState.value = state.copy(
                success = false,
                generalError = "Please fill required fields"
            )
            return
        }

        val medication = Medication(
            pID = patientId,
            name = medicationName,
            dosage = dosage,
            frequency = frequency,
            time = time,
            type = type,
            notes = notes
        )

        viewModelScope.launch(Dispatchers.IO) {

            repository.insertMedication(medication)
            _uiState.value = state.copy(
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
    class MedicationsViewModelFactory(context: Context) : ViewModelProvider.Factory {
        private val context = context.applicationContext
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            MedicationsViewModel(context) as T

    }

}

