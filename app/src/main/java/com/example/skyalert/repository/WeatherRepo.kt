package com.example.skyalert.repository

import com.example.skyalert.dataSource.local.sharedPref.ISharedPreference
import com.example.skyalert.dataSource.remote.IWeatherRemoteDataSource
import com.example.skyalert.model.Coord
import com.example.skyalert.network.LANG
import com.example.skyalert.network.MODE
import com.example.skyalert.network.UNITS
import com.example.skyalert.network.model.CurrentWeatherState
import com.example.skyalert.network.model.FiveDaysForecastState
import com.example.skyalert.view.screens.settings.model.LOCATION_SOURCE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WeatherRepo private constructor(
    private val weatherRemoteDatasource: IWeatherRemoteDataSource,
    private val _iSharedPreference: ISharedPreference
) : IWeatherRepo {


    companion object {
        @Volatile
        private var INSTANCE: WeatherRepo? = null
        fun getInstance(
            weatherRemoteDatasource: IWeatherRemoteDataSource, iSharedPreference: ISharedPreference
        ) = INSTANCE ?: synchronized(this) {
            INSTANCE ?: WeatherRepo(
                weatherRemoteDatasource, iSharedPreference
            ).also { INSTANCE = it }
        }
    }


    /**
     * Shared preference data source
     * */

    override fun getUnit() = _iSharedPreference.getUnit()


    override fun setUnit(unit: UNITS) {
        _iSharedPreference.saveUnit(unit)
    }

    override fun setGPSLocation(coord: Coord) {
        _iSharedPreference.setGPSLocation(coord)
    }

    override fun getGPSLocation() = _iSharedPreference.getGPSLocation()
    override fun setLocationSource(locationType: LOCATION_SOURCE) {
        _iSharedPreference.setLocationSource(locationType)
    }

    override fun getLocationSource(): LOCATION_SOURCE {
        return _iSharedPreference.getLocationSource()
    }

    override fun setMapLocation(coord: Coord) {
        _iSharedPreference.setMapLocation(coord)
    }

    override fun getMapLocation(): Coord {
        return _iSharedPreference.getMapLocation()
    }

    override fun saveAlertLocation(coord: Coord) {
        _iSharedPreference.saveAlertCoord(coord)
    }

    override fun getAlertLocation(): Coord {
        return _iSharedPreference.getAlertCoord()
    }

    override suspend fun getCurrentWeatherByCoord(coord: Coord): Flow<CurrentWeatherState> {
        val unit = _iSharedPreference.getUnit()
        return weatherRemoteDatasource.getCurrentWeather(
            coord.lat, coord.lon, MODE.JSON.value, unit.value, LANG.ENGLISH.value
        )
    }

    private fun getCordFromLocationSource() = when (getLocationSource()) {
        LOCATION_SOURCE.GPS -> getGPSLocation()
        LOCATION_SOURCE.MAP -> getMapLocation()
    }

    /**
     * Database Local data source
     * */


    /**
     *  Remote data source
     * */

    override suspend fun getCurrentWeather(): Flow<CurrentWeatherState> {
        val unit = _iSharedPreference.getUnit()
        val cord = getCordFromLocationSource()

        return weatherRemoteDatasource.getCurrentWeather(
            cord.lat, cord.lon, MODE.JSON.value, unit.value, LANG.ENGLISH.value
        ).map {
            when (it) {
                is CurrentWeatherState.Success -> {
                    CurrentWeatherState.Success(it.currentWeather.copy(unit = unit))
                }

                is CurrentWeatherState.Error -> it
                is CurrentWeatherState.Loading -> it
            }
        }
    }

    override suspend fun getCurrentWeather(coord: Coord): Flow<CurrentWeatherState> {
        val unit = _iSharedPreference.getUnit()
        return weatherRemoteDatasource.getCurrentWeather(
            coord.lat, coord.lon, MODE.JSON.value, unit.value, LANG.ENGLISH.value
        ).map {
            when (it) {
                is CurrentWeatherState.Success -> {
                    CurrentWeatherState.Success(it.currentWeather.copy(unit = unit))
                }

                is CurrentWeatherState.Error -> it
                is CurrentWeatherState.Loading -> it
            }
        }
    }

    override suspend fun getHourlyForecast(cnt: Int): Flow<FiveDaysForecastState> {
        val unit = _iSharedPreference.getUnit()
        val cord = getCordFromLocationSource()
        return weatherRemoteDatasource.getHourlyForecast(
            cord.lat, cord.lon, cnt, MODE.JSON.value, unit.value, LANG.ENGLISH.value
        )
    }


}