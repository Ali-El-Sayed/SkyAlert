package com.example.skyalert.interfaces

import com.example.skyalert.model.remote.Coord
import com.example.skyalert.model.remote.CurrentWeather

interface BottomSheetCallbacks {
    fun setDefaultLocation(coord: Coord)
    fun saveBookmark()
    fun setAlert()
    fun onAddToFavorite(currentWeather: CurrentWeather)
}