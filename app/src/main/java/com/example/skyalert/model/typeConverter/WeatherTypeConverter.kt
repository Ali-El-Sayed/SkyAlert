package com.example.skyalert.model.typeConverter

import androidx.room.TypeConverter
import com.example.skyalert.model.remote.Weather
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class WeatherTypeConverter {
    @TypeConverter
    fun fromWeatherList(weather: ArrayList<Weather>): String {
        return Gson().toJson(weather)
    }

    @TypeConverter
    fun toWeatherList(weather: String): ArrayList<Weather> {
        val listType = object : TypeToken<ArrayList<Weather>>() {}.type
        return Gson().fromJson(weather, listType)
    }
}
