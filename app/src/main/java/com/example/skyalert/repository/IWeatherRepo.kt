package com.example.skyalert.repository

import com.example.skyalert.dataSource.local.db.model.AlertsState
import com.example.skyalert.model.remote.Coord
import com.example.skyalert.model.remote.CurrentWeather
import com.example.skyalert.model.remote.FiveDaysForecast
import com.example.skyalert.network.UNITS
import com.example.skyalert.network.model.CurrentWeatherState
import com.example.skyalert.network.model.FiveDaysForecastState
import com.example.skyalert.services.alarm.model.Alert
import com.example.skyalert.view.screens.settings.model.LOCAL
import com.example.skyalert.view.screens.settings.model.LOCATION_SOURCE
import kotlinx.coroutines.flow.Flow

interface IWeatherRepo {

    /**
     * shared preference data source
     * */
    fun getUnit(): UNITS
    fun setUnit(unit: UNITS)

    fun setGPSLocation(coord: Coord)
    fun getGPSLocation(): Coord

    fun setLocationSource(locationType: LOCATION_SOURCE)
    fun getLocationSource(): LOCATION_SOURCE

    fun setMapLocation(coord: Coord)
    fun getMapLocation(): Coord

    fun saveAlertLocation(coord: Coord)
    fun getAlertLocation(): Coord
    fun setLanguage(language: LOCAL)
    fun getLanguage(): LOCAL


    /**
     * Database Local data source
     * */
    // weather methods
    fun getLocalCurrentWeather(): Flow<CurrentWeatherState>
    suspend fun getGPSWeather(): CurrentWeatherState
    suspend fun getMapWeather(): CurrentWeatherState
    suspend fun getFavoriteWeather(): Flow<List<CurrentWeather>>
    suspend fun deleteAlert(currentWeather: CurrentWeather): Int
    suspend fun insertCurrentWeather(currentWeather: CurrentWeather): Long

    // Alert methods
    suspend fun getAllAlerts(): Flow<AlertsState>
    suspend fun insertAlert(alert: Alert): Long
    suspend fun deleteAlert(alert: Alert): Int

    /**
     * Remote data source
     * */

    suspend fun getCurrentWeather(flag: Boolean = true): Flow<CurrentWeatherState>

    suspend fun getHourlyForecast(cnt: Int = 8, flag: Boolean = true): Flow<FiveDaysForecastState>

    suspend fun getCurrentWeatherByCoord(coord: Coord, flag: Boolean = true): Flow<CurrentWeatherState>

    /**
     * Local Storage
     * */

    suspend fun getLocalFiveDaysForecast(): Flow<FiveDaysForecastState>
    suspend fun saveLocalFiveDaysForecast(fiveDaysForecast: FiveDaysForecast)


}