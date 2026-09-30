package com.wan.s34476474.medtrack.data.symptoms

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.wan.s34476474.medtrack.data.medications.Medication
import kotlinx.coroutines.flow.Flow

@Dao
interface SymptomDao {

    @Insert
    suspend fun insert(symptom: Symptom)

    @Insert
    suspend fun insertAll(symptoms: List<Symptom>)

    @Query("SELECT * FROM symptoms WHERE pID = :patientID ORDER BY dateTime DESC")
    fun getAllSymptomsById(patientID: String): Flow<List<Symptom?>>

    // Returns the most frequently recorded symptom category
    @Query("""
        SELECT category
        FROM symptoms
        GROUP BY category
        ORDER BY COUNT(*) DESC
        LIMIT 1
    """)
    fun getMostCommonSymptomCategory(): Flow<String?>

    // Calculates the average severity of all recorded symptoms
    @Query("""
    SELECT AVG(severity)
    FROM symptoms
""")
    fun getAverageSeverity(): Flow<Float?>
}