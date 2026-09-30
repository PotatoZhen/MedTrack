package com.wan.s34476474.medtrack.utils

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SeedViewModel(application: Application) : AndroidViewModel(application) {

    private val seeder = DatabaseSeeder(application)

    fun getCurrentPatientId(context: Context): String {
        val prefs = context.getSharedPreferences("logged_in_patient_id", Context.MODE_PRIVATE)
        return prefs.getString("patient_id", "") ?: ""
    }

    fun seedIfNeeded() {
        viewModelScope.launch(Dispatchers.IO) {
            seeder.seedDatabaseIfFirstLaunch()
        }
    }
}