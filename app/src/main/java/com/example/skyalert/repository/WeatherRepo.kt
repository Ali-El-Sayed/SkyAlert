package com.example.skyalert.repository

import com.example.skyalert.dataSource.local.sharedPref.ISharedPreference
import com.example.skyalert.dataSource.remote.IWeatherRemoteDataSource
import com.example.skyalert.network.LANG
import com.example.skyalert.network.MODE
import com.example.skyalert.network.UNITS
import com.example.skyalert.network.model.CurrentWeatherState
import kotlinx.coroutines.flow.Flow

class WeatherRepo private constructor(
    private val weatherRemoteDatasource: IWeatherRemoteDataSource,
    private val _iSharedPreference: ISharedPreference
) :
    IWeatherRepo {


    companion object {
        @Volatile
        private var INSTANCE: WeatherRepo? = null
        fun getInstance(
            weatherRemoteDatasource: IWeatherRemoteDataSource,
            iSharedPreference: ISharedPreference
        ) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: WeatherRepo(
                    weatherRemoteDatasource,
                    iSharedPreference
                ).also { INSTANCE = it }
            }
    }

    override suspend fun getCurrentWeather(
        lat: Double, lon: Double
    ): Flow<CurrentWeatherState> {
        val unit = _iSharedPreference.getUnit()
        return weatherRemoteDatasource.getCurrentWeather(
            lat,
            lon,
            MODE.JSON.value,
            unit.value,
            LANG.ENGLISH.value
        )
    }

    override fun getUnit(): UNITS {
        return _iSharedPreference.getUnit()
    }

    override fun setUnit(unit: UNITS) {
        _iSharedPreference.saveUnit(unit)
    }
}