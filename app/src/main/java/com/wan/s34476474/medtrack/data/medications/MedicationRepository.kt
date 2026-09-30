package com.wan.s34476474.medtrack.data.medications

import android.app.Application
import android.content.Context
import com.wan.s34476474.medtrack.AppDatabase
import com.wan.s34476474.medtrack.data.patients.Patient
import kotlinx.coroutines.flow.Flow

class MedicationRepository(private val context : Context) {

    private val medicationsDao = AppDatabase.getDatabase(context).medicationDao()

    suspend fun insertMedication(medication: Medication) {
        medicationsDao.insertMedication(medication)
    }

    suspend fun insertAll(medications: List<Medication>) {
        medicationsDao.insertAll(medications)
    }

     fun getAllMedicationsById(patientID: String): Flow<List<Medication?>> {
        return medicationsDao.getAllMedicationById(patientID)
    }



}