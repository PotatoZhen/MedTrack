package com.wan.s34476474.medtrack.data.MedCoachTips

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.wan.s34476474.medtrack.AppDatabase

class MedCoachTipRepository(
    private val context : Context
) {

    private val tipsDao = AppDatabase.getDatabase(context).medCoachTipDao()

    // Checks device network connectivity status
    fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }

    // Saves a generated MedCoach tip into the Room database for a specific patient
    suspend fun saveTip(patientId: String, message: String) {
        tipsDao.insertTip(
            MedCoachTip(
                patientId = patientId,
                message = message
            )
        )
    }

    // Retrieves all saved MedCoach tips for a patient
    fun getTipsById(patientId: String) = tipsDao.getTipsById(patientId)
}