package com.example.skyalert.network

import com.example.example.FiveDaysForecast

object NetworkDataSourceImpl : NetworkDataSource {
    private const val apiKey: String = "fff516e2c3c9188a401a978960382b36"
    private val apiService = RetrofitClient.apiService

    override suspend fun getCurrentWeather(
        lat: Double, lon: Double, mode: String, units: String, lang: String
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun getForecast(
        lat: Double, lon: Double, mode: String, units: String, lang: String
    ): FiveDaysForecast {
        return apiService.getForecast(lat, lon, apiKey, mode, units, lang)
    }
}