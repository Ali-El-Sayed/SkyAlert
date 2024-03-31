package com.example.skyalert.repository

import com.example.skyalert.dataSource.local.db.model.BookmarkedWeatherState
import com.example.skyalert.dataSource.local.sharedPref.KEYS
import com.example.skyalert.model.remote.Coord
import com.example.skyalert.model.remote.CurrentWeather
import com.example.skyalert.network.UNITS
import com.example.skyalert.network.model.FiveDaysForecastState
import com.example.skyalert.view.screens.settings.model.LOCATION_SOURCE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

class FakeWeatherRepo : IWeatherRepo {
    private val unitsMap: MutableMap<String, UNITS> = mutableMapOf()
    private val locationSourceMap: MutableMap<String, LOCATION_SOURCE> = mutableMapOf()
    private val coordMap: MutableMap<String, Long> = mutableMapOf()
    private val mapLocationCoordMap: MutableMap<String, Long> = mutableMapOf()
    private val currentWeatherList: MutableStateFlow<List<CurrentWeather>> =
        MutableStateFlow(emptyList())

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

    override fun getLocalCurrentWeather(): Flow<BookmarkedWeatherState> {
        TODO("Not yet implemented")
    }

    override suspend fun getGPSWeather(): BookmarkedWeatherState {
        TODO("Not yet implemented")
    }

    override suspend fun getMapWeather(): BookmarkedWeatherState {
        TODO("Not yet implemented")
    }

    override suspend fun getFavoriteWeather(): List<CurrentWeather> {
        TODO("Not yet implemented")
    }

    override suspend fun insertCurrentWeather(currentWeather: CurrentWeather): Long {
        currentWeatherList.value += currentWeather
        return 1
    }

    override suspend fun getCurrentWeather(): Flow<BookmarkedWeatherState> {
        TODO("Not yet implemented")
    }

    override suspend fun getHourlyForecast(cnt: Int): Flow<FiveDaysForecastState> {
        TODO("Not yet implemented")
    }

    override suspend fun getCurrentWeatherByCoord(coord: Coord): Flow<BookmarkedWeatherState> {
        val currentWeather = currentWeatherList.value.find { it.coord == coord }
        if (currentWeather != null) {
            return flowOf(BookmarkedWeatherState.Success(currentWeather))
        }
        return flowOf(BookmarkedWeatherState.Error("Current weather not found"))
    }
}