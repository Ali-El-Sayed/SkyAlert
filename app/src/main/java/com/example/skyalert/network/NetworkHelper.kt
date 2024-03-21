package com.example.skyalert.network

object NetworkHelper {
    private const val IC_URL = "https://openweathermap.org/img/wn/"
    private const val IC_EXT = ".png"

    fun getIconUrl(icon: String): String {
        return "$IC_URL$icon$IC_EXT"
    }
}