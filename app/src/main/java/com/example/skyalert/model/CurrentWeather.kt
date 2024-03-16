package com.example.example

import com.example.skyalert.model.Clouds
import com.example.skyalert.model.Coord
import com.example.skyalert.model.Main
import com.example.skyalert.model.Sys
import com.example.skyalert.model.Weather
import com.example.skyalert.model.Wind
import com.google.gson.annotations.SerializedName


data class CurrentWeather(

    @SerializedName("coord") var coord: Coord,
    @SerializedName("weather") var weather: ArrayList<Weather>,
    @SerializedName("base") var base: String,
    @SerializedName("main") var main: Main,
    @SerializedName("visibility") var visibility: Int,
    @SerializedName("wind") var wind: Wind,
    @SerializedName("clouds") var clouds: Clouds,
    @SerializedName("dt") var dt: Int = 0,
    @SerializedName("sys") var sys: Sys,
    @SerializedName("timezone") var timezone: Int,
    @SerializedName("id") var id: Int,
    @SerializedName("name") var name: String,
    @SerializedName("cod") var cod: Int

)