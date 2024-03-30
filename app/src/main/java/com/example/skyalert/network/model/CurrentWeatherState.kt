package com.example.skyalert.network.model

import com.example.skyalert.model.remote.CurrentWeather

sealed class CurrentWeatherState {
    class Success(val currentWeather: CurrentWeather) : CurrentWeatherState()
    class Error(val message: String) : CurrentWeatherState()
    object Loading : CurrentWeatherState()
}