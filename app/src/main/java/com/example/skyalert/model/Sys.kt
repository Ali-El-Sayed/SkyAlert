package com.example.skyalert.model

import com.google.gson.annotations.SerializedName


data class Sys(
    @SerializedName("pod") var pod: String
)