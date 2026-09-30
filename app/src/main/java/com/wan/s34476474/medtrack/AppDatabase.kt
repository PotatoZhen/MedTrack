package com.wan.s34476474.medtrack

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.wan.s34476474.medtrack.data.MedCoachTips.MedCoachTipDao
import com.wan.s34476474.medtrack.data.patients.Patient
import com.wan.s34476474.medtrack.data.medications.Medication
import com.wan.s34476474.medtrack.data.medications.MedicationDao
import com.wan.s34476474.medtrack.data.patients.PatientDao
import com.wan.s34476474.medtrack.data.symptoms.Symptom
import com.wan.s34476474.medtrack.data.symptoms.SymptomDao
import com.wan.s34476474.medtrack.data.MedCoachTips.MedCoachTip
import com.wan.s34476474.medtrack.data.takenstatus.TakenStatus
import com.wan.s34476474.medtrack.data.takenstatus.TakenStatusDao
import kotlin.time.Instant

@Database(entities = [Patient::class, Medication::class, Symptom::class, MedCoachTip::class, TakenStatus::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun patientDao(): PatientDao

    abstract fun medicationDao(): MedicationDao

    abstract fun symptomDao(): SymptomDao

    abstract fun medCoachTipDao(): MedCoachTipDao

    abstract fun takenStatusDao(): TakenStatusDao

    companion object {
        @Volatile
        private var Instance: AppDatabase ?= null


        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "item_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}