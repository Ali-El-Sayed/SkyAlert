package com.example.skyalert.model.remote

import com.google.gson.annotations.SerializedName

data class Day(

    @SerializedName("dt") var dt: Int,
    @SerializedName("main") var main: Main,
    @SerializedName("weather") var weather: ArrayList<Weather>,
    @SerializedName("clouds") var clouds: Clouds,
    @SerializedName("wind") var wind: Wind,
    @SerializedName("visibility") var visibility: Int,
    @SerializedName("pop") var pop: Double,
    @SerializedName("sys") var sys: Sys,
    @SerializedName("rain") var rain: Rain,
    @SerializedName("dt_txt") var dtTxt: String

)