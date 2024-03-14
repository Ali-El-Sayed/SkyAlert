package com.example.skyalert.repository

import com.example.example.CurrentWeather
import com.example.skyalert.network.LANG
import com.example.skyalert.network.MODE
import com.example.skyalert.network.UNITS
import com.example.skyalert.network.model.CurrentWeatherState
import kotlinx.coroutines.flow.Flow

interface IWeatherRepo {
    suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        mode: String = MODE.JSON.value,
        units: String = UNITS.STANDARD.value,
        lang: String = LANG.ENGLISH.value
    ): Flow<CurrentWeatherState>

}