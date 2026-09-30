package com.wan.s34476474.medtrack.data.symptoms

import android.content.Context
import com.wan.s34476474.medtrack.AppDatabase
import com.wan.s34476474.medtrack.data.medications.Medication
import kotlinx.coroutines.flow.Flow

class SymptomRepository(private val context: Context) {

    private val symptomsDao = AppDatabase.getDatabase(context).symptomDao()

    suspend fun insertSymptom(symptom: Symptom) {
        symptomsDao.insert(symptom)
    }

    suspend fun insertAll(symptoms: List<Symptom>) {
        symptomsDao.insertAll(symptoms)
    }

    fun getAllSymptomsById(patientID: String): Flow<List<Symptom?>> {
        return symptomsDao.getAllSymptomsById(patientID)
    }

}