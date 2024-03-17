package com.example.skyalert.repository

import com.example.skyalert.network.UNITS
import com.example.skyalert.network.model.CurrentWeatherState
import kotlinx.coroutines.flow.Flow

interface IWeatherRepo {
    suspend fun getCurrentWeather(
        lat: Double,
        lon: Double
    ): Flow<CurrentWeatherState>

    fun getUnit(): UNITS
    fun setUnit(unit: UNITS)


}