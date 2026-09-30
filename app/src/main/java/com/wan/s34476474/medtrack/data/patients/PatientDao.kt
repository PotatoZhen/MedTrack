package com.wan.s34476474.medtrack.data.patients

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {
    @Insert
    suspend fun insertPatient(patient: Patient)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(patients: List<Patient>)

    @Query("SELECT * FROM patients")
    fun getAllPatients(): Flow<List<Patient>>

    @Query("SELECT * FROM patients WHERE patientID = :patientID")
    suspend fun getPatientById(patientID: String): Patient?

    @Query("SELECT * FROM patients WHERE phoneNumber = :phoneNumber")
    suspend fun getPatientByPhoneNumber(phoneNumber: String): Patient?

    // Updates a patient's password for account claiming
    @Query("UPDATE patients SET password = :password WHERE patientID = :patientID")
    suspend fun updatePassword(patientID: String, password: String)

    @Query("SELECT name FROM patients WHERE patientID = :patientID")
    fun getPatientName(patientID: String?): Flow<String?>

    // Returns patient phone number
    @Query("SELECT phoneNumber FROM patients WHERE patientID = :patientID")
    fun getPatientPhoneNumber(patientID: String?): Flow<String?>

    // Returns total number of patients in database for dashboard
    @Query("SELECT COUNT(*) FROM patients")
    fun getPatientCount(): Flow<Int>

}

