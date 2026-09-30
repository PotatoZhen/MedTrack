package com.wan.s34476474.medtrack.data.MedCoachTips

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface MedCoachTipDao {

    // Inserts a new MedCoach tip
    @Insert
    suspend fun insertTip(tip: MedCoachTip)

    // Retrieves all tips for a specific patient ordered by most recent first
    @Query("SELECT * FROM med_coach_tips WHERE patientId = :patientId ORDER BY timestamp DESC")
    fun getTipsById(patientId: String): Flow<List<MedCoachTip>>
}