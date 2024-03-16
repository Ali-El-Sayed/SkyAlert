package com.example.skyalert.repository

import com.example.skyalert.DataSource.remote.IWeatherRemoteDataSource
import com.example.skyalert.network.model.CurrentWeatherState
import kotlinx.coroutines.flow.Flow

class WeatherRepo private constructor(private val weatherRemoteDatasource: IWeatherRemoteDataSource) :
    IWeatherRepo {


    companion object {
        @Volatile
        private var INSTANCE: WeatherRepo? = null
        fun getInstance(weatherRemoteDatasource: IWeatherRemoteDataSource) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: WeatherRepo(weatherRemoteDatasource).also { INSTANCE = it }
            }
    }

    override suspend fun getCurrentWeather(
        lat: Double, lon: Double, mode: String, units: String, lang: String
    ): Flow<CurrentWeatherState> {
        return weatherRemoteDatasource.getCurrentWeather(lat, lon, mode, units, lang)
    }
}