package com.example.skyalert.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skyalert.repository.IWeatherRepo
import com.example.skyalert.view.screens.home.viewModel.WeatherScreenViewModel
import com.example.skyalert.view.screens.map.viewModel.MapViewModel
import com.example.skyalert.view.screens.settings.viewModel.SettingsViewModel

class WeatherViewModelFactory(private val repository: IWeatherRepo) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(WeatherScreenViewModel::class.java) ->
                WeatherScreenViewModel(repository) as T


            modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
                SettingsViewModel(repository) as T

            modelClass.isAssignableFrom(MapViewModel::class.java) -> MapViewModel(repository) as T

            else -> throw IllegalArgumentException("ViewModel Not Found")
        }

    }
}