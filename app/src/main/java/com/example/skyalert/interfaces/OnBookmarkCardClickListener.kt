package com.example.skyalert.interfaces

import com.example.skyalert.model.remote.CurrentWeather

interface OnBookmarkCardClickListener {
    fun onCardClick(currentWeather: CurrentWeather)
}