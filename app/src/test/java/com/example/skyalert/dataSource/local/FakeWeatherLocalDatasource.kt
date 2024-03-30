package com.example.skyalert.dataSource.local

import com.example.skyalert.model.remote.Coord
import com.example.skyalert.model.remote.CurrentWeather
import com.example.skyalert.network.UNITS
import com.example.skyalert.network.model.CurrentWeatherState
import com.example.skyalert.util.getEmptyWeatherObj
import com.example.skyalert.view.screens.settings.model.LOCATION_SOURCE
import kotlin.random.Random

class FakeWeatherLocalDatasource : IWeatherLocalDatasource {
    private var unit: UNITS = UNITS.METRIC
    private var locationSource: LOCATION_SOURCE = LOCATION_SOURCE.GPS
    private var coord: Coord = Coord(0.0, 0.0)
    private var mapLocationCoord: Coord = Coord(0.0, 0.0)
    private var currentWeatherList: MutableList<CurrentWeather> = mutableListOf()
    private var alertLocation: Coord = Coord(0.0, 0.0)
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

    override suspend fun getFavoriteWeather(): List<CurrentWeather> {
        return currentWeatherList.filter { it.isFavorite }
    }

    override suspend fun insertCurrentWeather(currentWeather: CurrentWeather): Long {
        val id = Random.nextLong()
        currentWeather.idRoom = id
        if (currentWeather.isFavorite) currentWeatherList.add(currentWeather)

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


}