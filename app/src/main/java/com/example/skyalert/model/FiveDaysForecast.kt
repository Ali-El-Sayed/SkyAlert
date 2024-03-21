package com.example.skyalert.model

import com.google.gson.annotations.SerializedName


data class FiveDaysForecast(

    @SerializedName("cod") var cod: String,
    @SerializedName("message") var message: Int,
    @SerializedName("cnt") var cnt: Int,
    @SerializedName("list") var list: ArrayList<Day> = arrayListOf(),
    @SerializedName("city") var city: City = City(
        0,
        "",
        Coord(0.0, 0.0),
        "",
        0,
        0,
        0, 0
    )

)