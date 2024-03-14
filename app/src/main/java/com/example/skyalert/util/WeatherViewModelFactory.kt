package com.example.skyalert.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skyalert.home.weatherScreen.viewModel.WeatherScreenViewModel
import com.example.skyalert.repository.IWeatherRepo

class WeatherViewModelFactory(private val repository: IWeatherRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(WeatherScreenViewModel::class.java) -> {
                return WeatherScreenViewModel(repository) as T
            }

            else -> throw IllegalArgumentException("ViewModel Not Found")
        }

    }
}