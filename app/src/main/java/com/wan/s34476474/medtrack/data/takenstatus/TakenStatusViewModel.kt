package com.wan.s34476474.medtrack.data.takenstatus

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.wan.s34476474.medtrack.data.MedCoachTips.MedCoachTipViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TakenStatusViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = TakenStatusRepository(application)

    // Retrieves today's medication taken status for a specific patient
    fun getToday(patientId: String, date: String) =
        repository.getToday(patientId, date)

    // Toggles and updates medication taken status
    fun toggleTaken(medicationId: Int, patientId: String, date: String, currentValue: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val newValue = !currentValue

            repository.insert(
                TakenStatus(
                    medicationId = medicationId,
                    patientId = patientId,
                    date = date,
                    isTaken = newValue
                )
            )
        }
    }

    // Converts taken medication records into a map for quick lookup of taken status by medication ID
    fun getTakenMap(takenStatuses: List<TakenStatus>): Map<Int, Boolean> {
        return takenStatuses
            .filter { it.isTaken }
            .associate { it.medicationId to true }
    }

    //Factory
    class TakenStatusViewModelFactory(
        private val application: Application
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            TakenStatusViewModel(application) as T

    }
}