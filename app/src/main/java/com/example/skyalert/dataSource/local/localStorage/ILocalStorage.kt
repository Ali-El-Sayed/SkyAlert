package com.example.skyalert.dataSource.local.localStorage

import com.example.skyalert.model.remote.FiveDaysForecast

interface ILocalStorage {
    suspend fun saveFiveDaysForecast(fiveDaysForecast: FiveDaysForecast): String
    suspend fun getFiveDaysForecast(fileName: String): FiveDaysForecast
}