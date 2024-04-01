package com.example.skyalert.dataSource.local

import com.example.skyalert.dataSource.local.db.model.AlertsState
import com.example.skyalert.model.remote.City
import com.example.skyalert.model.remote.Coord
import com.example.skyalert.model.remote.CurrentWeather
import com.example.skyalert.model.remote.FiveDaysForecast
import com.example.skyalert.network.UNITS
import com.example.skyalert.network.model.CurrentWeatherState
import com.example.skyalert.network.model.FiveDaysForecastState
import com.example.skyalert.services.alarm.model.Alert
import com.example.skyalert.util.getEmptyWeatherObj
import com.example.skyalert.view.screens.settings.model.LOCAL
import com.example.skyalert.view.screens.settings.model.LOCATION_SOURCE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import kotlin.random.Random

class FakeWeatherLocalDatasource : IWeatherLocalDatasource {
    private var unit: UNITS = UNITS.METRIC
    private var locationSource: LOCATION_SOURCE = LOCATION_SOURCE.GPS
    private var coord: Coord = Coord(0.0, 0.0)
    private var mapLocationCoord: Coord = Coord(0.0, 0.0)
    private var currentWeatherList: MutableList<CurrentWeather> = mutableListOf()
    private var alertLocation: Coord = Coord(0.0, 0.0)
    private var language: LOCAL = LOCAL.EN
    private var alerts: MutableList<Alert> = mutableListOf()
    override fun getUnit(): UNITS = unit

    override fun setUnit(unit: UNITS) {
        this.unit = unit
    }

    override fun setGPSLocation(coord: Coord) {
        this.coord = coord
    }

    override fun getGPSLocation(): Coord = coord
    override fun setLocationSource(locationType: LOCATION_SOURCE) {
        this.locationSource = locationType
    }

    override fun getLocationSource(): LOCATION_SOURCE = locationSource

    override fun setMapLocation(coord: Coord) {
        this.mapLocationCoord = coord
    }

    override fun getMapLocation(): Coord = mapLocationCoord
    override fun saveAlertLocation(coord: Coord) {
        this.alertLocation = coord
    }

    override fun getAlertLocation(): Coord = alertLocation
    override fun setLanguage(language: LOCAL) {
        this.language = language
    }

    override fun getLanguage(): LOCAL = language

    override suspend fun getGPSWeather(): CurrentWeatherState {
        for (weather in currentWeatherList) if (weather.isGPS) CurrentWeatherState.Success(weather)
        val emptyCurrentWeather = getEmptyWeatherObj()
        emptyCurrentWeather.isGPS = true
        return CurrentWeatherState.Success(emptyCurrentWeather)
    }

    override suspend fun getMapWeather(): CurrentWeatherState {
        for (weather in currentWeatherList) if (weather.isMap) CurrentWeatherState.Success(weather)
        val emptyCurrentWeather = getEmptyWeatherObj()
        emptyCurrentWeather.isMap = true
        return CurrentWeatherState.Success(emptyCurrentWeather)
    }

    override suspend fun getBookmarks(): Flow<List<CurrentWeather>> {
        return flow { currentWeatherList.filter { it.isFavorite } }
    }

    override suspend fun insertCurrentWeather(currentWeather: CurrentWeather): Long {
        val id = Random.nextLong()
        currentWeather.idRoom = id
        if (currentWeather.isFavorite) currentWeatherList.add(currentWeather)
        else

            when (locationSource) {
                LOCATION_SOURCE.GPS -> {
                    currentWeather.isGPS = true
                    currentWeatherList.removeIf { it.isMap }
                    currentWeather.idRoom = id
                    currentWeatherList.add(currentWeather)
                }

                LOCATION_SOURCE.MAP -> {
                    currentWeather.isMap = true
                    currentWeatherList.removeIf { it.isMap }
                    currentWeather.idRoom = id
                    currentWeatherList.add(currentWeather)
                }

            }
        return id
    }

    override suspend fun deleteBookmarks(currentWeather: CurrentWeather): Int {
        if (currentWeather.isFavorite) {
            currentWeatherList.remove(currentWeather)
            return 1
        }
        return 0
    }

    override suspend fun updateCurrentWeather(currentWeather: CurrentWeather): Int {
        val index = currentWeatherList.indexOfFirst { it.idRoom == currentWeather.idRoom }
        if (index != -1) {
            currentWeatherList[index] = currentWeather
            return 1
        }
        return 0
    }

    override suspend fun getLocalCurrentWeather(): CurrentWeather {
        for (weather in currentWeatherList) if (weather.isCurrent) return weather
        return getEmptyWeatherObj()
    }

    override suspend fun getAllAlarms(): Flow<AlertsState> {
        return flow { AlertsState.Success(alerts) }
    }

    override suspend fun insertAlert(alert: Alert): Long {
        alert.uuid = UUID.randomUUID()
        val res = alerts.add(alert)
        return if (res) 1 else 0
    }

    override suspend fun deleteAlert(alert: Alert): Int {
        val res = alerts.remove(alert)
        return if (res) 1 else 0
    }

    override suspend fun saveFiveDaysForecast(fiveDaysForecast: FiveDaysForecast) {}

    override suspend fun getFiveDaysForecast(): Flow<FiveDaysForecastState> {
        return flow {
            FiveDaysForecast(
                cod = "200", message = 0, cnt = 40, list = arrayListOf(), city = City(
                    id = 0,
                    name = "City",
                    coord = Coord(0.0, 0.0),
                    country = "Country",
                    population = 0,
                    timezone = 0,
                    sunrise = 0,
                    sunset = 0
                )
            )
        }
    }


}