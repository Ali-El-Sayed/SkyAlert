package com.example.skyalert.util

import com.example.skyalert.model.remote.Clouds
import com.example.skyalert.model.remote.Coord
import com.example.skyalert.model.remote.CurrentWeather
import com.example.skyalert.model.remote.Main
import com.example.skyalert.model.remote.Sys
import com.example.skyalert.model.remote.Weather
import com.example.skyalert.model.remote.Wind
import com.example.skyalert.network.UNITS

fun getEmptyWeatherObj(): CurrentWeather {
    val coord = Coord(0.0, 0.0)
    val weatherList = arrayListOf<Weather>()
    val main = Main(0.0, 0.0, 0.0, 0.0, 0, 0, 0, 0, 0.0)
    val wind = Wind(0.0, 0, 0.0)
    val clouds = Clouds(0)
    val sys = Sys("", "", 0, 0)

    return CurrentWeather(
        coord = coord,
        weather = weatherList,
        base = "",
        main = main,
        visibility = 0,
        wind = wind,
        clouds = clouds,
        dt = 0,
        sys = sys,
        timezone = 0,
        id = 0,
        name = "",
        cod = 0,
        unit = UNITS.METRIC,
        isFavorite = false,
        isGPS = false,
        isMap = false
    )
}
