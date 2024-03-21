package com.example.skyalert.repository

import com.example.skyalert.network.UNITS
import com.example.skyalert.network.model.CurrentWeatherState
import com.example.skyalert.network.model.FiveDaysForecastState
import kotlinx.coroutines.flow.Flow

interface IWeatherRepo {
    suspend fun getCurrentWeather(
        lat: Double,
        lon: Double
    ): Flow<CurrentWeatherState>

    suspend fun getHourlyForecast(
        lat: Double,
        lon: Double,
        cnt: Int = 8
    ): Flow<FiveDaysForecastState>


    fun getUnit(): UNITS
    fun setUnit(unit: UNITS)


}