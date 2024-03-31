package com.example.skyalert.repository

import com.example.skyalert.dataSource.local.db.model.BookmarkedWeatherState
import com.example.skyalert.model.remote.Coord
import com.example.skyalert.model.remote.CurrentWeather
import com.example.skyalert.network.UNITS
import com.example.skyalert.network.model.CurrentWeatherState
import com.example.skyalert.network.model.FiveDaysForecastState
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


    /**
     * Database Local data source
     * */
    fun getLocalCurrentWeather(): Flow<BookmarkedWeatherState>

    suspend fun getGPSWeather(): CurrentWeatherState
    suspend fun getMapWeather(): CurrentWeatherState
    suspend fun getFavoriteWeather(): Flow<List<CurrentWeather>>
    suspend fun deleteFavoriteWeather(currentWeather: CurrentWeather): Int

    suspend fun insertCurrentWeather(currentWeather: CurrentWeather): Long

    /**
     * Remote data source
     * */

    suspend fun getCurrentWeather(): Flow<CurrentWeatherState>

    suspend fun getHourlyForecast(cnt: Int = 8): Flow<FiveDaysForecastState>

    suspend fun getCurrentWeatherByCoord(coord: Coord): Flow<CurrentWeatherState>


}