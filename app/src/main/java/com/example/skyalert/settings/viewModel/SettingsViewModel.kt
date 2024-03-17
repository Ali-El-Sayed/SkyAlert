package com.example.skyalert.settings.viewModel

import androidx.lifecycle.ViewModel
import com.example.skyalert.network.UNITS
import com.example.skyalert.repository.IWeatherRepo

class SettingsViewModel(private val _repo: IWeatherRepo) : ViewModel(
) {

    fun setUnit(unit: UNITS) {
        _repo.setUnit(unit)
    }
}