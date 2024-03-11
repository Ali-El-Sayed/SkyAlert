package com.example.skyalert.network

import com.example.example.FiveDaysForecast
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("mode") mode: String = MODE.JSON.value,
        @Query("units") units: String = UNITS.STANDARD.value,
        @Query("lang") lang: String = LANG.ENGLISH.value
    )

    @GET("forecast")
    suspend fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("mode") mode: String = MODE.JSON.value,
        @Query("units") units: String = UNITS.STANDARD.value,
        @Query("lang") lang: String = LANG.ENGLISH.value
    ): FiveDaysForecast

}