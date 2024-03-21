package com.example.skyalert.dataSource.remote

import com.example.skyalert.network.LANG
import com.example.skyalert.network.MODE
import com.example.skyalert.network.UNITS
import com.example.skyalert.network.model.CurrentWeatherState
import com.example.skyalert.network.model.FiveDaysForecastState
import kotlinx.coroutines.flow.Flow

interface IWeatherRemoteDataSource {
    suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        mode: String = MODE.JSON.value,
        units: String = UNITS.METRIC.value,
        lang: String = LANG.ENGLISH.value
    ): Flow<CurrentWeatherState>

    suspend fun getForecast(
        lat: Double,
        lon: Double,
        cnt: Int = 8,
        mode: String = MODE.JSON.value,
        units: String = UNITS.STANDARD.value,
        lang: String = LANG.ENGLISH.value
    ): Flow<FiveDaysForecastState>

    fun getHourlyForecast(
        lat: Double,
        lon: Double,
        cnt: Int,
        mode: String = MODE.JSON.value,
        units: String = UNITS.STANDARD.value,
        lang: String = LANG.ENGLISH.value
    ): Flow<FiveDaysForecastState>


}