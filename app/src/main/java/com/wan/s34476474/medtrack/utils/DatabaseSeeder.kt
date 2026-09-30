package com.wan.s34476474.medtrack.utils

import android.content.Context
import android.util.Log
import com.wan.s34476474.medtrack.AppDatabase
import com.wan.s34476474.medtrack.SymptomActivity
import com.wan.s34476474.medtrack.data.medications.Medication
import com.wan.s34476474.medtrack.data.medications.MedicationRepository
import com.wan.s34476474.medtrack.data.patients.Patient
import com.wan.s34476474.medtrack.data.patients.PatientsRepository
import com.wan.s34476474.medtrack.data.symptoms.Symptom
import com.wan.s34476474.medtrack.data.symptoms.SymptomRepository
import com.wan.s34476474.medtrack.utils.sharedPreferencesReader.loadAccount
import com.wan.s34476474.medtrack.utils.sharedPreferencesReader.loadMedication
import kotlin.String


class DatabaseSeeder(private val context: Context) {

    private val patientsRepository = PatientsRepository(context)
    private val medicationRepository = MedicationRepository(context)

    private val symptomRepository = SymptomRepository(context)


    private val prefs =
        context.getSharedPreferences("medtrack_prefs", Context.MODE_PRIVATE)


    suspend fun seedDatabaseIfFirstLaunch() {
        patientsRepository.getAllPatients()

        val alreadySeeded = prefs.getBoolean("db_seeded", false)

        if (!alreadySeeded) {


            val csvPatients = CSVReader.readCSV(context, "patient.csv") { values ->
                Patient(
                    patientID = values[0],
                    phoneNumber = values[1],
                    name = values[2],
                    password = ""
                )
            }

            val prefPatients = loadAccount(context).map {
                Patient(
                    patientID = it.patientID,
                    phoneNumber = it.phoneNumber,
                    name = it.name,
                    password = ""
                )
            }

            val allPatients = csvPatients + prefPatients

            patientsRepository.insertAll(allPatients)

            val csvMedications = CSVReader.readCSV(context, "medications.csv") { values ->
                Medication(
                    pID = values[0],
                    name = values[1],
                    dosage = values[2],
                    frequency = values[3],
                    time = values[4],
                    type = values[5],
                    notes = values[6]
                )
            }

            val prefMedications = loadMedication(context).map {
                Medication(
                    pID = it.pID,
                    name = it.name,
                    dosage = it.dosage,
                    frequency = it.frequency,
                    time = it.time,
                    type = it.type,
                    notes = it.notes,

                )
            }

            val allMedications = csvMedications + prefMedications

            medicationRepository.insertAll(allMedications) // or insertAll

            val symptoms = CSVReader.readCSV(context, "symptoms.csv") { values ->
                Symptom(
                    pID = values[0],
                    category = values[1],
                    severity = values[2],
                    notes = values[3],
                    dateTime = values[4]

                )
            }

            symptomRepository.insertAll(symptoms) // or insertAll


            prefs.edit()
                .putBoolean("db_seeded", true)
                .apply()
        }
    }
}