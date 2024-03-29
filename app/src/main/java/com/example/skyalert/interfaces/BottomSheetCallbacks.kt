package com.example.skyalert.interfaces

import com.example.skyalert.model.Coord

interface BottomSheetCallbacks {
    fun setDefaultLocation(coord: Coord)
    fun saveBookmark()
    fun setAlert()
}