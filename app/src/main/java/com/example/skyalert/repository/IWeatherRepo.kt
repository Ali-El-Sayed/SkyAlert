package com.example.skyalert.repository

import com.example.skyalert.model.Coord
import com.example.skyalert.network.UNITS
import com.example.skyalert.network.model.CurrentWeatherState
import com.example.skyalert.network.model.FiveDaysForecastState
import com.example.skyalert.view.screens.settings.model.LOCATION_SOURCE
import kotlinx.coroutines.flow.Flow

interface IWeatherRepo {
    suspend fun getCurrentWeather(): Flow<CurrentWeatherState>

    suspend fun getHourlyForecast(cnt: Int = 8): Flow<FiveDaysForecastState>


    fun getUnit(): UNITS
    fun setUnit(unit: UNITS)

    fun setGPSLocation(coord: Coord)
    fun getGPSLocation(): Coord

    fun setLocationSource(locationType: LOCATION_SOURCE)
    fun getLocationSource(): LOCATION_SOURCE

    fun setMapLocation(coord: Coord)
    fun getMapLocation(): Coord
    suspend fun getCurrentWeatherByCoord(coord: Coord): Flow<CurrentWeatherState>


}