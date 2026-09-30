package com.wan.s34476474.medtrack.data.network

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface APIService {

    // Calls OpenFDA API to fetch drug label information based on search query
    @GET("drug/label.json")
    suspend fun getDrugInfo(
        @Query("search") search: String,
        @Query("limit") limit: Int = 1
    ): Response<ResponseModel>

    companion object {
        var BASE_URL = "https://api.fda.gov/"

        // Builds and returns Retrofit instance for API communication
        fun create(): APIService {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(APIService::class.java)
        }
    }
}