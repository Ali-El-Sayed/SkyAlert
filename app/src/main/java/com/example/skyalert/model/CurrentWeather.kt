package com.example.skyalert.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.skyalert.model.typeConverter.WeatherTypeConverter
import com.google.gson.annotations.SerializedName

@Entity(tableName = "current_weather")
data class CurrentWeather(
    @Embedded(prefix = "coord_")
    @SerializedName("coord") var coord: Coord,
    @SerializedName("weather")
    @TypeConverters(WeatherTypeConverter::class)
    var weather: ArrayList<Weather>,
    @SerializedName("base") var base: String,
    @Embedded(prefix = "main_")
    @SerializedName("main") var main: Main,
    @SerializedName("visibility") var visibility: Int,
    @Embedded(prefix = "wind_")
    @SerializedName("wind") var wind: Wind,
    @Embedded(prefix = "clouds_")
    @SerializedName("clouds") var clouds: Clouds,
    @SerializedName("dt") var dt: Int = 0,
    @Embedded(prefix = "sys_")
    @SerializedName("sys") var sys: Sys = Sys("", "", 0, 0),
    @SerializedName("timezone") var timezone: Int,
    @SerializedName("id") var id: Int,
    @SerializedName("name") var name: String,
    @SerializedName("cod") var cod: Int,
    var isFavorite: Boolean = false,
    @PrimaryKey
    var isCurrent: Boolean = false
)