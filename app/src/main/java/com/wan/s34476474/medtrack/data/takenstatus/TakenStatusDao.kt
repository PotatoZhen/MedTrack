package com.wan.s34476474.medtrack.data.takenstatus

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TakenStatusDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(status: TakenStatus)

    // Retrieves all medication status records for a patient on a specific date
    @Query("SELECT * FROM taken_status WHERE patientId = :patientId AND date = :date")
    fun getTodayStatus(patientId: String, date: String): Flow<List<TakenStatus>>


}