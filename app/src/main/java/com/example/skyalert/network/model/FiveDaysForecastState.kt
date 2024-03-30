package com.example.skyalert.network.model

import com.example.skyalert.model.remote.FiveDaysForecast

sealed class FiveDaysForecastState {
    data class Success(val data: FiveDaysForecast) : FiveDaysForecastState()
    data class Error(val message: String) : FiveDaysForecastState()
    object Loading : FiveDaysForecastState()
}