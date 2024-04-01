package com.example.skyalert.model.remote

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.skyalert.model.typeConverter.WeatherTypeConverter
import com.example.skyalert.network.UNITS
import com.google.gson.annotations.SerializedName

@Entity(tableName = "current_weather")
data class CurrentWeather(
    @PrimaryKey(autoGenerate = true) var idRoom: Long = 0,
    @Embedded(prefix = "coord_") @SerializedName("coord") var coord: Coord,
    @SerializedName("weather") @TypeConverters(WeatherTypeConverter::class) var weather: ArrayList<Weather>,
    @SerializedName("base") var base: String,
    @Embedded(prefix = "main_") @SerializedName("main") var main: Main,
    @SerializedName("visibility") var visibility: Int,
    @Embedded(prefix = "wind_") @SerializedName("wind") var wind: Wind,
    @Embedded(prefix = "clouds_") @SerializedName("clouds") var clouds: Clouds,
    @SerializedName("dt") var dt: Int = 0,
    @Embedded(prefix = "sys_") @SerializedName("sys") var sys: Sys = Sys("", "", 0, 0),
    @SerializedName("timezone") var timezone: Int,
    @SerializedName("id") var id: Int,
    @SerializedName("name") var name: String,
    @SerializedName("cod") var cod: Int,
    var unit: UNITS = UNITS.METRIC,
    var isFavorite: Boolean = false,
    var isCurrent: Boolean = false,
    var isGPS: Boolean = false,
    var isMap: Boolean = false
)