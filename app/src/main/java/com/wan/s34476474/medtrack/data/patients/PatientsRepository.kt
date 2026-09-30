package com.wan.s34476474.medtrack.data.patients

import android.content.Context
import android.util.Log
import com.wan.s34476474.medtrack.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class PatientsRepository(private val context: Context) {
    private val patientsDao = AppDatabase.getDatabase(context).patientDao()

    suspend fun insertPatient(patient: Patient) {
        patientsDao.insertPatient(patient)
    }

    fun getAllPatients(): Flow<List<Patient>> = patientsDao.getAllPatients()

    suspend fun insertAll(patients: List<Patient>) {
        withContext(Dispatchers.IO) {
            patientsDao.insertAll(patients)
        }
    }

    suspend fun getPatientById(patientID: String): Patient? {
        return patientsDao.getPatientById(patientID)
    }

    suspend fun updatePassword(patientID: String, password: String) {
        patientsDao.updatePassword(patientID, password)
    }

    fun getPatientName(patientID: String?): Flow<String?> {
        return patientsDao.getPatientName(patientID)
    }

    fun getPatientPhoneNumber(patientID: String?): Flow<String?> {
        return patientsDao.getPatientPhoneNumber(patientID)
    }

    suspend fun getPatientByPhone(phone: String): Patient? {
        return patientsDao.getPatientByPhoneNumber(phone)
    }




}