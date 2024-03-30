package com.example.skyalert.model.remote

import com.google.gson.annotations.SerializedName


data class Sys(
    @SerializedName("pod") var pod: String = "",
    @SerializedName("country") var country: String = "",
    @SerializedName("sunrise") var sunrise: Int = 0,
    @SerializedName("sunset") var sunset: Int = 0
)