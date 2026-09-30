package com.wan.s34476474.medtrack.data.takenstatus

import android.content.Context
import com.wan.s34476474.medtrack.AppDatabase

class TakenStatusRepository(private val context: Context) {

    private val takenStatusDao = AppDatabase.getDatabase(context).takenStatusDao()


    suspend fun insert(status: TakenStatus) {
        takenStatusDao.insert(status)
    }

    fun getToday(patientId: String, date: String) =
        takenStatusDao.getTodayStatus(patientId, date)
}