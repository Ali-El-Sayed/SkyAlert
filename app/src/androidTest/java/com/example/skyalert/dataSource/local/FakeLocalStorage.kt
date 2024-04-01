package com.example.skyalert.dataSource.local

import com.example.skyalert.dataSource.local.localStorage.ILocalStorage
import com.example.skyalert.model.remote.FiveDaysForecast

class FakeLocalStorage : ILocalStorage {
    override suspend fun saveFiveDaysForecast(fiveDaysForecast: FiveDaysForecast): String {
        TODO("Not yet implemented")
    }

    override suspend fun getFiveDaysForecast(fileName: String): FiveDaysForecast {
        TODO("Not yet implemented")
    }
}