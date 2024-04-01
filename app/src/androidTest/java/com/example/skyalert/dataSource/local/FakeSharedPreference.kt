package com.example.skyalert.dataSource.local

import com.example.skyalert.dataSource.local.sharedPref.ISharedPreference
import com.example.skyalert.model.remote.Coord
import com.example.skyalert.network.UNITS
import com.example.skyalert.view.screens.settings.model.LOCAL
import com.example.skyalert.view.screens.settings.model.LOCATION_SOURCE

class FakeSharedPreference : ISharedPreference {
    override fun saveUnit(unit: UNITS) {
        TODO("Not yet implemented")
    }

    override fun getUnit(): UNITS {
        TODO("Not yet implemented")
    }

    override fun setGPSLocation(coord: Coord) {
        TODO("Not yet implemented")
    }

    override fun getGPSLocation(): Coord {
        TODO("Not yet implemented")
    }

    override fun setMapLocation(coord: Coord) {
        TODO("Not yet implemented")
    }

    override fun getMapLocation(): Coord {
        TODO("Not yet implemented")
    }

    override fun setLocationSource(locationType: LOCATION_SOURCE) {
        TODO("Not yet implemented")
    }

    override fun getLocationSource(): LOCATION_SOURCE {
        TODO("Not yet implemented")
    }

    override fun saveAlertCoord(coord: Coord) {
        TODO("Not yet implemented")
    }

    override fun getAlertCoord(): Coord {
        TODO("Not yet implemented")
    }

    override fun setLanguage(language: LOCAL) {
        TODO("Not yet implemented")
    }

    override fun getLanguage(): LOCAL {
        TODO("Not yet implemented")
    }

    override fun getFiveDaysForecastFileName(): String {
        TODO("Not yet implemented")
    }

    override fun saveFiveDaysForecastFileName(fileName: String) {
        TODO("Not yet implemented")
    }

}
