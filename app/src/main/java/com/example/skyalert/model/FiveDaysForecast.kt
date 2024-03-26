package com.example.skyalert.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(tableName = "five_days_forecast")
data class FiveDaysForecast(
    @PrimaryKey @SerializedName("cod") var cod: String,
    @SerializedName("message") var message: Int,
    @SerializedName("cnt") var cnt: Int,
    @SerializedName("list") var list: ArrayList<Day>,
    @SerializedName("city") var city: City
)