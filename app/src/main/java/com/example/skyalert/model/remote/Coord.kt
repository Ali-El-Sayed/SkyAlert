package com.example.skyalert.model.remote

import com.google.gson.annotations.SerializedName


data class Coord(
    @SerializedName("lat") var lat: Double,
    @SerializedName("lon") var lon: Double
)