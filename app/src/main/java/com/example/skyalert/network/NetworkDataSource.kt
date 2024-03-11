package com.example.skyalert.network

import com.example.example.FiveDaysForecast

interface NetworkDataSource {

    suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        mode: String = MODE.JSON.value,
        units: String = UNITS.STANDARD.value,
        lang: String = LANG.ENGLISH.value
    )

    suspend fun getForecast(
        lat: Double,
        lon: Double,
        mode: String = MODE.JSON.value,
        units: String = UNITS.STANDARD.value,
        lang: String = LANG.ENGLISH.value
    ):FiveDaysForecast
}