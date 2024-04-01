package com.example.skyalert.repository

import android.util.Log
import com.example.skyalert.dataSource.local.IWeatherLocalDatasource
import com.example.skyalert.dataSource.local.db.model.AlertsState
import com.example.skyalert.dataSource.remote.IWeatherRemoteDataSource
import com.example.skyalert.model.remote.Coord
import com.example.skyalert.model.remote.CurrentWeather
import com.example.skyalert.model.remote.FiveDaysForecast
import com.example.skyalert.network.MODE
import com.example.skyalert.network.UNITS
import com.example.skyalert.network.model.CurrentWeatherState
import com.example.skyalert.network.model.FiveDaysForecastState
import com.example.skyalert.services.alarm.model.Alert
import com.example.skyalert.view.screens.settings.model.LOCAL
import com.example.skyalert.view.screens.settings.model.LOCATION_SOURCE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class WeatherRepo private constructor(
    private val _weatherRemoteDatasource: IWeatherRemoteDataSource, private val _weatherLocalDatasource: IWeatherLocalDatasource
) : IWeatherRepo {


    companion object {
        @Volatile
        private var INSTANCE: WeatherRepo? = null
        fun getInstance(
            weatherRemoteDatasource: IWeatherRemoteDataSource, iWeatherLocalDatasource: IWeatherLocalDatasource
        ) = INSTANCE ?: synchronized(this) {
            INSTANCE ?: WeatherRepo(
                weatherRemoteDatasource, iWeatherLocalDatasource
            ).also { INSTANCE = it }
        }
    }


    /**
     * Shared preference data source
     * */

    override fun getUnit() = _weatherLocalDatasource.getUnit()


    override fun setUnit(unit: UNITS) {
        _weatherLocalDatasource.setUnit(unit)
    }

    override fun setGPSLocation(coord: Coord) {
        _weatherLocalDatasource.setGPSLocation(coord)
    }

    override fun getGPSLocation() = _weatherLocalDatasource.getGPSLocation()
    override fun setLocationSource(locationType: LOCATION_SOURCE) {
        _weatherLocalDatasource.setLocationSource(locationType)
    }

    override fun getLocationSource(): LOCATION_SOURCE = _weatherLocalDatasource.getLocationSource()
    override fun setMapLocation(coord: Coord) {
        _weatherLocalDatasource.setMapLocation(coord)
    }

    override fun getMapLocation(): Coord = _weatherLocalDatasource.getMapLocation()
    override fun saveAlertLocation(coord: Coord) {
        _weatherLocalDatasource.saveAlertLocation(coord)
    }

    override fun getAlertLocation(): Coord = _weatherLocalDatasource.getAlertLocation()


    private fun getCordFromLocationSource() = when (getLocationSource()) {
        LOCATION_SOURCE.GPS -> getGPSLocation()
        LOCATION_SOURCE.MAP -> getMapLocation()
    }

    override fun setLanguage(language: LOCAL) {
        _weatherLocalDatasource.setLanguage(language)
    }

    override fun getLanguage(): LOCAL = _weatherLocalDatasource.getLanguage()

    /**
     * Database Local data source
     * */
    override suspend fun insertCurrentWeather(currentWeather: CurrentWeather): Long =
        _weatherLocalDatasource.insertCurrentWeather(currentWeather)

    override suspend fun getAllAlerts(): Flow<AlertsState> = _weatherLocalDatasource.getAllAlarms()
    override suspend fun insertAlert(alert: Alert): Long = _weatherLocalDatasource.insertAlert(alert)
    override suspend fun deleteAlert(alert: Alert): Int = _weatherLocalDatasource.deleteAlert(alert)

    override suspend fun deleteAlert(currentWeather: CurrentWeather): Int =
        _weatherLocalDatasource.deleteBookmarks(currentWeather)

    override fun getLocalCurrentWeather(): Flow<CurrentWeatherState> = flow {
        _weatherLocalDatasource.getLocalCurrentWeather()
    }

    override suspend fun getGPSWeather(): CurrentWeatherState = _weatherLocalDatasource.getGPSWeather()

    override suspend fun getMapWeather(): CurrentWeatherState = _weatherLocalDatasource.getMapWeather()

    override suspend fun getFavoriteWeather(): Flow<List<CurrentWeather>> = _weatherLocalDatasource.getBookmarks()

    /**
     * Local Storage
     * */
    override suspend fun saveLocalFiveDaysForecast(fiveDaysForecast: FiveDaysForecast) {
        _weatherLocalDatasource.saveFiveDaysForecast(fiveDaysForecast)
    }

    override suspend fun getLocalFiveDaysForecast(): Flow<FiveDaysForecastState> {
        return _weatherLocalDatasource.getFiveDaysForecast()
    }

    /**
     *  Remote data source
     * */

    override suspend fun getCurrentWeatherByCoord(coord: Coord, flag: Boolean): Flow<CurrentWeatherState> {
        val unit = _weatherLocalDatasource.getUnit()
        val lang = _weatherLocalDatasource.getLanguage()
        if (flag) return _weatherRemoteDatasource.getCurrentWeather(
            coord.lat, coord.lon, MODE.JSON.value, unit.value, lang.value
        ).map {
            when (it) {
                is CurrentWeatherState.Success -> {
                    val currentWeather = it.currentWeather.copy(unit = unit)

                    getLocationSource().let { source ->
                        when (source) {
                            LOCATION_SOURCE.GPS -> currentWeather.isGPS = true
                            LOCATION_SOURCE.MAP -> currentWeather.isMap = true
                        }
                    }
                    CurrentWeatherState.Success(currentWeather)
                }

                is CurrentWeatherState.Error -> it
                is CurrentWeatherState.Loading -> it
            }
        }
        return flow { CurrentWeatherState.Error("No internet connection") }
    }

    override suspend fun getCurrentWeather(flag: Boolean): Flow<CurrentWeatherState> {
        val unit = _weatherLocalDatasource.getUnit()
        val cord = getCordFromLocationSource()
        val lang = _weatherLocalDatasource.getLanguage()
        Log.d("lang", "getHourlyForecast: $lang")

        return if (flag) {
            _weatherRemoteDatasource.getCurrentWeather(
                cord.lat, cord.lon, MODE.JSON.value, unit.value, lang.value
            ).map {
                when (it) {
                    is CurrentWeatherState.Success -> {
                        val currentWeather = it.currentWeather.copy(unit = unit)

                        getLocationSource().let { source ->
                            when (source) {
                                LOCATION_SOURCE.GPS -> currentWeather.isGPS = true
                                LOCATION_SOURCE.MAP -> currentWeather.isMap = true
                            }
                        }
                        CurrentWeatherState.Success(currentWeather)
                    }

                    is CurrentWeatherState.Error -> it
                    is CurrentWeatherState.Loading -> it
                }
            }
        } else getLocalCurrentWeather()

    }

    override suspend fun getHourlyForecast(cnt: Int, flag: Boolean): Flow<FiveDaysForecastState> {
        return if (flag) {
            val unit = _weatherLocalDatasource.getUnit()
            val cord = getCordFromLocationSource()
            val lang = _weatherLocalDatasource.getLanguage()
            Log.d("lang", "getHourlyForecast: $lang")
            _weatherRemoteDatasource.getHourlyForecast(
                cord.lat, cord.lon, cnt, MODE.JSON.value, unit.value, lang.value
            )
        } else getLocalFiveDaysForecast()
    }

}