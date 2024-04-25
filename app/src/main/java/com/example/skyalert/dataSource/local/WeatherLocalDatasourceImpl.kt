package com.example.skyalert.dataSource.local

import android.util.Log
import com.example.skyalert.dataSource.local.db.WeatherDao
import com.example.skyalert.dataSource.local.db.model.AlertsState
import com.example.skyalert.dataSource.local.localStorage.ILocalStorage
import com.example.skyalert.dataSource.local.sharedPref.ISharedPreference
import com.example.skyalert.model.remote.Coord
import com.example.skyalert.model.remote.CurrentWeather
import com.example.skyalert.model.remote.FiveDaysForecast
import com.example.skyalert.network.UNITS
import com.example.skyalert.network.model.CurrentWeatherState
import com.example.skyalert.network.model.FiveDaysForecastState
import com.example.skyalert.services.alarm.model.Alert
import com.example.skyalert.view.screens.settings.model.LOCAL
import com.example.skyalert.view.screens.settings.model.LOCATION_SOURCE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WeatherLocalDatasourceImpl(
    private val weatherDao: WeatherDao, private val sharedPreference: ISharedPreference, private val localStorage: ILocalStorage
) : IWeatherLocalDatasource {

    /**
     * Singleton pattern
     * */
    companion object {
        @Volatile
        private lateinit var INSTANCE: IWeatherLocalDatasource
        fun getInstance(
            weatherDao: WeatherDao, sharedPreference: ISharedPreference, localStorage: ILocalStorage
        ) = if (::INSTANCE.isInitialized) INSTANCE else synchronized(this) {
            INSTANCE = WeatherLocalDatasourceImpl(weatherDao, sharedPreference, localStorage)
            INSTANCE
        }
    }


    /**
     * Shared preferences methods
     * */
    override fun getUnit(): UNITS = sharedPreference.getUnit()

    override fun setUnit(unit: UNITS) {
        sharedPreference.saveUnit(unit)
    }

    override fun setGPSLocation(coord: Coord) {
        sharedPreference.setGPSLocation(coord)
    }

    override fun getGPSLocation(): Coord = sharedPreference.getGPSLocation()


    override fun setLocationSource(locationType: LOCATION_SOURCE) {
        sharedPreference.setLocationSource(locationType)
    }

    override fun getLocationSource(): LOCATION_SOURCE = sharedPreference.getLocationSource()

    override fun setMapLocation(coord: Coord) {
        sharedPreference.setMapLocation(coord)
    }

    override fun getMapLocation(): Coord = sharedPreference.getMapLocation()
    override fun saveAlertLocation(coord: Coord) {
        sharedPreference.saveAlertCoord(coord)
    }

    override fun getAlertLocation(): Coord = sharedPreference.getAlertCoord()
    override fun setLanguage(language: LOCAL) {
        sharedPreference.setLanguage(language)
    }

    override fun getLanguage(): LOCAL = sharedPreference.getLanguage()

    /**
     *  Database methods
     * */
    // weather methods
    override suspend fun getGPSWeather(): CurrentWeatherState {
        val weather = weatherDao.getGPSWeather()
        return CurrentWeatherState.Success(weather)
    }

    override suspend fun getMapWeather(): CurrentWeatherState {
        val weather = weatherDao.getMapWeather()
        return CurrentWeatherState.Success(weather)

    }

    // bookmark methods
    override suspend fun insertCurrentWeather(currentWeather: CurrentWeather): Long {
        if (currentWeather.isCurrent) {
            val result = weatherDao.getLocalCurrentWeather()
            if (result != null) {
                val id = result.id
                currentWeather.id = id
                updateCurrentWeather(currentWeather)
                return 1
            }
        }

        return weatherDao.insertCurrentWeather(currentWeather)
    }


    override suspend fun getBookmarks(): Flow<List<CurrentWeather>> = flow {
        emit(weatherDao.getBookmarks())
    }

    override suspend fun deleteBookmarks(currentWeather: CurrentWeather): Int = weatherDao.deleteBookmarks(currentWeather)
    override suspend fun updateCurrentWeather(currentWeather: CurrentWeather): Int {
        return weatherDao.updateCurrentWeather(currentWeather)
    }

    override suspend fun getLocalCurrentWeather(): CurrentWeather = weatherDao.getLocalCurrentWeather()

    // alert methods
    override suspend fun getAllAlarms(): Flow<AlertsState> = flow {
        val alarms = weatherDao.getAllAlarms()
        if (alarms.isNotEmpty()) {
            emit(AlertsState.Success(alarms.toMutableList()))
        } else {
            emit(AlertsState.Error("No Alarms Found"))
        }
    }

    override suspend fun insertAlert(alert: Alert): Long = weatherDao.insertAlert(alert)
    override suspend fun deleteAlert(alert: Alert): Int = weatherDao.deleteAlert(alert)
    override suspend fun saveFiveDaysForecast(fiveDaysForecast: FiveDaysForecast) {
        localStorage.saveFiveDaysForecast(fiveDaysForecast)
    }

    override suspend fun getFiveDaysForecast(): Flow<FiveDaysForecastState> = flow {
        val fileName = sharedPreference.getFiveDaysForecastFileName()
        val result = localStorage.getFiveDaysForecast(fileName)
        Log .d("TAG", "getLocalFiveDaysForecast: $result")
        if (result != null) {
            emit(FiveDaysForecastState.Success(result))
        } else {
            emit(FiveDaysForecastState.Error("No Forecast Found"))
        }
    }
}