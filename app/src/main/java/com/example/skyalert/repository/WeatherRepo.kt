package com.example.skyalert.repository

import com.example.skyalert.dataSource.local.sharedPref.ISharedPreference
import com.example.skyalert.dataSource.remote.IWeatherRemoteDataSource
import com.example.skyalert.model.Coord
import com.example.skyalert.network.LANG
import com.example.skyalert.network.MODE
import com.example.skyalert.network.UNITS
import com.example.skyalert.network.model.CurrentWeatherState
import com.example.skyalert.network.model.FiveDaysForecastState
import kotlinx.coroutines.flow.Flow

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

    override suspend fun getCurrentWeather(): Flow<CurrentWeatherState> {
        val unit = _iSharedPreference.getUnit()
        val cord = _iSharedPreference.getDefaultLocation()
        return weatherRemoteDatasource.getCurrentWeather(
            cord.lat, cord.lon, MODE.JSON.value, unit.value, LANG.ENGLISH.value
        )
    }

    override suspend fun getHourlyForecast(cnt: Int): Flow<FiveDaysForecastState> {
        val unit = _iSharedPreference.getUnit()
        val cord = _iSharedPreference.getDefaultLocation()
        return weatherRemoteDatasource.getHourlyForecast(
            cord.lat, cord.lon, cnt, MODE.JSON.value, unit.value, LANG.ENGLISH.value
        )
    }

    override fun getUnit() = _iSharedPreference.getUnit()


    override fun setUnit(unit: UNITS) {
        _iSharedPreference.saveUnit(unit)
    }

    override fun setDefaultLocation(coord: Coord) {
        _iSharedPreference.setDefaultLocation(coord)
    }

    override fun getDefaultLocation() = _iSharedPreference.getDefaultLocation()
}