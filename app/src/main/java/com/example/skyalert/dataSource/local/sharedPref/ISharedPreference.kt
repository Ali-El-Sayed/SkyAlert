package com.example.skyalert.dataSource.local.sharedPref

import com.example.skyalert.network.UNITS

interface ISharedPreference {
    fun saveUnit(unit: UNITS)
    fun getUnit(): UNITS


}