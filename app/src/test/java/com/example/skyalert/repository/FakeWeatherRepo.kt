package com.example.skyalert.repository

import com.example.skyalert.dataSource.local.db.model.AlertsState
import com.example.skyalert.dataSource.local.sharedPref.KEYS
import com.example.skyalert.model.remote.City
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import java.util.UUID
import kotlin.random.Random

class FakeWeatherRepo : IWeatherRepo {
    private val unitsMap: MutableMap<String, UNITS> = mutableMapOf()
    private val locationSourceMap: MutableMap<String, LOCATION_SOURCE> = mutableMapOf()
    private val coordMap: MutableMap<String, Long> = mutableMapOf()
    private val mapLocationCoordMap: MutableMap<String, Long> = mutableMapOf()
    private val currentWeatherList: MutableStateFlow<MutableList<CurrentWeather>> =
        MutableStateFlow(mutableListOf())
    private var language: LOCAL = LOCAL.EN
    private var alerts: MutableList<Alert> = mutableListOf()

    override fun getUnit(): UNITS = unitsMap.getOrDefault(KEYS.UNIT, UNITS.METRIC)
    override fun setUnit(unit: UNITS) {
        unitsMap[KEYS.UNIT] = unit
    }

    override fun setGPSLocation(coord: Coord) {
        coordMap[KEYS.GPS_LOCATION_LAT] = coord.lat.toLong()
        coordMap[KEYS.GPS_LOCATION_LON] = coord.lon.toLong()
    }

    override fun getGPSLocation(): Coord {
        val lat = coordMap.getOrDefault(KEYS.GPS_LOCATION_LAT, 0)
        val lon = coordMap.getOrDefault(KEYS.GPS_LOCATION_LON, 0)
        return Coord(lat.toDouble(), lon.toDouble())
    }

    override fun setLocationSource(locationType: LOCATION_SOURCE) {
        locationSourceMap[KEYS.LOCATION_SOURCE] = locationType
    }

    override fun getLocationSource(): LOCATION_SOURCE =
        locationSourceMap.getOrDefault(KEYS.LOCATION_SOURCE, LOCATION_SOURCE.GPS)

    override fun setMapLocation(coord: Coord) {
        mapLocationCoordMap[KEYS.MAP_LOCATION_LAT] = coord.lat.toLong()
        mapLocationCoordMap[KEYS.MAP_LOCATION_LON] = coord.lon.toLong()
    }

    override fun getMapLocation(): Coord {
        val lat = mapLocationCoordMap.getOrDefault(KEYS.MAP_LOCATION_LAT, 0)
        val lon = mapLocationCoordMap.getOrDefault(KEYS.MAP_LOCATION_LON, 0)
        return Coord(lat.toDouble(), lon.toDouble())
    }

    override fun saveAlertLocation(coord: Coord) {
        coordMap[KEYS.ALERT_LOCATION_LAT] = coord.lat.toLong()
        coordMap[KEYS.ALERT_LOCATION_LON] = coord.lon.toLong()
    }

    override fun getAlertLocation(): Coord {
        val lat = coordMap.getOrDefault(KEYS.ALERT_LOCATION_LAT, 0)
        val lon = coordMap.getOrDefault(KEYS.ALERT_LOCATION_LON, 0)
        return Coord(lat.toDouble(), lon.toDouble())
    }

    override fun setLanguage(language: LOCAL) {
        this.language = language
    }

    override fun getLanguage(): LOCAL = language

    override fun getLocalCurrentWeather(): Flow<CurrentWeatherState> = flow {
        val source = getLocationSource()
        var currentWeather =
            if (source == LOCATION_SOURCE.GPS)
                currentWeatherList.value.find { it.isGPS }
            else
                currentWeatherList.value.find { it.isMap }

        if (currentWeather != null) {
            emit(CurrentWeatherState.Success(currentWeather))
        } else {
            emit(CurrentWeatherState.Error("Current weather not found"))
        }
    }

    override suspend fun getGPSWeather(): CurrentWeatherState {
        val currentWeather = currentWeatherList.value.find { it.isGPS }
        if (currentWeather != null) {
            return CurrentWeatherState.Success(currentWeather)
        }
        return CurrentWeatherState.Error("Current weather not found")
    }

    override suspend fun getMapWeather(): CurrentWeatherState {
        val currentWeather = currentWeatherList.value.find { it.isMap }
        if (currentWeather != null) {
            return CurrentWeatherState.Success(currentWeather)
        }
        return CurrentWeatherState.Error("Current weather not found")
    }

    override suspend fun getFavoriteWeather(): Flow<List<CurrentWeather>> {
        return flow { emit(currentWeatherList.value.filter { it.isFavorite }) }
    }

    override suspend fun deleteAlert(currentWeather: CurrentWeather): Int {
        if (currentWeatherList.value.remove(currentWeather))
            return 1
        return 0
    }

    override suspend fun deleteAlert(alert: Alert): Int {
        if (alerts.remove(alert))
            return 1
        return 0
    }

    override suspend fun insertCurrentWeather(currentWeather: CurrentWeather): Long {
        val id = Random.nextLong()
        currentWeather.idRoom = id
        currentWeatherList.value.add(currentWeather)
        return id
    }

    override suspend fun getAllAlerts(): Flow<AlertsState> {
        return flow { emit(AlertsState.Success(alerts)) }
    }

    override suspend fun insertAlert(alert: Alert): Long {
        alert.uuid = UUID.randomUUID()
        val res = alerts.add(alert)
        return if (res) 1 else 0
    }

    override suspend fun getCurrentWeather(): Flow<CurrentWeatherState> {
        val source = getLocationSource()
        var currentWeather =
            if (source == LOCATION_SOURCE.GPS)
                currentWeatherList.value.find { it.isGPS }
            else
                currentWeatherList.value.find { it.isMap }

        if (currentWeather != null) {
            return flowOf(CurrentWeatherState.Success(currentWeather))
        }
        return flowOf(CurrentWeatherState.Error("Current weather not found"))
    }

    override suspend fun getHourlyForecast(cnt: Int): Flow<FiveDaysForecastState> {
        val currentWeather = currentWeatherList.value.find { it.isGPS }
        if (currentWeather != null) {
            return flowOf(
                FiveDaysForecastState.Success(
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
                )
            )
        }
        return flowOf(FiveDaysForecastState.Error("Current weather not found"))
    }

    override suspend fun getCurrentWeatherByCoord(coord: Coord): Flow<CurrentWeatherState> {
        val currentWeather = currentWeatherList.value.find { it.coord == coord }
        if (currentWeather != null) {
            return flowOf(CurrentWeatherState.Success(currentWeather))
        }
        return flowOf(CurrentWeatherState.Error("Current weather not found"))
    }

    override suspend fun saveLocalFiveDaysForecast(fiveDaysForecast: FiveDaysForecast): String {
        return "FiveDaysForecast"
    }

    override suspend fun getFiveDaysForecast(fileName: String): Flow<FiveDaysForecastState> {
        return flow {
            emit(
                FiveDaysForecastState.Success(
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
                )
            )
        }
    }

}