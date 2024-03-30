package com.example.skyalert.model.remote

import com.google.gson.annotations.SerializedName

data class City(
    @SerializedName("id") var id: Int,
    @SerializedName("name") var name: String,
    @SerializedName("coord") var coord: Coord = Coord(0.0, 0.0),
    @SerializedName("country") var country: String,
    @SerializedName("population") var population: Int,
    @SerializedName("timezone") var timezone: Int,
    @SerializedName("sunrise") var sunrise: Int,
    @SerializedName("sunset") var sunset: Int
)