package com.example.skyalert.view.screens.map.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skyalert.model.remote.Coord
import com.example.skyalert.model.remote.CurrentWeather
import com.example.skyalert.network.model.CurrentWeatherState
import com.example.skyalert.repository.IWeatherRepo
import com.example.skyalert.services.alarm.model.Alert
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class MapViewModel(private val _weatherRepo: IWeatherRepo) : ViewModel() {

    private val _currentWeather = MutableStateFlow<CurrentWeatherState>(CurrentWeatherState.Loading)
    val currentWeather: StateFlow<CurrentWeatherState> = _currentWeather.asStateFlow()
    val alert by lazy { Alert() }
    fun getCurrentWeatherByCoord(coord: Coord) {
        _currentWeather.value = CurrentWeatherState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            _weatherRepo.getCurrentWeatherByCoord(coord).catch { e ->
                e.printStackTrace()
                _currentWeather.value = CurrentWeatherState.Error(e.message ?: "An error occurred")
            }.collect { _currentWeather.value = it }
        }
    }

    fun getMapLocation(): Coord = _weatherRepo.getMapLocation()


    fun setMapLocation(coord: Coord) {
        _weatherRepo.setMapLocation(coord)
    }

    fun saveAlertLocation(coord: Coord) {
        _weatherRepo.saveAlertLocation(coord)
    }

    fun insertNewAlert(alert: Alert) {
        viewModelScope.launch(Dispatchers.IO) { _weatherRepo.insertAlert(alert) }
    }

    suspend fun addToFavorite(currentWeather: CurrentWeather): Long {
        currentWeather.isFavorite = true
        return viewModelScope.async(Dispatchers.IO) {
            _weatherRepo.insertCurrentWeather(currentWeather)
        }.await()
    }
}