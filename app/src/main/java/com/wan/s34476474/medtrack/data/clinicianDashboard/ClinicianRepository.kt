package com.wan.s34476474.medtrack.data.clinicianDashboard

import android.content.Context
import com.wan.s34476474.medtrack.AppDatabase
import kotlinx.coroutines.flow.Flow

class ClinicianRepository(context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val patientDao = db.patientDao()
    private val medicationDao = db.medicationDao()
    private val symptomDao = db.symptomDao()

    // Returns combined dashboard statistics as a single Flow of ClinicianStats
    fun getDashboardStats(): Flow<ClinicianStats> =
        kotlinx.coroutines.flow.combine(
            patientDao.getPatientCount(),
            medicationDao.getAverageMedicationsPerPatient(),
            symptomDao.getMostCommonSymptomCategory(),
            symptomDao.getAverageSeverity()
        ) { patientCount, avgMeds, symptom, severity ->

            ClinicianStats(
                totalPatients = patientCount,
                avgMedicationsPerPatient = avgMeds,
                mostCommonSymptom = symptom,
                avgSymptomSeverity = severity
            )
        }
}