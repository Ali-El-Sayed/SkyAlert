package com.example.skyalert.dataSource.remote

import com.example.skyalert.dataSource.local.db.model.BookmarkedWeatherState
import com.example.skyalert.network.model.FiveDaysForecastState
import kotlinx.coroutines.flow.Flow

class FakeWeatherRemoteDatasource : IWeatherRemoteDataSource {
    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        mode: String,
        units: String,
        lang: String
    ): Flow<BookmarkedWeatherState> {
        TODO("Not yet implemented")
    }

    override suspend fun getForecast(
        lat: Double,
        lon: Double,
        cnt: Int,
        mode: String,
        units: String,
        lang: String
    ): Flow<FiveDaysForecastState> {
        TODO("Not yet implemented")
    }

    override fun getHourlyForecast(
        lat: Double,
        lon: Double,
        cnt: Int,
        mode: String,
        units: String,
        lang: String
    ): Flow<FiveDaysForecastState> {
        TODO("Not yet implemented")
    }
}