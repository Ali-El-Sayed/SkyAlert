package com.example.skyalert.model.remote

import com.google.gson.annotations.SerializedName

data class Clouds(
    @SerializedName("all") var all: Int
)