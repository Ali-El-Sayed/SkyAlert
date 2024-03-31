package com.example.skyalert.dataSource.local.db.model

import com.example.skyalert.model.remote.CurrentWeather

sealed class BookmarkedWeatherState {
    class Success(val currentWeather: MutableList<CurrentWeather>) : BookmarkedWeatherState()
    class Error(val message: String) : BookmarkedWeatherState()
    object Loading : BookmarkedWeatherState()
}