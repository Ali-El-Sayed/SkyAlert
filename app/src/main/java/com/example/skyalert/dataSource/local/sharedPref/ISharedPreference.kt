package com.example.skyalert.dataSource.local.sharedPref

import com.example.skyalert.model.Coord
import com.example.skyalert.network.UNITS

interface ISharedPreference {
    fun saveUnit(unit: UNITS)
    fun getUnit(): UNITS
    fun setDefaultLocation(coord: Coord)
    fun getDefaultLocation(): Coord
}