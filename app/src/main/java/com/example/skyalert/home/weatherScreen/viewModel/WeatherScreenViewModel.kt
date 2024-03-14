package com.example.skyalert.home.weatherScreen.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skyalert.network.model.CurrentWeatherState
import com.example.skyalert.repository.IWeatherRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherScreenViewModel(private val _weatherRepo: IWeatherRepo) : ViewModel() {
    private val _currentWeather = MutableStateFlow<CurrentWeatherState>(CurrentWeatherState.Loading)
    val currentWeather: StateFlow<CurrentWeatherState> = _currentWeather


    fun getCurrentWeather(lat: Double, lon: Double) {
        _currentWeather.value = CurrentWeatherState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            _weatherRepo.getCurrentWeather(lat, lon).collect {
                _currentWeather.value = it
            }
        }

    }

}