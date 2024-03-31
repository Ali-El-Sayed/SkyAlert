package com.example.skyalert.dataSource.local

import com.example.skyalert.dataSource.local.db.model.AlertsState
import com.example.skyalert.model.remote.Coord
import com.example.skyalert.model.remote.CurrentWeather
import com.example.skyalert.network.UNITS
import com.example.skyalert.network.model.CurrentWeatherState
import com.example.skyalert.services.alarm.model.Alert
import com.example.skyalert.view.screens.settings.model.LOCATION_SOURCE
import kotlinx.coroutines.flow.Flow

interface IWeatherLocalDatasource {
    /**
     * Shared preferences methods
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

    /**
     *  Database methods
     * */

    // weather methods
    suspend fun getGPSWeather(): CurrentWeatherState
    suspend fun getMapWeather(): CurrentWeatherState
    suspend fun getBookmarks(): Flow<List<CurrentWeather>>
    suspend fun insertCurrentWeather(currentWeather: CurrentWeather): Long
    suspend fun deleteBookmarks(currentWeather: CurrentWeather): Int

    // alert methods
    suspend fun getAllAlarms(): Flow<AlertsState>
    suspend fun insertAlert(alert: Alert): Long
    suspend fun deleteAlert(alert: Alert): Int
}