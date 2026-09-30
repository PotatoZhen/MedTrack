package com.wan.s34476474.medtrack.data.medications

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wan.s34476474.medtrack.data.patients.Patient
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationDao {

    // Inserts a single medication record into the database
    @Insert
    suspend fun insertMedication(medication: Medication)

    // Inserts a list of medications into the database
    @Insert
    suspend fun insertAll(medications: List<Medication>)

    // Retrieves all medications from the database
    @Query("SELECT * FROM medications")
    fun getAllMedications(): Flow<List<Medication>>

    // Retrieves all medications belonging to a specific patient
    @Query("SELECT * FROM medications WHERE pID = :patientID")
    fun getAllMedicationById(patientID: String): Flow<List<Medication?>>

    // Calculates the average number of medications per patient
    @Query("""
        SELECT AVG(medCount)
        FROM (
            SELECT COUNT(*) as medCount
            FROM medications
            GROUP BY pID
        )
    """)
    fun getAverageMedicationsPerPatient(): Flow<Float?>

}