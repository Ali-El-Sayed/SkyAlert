package com.example.skyalert.repository

import com.example.skyalert.model.Coord
import com.example.skyalert.network.UNITS
import com.example.skyalert.network.model.CurrentWeatherState
import com.example.skyalert.network.model.FiveDaysForecastState
import kotlinx.coroutines.flow.Flow

interface IWeatherRepo {
    suspend fun getCurrentWeather(): Flow<CurrentWeatherState>

    suspend fun getHourlyForecast(cnt: Int = 8): Flow<FiveDaysForecastState>


    fun getUnit(): UNITS
    fun setUnit(unit: UNITS)

    fun setDefaultLocation(coord: Coord)
    fun getDefaultLocation(): Coord

}