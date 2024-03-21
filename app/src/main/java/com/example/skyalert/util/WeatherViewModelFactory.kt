package com.example.skyalert.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skyalert.repository.IWeatherRepo
import com.example.skyalert.settings.viewModel.SettingsViewModel
import com.example.skyalert.view.screens.home.weatherScreen.viewModel.WeatherScreenViewModel

class WeatherViewModelFactory(private val repository: IWeatherRepo) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(WeatherScreenViewModel::class.java) -> {
                return WeatherScreenViewModel(repository) as T
            }

            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                return SettingsViewModel(repository) as T
            }

            else -> throw IllegalArgumentException("ViewModel Not Found")
        }

    }
}