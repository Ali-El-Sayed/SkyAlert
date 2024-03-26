package com.example.skyalert.view.screens.home.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skyalert.model.Coord
import com.example.skyalert.network.model.CurrentWeatherState
import com.example.skyalert.network.model.FiveDaysForecastState
import com.example.skyalert.repository.IWeatherRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class WeatherScreenViewModel(private val _weatherRepo: IWeatherRepo) : ViewModel() {
    private val _currentWeather = MutableStateFlow<CurrentWeatherState>(CurrentWeatherState.Loading)
    val currentWeather: StateFlow<CurrentWeatherState> = _currentWeather.asStateFlow()

    private val _hourlyWeather =
        MutableStateFlow<FiveDaysForecastState>(FiveDaysForecastState.Loading)
    val hourlyWeather: StateFlow<FiveDaysForecastState> = _hourlyWeather.asStateFlow()


    fun getCurrentWeather() {
        _currentWeather.value = CurrentWeatherState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            _weatherRepo.getCurrentWeather().catch { e ->
                e.printStackTrace()
                _currentWeather.value =
                    CurrentWeatherState.Error(e.message ?: "An error occurred")
            }.collect { _currentWeather.value = it }
        }
    }

    fun getCurrentWeatherByCoord(coord: Coord) {
        _currentWeather.value = CurrentWeatherState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            _weatherRepo.getCurrentWeatherByCoord(coord).catch { e ->
                e.printStackTrace()
                _currentWeather.value =
                    CurrentWeatherState.Error(e.message ?: "An error occurred")
            }.collect { _currentWeather.value = it }
        }
    }

    fun getHourlyWeather(cnt: Int = 8) {
        _hourlyWeather.value = FiveDaysForecastState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            _weatherRepo.getHourlyForecast(cnt).catch { e ->
                e.printStackTrace()
                _hourlyWeather.value =
                    FiveDaysForecastState.Error(e.message ?: "An error occurred")
            }.collect { _hourlyWeather.value = it }
        }
    }

    fun getUnit() = _weatherRepo.getUnit()

    fun setDefaultLocation(coord: Coord) {
        _weatherRepo.setGPSLocation(coord)
    }

}