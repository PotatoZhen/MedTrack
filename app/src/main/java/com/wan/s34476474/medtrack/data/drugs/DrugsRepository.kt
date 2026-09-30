package com.wan.s34476474.medtrack.data.drugs

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.wan.s34476474.medtrack.data.network.APIService
import com.wan.s34476474.medtrack.data.network.ResponseModel

class DrugsRepository( private val applicationContext: Context) {
    private val apiService = APIService.Companion.create()

    // Fetches drug label information for a given medication name from OpenFDA API
    suspend fun getDrugInfo(medicationName: String): ResponseModel? {

        return try {
            if (!isNetworkAvailable()) return null

            val searchQuery = """openfda.brand_name:"$medicationName""""
            val response = apiService.getDrugInfo(searchQuery)

            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }

        } catch (e: Exception) {
            null
        }
    }

    // Checks whether the device currently has an active internet connection
    fun isNetworkAvailable(): Boolean {
        val connectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }
}