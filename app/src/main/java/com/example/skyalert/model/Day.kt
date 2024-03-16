package com.example.skyalert.model

import com.google.gson.annotations.SerializedName

data class Day(

    @SerializedName("dt") var dt: Int,
    @SerializedName("main") var main: Main = Main(
        0.0, 0.0, 0.0, 0.0, 0, 0, 0, 0, 0.0
    ),
    @SerializedName("weather") var weather: ArrayList<Weather> = arrayListOf(),
    @SerializedName("clouds") var clouds: Clouds = Clouds(0),
    @SerializedName("wind") var wind: Wind = Wind(0.0, 0, 0.0),
    @SerializedName("visibility") var visibility: Int,
    @SerializedName("pop") var pop: Int,
    @SerializedName("sys") var sys: Sys = Sys("", "", 0, 0),
    @SerializedName("dt_txt") var dtTxt: String

)