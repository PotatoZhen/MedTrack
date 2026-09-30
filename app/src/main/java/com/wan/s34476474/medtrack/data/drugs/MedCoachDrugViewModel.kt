package com.wan.s34476474.medtrack.data.drugs

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.wan.s34476474.medtrack.data.MedCoachTips.MedCoachTipViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MedCoachDrugViewModel(context: Context) : ViewModel() {



    private val repository = DrugsRepository(context)

    // Internal UI state
    private val _uiState = MutableStateFlow(MedCoachDrugUiState())
    val uiState: StateFlow<MedCoachDrugUiState> =
    _uiState.asStateFlow()

    // Handles drug search and updates UI state
    fun searchDrug(name: String) {

        if (name.isBlank()) {
            _uiState.value = _uiState.value.copy(
                medicationNameError = true,
                error = "Please enter medication name"
            )
            return
        }

            viewModelScope.launch {
            // show loading
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = "",

            )

            if (!repository.isNetworkAvailable()) {
                _uiState.value = MedCoachDrugUiState(
                    isLoading = false,
                    error = "No internet connection"
                )
                return@launch
            }

                val response = repository.getDrugInfo(name)

            if (response == null) {
                _uiState.value = MedCoachDrugUiState(
                    isLoading = false,
                    error = "Drug is not found."
                )
                return@launch
            }

                val drug = response.results.firstOrNull()

            if (drug == null) {
                _uiState.value = MedCoachDrugUiState(
                    isLoading = false,
                    error = "No drug information found"
                )
                return@launch
            }

                _uiState.value = MedCoachDrugUiState(
                    purpose = drug.purpose?.firstOrNull() ?: "No purpose found",
                    warnings = drug.warnings?.firstOrNull() ?: "No warnings found",
                    dosage = drug.dosage_and_administration?.firstOrNull() ?: "No dosage found"


                )
        }
    }

    // Factory
    class MedCoachDrugViewModelFactory(context: Context) : ViewModelProvider.Factory {
        private val context = context.applicationContext
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            MedCoachDrugViewModel(context) as T

    }
}