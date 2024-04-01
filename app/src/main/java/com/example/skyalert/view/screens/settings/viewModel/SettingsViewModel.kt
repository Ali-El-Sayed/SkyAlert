package com.example.skyalert.view.screens.settings.viewModel

import androidx.lifecycle.ViewModel
import com.example.skyalert.model.remote.Coord
import com.example.skyalert.network.UNITS
import com.example.skyalert.repository.IWeatherRepo
import com.example.skyalert.view.screens.settings.model.LOCAL
import com.example.skyalert.view.screens.settings.model.LOCATION_SOURCE

class SettingsViewModel(private val _repo: IWeatherRepo) : ViewModel() {

    fun setUnit(unit: UNITS) {
        _repo.setUnit(unit)
    }


    fun getMapLocation() = _repo.getMapLocation()
    fun setMapLocation(coord: Coord) {
        _repo.setMapLocation(coord)
    }

    fun setLocationType(locationType: LOCATION_SOURCE) {
        _repo.setLocationSource(locationType)
    }

    fun setLanguage(language: LOCAL) {
        _repo.setLanguage(language)
    }

}