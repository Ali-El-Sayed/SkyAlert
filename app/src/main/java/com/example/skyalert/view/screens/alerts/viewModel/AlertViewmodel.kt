package com.example.skyalert.view.screens.alerts.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skyalert.dataSource.local.db.model.AlertsState
import com.example.skyalert.repository.IWeatherRepo
import com.example.skyalert.services.alarm.model.Alert
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AlertViewmodel(private val repo: IWeatherRepo) : ViewModel() {
    private val _alerts: MutableStateFlow<AlertsState> = MutableStateFlow(AlertsState.Loading)
    val alerts = _alerts.asStateFlow()

    fun getAlerts() {
        _alerts.value = AlertsState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            repo.getAllAlerts().collect { _alerts.value = it }
        }
    }

    suspend fun deleteAlert(alert: Alert) {
        val result = viewModelScope.async(Dispatchers.IO) { repo.deleteAlert(alert) }.await()
        if (result > 0) {
            _alerts.emit(
                AlertsState.Success(
                    (_alerts.value as AlertsState.Success).alerts.apply {
                        remove(alert)
                    }
                )
            )
        }
    }

}