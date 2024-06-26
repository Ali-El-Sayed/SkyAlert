package com.example.skyalert.dataSource.remote

import com.example.skyalert.network.ApiService
import com.example.skyalert.network.model.CurrentWeatherState
import com.example.skyalert.network.model.FiveDaysForecastState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WeatherRemoteDatasource
private constructor(
    private val apiService: ApiService
) : IWeatherRemoteDataSource {
    private val apiKey: String = "fff516e2c3c9188a401a978960382b36"

    companion object {
        @Volatile
        private var INSTANCE: WeatherRemoteDatasource? = null

        fun getInstance(apiService: ApiService) = INSTANCE ?: synchronized(this) {
            INSTANCE ?: WeatherRemoteDatasource(apiService).also { INSTANCE = it }
        }
    }

    override suspend fun getCurrentWeather(
        lat: Double, lon: Double, mode: String, units: String, lang: String
    ): Flow<CurrentWeatherState> = flow {
        val response = apiService.getCurrentWeather(lat, lon, apiKey, mode, units, lang).execute()
        when {
            response.isSuccessful -> {
                val body = response.body()
                if (body != null) {
                    emit(CurrentWeatherState.Success(body))
                } else {
                    emit(CurrentWeatherState.Error("No data found"))
                }
            }

            else -> {
                emit(CurrentWeatherState.Error(response.message()))
            }
        }
    }

    override suspend fun getForecast(
        lat: Double, lon: Double, cnt: Int, mode: String, units: String, lang: String
    ): Flow<FiveDaysForecastState> {
        return flow {
            val response = apiService.getForecast(lat, lon, cnt, apiKey, mode, units, lang)
            if (response.list.isNotEmpty()) {
                emit(FiveDaysForecastState.Success(response))
            } else {
                emit(FiveDaysForecastState.Error("No data found"))
            }
        }
    }

    override fun getHourlyForecast(
        lat: Double,
        lon: Double,
        cnt: Int,
        mode: String,
        units: String,
        lang: String
    ): Flow<FiveDaysForecastState> {
        return flow {
            val response = apiService.getForecast(lat, lon, cnt, apiKey, mode, units, lang)
            if (response.list.isNotEmpty()) {
                emit(FiveDaysForecastState.Success(response))
            } else {
                emit(FiveDaysForecastState.Error("No data found"))
            }
        }
    }
}